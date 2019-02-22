package com.camellias.mysticalmetallurgy.library.tileslottedinventory;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemStackHandler;

import javax.activity.InvalidActivityException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class TileEntitySlottedInventory<T extends InventorySlot> extends IInventoryAccess<T>
{
    private static final String NBT_INVENTORY = "inventory";
    private static final String NBT_SLOTID = "slotid";
    private static final String NBT_SLOT = "slot";
    private static final String NBT_SLOTLIST = "slot_list";

    private List<T> slots = new ArrayList<>();
    private boolean isInit;

    protected ItemStackHandler inventory;

    public TileEntitySlottedInventory(int limitStackSize, TileEntityType<?> type) {
        super(type);
        try
        {
            initSlots();
        }
        catch (InvalidActivityException ignored)
        {
            //it wont throw
        }

        isInit = true;
        inventory = new ItemStackHandler(slots.size())
        {
            @Override
            public int getSlotLimit(int slot)
            {
                return limitStackSize;
            }

            @Override
            protected void onContentsChanged(int slot) {
                T s = slots.get(slot);
                s.onChanged();
                onSlotContentChanged(s);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                return slots.get(slot).acceptStack(stack);
            }
        };
    }

    protected abstract void initSlots() throws InvalidActivityException;

    protected void addSlot(T slot) throws InvalidActivityException
    {
        if (isInit)
            throw new InvalidActivityException("not allowed to add slots after init!");
        slot.slot = slots.size();
        slot.inventory = this;
        slots.add(slot);
    }

    public Iterable<T> getSlots() { return slots; }

    @Nullable
    public InventorySlot getSlotHit(EnumFacing facing, float x, float z)
    {
        return getSlotHit(facing, (double) x, z);
    }

    @Nullable
    public T getSlotHit(EnumFacing facing, double x, double z)
    {
        for (T slot : slots)
            if (slot.isHit(facing, x, z))
                return slot;
        return null;
    }

    protected void onSlotContentChanged(T slot) {

    }

    @Override
    ItemStack extractInternal(@Nonnull T slot, int amount, boolean simulate)
    {
        return inventory.extractItem(slot.slot, amount, simulate);
    }

    @Override
    ItemStack insertInternal(@Nonnull T slot, ItemStack stack, boolean simulate)
    {
        if (slot.acceptStack(stack))
            return inventory.insertItem(slot.slot, stack, simulate);
        return stack;
    }

    @Override
    ItemStack getStackInSlotInternal(@Nonnull T slot)
    {
        return inventory.getStackInSlot(slot.slot);
    }

    @Override
    void setStackInSlotInternal(@Nonnull T slot, ItemStack stack)
    {
        inventory.setStackInSlot(slot.slot, stack);
    }



    //region <syncing>
    @Override
    public void markDirty()
    {
        IBlockState state = world.getBlockState(pos);
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 0);
        super.markDirty();
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag)
    {
        super.handleUpdateTag(tag);
        markDirty();
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag()
    {
        return write(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(getPos(), -999, write(new NBTTagCompound()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
    {
        super.onDataPacket(net, packet);
        read(packet.getNbtCompound());
    }

    @Nonnull
    @Override
    public NBTTagCompound write(NBTTagCompound compound)
    {
        compound = super.write(compound);
        compound.put(NBT_INVENTORY, inventory.serializeNBT());
        NBTTagList list = new NBTTagList();
        for (T slot : slots)
        {
            if (slot.hasNBT())
            {
                NBTTagCompound slotNbt = new NBTTagCompound();
                slotNbt.putInt(NBT_SLOTID, slot.slot);
                slotNbt.put(NBT_SLOT, slot.serializeNBT());
                list.add(slotNbt);
            }
        }
        compound.put(NBT_SLOTLIST, list);
        return compound;
    }

    @Override
    public void read(@Nonnull NBTTagCompound cmp)
    {
        super.read(cmp);
        if (cmp.hasUniqueId(NBT_INVENTORY)) inventory.deserializeNBT(cmp.getCompound(NBT_INVENTORY));
        if (cmp.hasUniqueId(NBT_SLOTLIST))
        {
            NBTTagList list = (NBTTagList)cmp.get(NBT_SLOTLIST);
            list.forEach(nbtBase -> {
                NBTTagCompound nbt = (NBTTagCompound)nbtBase;
                if (nbt.hasUniqueId(NBT_SLOTID) && nbt.hasUniqueId(NBT_SLOT))
                {
                    int slotid = nbt.getInt(NBT_SLOTID);
                    if (slotid < slots.size() && slotid >= 0)
                    {
                        slots.get(slotid).deserializeNBT(nbt.getCompound(NBT_SLOT));
                    }
                }
            });
        }
    }
    //endregion
}
