package com.camellias.mysticalmetallurgy.util.handlers;

import java.io.File;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.Reference;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler 
{
	public static Configuration config;
	
	public static void init(File file)
	{
		config = new Configuration(file);
		
		String category;
		
		config.save();
	}
	
	public static void registerConfig(FMLPreInitializationEvent event)
	{
		Main.config = new File(event.getModConfigurationDirectory() + "/" + Reference.MODID);
		Main.config.mkdirs();
		init(new File(Main.config.getPath(), Reference.MODID + ".cfg"));
	}
}
