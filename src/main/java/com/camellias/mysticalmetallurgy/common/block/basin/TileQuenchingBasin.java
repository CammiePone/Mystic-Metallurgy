package com.camellias.mysticalmetallurgy.common.block.basin;

import com.camellias.mysticalmetallurgy.library.utils.ItemUtils;
import com.camellias.mysticalmetallurgy.common.item.tool.ItemMysticalTool;
import com.camellias.mysticalmetallurgy.library.tileslottedinventory.InventorySlot;
import com.camellias.mysticalmetallurgy.library.tileslottedinventory.TileEntitySlottedInventory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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

public class TileQuenchingBasin extends TileEntitySlottedInventory<InventorySlot> implements ITickable
{

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
    public void update()
    {
        if (world.isRemote) return;
        if (world.getTotalWorldTime() % 10 != 0) return;
        if (ItemUtils.getFirstOccupiedSlot(inventory) < 0) return;

        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemMysticalTool)
            {
                //TODO implement cooling
            }
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
}
