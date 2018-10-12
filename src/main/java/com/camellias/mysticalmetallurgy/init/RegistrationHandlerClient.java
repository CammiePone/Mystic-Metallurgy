package com.camellias.mysticalmetallurgy.init;

import com.camellias.mysticalmetallurgy.common.item.ItemIngot;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RegistrationHandlerClient
{
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        registerAllItemModel(
                ModItems.INGOT
        );
    }
    
    private static void registerAllItemModel(Item... items)
    {
        for (Item item : items)
        {
            if (item.getHasSubtypes())
            {
                NonNullList<ItemStack> list = NonNullList.create();
                item.getSubItems(ModTabs.MYSTICAL_METALS_ITEMS, list);
                list.forEach(stack -> registerItemModel(item, stack.getItemDamage(), ((ItemIngot) item).getVariantId(stack)));
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
}
