package com.camellias.mysticalmetallurgy.common.item.tool;

import com.camellias.mysticalmetallurgy.Main;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Random;

public class ItemHammer extends ItemPickaxe
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "hammer");

    public ItemHammer(Properties props)
    {
        super(ItemTier.IRON, 6, 4, props);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return true;
    }
    private final Random rand = new Random();

    @Nonnull
    @Override
    public ItemStack getContainerItem(@Nonnull ItemStack stack)
    {
        ItemStack container = stack.copy();
        container.attemptDamageItem(1, rand, null);
        return container;
    }
}
