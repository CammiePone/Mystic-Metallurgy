package com.camellias.mysticalmetallurgy;

import com.camellias.mysticalmetallurgy.common.effect.EffectHandler;
import com.camellias.mysticalmetallurgy.init.RegistrationHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

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
	
	//Initialization
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		EffectHandler.raiseRegisterEvent();
		RegistrationHandler.registerOreDict();
	}
}
