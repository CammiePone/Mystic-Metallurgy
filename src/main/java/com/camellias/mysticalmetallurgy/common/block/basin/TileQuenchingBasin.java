package com.camellias.mysticalmetallurgy.common.block.basin;

import com.camellias.mysticalmetallurgy.api.IMysticalItem;
import com.camellias.mysticalmetallurgy.library.utils.ItemUtils;
import com.camellias.mysticalmetallurgy.library.tileslottedinventory.InventorySlot;
import com.camellias.mysticalmetallurgy.library.tileslottedinventory.TileEntitySlottedInventory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.activity.InvalidActivityException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.geom.Point2D;

import static com.camellias.mysticalmetallurgy.common.block.basin.BlockQuenchingBasin.COOLING;

public class TileQuenchingBasin extends TileEntitySlottedInventory<InventorySlot> implements ITickable
{
    private static final String NBT_ISCOOLING = "iscooling";
    private boolean isCooling = false;

    public TileQuenchingBasin()
    {
        super(1);
    }

    @Override
    protected void initSlots() throws InvalidActivityException
    {
        addSlot(new InventorySlot(new Point2D.Float(0.530F, 0.375F),
                new Point2D.Float(0.780F, 0.635F),
                new Vec3d(0.755F, 0F, 0.25F)));

        addSlot(new InventorySlot(new Point2D.Float(0.250F, 0.500F),
                new Point2D.Float(0.500F, 0.750F),
                new Vec3d(0.5F, 0.5F, 0.25F)));
    }

    FluidTank tank = new FluidTank(1000);

    @Override
    public void tick()
    {
        if (world.isRemote) return;
        if (world.getGameTime() % 20 != 0) return;
        if (ItemUtils.getFirstOccupiedSlot(inventory) < 0) return;

        boolean isCooling = false;
        if (tank.getFluid() != null)
        {
            int temp = tank.getFluid().getFluid().getTemperature();
            if (temp <= 300)
            {
                for (InventorySlot slot : getSlots())
                {
                    ItemStack stack = slot.getStack();
                    if (!stack.isEmpty() && stack.getItem() instanceof IMysticalItem)
                    {
                        int coolTime = ((IMysticalItem) stack.getItem()).getRemainingCoolingTime(stack);
                        if (coolTime > 0)
                        {
                            isCooling = true;
                            coolTime -= Math.floor((300 - temp) / 50d) + 1;
                            ((IMysticalItem) stack.getItem()).setRemainingCoolingTime(stack, --coolTime);
                            tank.drain(2, true);
                        }
                    }
                }
            }
        }

        if (this.isCooling != isCooling)
        {
            world.setBlockState(pos, getBlockState().with(COOLING, isCooling));
            this.isCooling = isCooling;
            markDirty();
        }
    }

    //region <inventory>
    public ItemStack extract(@Nonnull InventorySlot slot, boolean simulate)
    {
        return slot.extract(1, simulate);
    }

    public ItemStack insert(@Nonnull InventorySlot slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (!(stack.getItem() instanceof ItemBlock))
            return slot.insert(stack, simulate);
        return stack;
    }
    //endregion

    //region <caps>
    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
            return LazyOptional.of(() -> (T) tank);
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (facing != EnumFacing.DOWN)
                return LazyOptional.of(() -> (T) inventory);
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if (facing == EnumFacing.DOWN)
                return LazyOptional.of(() -> (T) tank);
        }

        return super.getCapability(capability, facing);
    }
    //endregion

    //region <syncing>
    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.putBoolean(NBT_ISCOOLING, isCooling);
        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound cmp) {
        super.readFromNBT(cmp);
        if (cmp.hasUniqueId(NBT_ISCOOLING)) isCooling = cmp.getBoolean(NBT_ISCOOLING);
    }
    //endregion
}
