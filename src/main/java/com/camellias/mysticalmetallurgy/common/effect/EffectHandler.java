package com.camellias.mysticalmetallurgy.common.effect;

import com.camellias.mysticalmetallurgy.Main;
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
import net.minecraftforge.oredict.OreDictionary;

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
    private Map<String, List<EffectLevelPair>> oreDictEffects = new HashMap<>();

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

    @SuppressWarnings("unchecked")
    public void registerItemWithEffect(@Nonnull String oreDict, @Nonnull ResourceLocation effect, int level)
    {
        return itemEffects.get(new ItemMeta(item, meta));
                }
                Main.logger.info(String.format("successfully registered %s effect to oredict %s", effect.toString(), oreDict));
    }

    @Nonnull
    private List<EffectLevelPair> getItemEffects(ItemStack stack)
    {
        List<EffectLevelPair> effects = new ArrayList<>();
        for (int id : OreDictionary.getOreIDs(stack))
        {
            String oreDictEntry = OreDictionary.getOreName(id);
            if (oreDictEffects.containsKey(oreDictEntry))
                effects.addAll(oreDictEffects.get(oreDictEntry));
        }

        ItemMeta im = new ItemMeta(stack.getItem(), stack.getMetadata());
        if (itemEffects.containsKey(im))
            effects.addAll(itemEffects.get(im));

        return effects;
    }

    private boolean isRegistered(ItemStack stack)
    {
        for (int id : OreDictionary.getOreIDs(stack))
        {
            String oreDictEntry = OreDictionary.getOreName(id);
            if (oreDictEffects.containsKey(oreDictEntry))
                return true;
        }

        return itemEffects.containsKey(new ItemMeta(stack.getItem(), stack.getMetadata()));
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
