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
import com.camellias.mysticalmetallurgy.common.item.ItemVariant;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RegistrationHandlerClient
{
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        registerAllItemModel(
                Item.getItemFromBlock(ModBlocks.CRUCIBLE),
                Item.getItemFromBlock(ModBlocks.BRAZIER),
                Item.getItemFromBlock(ModBlocks.STONE_ANVIL),
                Item.getItemFromBlock(ModBlocks.BASIN),
                Item.getItemFromBlock(ModBlocks.RACK),

                ModItems.LADLE,
                ModItems.HAMMER,
                ModItems.GLOVES
                //ModItems.INGOT
        );

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
    private static void registerAllItemModel(Item... items)
    {
        for (Item item : items)
        {
            if (item instanceof ItemVariant)
            {
                NonNullList<ItemStack> list = NonNullList.create();
                for (CreativeTabs tab : item.getCreativeTabs())
                {
                    item.getSubItems(tab, list);
                    list.forEach(stack -> registerItemModel(item, stack.getItemDamage(), ((ItemVariant) item).getVariantId(stack)));
                }
            }
            else registerItemModel(item);
        }
    }
    
    private static void registerItemModel(Item item)
    {
        registerItemModel(item, 0, item.getRegistryName());
    }
    
    private static void registerItemModel(Item item, int meta, ResourceLocation location)
    {
        registerItemModel(item, meta, location, "inventory");
    }
    
    private static void registerItemModel(Item item, int meta, ResourceLocation location, String variant)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(location, variant));
    }

    private static void registerAllTex(TextureMap map, ResourceLocation... locations)
    {
        for (ResourceLocation tex : locations)
            map.registerSprite(tex);
    }

    private static void registerFluidModel(Block block, Fluid fluid, ResourceLocation location)
    {
        Item item = Item.getItemFromBlock(block);
        FluidStateMapper mapper = new FluidStateMapper(fluid, new ModelResourceLocation(location, "normal"));

        ModelLoader.registerItemVariants(item);
        ModelLoader.setCustomMeshDefinition(item, mapper);
        ModelLoader.setCustomStateMapper(block, mapper);
    }

    public static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition
    {

        public final Fluid fluid;
        public final ModelResourceLocation location;

        public FluidStateMapper(Fluid fluid, ModelResourceLocation location) {
            this.fluid = fluid;

            // have each block hold its fluid per nbt? hm
            this.location = location;
        }

        @Nonnull
        @Override
        protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
            return location;
        }

        @Nonnull
        @Override
        public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
            return location;
        }
    }
    //endregion
}
