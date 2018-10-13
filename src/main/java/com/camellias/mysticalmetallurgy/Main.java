package com.camellias.mysticalmetallurgy;

import com.camellias.mysticalmetallurgy.common.effect.EffectHandler;
import com.camellias.mysticalmetallurgy.init.RegistrationHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

@Mod(	modid = Main.MODID,
		name = Main.NAME,
		version = Main.VERSION,
		acceptedMinecraftVersions = Main.ACCEPTEDVERSIONS)
public class Main
{
	public static final String MODID = "mysticalmetallurgy";
	public static final String NAME = "Mystical Metallurgy";
	static final String VERSION = "@GRADLE:VERSION@";
	static final String ACCEPTEDVERSIONS = "[1.12.2]";

	public static Logger logger;

	//Initialization
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		RegistrationHandler.registerOreDict();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		EffectHandler.raiseRegisterEvent();
	}
}
