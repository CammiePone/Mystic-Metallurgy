package com.camellias.mysticalmetallurgy.tabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class MysticMetalOres extends CreativeTabs 
{
	public MysticMetalOres(String label)
	{
		super("mystical_metals_ores");
	}

	@Override
	public ItemStack getTabIconItem() 
	{
		return new ItemStack(Blocks.IRON_ORE);
	}
}
