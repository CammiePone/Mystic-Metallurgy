package com.camellias.mysticalmetallurgy.common.effect;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.api.Effect;
import com.camellias.mysticalmetallurgy.api.RegisterItemEffectsEvent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
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

    public static final String NBT_EFFECTS = "mystical_effects";

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
            Main.logger.info(String.format("successfully registered %s effect to item %s:%d", effect.toString(), item.getRegistryName().toString(), meta));
        }
        else
            Main.logger.warn(String.format("failed to register %s effect to item %s:%d - effect does not exist",  effect.toString(), item.getRegistryName().toString(), meta));
    }

    @SuppressWarnings("unchecked")
    public void registerItemWithEffect(@Nonnull String oreDict, @Nonnull ResourceLocation effect, int level)
    {
        if (Effect.exists(effect))
        {
            if (OreDictionary.doesOreNameExist(oreDict))
            {
                if (oreDictEffects.containsKey(oreDict))
                {
                    oreDictEffects.get(oreDict).add(new EffectLevelPair(effect, level));
                }
                else
                {
                    oreDictEffects.put(oreDict, new ArrayList<>(Collections.singletonList(new EffectLevelPair(effect, level))));
                }
                Main.logger.info(String.format("successfully registered %s effect to oredict %s", effect.toString(), oreDict));
            }
            else
                Main.logger.warn(String.format("failed to register %s effect to oredict %s - oredict entry does not exist", effect.toString(), oreDict));
        }
        else
            Main.logger.warn(String.format("failed to register %s effect to oredict %s - effect does not exist", effect.toString(), oreDict));
    }

    @Nonnull
    public static List<EffectLevelPair> getItemEffects(ItemStack stack)
    {
        List<EffectLevelPair> effects = new ArrayList<>();
        for (int id : OreDictionary.getOreIDs(stack))
        {
            String oreDictEntry = OreDictionary.getOreName(id);
            if (INSTANCE.oreDictEffects.containsKey(oreDictEntry))
                effects.addAll(INSTANCE.oreDictEffects.get(oreDictEntry));
        }

        ItemMeta im = new ItemMeta(stack.getItem(), stack.getMetadata());
        if (INSTANCE.itemEffects.containsKey(im))
            effects.addAll(INSTANCE.itemEffects.get(im));

        return effects;
    }

    public static boolean hasStackEffects(ItemStack stack)
    {
        if (stack.isEmpty()) return false;
        for (int id : OreDictionary.getOreIDs(stack))
        {
            String oreDictEntry = OreDictionary.getOreName(id);
            if (INSTANCE.oreDictEffects.containsKey(oreDictEntry))
                return true;
        }

        return INSTANCE.itemEffects.containsKey(new ItemMeta(stack.getItem(), stack.getMetadata()));
    }

    @Nonnull
    public static List<EffectLevelPair> readEffectsFromNBT(NBTTagCompound tag)
    {
        List<EffectLevelPair> list = new ArrayList<>();
        if (tag.hasKey(NBT_EFFECTS))
        {
            NBTTagList tagList = (NBTTagList)tag.getTag(NBT_EFFECTS);
            tagList.forEach(nbt ->
                    list.add(EffectLevelPair.fromNBT((NBTTagCompound) nbt)));
        }
        return list;
    }

    @Nonnull
    public static List<EffectLevelPair> combineStackEffects(ItemStack... stacks)
    {
        List<EffectLevelPair> list = new ArrayList<>(stacks.length);
        for (ItemStack stack : stacks)
        {
            if (!stack.isEmpty() && hasStackEffects(stack))
            {
                for (EffectHandler.EffectLevelPair el : getItemEffects(stack))
                {
                    int idx = list.indexOf(el);
                    if (idx >= 0)
                        list.get(idx).level++;
                    else
                    {
                        int newLevel = Math.round((float) el.level / (float) stacks.length);
                        if (newLevel > 0)
                            list.add(new EffectLevelPair(el.effect, newLevel));
                    }
                }
            }
        }
        return list;
    }

    public static NBTTagCompound combineStackEffectsToNBT(ItemStack... stacks)
    {
        List<EffectLevelPair> effects = combineStackEffects(stacks);

        NBTTagList tagList = new NBTTagList();
        for (EffectHandler.EffectLevelPair el : effects)
        {
            tagList.appendTag(el.serializeNBT());
        }

        NBTTagCompound cmp = new NBTTagCompound();
        cmp.setTag(NBT_EFFECTS, tagList);
        return cmp;
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

    public static class EffectLevelPair implements INBTSerializable<NBTTagCompound>
    {
        private static final String NBT_EFFECT = "effect_id";
        private static final String NBT_LEVEL = "effect_level";

        public ResourceLocation effect;
        public int level;

        EffectLevelPair(ResourceLocation effect, int level)
        {
            this.effect = effect;
            this.level = level;
        }

        private EffectLevelPair() {}

        @Override
        public boolean equals(Object other)
        {
            if (other == this) return true;
            if (!(other instanceof EffectLevelPair))return false;
            EffectLevelPair otherMyClass = (EffectLevelPair)other;
            return otherMyClass.effect.equals(effect);
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(NBT_EFFECT, effect.toString());
            tag.setInteger(NBT_LEVEL, level);
            return tag;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            effect = new ResourceLocation(nbt.getString(NBT_EFFECT));
            level = nbt.getInteger(NBT_LEVEL);
        }

        public static EffectLevelPair fromNBT(NBTTagCompound nbt)
        {
            EffectLevelPair elp = new EffectLevelPair();
            elp.deserializeNBT(nbt);
            return elp;
        }
    }

    @SubscribeEvent
    public void addToolTip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        List<String> tooltip = event.getToolTip();

        if (hasStackEffects(stack))
        {
            //<Hold Shift For More Info>
            if (!GuiScreen.isShiftKeyDown())
            {
                tooltip.add(TextFormatting.GOLD + I18n.format("info.mysticalmetallurgy.shift"));
            }
            else
            {
                //Attributes:
                tooltip.add(TextFormatting.GOLD + TextFormatting.BOLD.toString() + I18n.format("info.mysticalmetallurgy.attributes"));

                for (EffectLevelPair effectPair : getItemEffects(stack))
                {
                    tooltip.add(String.format("%s %d",Effect.getEffect(effectPair.effect).getAttributeInfo(), effectPair.level));
                }
            }
        }
    }
}
