package com.camellias.mysticalmetallurgy.library.tileslottedinventory;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

abstract class IInventoryAccess<T extends InventorySlot> extends TileEntity
{
    abstract ItemStack extractInternal(@Nonnull T slot, int amount, boolean simulate);

    abstract ItemStack insertInternal(@Nonnull T slot, ItemStack stack, boolean simulate);

    abstract ItemStack getStackInSlotInternal(@Nonnull T slot);

    abstract void setStackInSlotInternal(@Nonnull T slot, ItemStack stack);
}
