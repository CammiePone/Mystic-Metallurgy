package com.camellias.mysticalmetallurgy.init;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ModGroups
{
    public static final ItemGroup MYSTICAL_METALS_ITEMS = new ModGroup("mystical_metals_items", Items.IRON_INGOT);
    public static final ItemGroup MYSTICAL_METALS_ORES = new ModGroup("mystical_metals_ores", Blocks.IRON_ORE);
    public static final ItemGroup MYSTICAL_METALS_BLOCKS = new ModGroup("mystical_metals_blocks", Blocks.IRON_BLOCK);

    private static class ModGroup extends ItemGroup
    {
        Item icon;

        ModGroup(String label, @Nonnull Item itemIcon)
        {
            super(label);
            icon = itemIcon;
        }

        ModGroup(String label, @Nonnull Block blockIcon)
        {
            this(label, Item.BLOCK_TO_ITEM.get(blockIcon));
        }

        @Nonnull
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(icon);
        }
    }
}
