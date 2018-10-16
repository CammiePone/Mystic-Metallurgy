package com.camellias.mysticalmetallurgy.init;

import com.camellias.mysticalmetallurgy.api.Effect;
import com.camellias.mysticalmetallurgy.api.RegisterItemEffectsEvent;
import com.camellias.mysticalmetallurgy.common.block.BlockBrazier;
import com.camellias.mysticalmetallurgy.common.block.crucible.BlockCrucible;
import com.camellias.mysticalmetallurgy.common.block.crucible.TileCrucible;
import com.camellias.mysticalmetallurgy.common.effect.*;
import com.camellias.mysticalmetallurgy.common.fluid.FluidMysticMetal;

import com.camellias.mysticalmetallurgy.common.item.tool.ItemLadle;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
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
                asFluid(ModFluids.MYSTICAL_METAL, Material.LAVA, FluidMysticMetal.ID, ModTabs.MYSTICAL_METALS_BLOCKS)
        );

        GameRegistry.registerTileEntity(TileCrucible.class, BlockCrucible.LOC);
    }
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                asItem(ModBlocks.CRUCIBLE, BlockCrucible.LOC),
                asItem(ModBlocks.BRAZIER, BlockBrazier.LOC),
                asItem(ModBlocks.MYSTICAL_LIQUID_METAL, FluidMysticMetal.ID),

                asDefault(new ItemLadle(), ItemLadle.LOC, ModTabs.MYSTICAL_METALS_ITEMS)
                //asDefault(new ItemIngot()
                //                .addVariant(0, new ResourceLocation(Main.MODID, "ingot_silver")),
                //        new ResourceLocation(Main.MODID, "ingot"),
                //        ModTabs.MYSTICAL_METALS_ITEMS)

        );
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
        event.getRegistry().registerItemWithEffect("ingotIron", EffectDense.ID, 3);
        event.getRegistry().registerItemWithEffect("ingotIron", EffectFire.ID, 5);
        event.getRegistry().registerItemWithEffect("ingotGold", EffectFire.ID, 3);
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
