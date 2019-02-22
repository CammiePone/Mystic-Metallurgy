package com.camellias.mysticalmetallurgy;

import com.camellias.mysticalmetallurgy.api.effect.Effect;
import com.camellias.mysticalmetallurgy.api.effect.EffectLinker;
import com.camellias.mysticalmetallurgy.init.RegistrationHandler;
import com.camellias.mysticalmetallurgy.network.NetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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

	public Main()
	{
		MinecraftForge.EVENT_BUS.register(EffectLinker.INSTANCE);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupConfig);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, RegistrationHandler::registerBlocks);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, RegistrationHandler::registerItems);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, RegistrationHandler::registerTileEntity);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Effect.class, RegistrationHandler::registerEffects);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(RegistrationHandler::registerItemEffects);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(RegistrationHandler::registerRegistry);
	}

	private void setup(FMLCommonSetupEvent event)
	{
		NetworkHandler.registerPackets();
		//RegistrationHandler.registerOreDict();
		EffectLinker.raiseRegisterEvent();
	}

	private void setupConfig(ModConfig.ModConfigEvent event)
	{

	}
}
