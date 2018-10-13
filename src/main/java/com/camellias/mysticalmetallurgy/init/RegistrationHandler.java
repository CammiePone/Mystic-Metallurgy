package com.camellias.mysticalmetallurgy.init;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.api.Effect;
import com.camellias.mysticalmetallurgy.api.RegisterItemEffectsEvent;
import com.camellias.mysticalmetallurgy.common.effect.*;
import com.camellias.mysticalmetallurgy.common.item.ItemIngot;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber
public class RegistrationHandler
{
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.NewRegistry event)
    {
        new RegistryBuilder<Effect>().setName(Effect.REGISTRY_NAME).setMaxID(Effect.MAX_ID).create();
    }
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(
        );
    }
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
        		
                asDefault(new ItemIngot()
                                .addVariant(0, new ResourceLocation(Main.MODID, "ingot_silver")),
                        new ResourceLocation(Main.MODID, "ingot"),
                        ModTabs.MYSTICAL_METALS_ITEMS)

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
        event.getRegistry().registerItemWithEffect("ingotSilver", EffectFire.ID, 3);
    }
    
    public static void registerOreDict()
    {
        OreDictionary.registerOre("ingotSilver", new ItemStack(ModItems.INGOT, 1, 0));
    }
    
    private static Block asDefault(Block block, ResourceLocation loc, CreativeTabs tab)
    {
        return block.setRegistryName(loc).setCreativeTab(tab).setTranslationKey(loc.toString().replace(':', '.'));
    }
    
    private static Item asDefault(Item item, ResourceLocation loc, CreativeTabs tab)
    {
        return item.setRegistryName(loc).setCreativeTab(tab).setTranslationKey(loc.toString().replace(':', '.'));
    }
    
    private static Item asItem(Block block, ResourceLocation loc)
    {
        return new ItemBlock(block).setRegistryName(loc);
    }
}
