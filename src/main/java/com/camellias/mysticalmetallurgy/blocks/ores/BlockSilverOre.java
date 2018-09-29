package com.camellias.mysticalmetallurgy.blocks.ores;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.init.ModBlocks;
import com.camellias.mysticalmetallurgy.init.ModItems;
import com.camellias.mysticalmetallurgy.util.IHasModel;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class BlockSilverOre extends Block implements IHasModel
{
	public BlockSilverOre(String name, Material material)
	{
		super(material);
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(3.0F);
		setHarvestLevel("pickaxe", 2);
		setCreativeTab(Main.mysticalMetallurgyOres);
		
		ModBlocks.BLOCKS.add(this);
		ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
	}
	
	@Override
	public void registerModels() 
	{
		Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
	}
}
