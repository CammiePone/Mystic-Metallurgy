package com.camellias.mysticalmetallurgy.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ItemIngot extends Item {
    private Map<Integer, ResourceLocation> variants = new HashMap<>();

    public ItemIngot() {
        hasSubtypes = true;
    }

    @Nonnull
    @Override
    public String getTranslationKey(ItemStack stack) {
        return "item." + variants.get(stack.getMetadata()).toString().replace(':', '.');
    }

    public ItemIngot addVariant(int meta, @Nonnull ResourceLocation loc) {
        variants.put(meta, loc);
        return this;
    }

    public ItemIngot addVariant(@Nonnull ResourceLocation loc) {
        return addVariant(variants.size(), loc);
    }

    public ResourceLocation getVariantId(@Nonnull ItemStack stack) {
        return variants.get(stack.getMetadata());
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for (Integer meta : variants.keySet()) {
                items.add(new ItemStack(this, 1, meta));
            }
        }
    }
}
