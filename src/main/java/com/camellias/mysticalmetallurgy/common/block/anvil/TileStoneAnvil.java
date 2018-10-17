package com.camellias.mysticalmetallurgy.common.block.anvil;

import com.camellias.mysticalmetallurgy.api.utils.ItemUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class TileStoneAnvil extends TileEntity
{
    private static final String NBT_INVENTORY = "inventory";
    static final int SLOT_PRINT = 0;
    static final int SLOT_INPUT = 1;
    static final int SLOT_EXTRA = 2;

    private ItemStackHandler inventory = new ItemStackHandler(3)
    {
        @Override
        public int getSlotLimit(int slot)
        {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            super.onContentsChanged(slot);
            markDirty();
        }
    };

    //region <inventory proxy>
    public ItemStack extractPrint(boolean simulate)
    {
        return inventory.extractItem(SLOT_PRINT, 1, simulate);
    }

    public ItemStack insertPrint(ItemStack stack, boolean simulate)
    {
        //noinspection SuspiciousMethodCalls
        if (ItemUtils.stackHasOreName(stack, "paper"))
            return inventory.insertItem(SLOT_PRINT, stack, simulate);
        return stack;
    }

    public ItemStack extractExtra(boolean simulate)
    {
        return inventory.extractItem(SLOT_EXTRA, 1, simulate);
    }

    public ItemStack insertExtra(ItemStack stack, boolean simulate)
    {
        return inventory.insertItem(SLOT_EXTRA, stack, simulate);
    }

    public ItemStack extractInput(boolean simulate)
    {
        return inventory.extractItem(SLOT_INPUT, 1, simulate);
    }

    public ItemStack insertInput(ItemStack stack, boolean simulate)
    {
        return inventory.insertItem(SLOT_INPUT, stack, simulate);
    }
    //endregion

    //region <syncing>
    @Override
    public void markDirty() {
        IBlockState state = world.getBlockState(pos);
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        world.scheduleBlockUpdate(pos, blockType, 0, 0);
        super.markDirty();
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        markDirty();
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), -999, writeToNBT(new NBTTagCompound()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        readFromNBT(packet.getNbtCompound());
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setTag(NBT_INVENTORY, inventory.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound cmp) {
        super.readFromNBT(cmp);
        if (cmp.hasKey(NBT_INVENTORY)) inventory.deserializeNBT(cmp.getCompoundTag(NBT_INVENTORY));
    }
    //endregion
}
