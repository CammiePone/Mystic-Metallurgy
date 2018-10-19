package com.camellias.mysticalmetallurgy.common.block.anvil;

import com.camellias.mysticalmetallurgy.api.utils.ItemUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.geom.Line2D;

public class TileStoneAnvil extends TileEntity
{
    private static final String NBT_INVENTORY = "inventory";
    public enum Slot
    {
        PRINT(0, new Vec3d(0.530F, 0, 0.375F),
                new Vec3d(0.780F, 0, 0.625F),
                new Vec3d(0.755F, 0F, 0.25F)),
        INPUT(1, new Vec3d(0.250F, 0, 0.500F),
                new Vec3d(0.500F, 0, 0.750F),
                new Vec3d(-0.5F, 0.5F, 0.25F)),
        EXTRA(2, new Vec3d(0.250F, 0, 0.250F),
                new Vec3d(0.500F, 0, 0.500F),
                new Vec3d(-0.5F, -0.5F, 0.25F));

        private int slot;
        private Vec3d bl, tr, ro;
        private static final Vec3d diff = new Vec3d(0.5, 0, 0.5);

        Slot(int slot, Vec3d bl, Vec3d tr, Vec3d renderOffset)
        {
            this.slot = slot;
            this.bl = bl;
            this.tr = tr;
            this.ro = renderOffset;
        }

        public boolean isHit(EnumFacing facing, double x, double z)
        {
            float angle = getAngle(facing);
            Vec3d vec1 = bl.subtract(diff).rotateYaw(angle).add(diff);
            Vec3d vec2 = tr.subtract(diff).rotateYaw(angle).add(diff);

            return (new Line2D.Double(
                    Math.min(vec1.x, vec2.x),
                    Math.min(vec1.z, vec2.z),
                    Math.max(vec1.x, vec2.x),
                    Math.max(vec1.z, vec2.z))
                    .getBounds2D()).contains(x, z);
        }

        public Vec3d getRenderOffset()
        {
            return ro;
        }

        private float getAngle(EnumFacing facing)
        {
            int angle = 0;
            switch (facing)
            {
                case NORTH:
                    break;
                case EAST:
                    angle = 270;
                    break;
                case SOUTH:
                    angle = 180;
                    break;
                case WEST:
                    angle = 90;
                    break;
            }
            return (float)Math.toRadians(angle);
        }

        @Nullable
        public static Slot getSlotHit(EnumFacing facing, float x, float z) { return getSlotHit(facing, (double)x, z); }
        @Nullable
        public static Slot getSlotHit(EnumFacing facing, double x, double z)
        {
            for (Slot slot : values())
                if (slot.isHit(facing, x, z))
                    return slot;
            return null;
        }
    }

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

    public IBlockState getBlockState()
    {
        return world.getBlockState(pos);
    }

    //region <inventory proxy>
    public ItemStack extract(@Nonnull Slot slot, boolean simulate)
    {
        return inventory.extractItem(slot.slot, 1, simulate);
    }

    public ItemStack insert(@Nonnull Slot slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (slot == Slot.PRINT)
        {
            if (ItemUtils.stackHasOreName(stack, "paper"))
                return inventory.insertItem(slot.slot, stack, simulate);
            return stack;
        }
        return inventory.insertItem(slot.slot, stack, simulate);
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
