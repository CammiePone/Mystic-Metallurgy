package com.camellias.mysticalmetallurgy;

import com.camellias.mysticalmetallurgy.api.effect.EffectLinker;
import com.camellias.mysticalmetallurgy.init.RegistrationHandler;
import com.camellias.mysticalmetallurgy.network.NetworkHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Logger;

@Mod(Main.MODID)
public class Main
{
	public static final String MODID = "mysticalmetallurgy";
	public static final ResourceLocation CHANNEL = new ResourceLocation(MODID, "networking");

	public static Logger logger;
	static
	{
		FluidRegistry.enableUniversalBucket();
	}

	public Main() {
		NetworkHandler.registerPackets();
		RegistrationHandler.registerOreDict();
		EffectLinker.raiseRegisterEvent();
		MinecraftForge.EVENT_BUS.register(EffectLinker.INSTANCE);
	}
}
