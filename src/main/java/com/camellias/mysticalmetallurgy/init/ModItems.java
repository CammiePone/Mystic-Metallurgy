package com.camellias.mysticalmetallurgy.init;

import java.util.ArrayList;
import java.util.List;

import com.camellias.mysticalmetallurgy.items.metals.ingots.ItemSilverIngot;
import com.camellias.mysticalmetallurgy.items.tools.hammer.ItemHammer;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;

public class ModItems 
{
	//-----Item list-----//
	public static final List<Item> ITEMS = new ArrayList<Item>();
	
	public static final Item HAMMER = new ItemHammer("hammer", ToolMaterial.IRON);
	
	public static final Item SILVER_INGOT = new ItemSilverIngot("ingotSilver");
}
