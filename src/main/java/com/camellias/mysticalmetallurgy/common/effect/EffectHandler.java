package com.camellias.mysticalmetallurgy.common.effect;

import com.camellias.mysticalmetallurgy.api.Effect;
import com.camellias.mysticalmetallurgy.api.RegisterItemEffectsEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.*;

public class EffectHandler
{
    public static void raiseRegisterEvent()
    {
        MinecraftForge.EVENT_BUS.post(new RegisterItemEffectsEvent(INSTANCE));
    }

    private EffectHandler() {}

    public static EffectHandler INSTANCE = new EffectHandler();

    private Map<ItemMeta, List<EffectLevelPair>> itemEffects = new HashMap<>();

    @SuppressWarnings("unchecked")
    public void registerItemWithEffect(@Nonnull Item item, int meta, @Nonnull ResourceLocation effect, int level)
    {
        if (Effect.exists(effect))
        {
            ItemMeta im = new ItemMeta(item, meta);
            
            if (itemEffects.containsKey(im))
            {
                itemEffects.get(im).add(new EffectLevelPair(effect, level));
            }
            else
            {
                itemEffects.put(im, new ArrayList<>(Collections.singletonList(new EffectLevelPair(effect, level))));
            }
        }
    }

    private List<EffectLevelPair> getItemEffects(Item item, int meta)
    {
        return itemEffects.get(new ItemMeta(item, meta));
    }

    private List<EffectLevelPair> getItemEffects(ItemStack stack)
    {
        return getItemEffects(stack.getItem(), stack.getMetadata());
    }

    private boolean isRegistered(Item item, int meta)
    {
        return itemEffects.containsKey(new ItemMeta(item, meta));
    }

    private boolean isRegistered(ItemStack stack)
    {
        return isRegistered(stack.getItem(), stack.getMetadata());
    }

    private static class ItemMeta
    {
        private Item item;
        private int meta;

        ItemMeta(Item item, int meta)
        {
            this.item = item;
            this.meta = meta;
        }

        @Override
        public boolean equals(Object other)
        {
            if (other == this) return true;
            if (!(other instanceof ItemMeta))return false;
            ItemMeta otherMyClass = (ItemMeta)other;
            return otherMyClass.meta == meta && otherMyClass.item == item;
        }
    }

    private static class EffectLevelPair
    {
        ResourceLocation effect;
        int level;

        EffectLevelPair(ResourceLocation effect, int level)
        {
            this.effect = effect;
            this.level = level;
        }
    }

    @SubscribeEvent
    public void addToolTip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        List<String> tooltip = event.getToolTip();

        if (isRegistered(stack))
        {
            //<Hold Shift For More Info>
            if (!event.getFlags().isAdvanced())
            {
                tooltip.add(TextFormatting.GOLD + I18n.format("info.mysticalmetallurgy.shift"));
            }
            else
            {
                //Attributes:
                tooltip.add(TextFormatting.GOLD + TextFormatting.BOLD.toString() + I18n.format("info.mysticalmetallurgy.attributes"));

                for (EffectLevelPair effectPair : getItemEffects(stack))
                {
                    tooltip.add(Effect.getEffect(effectPair.effect).getAttributeInfo());
                }
            }
        }
    }
}
