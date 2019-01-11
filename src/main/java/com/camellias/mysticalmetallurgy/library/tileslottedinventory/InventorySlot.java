package com.camellias.mysticalmetallurgy.library.tileslottedinventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class InventorySlot implements INBTSerializable<NBTTagCompound>
{
    int slot;
    IInventoryAccess inventory;

    private Vec3d bl, tr, ro;
    private static final Vec3d diff = new Vec3d(0.5, 0, 0.5);


    public InventorySlot(Point2D bl, Point2D tr, Vec3d renderOffset)
    {
        this.bl = new Vec3d(bl.getX(), 0, bl.getY());
        this.tr = new Vec3d(tr.getX(), 0, tr.getY());
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

    public boolean acceptStack(ItemStack stack)
    {
        return true;
    }

    public void onChanged() {}

    public ItemStack extract(int amount, boolean simulate)
    {
        return inventory.extractInternal(this, amount, simulate);
    }

    public ItemStack insert(ItemStack stack, boolean simulate)
    {
        return inventory.insertInternal(this, stack, simulate);
    }

    public void empty() { setStack(ItemStack.EMPTY); }

    public ItemStack getStack() { return inventory.getStackInSlotInternal(this); }

    public void setStack(ItemStack stack) { inventory.setStackInSlotInternal(this, stack); }

    public boolean hasNBT() {
        return false;
    }

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT()
    {
        return new NBTTagCompound();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {

    }
}
