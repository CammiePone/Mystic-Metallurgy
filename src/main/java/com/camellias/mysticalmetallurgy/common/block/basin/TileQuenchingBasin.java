package com.camellias.mysticalmetallurgy.common.block.basin;

import com.camellias.mysticalmetallurgy.api.ConfigValues;
import com.camellias.mysticalmetallurgy.common.capability.HotItem.IHotStack;
import com.camellias.mysticalmetallurgy.library.tileslottedinventory.InventorySlot;
import com.camellias.mysticalmetallurgy.library.tileslottedinventory.TileEntitySlottedInventory;
import com.camellias.mysticalmetallurgy.library.utils.HotUtils;
import com.camellias.mysticalmetallurgy.library.utils.ItemUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
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
        addSlot(new InventorySlot(new Point2D.Float(0.0F, 0.0F),
                new Point2D.Float(0.50F, 0.55F),
                new Vec3d(0.5F, 0F, 0.5F)));

        addSlot(new InventorySlot(new Point2D.Float(0.50F, 0.500F),
                new Point2D.Float(1.0F, 1.0F),
                new Vec3d(0.5F, 0.5F, 0.5F)));
    }

    FluidTank tank = new FluidTank(1000);

    @Override
    public void update()
    {
        if (world.isRemote) return;
        if (world.getTotalWorldTime() % 20 != 0) return;
        if (ItemUtils.getFirstOccupiedSlot(inventory) < 0) return;

        boolean isCooling = false;
        if (tank.getFluid() != null)
        {
            int temp = tank.getFluid().getFluid().getTemperature();
            if (temp <= 300)
            {
                int coolTemp = (int) (Math.floor((300 - temp) / 50d) + 1);
                for (InventorySlot slot : getSlots())
                {
                    IHotStack hotStack = HotUtils.getHotStackCap(slot.getStack());
                    if (hotStack != null && hotStack.isHot() && hotStack.canCool())
                    {
                        hotStack.setTemp(hotStack.getTemp() - coolTemp);
                        isCooling = true;
                        tank.drain(ConfigValues.Heat.LiquidDrainedPerSec, true);
                    }
                }
            }
        }

        if (this.isCooling != isCooling)
        {
            world.setBlockState(pos, getBlockState().withProperty(COOLING, isCooling));
            this.isCooling = isCooling;
            markDirty();
        }
    }

    IBlockState getBlockState()
    {
        return world.getBlockState(pos);
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
    @Override
    public boolean hasCapability(@Nullable Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
                capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ||
                capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nullable Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
            return (T) tank;
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (facing != EnumFacing.DOWN)
                return (T) inventory;
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if (facing == EnumFacing.DOWN)
                return (T) tank;
        }
        return super.getCapability(capability, facing);
    }
    //endregion

    //region <syncing>
    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setBoolean(NBT_ISCOOLING, isCooling);
        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound cmp) {
        super.readFromNBT(cmp);
        if (cmp.hasKey(NBT_ISCOOLING)) isCooling = cmp.getBoolean(NBT_ISCOOLING);
    }
    //endregion
}
