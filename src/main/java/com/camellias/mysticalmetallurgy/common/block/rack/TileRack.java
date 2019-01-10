package com.camellias.mysticalmetallurgy.common.block.rack;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    IBlockState getBlockState()
    {
        return world.getBlockState(pos);
    }

    //region <caps>
    @Override
    public boolean hasCapability(@Nullable Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nullable Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) inventory;
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
        world.scheduleBlockUpdate(pos, blockType, 0, 0);
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
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(getPos(), -999, writeToNBT(new NBTTagCompound()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
    {
        super.onDataPacket(net, packet);
        readFromNBT(packet.getNbtCompound());
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        compound.setTag(NBT_INVENTORY, inventory.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound cmp)
    {
        super.readFromNBT(cmp);
        if (cmp.hasKey(NBT_INVENTORY)) inventory.deserializeNBT(cmp.getCompoundTag(NBT_INVENTORY));
    }
    //endregion
}
