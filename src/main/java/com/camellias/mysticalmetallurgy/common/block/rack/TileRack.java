package com.camellias.mysticalmetallurgy.common.block.rack;

import com.camellias.mysticalmetallurgy.init.ModTiles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class TileRack extends TileEntity
{
    private static final String NBT_INVENTORY = "inventory";

    ItemStackHandler inventory = new ItemStackHandler(1)
    {
        @Override
        public int getSlotLimit(int slot)
        {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack)
        {
            return !(stack.getItem() instanceof ItemBlock);
        }
    };

    public TileRack() {
        super(ModTiles.RACK);
    }

    //region <caps>
    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return LazyOptional.of(() -> (T) inventory);
        return super.getCapability(capability, facing);
    }
    //endregion

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
        return compound;
    }

    @Override
    public void read(@Nonnull NBTTagCompound cmp)
    {
        super.read(cmp);
        if (cmp.hasUniqueId(NBT_INVENTORY)) inventory.deserializeNBT(cmp.getCompound(NBT_INVENTORY));
    }
    //endregion
}
