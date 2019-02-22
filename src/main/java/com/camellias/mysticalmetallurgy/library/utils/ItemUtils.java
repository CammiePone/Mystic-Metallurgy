package com.camellias.mysticalmetallurgy.library.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class ItemUtils
{
    public static IItemHandler getItemHandler(@Nonnull EntityPlayer player)
    {
        return player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElseGet(null);
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
}
