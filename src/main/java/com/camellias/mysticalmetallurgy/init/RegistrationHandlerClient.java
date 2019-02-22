package com.camellias.mysticalmetallurgy.init;

import com.camellias.mysticalmetallurgy.common.block.anvil.RendererStoneAnvil;
import com.camellias.mysticalmetallurgy.common.block.anvil.TileStoneAnvil;
import com.camellias.mysticalmetallurgy.common.block.basin.RendererQuenchingBasin;
import com.camellias.mysticalmetallurgy.common.block.basin.TileQuenchingBasin;
import com.camellias.mysticalmetallurgy.common.block.crucible.RendererCrucible;
import com.camellias.mysticalmetallurgy.common.block.crucible.TileCrucible;
import com.camellias.mysticalmetallurgy.common.block.rack.RendererRack;
import com.camellias.mysticalmetallurgy.common.block.rack.TileRack;
import com.camellias.mysticalmetallurgy.common.fluid.FluidMysticMetal;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RegistrationHandlerClient
{
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        registerFluidModel(ModBlocks.MYSTICAL_LIQUID_METAL, ModFluids.MYSTICAL_METAL, FluidMysticMetal.ID);

        ClientRegistry.bindTileEntitySpecialRenderer(TileRack.class, new RendererRack());
        ClientRegistry.bindTileEntitySpecialRenderer(TileCrucible.class, new RendererCrucible());
        ClientRegistry.bindTileEntitySpecialRenderer(TileStoneAnvil.class, new RendererStoneAnvil());
        ClientRegistry.bindTileEntitySpecialRenderer(TileQuenchingBasin.class, new RendererQuenchingBasin());
    }

    @SubscribeEvent
    public static void registerTex(TextureStitchEvent.Pre event)
    {
        registerAllTex(event.getMap(),
                FluidMysticMetal.FLOW,
                FluidMysticMetal.STILL
        );
    }


    //region <helper>
    private static void registerAllTex(TextureMap map, ResourceLocation... locations)
    {
        for (ResourceLocation tex : locations)
            map.registerSprite(Minecraft.getInstance().getResourceManager(), tex);
    }

    private static void registerFluidModel(Block block, Fluid fluid, ResourceLocation location)
    {
        Item item = Item.getItemFromBlock(block);
        FluidStateMapper mapper = new FluidStateMapper(fluid, new ModelResourceLocation(location, "normal"));

        ModelLoader.registerItemVariants(item);
        ModelLoader.setCustomMeshDefinition(item, mapper);
        ModelLoader.setCustomStateMapper(block, mapper);
    }
    //endregion
}
