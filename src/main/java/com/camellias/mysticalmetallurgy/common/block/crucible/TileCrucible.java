package com.camellias.mysticalmetallurgy.common.block.crucible;

import com.camellias.mysticalmetallurgy.api.ConfigValues;
import com.camellias.mysticalmetallurgy.api.effect.EffectLinker;
import com.camellias.mysticalmetallurgy.api.effect.Trait;
import com.camellias.mysticalmetallurgy.init.ModFluids;
import com.camellias.mysticalmetallurgy.init.ModTiles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
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
    InternalStackHandler input = new InternalStackHandler(3);

    public TileCrucible(TileEntityType<?> type) {
        super(type);
    }

    public TileCrucible() {
        super(ModTiles.CRUCIBLE);
    }

    class InternalStackHandler extends ItemStackHandler
    {
        InternalStackHandler(int size)
        {
            super(size);
        }

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
            else if (!output.canFill() || slot == FUEL_SLOT)
                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if (progress < 100 || slot == FUEL_SLOT)
                return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }

        @Nonnull
        @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
        ItemStack extractItemInternal(int slot, int amount, boolean simulate)
        {
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            if (slot == FUEL_SLOT)
            {
                setLit(lit && !getStackInSlot(slot).isEmpty());
                world.setBlockState(pos, world.getBlockState(pos).with(BlockCrucible.COAL_LEVEL, getStackInSlot(slot).getCount()));
            }
            markDirty();
        }
    }

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
    public void tick()
    {
        if (world.isRemote) return;
        if (world.getGameTime() % 10 != 0) return;
        if (progress >= 100)
        {
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
                tier = Math.max(tier, EffectLinker.getStackTier(stack));
                stacks.add(stack);
            }

            List<Trait> effects = EffectLinker.combineStackTraits(stacks.toArray(new ItemStack[0]));
            NBTTagCompound fluidTag = new NBTTagCompound();
            Trait.toNBT(fluidTag, effects);
            EffectLinker.writeTierToNBT(fluidTag, tier + 1);

            output.fillInternal(new FluidStack(ModFluids.MYSTICAL_METAL, 144, fluidTag), true);

            for (int slot = 0; slot < input.getSlots(); slot ++)
                input.extractItemInternal(slot, 1, false);
        }

        if (progress <= 100) markDirty();
    }

    //region <helper>
    private void setLit(boolean state)
    {
        lit = state;
        world.setBlockState(pos, world.getBlockState(pos).with(BlockCrucible.LIT, state));
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
            if (!EffectLinker.hasStackRegisteredTraits(stack))
                return false;
            if (EffectLinker.getStackTier(stack) >= ConfigValues.MaxCombineTier)
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

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (facing != EnumFacing.DOWN)
                return LazyOptional.of(() -> (T) input);
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if (facing == EnumFacing.DOWN)
                return LazyOptional.of(() -> (T) output);
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
        world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 0);
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
        return write(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), -999, write(new NBTTagCompound()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        read(packet.getNbtCompound());
    }

    @Nonnull
    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        compound = super.write(compound);
        compound.put(NBT_INPUT, input.serializeNBT());
        compound.put(NBT_OUTPUT, output.writeToNBT(new NBTTagCompound()));
        compound.putInt(NBT_PROGRESS, progress);
        compound.putBoolean(NBT_LIT, lit);
        return compound;
    }

    @Override
    public void read(@Nonnull NBTTagCompound cmp) {
        super.read(cmp);
        if (cmp.hasUniqueId(NBT_INPUT)) input.deserializeNBT(cmp.getCompound(NBT_INPUT));
        if (cmp.hasUniqueId(NBT_OUTPUT)) output.readFromNBT(cmp.getCompound(NBT_OUTPUT));
        if (cmp.hasUniqueId(NBT_PROGRESS)) progress = cmp.getInt(NBT_PROGRESS);
        if (cmp.hasUniqueId(NBT_LIT)) lit = cmp.getBoolean(NBT_LIT);
    }
    //endregion
}
