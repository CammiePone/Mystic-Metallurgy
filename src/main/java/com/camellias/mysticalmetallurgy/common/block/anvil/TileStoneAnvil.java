package com.camellias.mysticalmetallurgy.common.block.anvil;

import com.camellias.mysticalmetallurgy.api.effect.Trait;
import com.camellias.mysticalmetallurgy.api.recipe.AnvilRecipe;
import com.camellias.mysticalmetallurgy.common.fluid.FluidMysticMetal;
import com.camellias.mysticalmetallurgy.common.item.tool.ItemLadle;
import com.camellias.mysticalmetallurgy.init.ModItems;
import com.camellias.mysticalmetallurgy.library.utils.ItemUtils;
import com.camellias.mysticalmetallurgy.library.tileslottedinventory.InventorySlot;
import com.camellias.mysticalmetallurgy.library.tileslottedinventory.TileEntitySlottedInventory;
import com.camellias.mysticalmetallurgy.network.NetworkHandler;
import com.camellias.mysticalmetallurgy.network.packet.PlaySoundPacket;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.activity.InvalidActivityException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.geom.Point2D;

public class TileStoneAnvil extends TileEntitySlottedInventory<TileStoneAnvil.InventorySlotTyped>
{
    private static final String NBT_SWINGS = "swings";

    public TileStoneAnvil()
    {
        super(1);
    }

    InventorySlotTyped slotOut;
    private InventorySlotTyped slotExtra;
    private InventorySlotTyped slotIn;
    InventorySlotTyped slotPrint;

    @Override
    protected void initSlots() throws InvalidActivityException
    {
        slotPrint = new InventorySlotTyped(new Point2D.Float(0.530F, 0.375F),
                new Point2D.Float(0.780F, 0.625F),
                new Vec3d(0.755F, 0F, 0.25F),
                InventorySlotTyped.SlotType.INPUT, "paper");
        addSlot(slotPrint);

        slotIn = new InventorySlotTyped(new Point2D.Float(0.250F, 0.500F),
                new Point2D.Float(0.500F, 0.750F),
                new Vec3d(-0.5F, 0.8F, 0.25F),
                InventorySlotTyped.SlotType.INPUT, "");
        addSlot(slotIn);

        slotExtra = new InventorySlotTyped(new Point2D.Float(0.250F, 0.250F),
                new Point2D.Float(0.500F, 0.500F),
                new Vec3d(-0.5F, -0.2F, 0.25F),
                InventorySlotTyped.SlotType.INPUT, "");
        addSlot(slotExtra);

        slotOut = new InventorySlotTyped(new Point2D.Float(0.375F, 0.250F),
                new Point2D.Float(0.635F, 0.500F),
                new Vec3d(-0.5F, 0, 0.25F),
                InventorySlotTyped.SlotType.OUTPUT, "");
        addSlot(slotOut);
    }

    public static class InventorySlotTyped extends InventorySlot {

        public enum SlotType
        {
            INPUT,
            OUTPUT
        }

        private SlotType type;
        private String oreDict;

        public InventorySlotTyped(Point2D bl, Point2D tr, Vec3d renderOffset, SlotType type, String oreDict)
        {
            super(bl, tr, renderOffset);
            this.type = type;
            this.oreDict = oreDict;
        }

        @Override
        public boolean acceptStack(ItemStack stack)
        {
            if (type == SlotType.OUTPUT)
                return false;

            if (!oreDict.isEmpty())
                return ItemUtils.stackHasOreName(stack, oreDict);
            return true;
        }

        @Override
        public ItemStack insert(ItemStack stack, boolean simulate)
        {
            if (type == SlotType.INPUT && !tryInsertFluid(stack, simulate))
                return super.insert(stack, simulate);

            return stack;
        }

        private boolean tryInsertFluid(ItemStack stack, boolean simulate)
        {
            if (getStack().isEmpty() && IsValidFluidInput(stack))
            {
                if (simulate)
                    return true;

                FluidStack fluid = FluidUtil.getFluidHandler(stack).drain(ItemLadle.CAPACITY, true);
                NBTTagCompound nbt = new NBTTagCompound();
                Trait.toNBT(nbt, Trait.fromNBT(fluid.tag));

                insert(new ItemStack(ModItems.METAL_CLUMP, 1, 0, nbt), false);
                return true;
            }

            return false;
        }

        private boolean IsValidFluidInput(ItemStack stack)
        {
            IFluidHandler handler = FluidUtil.getFluidHandler(stack);
            if (handler == null)
                return false;

            if (type != SlotType.INPUT)
                return false;

            FluidStack fluid = handler.drain(ItemLadle.CAPACITY, false);

            if (fluid == null)
                return false;

            return fluid.getFluid() instanceof FluidMysticMetal && fluid.amount == ItemLadle.CAPACITY;
        }
    }

    private int swings = 0;
    private AnvilRecipe recipe;

    @Override
    protected void onSlotContentChanged(InventorySlotTyped slot)
    {
        if (slot == slotOut)
        {
            recipe = null;
            swings = 0;
        }
        markDirty();
    }

    IBlockState getBlockState()
    {
        return world.getBlockState(pos);
    }

    public boolean tryHammer()
    {
        AnvilRecipe recipe = AnvilRecipe.getMatching(slotPrint.getStack(),
                slotIn.getStack(), slotExtra.getStack());

        if (recipe != null)
        {
            if (++swings >= recipe.getSwings())
            {
                ItemStack result = recipe.craft(slotIn.getStack(), slotExtra.getStack());
                slotIn.empty();
                slotExtra.empty();
                slotOut.setStack(result);

                NetworkHandler.sendAround(new PlaySoundPacket(pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.AMBIENT, 1F, 1.0F), pos, world.provider.getDimension());
            }

            markDirty();
            return true;
        }
        return false;
    }

    @Nullable
    public AnvilRecipe getActiveRecipe()
    {
        if (swings > 0)
        {
            if (recipe == null)
            {
                recipe = AnvilRecipe.getMatching(slotPrint.getStack(),
                        slotIn.getStack(), slotExtra.getStack());
            }
            return recipe;
        }
        return null;
    }

    public int doneSwings() { return swings; }

    ItemStack getPrintForRendering()
    {
        return slotPrint.getStack();
    }


    @Nullable
    public InventorySlotTyped getSlotHit(EnumFacing facing, double x, double z, @Nullable InventorySlotTyped.SlotType type)
    {
        for (InventorySlotTyped slot : getSlots())
            if ((type == null || type == slot.type) && slot.isHit(facing, x, z))
                return slot;
        return null;
    }

    //region <inventory>
    public boolean hasOutput()
    {
        return !slotOut.getStack().isEmpty();
    }

    public boolean canExtract(InventorySlotTyped slot)
    {
        return swings > 0 && slot != slotOut;
    }
    //endregion

    //region <syncing>
    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setInteger(NBT_SWINGS, swings);
        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound cmp) {
        super.readFromNBT(cmp);
        if (cmp.hasKey(NBT_SWINGS)) swings = cmp.getInteger(NBT_SWINGS);
    }
    //endregion
}
