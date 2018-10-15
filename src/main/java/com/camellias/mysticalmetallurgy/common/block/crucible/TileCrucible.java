package com.camellias.mysticalmetallurgy.common.block.crucible;

import com.camellias.mysticalmetallurgy.api.ConfigValues;
import com.camellias.mysticalmetallurgy.common.effect.EffectHandler;
import com.camellias.mysticalmetallurgy.init.ModFluids;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileCrucible extends TileEntity implements ITickable
{
    private static final String NBT_INPUT = "input";
    private static final String NBT_OUTPUT = "output";
    private static final String NBT_PROGRESS = "progress";
    private static final String NBT_LIT = "onfire";

    private static final int MaxFuelLevel = 4;
    public static int INPUT_SLOTS = 2;
    public static int FUEL_SLOT = INPUT_SLOTS;

    private boolean lit = false;
    private int progress = 100;

    //region <inventory>
    ItemStackHandler input = new ItemStackHandler(3)
    {
        @Override
        public int getSlotLimit(int slot)
        {
            if(slot == FUEL_SLOT)
                return MaxFuelLevel;
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if (isValidFuel(stack))
                slot = FUEL_SLOT;
            if (!output.canFill() || !isValidFuel(stack) && slot == FUEL_SLOT)
                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if (progress < 100)
                return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            if (slot == FUEL_SLOT)
            {
                setLit(lit && !getStackInSlot(slot).isEmpty());
                world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockCrucible.COAL_LEVEL, getStackInSlot(slot).getCount()));
            }
            markDirty();
        }
    };

    private static final int MaxFluidAmount = 144;
    FluidTank output = new FluidTank(MaxFluidAmount)
    {
        @Override
        public boolean canFill()
        {
            return getFluidAmount() < MaxFluidAmount;
        }

        @Override
        public boolean canDrain()
        {
            if (progress < 100)
                return false;
            return super.canDrain();
        }
    };
    //endregion

    @Override
    public void update()
    {
        if (world.isRemote) return;
        if (world.getTotalWorldTime() % 10 != 0) return;
        if (progress >= 100)
        {
            if (progress == 100)
            {
                input.extractItem(FUEL_SLOT, 1, false);
                progress++;
            }
            if (canStart())
                progress = 0;
        }
        else if (++progress == 99)
        {
            List<ItemStack> stacks = new ArrayList<>();
            int tier = 0;
            for (int slot = 0; slot < INPUT_SLOTS; slot ++)
            {
                ItemStack stack = input.getStackInSlot(slot);
                tier = Math.max(tier, EffectHandler.getStackTier(stack));
                stacks.add(stack);
            }

            NBTTagCompound fluidTag = EffectHandler.combineStackEffectsToNBT(stacks.toArray(new ItemStack[0]));
            EffectHandler.writeTierToNBT(fluidTag, tier + 1);

            output.fillInternal(new FluidStack(ModFluids.MYSTICAL_METAL, 144, fluidTag), true);

            for (int slot = 0; slot < INPUT_SLOTS; slot ++)
                input.setStackInSlot(slot, ItemStack.EMPTY);
        }

        if (progress <= 100) markDirty();
    }

    //region <helper>
    private void setLit(boolean state)
    {
        lit = state;
        world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockCrucible.LIT, state));
    }

    private boolean canStart()
    {
        return output.canFill() && hasValidContent() && progress >= 100 && !input.getStackInSlot(FUEL_SLOT).isEmpty() && lit;
    }
    //endregion

    public boolean hasValidContent()
    {
        for (int slot = 0; slot < INPUT_SLOTS; slot++)
        {
            ItemStack stack = input.getStackInSlot(slot);
            if (!EffectHandler.hasStackEffects())
                return false;
            if (EffectHandler.getStackTier(stack) >= ConfigValues.MaxCombineTier)
                return false;
        }

        return true;
    }

    public boolean canLight() { return !input.getStackInSlot(FUEL_SLOT).isEmpty(); }
    public void setLit() { setLit(true); }

    public static boolean isValidFuel(ItemStack stack)
    {
        return stack.getItem() == Items.COAL;
    }

    //region <caps>
    @Override
    public boolean hasCapability(@Nullable Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nullable Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (facing == null || facing != EnumFacing.DOWN)
                return (T) input;
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if (facing == EnumFacing.DOWN)
                return (T) output;
        }
        return super.getCapability(capability, facing);
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
    public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newSate)
    {
        return false;
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
        compound.setTag(NBT_INPUT, input.serializeNBT());
        compound.setTag(NBT_OUTPUT, output.writeToNBT(new NBTTagCompound()));
        compound.setInteger(NBT_PROGRESS, progress);
        compound.setBoolean(NBT_LIT, lit);
        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound cmp) {
        super.readFromNBT(cmp);
        if (cmp.hasKey(NBT_INPUT)) input.deserializeNBT(cmp.getCompoundTag(NBT_INPUT));
        if (cmp.hasKey(NBT_OUTPUT)) output.readFromNBT(cmp.getCompoundTag(NBT_OUTPUT));
        if (cmp.hasKey(NBT_PROGRESS)) progress = cmp.getInteger(NBT_PROGRESS);
        if (cmp.hasKey(NBT_LIT)) lit = cmp.getBoolean(NBT_LIT);
    }
    //endregion
}
