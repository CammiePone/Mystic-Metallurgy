package com.camellias.mysticalmetallurgy.init;

import com.camellias.mysticalmetallurgy.api.effect.Effect;
import com.camellias.mysticalmetallurgy.api.RegisterItemEffectsEvent;
import com.camellias.mysticalmetallurgy.api.recipe.AnvilRecipe;
import com.camellias.mysticalmetallurgy.common.block.BlockBrazier;
import com.camellias.mysticalmetallurgy.common.block.anvil.BlockStoneAnvil;
import com.camellias.mysticalmetallurgy.common.block.anvil.TileStoneAnvil;
import com.camellias.mysticalmetallurgy.common.block.basin.BlockQuenchingBasin;
import com.camellias.mysticalmetallurgy.common.block.basin.TileQuenchingBasin;
import com.camellias.mysticalmetallurgy.common.block.crucible.BlockCrucible;
import com.camellias.mysticalmetallurgy.common.block.crucible.TileCrucible;
import com.camellias.mysticalmetallurgy.common.block.rack.BlockRack;
import com.camellias.mysticalmetallurgy.common.block.rack.TileRack;
import com.camellias.mysticalmetallurgy.common.effect.*;
import com.camellias.mysticalmetallurgy.common.fluid.FluidMysticMetal;

import com.camellias.mysticalmetallurgy.common.item.ItemMetalClump;
import com.camellias.mysticalmetallurgy.common.item.tool.ItemHammer;
import com.camellias.mysticalmetallurgy.common.item.tool.ItemLadle;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.*;

@Mod.EventBusSubscriber
public class RegistrationHandler
{
    @SubscribeEvent
    public static void registerRegistry(RegistryEvent.NewRegistry event)
    {
        //noinspection unchecked
        new RegistryBuilder<>().setName(Effect.REGISTRY_NAME).setType((Class)Effect.class).setMaxID(Effect.MAX_ID).create();
    }
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(
                asDefault(new BlockCrucible(), BlockCrucible.LOC, ModTabs.MYSTICAL_METALS_BLOCKS),
                asDefault(new BlockBrazier(), BlockBrazier.LOC, ModTabs.MYSTICAL_METALS_BLOCKS),
                asDefault(new BlockStoneAnvil(), BlockStoneAnvil.LOC, ModTabs.MYSTICAL_METALS_BLOCKS),
                asDefault(new BlockQuenchingBasin(), BlockQuenchingBasin.LOC, ModTabs.MYSTICAL_METALS_BLOCKS),
                asDefault(new BlockRack(), BlockRack.LOC, ModTabs.MYSTICAL_METALS_BLOCKS),
                asFluid(ModFluids.MYSTICAL_METAL, Material.LAVA, FluidMysticMetal.ID, ModTabs.MYSTICAL_METALS_BLOCKS)
        );

        GameRegistry.registerTileEntity(TileRack.class, BlockRack.LOC);
        GameRegistry.registerTileEntity(TileCrucible.class, BlockCrucible.LOC);
        GameRegistry.registerTileEntity(TileStoneAnvil.class, BlockStoneAnvil.LOC);
        GameRegistry.registerTileEntity(TileQuenchingBasin.class, BlockQuenchingBasin.LOC);
    }
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                //Blocks
                asItem(ModBlocks.CRUCIBLE, BlockCrucible.LOC),
                asItem(ModBlocks.BRAZIER, BlockBrazier.LOC),
                asItem(ModBlocks.STONE_ANVIL, BlockStoneAnvil.LOC),
                asItem(ModBlocks.BASIN, BlockQuenchingBasin.LOC),
                asItem(ModBlocks.RACK, BlockRack.LOC),
                asItem(ModBlocks.MYSTICAL_LIQUID_METAL, FluidMysticMetal.ID),

                //Items
                asDefault(new ItemMetalClump()
                                .addVariant(0, ItemMetalClump.CLUMP_0_LOC)
                                .addVariant(1, ItemMetalClump.CLUMP_1_LOC)
                                .addVariant(2, ItemMetalClump.CLUMP_2_LOC),
                        ItemMetalClump.LOC,
                        ModTabs.MYSTICAL_METALS_ITEMS),

                //Tools
                asDefault(new ItemLadle(), ItemLadle.LOC, ModTabs.MYSTICAL_METALS_ITEMS),
                asDefault(new ItemHammer(), ItemHammer.LOC, ModTabs.MYSTICAL_METALS_ITEMS)
                //asDefault(new ItemIngot()
                //                .addVariant(0, new ResourceLocation(Main.MODID, "ingot_silver")),
                //        new ResourceLocation(Main.MODID, "ingot"),
                //        ModTabs.MYSTICAL_METALS_ITEMS)

        );
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        AnvilRecipe.register(new AnvilRecipe(new ItemStack(Items.ARROW), 4, Items.PAPER, "ingotIron", null, Items.COAL, Items.BONE, Items.APPLE, Items.BEEF));
    }

    @SubscribeEvent
    public static void registerEffects(RegistryEvent.Register<Effect> event)
    {
        event.getRegistry().registerAll(
                new EffectFire(),
                new EffectDense()
        );
    }
    
    @SubscribeEvent
    public static void registerItemEffects(RegisterItemEffectsEvent event)
    {
        event.getRegistry().registerItemWithTrait("ingotIron", EffectDense.ID, 3);
        event.getRegistry().registerItemWithTrait("ingotIron", EffectFire.ID, 5);
        event.getRegistry().registerItemWithTrait("ingotGold", EffectFire.ID, 3);
    }

    public static void registerOreDict()
    {
        //OreDictionary.registerOre("ingotSilver", new ItemStack(ModItems.INGOT, 1, 0));
    }

    //region <helper>
    private static Block asDefault(Block block, ResourceLocation loc, CreativeTabs tab)
    {
        return block.setRegistryName(loc).setCreativeTab(tab).setTranslationKey(loc.toString().replace(':', '.'));
    }
    
    private static Item asDefault(Item item, ResourceLocation loc, CreativeTabs tab)
    {
        return item.setRegistryName(loc).setCreativeTab(tab).setTranslationKey(loc.toString().replace(':', '.'));
    }

    private static BlockFluidClassic asFluid(Fluid fluid, Material material, ResourceLocation loc, CreativeTabs tab)
    {
        FluidRegistry.registerFluid(fluid);
        FluidRegistry.addBucketForFluid(fluid);
        return (BlockFluidClassic)new BlockFluidClassic(fluid, material).setRegistryName(loc).setCreativeTab(tab).setTranslationKey(loc.toString().replace(':', '.'));
    }
    
    private static Item asItem(Block block, ResourceLocation loc)
    {
        return new ItemBlock(block).setRegistryName(loc);
    }
    //endregion
}
