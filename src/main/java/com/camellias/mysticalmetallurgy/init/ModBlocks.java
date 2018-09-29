package com.camellias.mysticalmetallurgy.init;

import java.util.ArrayList;
import java.util.List;

import com.camellias.mysticalmetallurgy.blocks.ores.BlockSilverOre;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ModBlocks 
{
	//-----Block list-----//
	public static final List<Block> BLOCKS = new ArrayList<Block>();
			
	//Overworld Ores
	public static final Block SILVER_ORE = new BlockSilverOre("oreSilver", Material.ROCK);
	
	//Nether Ores
	
	
	//End Ores
}
