package com.camellias.mysticalmetallurgy.library.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemUtils
{
    public static IItemHandler getItemHandler(@Nonnull EntityPlayer player)
    {
        return player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    public static ItemStack giveStack(@Nonnull EntityPlayer player, ItemStack stack)
    {
        return giveStack((IItemHandlerModifiable)getItemHandler(player), stack);
    }

    public static ItemStack giveStack(IItemHandlerModifiable inventory, ItemStack stack) {
        int slot = getFittingSlot(inventory, stack);
        if (slot < 0) return stack;
        ItemStack remain = inventory.insertItem(slot, stack, false);
        if (!remain.isEmpty()) giveStack(inventory, remain);
        return ItemStack.EMPTY;
    }

    public static int getFittingSlot(IItemHandler inventory, ItemStack stack) {
        int slot = findAvailableSlotForItem(inventory, stack);
        return slot < 0 ? getFirstUnOccupiedSlot(inventory) : slot;
    }

    public static int findAvailableSlotForItem(IItemHandler inventory, ItemStack stack) {
        for (int i = 0; i < inventory.getSlots(); ++i)
            if (inventory.getStackInSlot(i).getCount() < inventory.getStackInSlot(i).getMaxStackSize() && ItemStack.areItemsEqual(inventory.getStackInSlot(i), stack))
                return i;
        return -1;
    }

    public static int getFirstUnOccupiedSlot(IItemHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); ++i) if (inventory.getStackInSlot(i).isEmpty()) return i;
        return -1;
    }

    public static int getFirstOccupiedSlot(IItemHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); ++i) if (!inventory.getStackInSlot(i).isEmpty()) return i;
        return -1;
    }

    public static boolean stackHasOreName(ItemStack stack, String oreName)
    {
        if (stack.isEmpty())
            return false;

        int id = OreDictionary.getOreID(oreName);
        for (int i : OreDictionary.getOreIDs(stack))
            if (i == id) return true;
        return false;
    }

    public static ItemStack copyStackWithSize(ItemStack stack, int size)
    {
        ItemStack copy = stack.copy();
        copy.setCount(size);
        return copy;
    }

    public static boolean isNBTEqual(ItemStack thisStack, ItemStack other, boolean strict)
    {
        boolean thisHasTag = thisStack.hasTagCompound() && !isTagEmpty(thisStack.getTagCompound());
        boolean sampleHasTag = other.hasTagCompound() && !isTagEmpty(other.getTagCompound());

        if (strict) {
            if (!thisHasTag && sampleHasTag) {
                return false;
            } else if (thisHasTag && (!sampleHasTag || !thisStack.getTagCompound().equals(other.getTagCompound()))) {
                return false;
            }
        } else {
            if (thisHasTag) {
                if (!sampleHasTag) {
                    return false;
                }

                if (!containsTag(thisStack.getTagCompound(), other.getTagCompound())) {
                    return false;
                }
            }
        }

        return true;
    }

    public static FluidStack drainFluidFromItem(ItemStack stack, int amount, boolean doDrain)
    {
        IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
        if (fluidHandler != null)
            return fluidHandler.drain(amount, doDrain);

        return null;
    }

    public static boolean canDrainFluidAmountFromItem(ItemStack stack, int amount)
    {
        FluidStack fs = drainFluidFromItem(stack, amount, false);
        if (fs != null)
            return fs.amount == amount;

        return false;
    }

    private static boolean isTagEmpty(NBTTagCompound compound) {
        for (String key : compound.getKeySet()) {
            NBTBase value = compound.getTag(key);
            if (value instanceof NBTTagCompound) {
                if (!isTagEmpty((NBTTagCompound) value)) {
                    return false;
                }
            } else if (value instanceof NBTTagList) {
                if (!isTagListEmpty((NBTTagList) value)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private static boolean isTagListEmpty(NBTTagList list) {
        if (!list.isEmpty()) {
            if (list.getTagType() != Constants.NBT.TAG_LIST &&
                    list.getTagType() != Constants.NBT.TAG_COMPOUND) {
                return false;
            }
            for (NBTBase element : list) {
                if (element instanceof NBTTagCompound) {
                    if (!isTagEmpty((NBTTagCompound) element)) {
                        return false;
                    }
                } else if (element instanceof NBTTagList) {
                    if (!isTagListEmpty((NBTTagList) element)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean containsTag(@Nonnull NBTTagCompound thisCompound, @Nonnull NBTTagCompound otherCompound) {
        for (String key : thisCompound.getKeySet()) {
            if (!otherCompound.hasKey(key, thisCompound.getTagId(key))) {
                return false;
            }

            NBTBase thisNBT = thisCompound.getTag(key);
            NBTBase otherNBT = otherCompound.getTag(key);
            if (!compareTag(thisNBT, otherNBT)) {
                return false;
            }
        }
        return true;
    }

    private static boolean containTagList(NBTTagList base, NBTTagList other) {
        if (base.tagCount() > other.tagCount()) {
            return false;
        }

        List<Integer> matched = new ArrayList<>();
        lblMatching:
        for (int index = 0; index < base.tagCount(); index++) {
            NBTBase thisNbt = base.get(index);

            for (int matchIndex = 0; matchIndex < other.tagCount(); matchIndex++) {
                NBTBase matchNBT = other.get(matchIndex);

                if (!matched.contains(matchIndex)){
                    if (compareTag(thisNbt, matchNBT)) {
                        matched.add(matchIndex);
                        continue lblMatching;
                    }
                }
            }

            return false;
        }

        return true;
    }

    private static boolean compareTag(NBTBase thisEntry, NBTBase thatEntry) {
        if (thisEntry instanceof NBTTagCompound && thatEntry instanceof NBTTagCompound) {
            return containsTag((NBTTagCompound) thisEntry, (NBTTagCompound) thatEntry);
        } else if (thisEntry instanceof NBTTagList && thatEntry instanceof NBTTagList) {
            return containTagList((NBTTagList) thisEntry, (NBTTagList) thatEntry);
        } else {
            return thisEntry.equals(thatEntry);
        }
    }
}
