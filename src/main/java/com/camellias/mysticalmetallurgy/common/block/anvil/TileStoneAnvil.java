package com.camellias.mysticalmetallurgy.common.block.anvil;

import com.camellias.mysticalmetallurgy.api.effect.Trait;
import com.camellias.mysticalmetallurgy.api.recipe.AnvilRecipe;
import com.camellias.mysticalmetallurgy.common.item.tool.ItemLadle;
import com.camellias.mysticalmetallurgy.init.ModItems;
import com.camellias.mysticalmetallurgy.init.ModTiles;
import com.camellias.mysticalmetallurgy.library.tileslottedinventory.InventorySlot;
import com.camellias.mysticalmetallurgy.library.tileslottedinventory.TileEntitySlottedInventory;
import com.camellias.mysticalmetallurgy.library.utils.ItemUtils;
import com.camellias.mysticalmetallurgy.network.NetworkHandler;
import com.camellias.mysticalmetallurgy.network.packet.PlaySoundPacket;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.activity.InvalidActivityException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.geom.Point2D;
import java.util.concurrent.atomic.AtomicBoolean;

public class TileStoneAnvil extends TileEntitySlottedInventory<TileStoneAnvil.InventorySlotTyped>
{
    private static final String NBT_SWINGS = "swings";

    public TileStoneAnvil(TileEntityType<?> type)
    {
        super(1, type);
    }

    public TileStoneAnvil()
    {
        super(1, ModTiles.STONE_ANVIL);
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
                InventorySlotTyped.SlotType.INPUT, null);
        addSlot(slotPrint);

        slotIn = new InventorySlotTyped(new Point2D.Float(0.250F, 0.500F),
                new Point2D.Float(0.500F, 0.750F),
                new Vec3d(-0.5F, 0.8F, 0.25F),
                InventorySlotTyped.SlotType.INPUT, null);
        addSlot(slotIn);

        slotExtra = new InventorySlotTyped(new Point2D.Float(0.250F, 0.250F),
                new Point2D.Float(0.500F, 0.500F),
                new Vec3d(-0.5F, -0.2F, 0.25F),
                InventorySlotTyped.SlotType.INPUT, null);
        addSlot(slotExtra);

        slotOut = new InventorySlotTyped(new Point2D.Float(0.375F, 0.250F),
                new Point2D.Float(0.635F, 0.500F),
                new Vec3d(-0.5F, 0, 0.25F),
                InventorySlotTyped.SlotType.OUTPUT, null);
        addSlot(slotOut);
    }

    public static class InventorySlotTyped extends InventorySlot {

        public enum SlotType
        {
            INPUT,
            OUTPUT
        }

        private SlotType type;
        private Tag<Item> tag;

        public InventorySlotTyped(Point2D bl, Point2D tr, Vec3d renderOffset, SlotType type, Tag<Item> tag)
        {
            super(bl, tr, renderOffset);
            this.type = type;
            this.tag = tag;
        }

        @Override
        public boolean acceptStack(ItemStack stack)
        {
            if (type == SlotType.OUTPUT)
                return false;

            if (tag != null)
                return stack.getItem().isIn(tag);
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
            AtomicBoolean inserted = new AtomicBoolean(false);
            if (getStack().isEmpty() && type == SlotType.INPUT)
            {
                FluidUtil.getFluidHandler(stack).ifPresent(fluidHandler -> {
                    FluidStack fluid = fluidHandler.drain(ItemLadle.CAPACITY, false);
                    if (fluid != null && fluid.amount == ItemLadle.CAPACITY)
                    {
                        inserted.set(true);
                        if (!simulate) {
                            NBTTagCompound nbt = new NBTTagCompound();
                            Trait.toNBT(nbt, Trait.fromNBT(fluid.tag));

                            insert(new ItemStack(ModItems.METAL_CLUMP, 0, nbt), false);
                        }
                    }
                });
            }

            return inserted.get();
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

                NetworkHandler.sendAround(new PlaySoundPacket(pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.AMBIENT, 1F, 1.0F), pos, world.dimension.getType());
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
    public NBTTagCompound write(NBTTagCompound compound) {
        compound = super.write(compound);
        compound.putInt(NBT_SWINGS, swings);
        return compound;
    }

    @Override
    public void read(@Nonnull NBTTagCompound cmp) {
        super.read(cmp);
        if (cmp.hasUniqueId(NBT_SWINGS)) swings = cmp.getInt(NBT_SWINGS);
    }
    //endregion
}
