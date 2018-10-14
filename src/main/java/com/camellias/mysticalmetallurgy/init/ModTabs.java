package com.camellias.mysticalmetallurgy.init;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ModTabs
{
    public static final CreativeTabs MYSTICAL_METALS_ITEMS = new ModTab("mystical_metals_items", Items.IRON_INGOT);
    public static final CreativeTabs MYSTICAL_METALS_ORES = new ModTab("mystical_metals_ores", Blocks.IRON_ORE);
    public static final CreativeTabs MYSTICAL_METALS_BLOCKS = new ModTab("mystical_metals_blocks", Blocks.IRON_BLOCK);

    private static class ModTab extends CreativeTabs
    {
        Item icon;
        
        ModTab(String label, @Nonnull Item itemIcon)
        {
            super(label);
            icon = itemIcon;
        }
        
        ModTab(String label, @Nonnull Block blockIcon)
        {
            this(label, Item.getItemFromBlock(blockIcon));
        }
        
        @Nonnull
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(icon);
        }
    }
}
