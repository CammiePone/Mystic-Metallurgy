package com.camellias.mysticalmetallurgy.common.block.crucible;

import com.camellias.mysticalmetallurgy.api.ConfigValues;
import com.camellias.mysticalmetallurgy.api.effect.EffectLinker;
import com.camellias.mysticalmetallurgy.api.effect.Trait;
import com.camellias.mysticalmetallurgy.init.ModItems;
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
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
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
    int progress = 100;

    //region <inventory>
    InternalStackHandler input = new InternalStackHandler(INPUT_SLOTS + 1);
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
            else if (!isSlotEmpty(slot) || slot == FUEL_SLOT)
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
                world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockCrucible.COAL_LEVEL, getStackInSlot(slot).getCount()));
            }
            markDirty();
        }

        public boolean isSlotEmpty(int slot)
        {
            return getStackInSlot(slot).isEmpty();
        }
    }

    public ItemStackHandler output = new ItemStackHandler(1)
    {
        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            return stack;
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
            NBTTagCompound tag = new NBTTagCompound();
            Trait.toNBT(tag, effects);
            EffectLinker.writeTierToNBT(tag, tier + 1);

            output.setStackInSlot(0, new ItemStack(ModItems.METAL_CLUMP, 1, 0, tag));

            for (int slot = 0; slot < input.getSlots(); slot ++)
                input.extractItemInternal(slot, 1, false);
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
        return output.getStackInSlot(0).isEmpty() && hasValidContent() && progress >= 100 && !input.getStackInSlot(FUEL_SLOT).isEmpty() && lit;
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
        {
            if (facing != EnumFacing.DOWN)
                return (T) input;
            else
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
        compound.setTag(NBT_OUTPUT, output.serializeNBT());
        compound.setInteger(NBT_PROGRESS, progress);
        compound.setBoolean(NBT_LIT, lit);
        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound cmp) {
        super.readFromNBT(cmp);
        if (cmp.hasKey(NBT_INPUT)) input.deserializeNBT(cmp.getCompoundTag(NBT_INPUT));
        if (cmp.hasKey(NBT_OUTPUT)) output.deserializeNBT(cmp.getCompoundTag(NBT_OUTPUT));
        if (cmp.hasKey(NBT_PROGRESS)) progress = cmp.getInteger(NBT_PROGRESS);
        if (cmp.hasKey(NBT_LIT)) lit = cmp.getBoolean(NBT_LIT);
    }
    //endregion
}
