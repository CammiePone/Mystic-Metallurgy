package com.camellias.mysticalmetallurgy.api.effect;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.api.RegisterItemEffectsEvent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.*;

public class EffectLinker
{
    public static void raiseRegisterEvent()
    {
        MinecraftForge.EVENT_BUS.post(new RegisterItemEffectsEvent(INSTANCE));
    }

    private EffectLinker() {}

    public static EffectLinker INSTANCE = new EffectLinker();

    private static final String NBT_TIER = "mystic_tier";

    private Map<ItemMeta, List<Trait>> itemEffects = new HashMap<>();
    private Map<String, List<Trait>> oreDictEffects = new HashMap<>();

    @SuppressWarnings("unchecked")
    public void registerItemWithTrait(@Nonnull Item item, int meta, @Nonnull ResourceLocation effect, int level)
    {
        if (Effect.exists(effect))
        {
            ItemMeta im = new ItemMeta(item, meta);

            if (itemEffects.containsKey(im))
            {
                itemEffects.get(im).add(new Trait(effect, level));
            }
            else
            {
                itemEffects.put(im, new ArrayList<>(Collections.singletonList(new Trait(effect, level))));
            }
            Main.logger.info(String.format("successfully registered %s effect to item %s:%d", effect.toString(), item.getRegistryName().toString(), meta));
        }
        else
            Main.logger.warn(String.format("failed to register %s effect to item %s:%d - effect does not exist",  effect.toString(), item.getRegistryName().toString(), meta));
    }

    @SuppressWarnings("unchecked")
    public void registerItemWithTrait(@Nonnull String oreDict, @Nonnull ResourceLocation effect, int level)
    {
        if (Effect.exists(effect))
        {
            if (OreDictionary.doesOreNameExist(oreDict))
            {
                if (oreDictEffects.containsKey(oreDict))
                {
                    oreDictEffects.get(oreDict).add(new Trait(effect, level));
                }
                else
                {
                    oreDictEffects.put(oreDict, new ArrayList<>(Collections.singletonList(new Trait(effect, level))));
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
    public static List<Trait> getItemRegisteredTraits(ItemStack stack)
    {
        List<Trait> effects = new ArrayList<>();
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

    public static boolean hasStackRegisteredTraits(ItemStack stack)
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
    public static List<Trait> combineStackTraits(ItemStack... stacks)
    {
        List<Trait> allStackEffects = new ArrayList<>();
        for (ItemStack stack : stacks)
        {
            if (!stack.isEmpty())
            {
                allStackEffects.addAll(getItemRegisteredTraits(stack));
            }
        }

        return Trait.combine(allStackEffects, stacks.length);
    }

    public static int getStackTier(ItemStack stack)
    {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey(NBT_TIER))
            return 0;

        return nbt.getInteger(NBT_TIER);
    }

    public static NBTTagCompound writeTierToNBT(NBTTagCompound nbt, int tier)
    {
        if (nbt == null)
            nbt = new NBTTagCompound();

        nbt.setInteger(NBT_TIER, tier);
        return nbt;
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

    @SubscribeEvent
    public void addToolTip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        List<String> tooltip = event.getToolTip();

        if (hasStackRegisteredTraits(stack))
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

                for (Trait trait : getItemRegisteredTraits(stack))
                {
                    tooltip.add(String.format("%s %d", trait.getEffect().getAttributeInfo(), trait.level));
                }
            }
        }
    }
}
