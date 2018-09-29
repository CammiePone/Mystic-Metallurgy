package com.camellias.mysticalmetallurgy.tabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class MysticMetalItems extends CreativeTabs 
{
	public MysticMetalItems(String label)
	{
		super("mystical_metals_items");
	}

	@Override
	public ItemStack getTabIconItem() 
	{
		return new ItemStack(Items.IRON_INGOT);
	}
}
