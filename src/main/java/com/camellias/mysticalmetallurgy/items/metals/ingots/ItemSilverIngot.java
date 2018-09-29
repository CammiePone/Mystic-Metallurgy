package com.camellias.mysticalmetallurgy.items.metals.ingots;

import java.util.List;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.init.ModItems;
import com.camellias.mysticalmetallurgy.util.IHasModel;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSilverIngot extends Item implements IHasModel
{
	public ItemSilverIngot(String name)
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.mysticalMetallurgyItems);
		
		ModItems.ITEMS.add(this);
	}
	
	@Override
	public void registerModels() 
	{
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}
}
