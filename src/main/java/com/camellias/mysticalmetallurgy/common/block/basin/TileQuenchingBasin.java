package com.camellias.mysticalmetallurgy.common.block.basin;

import com.camellias.mysticalmetallurgy.api.IMysticalItem;
import com.camellias.mysticalmetallurgy.init.ModTiles;
import com.camellias.mysticalmetallurgy.library.utils.ItemUtils;
import com.camellias.mysticalmetallurgy.library.tileslottedinventory.InventorySlot;
import com.camellias.mysticalmetallurgy.library.tileslottedinventory.TileEntitySlottedInventory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.activity.InvalidActivityException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.geom.Point2D;

import static com.camellias.mysticalmetallurgy.common.block.basin.BlockQuenchingBasin.COOLING;

public class TileQuenchingBasin extends TileEntity implements ITickable
{
    private static final String NBT_ISCOOLING = "iscooling";
    private static final String NBT_INV = "inventory";
    private static final String NBT_TANK = "tank";
    private boolean isCooling = false;

    public TileQuenchingBasin(TileEntityType<?> type)
    {
        super(type);
    }

    public TileQuenchingBasin()
    {
        super(ModTiles.BASIN);
    }

    FluidTank tank = new FluidTank(1000);
    private ItemStackHandler inventory = new ItemStackHandler()
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
                for (int slot = 0; slot < inventory.getSlots(); slot++)
                {
                    ItemStack stack = inventory.getStackInSlot(slot);
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
    public ItemStack extract(boolean simulate)
    {
        return inventory.extractItem(0,1, simulate);
    }

    public ItemStack insert(@Nonnull ItemStack stack, boolean simulate)
    {
        return inventory.insertItem(0, stack, simulate);
    }
    //endregion

    //region <caps>
    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
            return LazyOptional.of(() -> tank).cast();
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (facing != EnumFacing.DOWN)
                return LazyOptional.of(() -> inventory).cast();
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if (facing == EnumFacing.DOWN)
                return LazyOptional.of(() -> tank).cast();
        }

        return super.getCapability(capability, facing);
    }
    //endregion

    //region <syncing>
    @Nonnull
    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        compound = super.write(compound);
        compound.putBoolean(NBT_ISCOOLING, isCooling);
        compound.put(NBT_INV, inventory.serializeNBT());
        compound.put(NBT_TANK, tank.writeToNBT(new NBTTagCompound()));
        return compound;
    }

    @Override
    public void read(@Nonnull NBTTagCompound cmp) {
        super.read(cmp);
        if (cmp.hasUniqueId(NBT_ISCOOLING)) isCooling = cmp.getBoolean(NBT_ISCOOLING);
        if (cmp.hasUniqueId(NBT_INV)) inventory.deserializeNBT(cmp.getCompound(NBT_ISCOOLING));
        if (cmp.hasUniqueId(NBT_TANK)) tank.readFromNBT(cmp.getCompound(NBT_ISCOOLING));
    }
    //endregion
}
