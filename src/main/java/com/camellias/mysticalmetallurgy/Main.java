package com.camellias.mysticalmetallurgy;

import com.camellias.mysticalmetallurgy.api.effect.EffectLinker;
import com.camellias.mysticalmetallurgy.init.RegistrationHandler;
import com.camellias.mysticalmetallurgy.network.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
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
	public static final String CHANNEL = MODID;

	public static Logger logger;
	static
	{
		FluidRegistry.enableUniversalBucket();
	}

	//Initialization
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		NetworkHandler.registerPackets();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		RegistrationHandler.registerOreDict();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		EffectLinker.raiseRegisterEvent();
		MinecraftForge.EVENT_BUS.register(EffectLinker.INSTANCE);
	}
}
