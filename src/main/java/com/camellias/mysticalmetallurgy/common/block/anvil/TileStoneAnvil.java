package com.camellias.mysticalmetallurgy.common.block.anvil;

import com.camellias.mysticalmetallurgy.api.recipe.AnvilRecipe;
import com.camellias.mysticalmetallurgy.api.utils.ItemUtils;
import com.camellias.mysticalmetallurgy.network.NetworkHandler;
import com.camellias.mysticalmetallurgy.network.packet.PlaySoundPacket;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.geom.Line2D;

public class TileStoneAnvil extends TileEntity
{
    private static final String NBT_INVENTORY = "inventory";
    private static final String NBT_SWINGS = "swings";
    public enum Slot
    {
        PRINT(0, SlotType.INPUT, "paper",
                new Vec3d(0.530F, 0, 0.375F),
                new Vec3d(0.780F, 0, 0.625F),
                new Vec3d(0.755F, 0F, 0.25F)),
        INPUT(1, SlotType.INPUT, "",
                new Vec3d(0.250F, 0, 0.500F),
                new Vec3d(0.500F, 0, 0.750F),
                new Vec3d(-0.5F, 0.5F, 0.25F)),
        EXTRA(2, SlotType.INPUT, "",
                new Vec3d(0.250F, 0, 0.250F),
                new Vec3d(0.500F, 0, 0.500F),
                new Vec3d(-0.5F, -0.5F, 0.25F)),
        OUT(3, SlotType.OUTPUT, "",
                new Vec3d(0.375F, 0, 0.250F),
                new Vec3d(0.635F, 0, 0.500F),
                new Vec3d(-0.5F, 0, 0.25F));


        public enum SlotType
        {
            INPUT,
            OUTPUT
        }

        private int slot;
        private SlotType type;
        private String oreDict;
        private Vec3d bl, tr, ro;
        private static final Vec3d diff = new Vec3d(0.5, 0, 0.5);

        Slot(int slot, SlotType type, String oreDict, Vec3d bl, Vec3d tr, Vec3d renderOffset)
        {
            this.slot = slot;
            this.bl = bl;
            this.tr = tr;
            this.ro = renderOffset;
            this.oreDict = oreDict;
            this.type = type;
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

        public boolean acceptStack(ItemStack stack)
        {
            if (!oreDict.isEmpty())
                return ItemUtils.stackHasOreName(stack, oreDict);
            return true;
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

        @Nullable
        public static Slot getSlotHit(EnumFacing facing, float x, float z, @Nullable SlotType type)
        {
            return getSlotHit(facing, (double) x, z, type);
        }

        @Nullable
        public static Slot getSlotHit(EnumFacing facing, double x, double z, @Nullable SlotType type)
        {
            for (Slot slot : values())
                if ((type == null || type == slot.type) && slot.isHit(facing, x, z))
                    return slot;
            return null;
        }
    }

    private int swings = 0;
    private AnvilRecipe recipe;
    ItemStackHandler inventory = new ItemStackHandler(Slot.values().length)
    {
        @Override
        public int getSlotLimit(int slot)
        {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            if (slot == Slot.OUT.slot)
            {
                recipe = null;
                swings = 0;
            }
            markDirty();
        }
    };

    IBlockState getBlockState()
    {
        return world.getBlockState(pos);
    }

    public boolean tryHammer()
    {
        AnvilRecipe recipe = AnvilRecipe.getMatching(inventory.getStackInSlot(Slot.PRINT.slot),
                inventory.getStackInSlot(Slot.INPUT.slot),
                inventory.getStackInSlot(Slot.EXTRA.slot));

        if (recipe != null)
        {
            if (++swings >= recipe.getSwings())
            {
                inventory.setStackInSlot(Slot.INPUT.slot, ItemStack.EMPTY);
                inventory.setStackInSlot(Slot.EXTRA.slot, ItemStack.EMPTY);
                inventory.setStackInSlot(Slot.OUT.slot, recipe.getResult());
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
                recipe = AnvilRecipe.getMatching(inventory.getStackInSlot(Slot.PRINT.slot),
                        inventory.getStackInSlot(Slot.INPUT.slot),
                        inventory.getStackInSlot(Slot.EXTRA.slot));
            }
            return recipe;
        }
        return null;
    }

    public int doneSwings() { return swings; }

    ItemStack getPrintForRendering()
    {
        return inventory.getStackInSlot(Slot.PRINT.slot);
    }

    //region <inventory>
    public boolean hasOutput()
    {
        return !inventory.getStackInSlot(Slot.OUT.slot).isEmpty();
    }

    public ItemStack extract(@Nonnull Slot slot, boolean simulate)
    {
        if (swings > 0 && slot != Slot.OUT )
            return ItemStack.EMPTY;
        return inventory.extractItem(slot.slot, 1, simulate);
    }

    public ItemStack insert(@Nonnull Slot slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (slot.acceptStack(stack) && !hasOutput())
            return inventory.insertItem(slot.slot, stack, simulate);
        return stack;
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
        compound.setTag(NBT_INVENTORY, inventory.serializeNBT());
        compound.setInteger(NBT_SWINGS, swings);
        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound cmp) {
        super.readFromNBT(cmp);
        if (cmp.hasKey(NBT_INVENTORY)) inventory.deserializeNBT(cmp.getCompoundTag(NBT_INVENTORY));
        if (cmp.hasKey(NBT_INVENTORY)) swings = cmp.getInteger(NBT_SWINGS);
    }
    //endregion
}
