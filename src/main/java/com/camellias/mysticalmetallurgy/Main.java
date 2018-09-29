package com.camellias.mysticalmetallurgy;

import java.io.File;

import com.camellias.mysticalmetallurgy.init.ModSmelting;
import com.camellias.mysticalmetallurgy.proxy.CommonProxy;
import com.camellias.mysticalmetallurgy.tabs.MysticMetalItems;
import com.camellias.mysticalmetallurgy.tabs.MysticMetalOres;
import com.camellias.mysticalmetallurgy.util.handlers.RegistryHandler;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.ACCEPTEDVERSIONS)
public class Main 
{
	public static File config;
	
	@Instance
	public static Main instance;
	
	public static final CreativeTabs mysticalMetallurgyItems = new MysticMetalItems("mystical_metals_items");
	public static final CreativeTabs mysticalMetallurgyOres = new MysticMetalOres("mystical_metals_ores");
	
	//Proxy
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;
	
	//Initialization
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		RegistryHandler.otherRegistries();
		RegistryHandler.preInitRegistries(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		RegistryHandler.initRegistries();
		ModSmelting.init();	
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		System.out.println("Pounding away at the forge!");
	}
	
	@EventHandler
	public static void serverInit(FMLServerStartingEvent event)
	{
		RegistryHandler.serverRegistries(event);
		
		System.out.println("Pounding away at the forge!");
	}
}
