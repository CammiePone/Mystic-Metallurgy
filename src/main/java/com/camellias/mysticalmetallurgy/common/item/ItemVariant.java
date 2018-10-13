package com.camellias.mysticalmetallurgy.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public abstract class ItemVariant extends Item
{
    public ItemVariant()
    {
        hasSubtypes = true;
    }

    public abstract ItemVariant addVariant(int meta, @Nonnull ResourceLocation loc);

    public abstract ResourceLocation getVariantId(@Nonnull ItemStack stack);

    @Override
    public abstract void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items);
}
