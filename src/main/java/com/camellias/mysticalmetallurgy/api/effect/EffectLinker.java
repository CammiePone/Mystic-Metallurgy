package com.camellias.mysticalmetallurgy.api.effect;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.api.RegisterItemEffectsEvent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

    private Map<ResourceLocation, List<Trait>> itemEffects = new HashMap<>();
    private Map<ResourceLocation, List<Trait>> oreDictEffects = new HashMap<>();

    @SuppressWarnings("unchecked")
    public void registerItemWithTrait(@Nonnull Item item, @Nonnull ResourceLocation effect, int level)
    {
        if (Effect.exists(effect))
        {
            if (itemEffects.containsKey(item.getRegistryName()))
            {
                itemEffects.get(item.getRegistryName()).add(new Trait(effect, level));
            }
            else
            {
                itemEffects.put(item.getRegistryName(), new ArrayList<>(Collections.singletonList(new Trait(effect, level))));
            }
            Main.logger.info(String.format("successfully registered %s effect to item %s", effect.toString(), item.getRegistryName().toString()));
        }
        else
            Main.logger.warn(String.format("failed to register %s effect to item %s - effect does not exist",  effect.toString(), item.getRegistryName().toString()));
    }

    @SuppressWarnings("unchecked")
    public void registerTagWithTrait(@Nonnull ResourceLocation tag, @Nonnull ResourceLocation effect, int level)
    {
        if (Effect.exists(effect))
        {
            if (ItemTags.getCollection().get(tag) != null)
            {
                if (oreDictEffects.containsKey(tag))
                {
                    oreDictEffects.get(tag).add(new Trait(effect, level));
                }
                else
                {
                    oreDictEffects.put(tag, new ArrayList<>(Collections.singletonList(new Trait(effect, level))));
                }
                Main.logger.info(String.format("successfully registered %s effect to oredict %s", effect.toString(), tag.toString()));
            }
            else
                Main.logger.warn(String.format("failed to register %s effect to oredict %s - oredict entry does not exist", effect.toString(), tag.toString()));
        }
        else
            Main.logger.warn(String.format("failed to register %s effect to oredict %s - effect does not exist", effect.toString(), tag.toString()));
    }

    @Nonnull
    public static List<Trait> getItemRegisteredTraits(ItemStack stack)
    {
        List<Trait> effects = new ArrayList<>();

        for (Map.Entry<ResourceLocation, List<Trait>> entry : INSTANCE.oreDictEffects.entrySet())
        {
            Tag<Item> tag = ItemTags.getCollection().get(entry.getKey());
            if (tag != null && tag.contains(stack.getItem()))
                effects.addAll(entry.getValue());
        }

        if (INSTANCE.itemEffects.containsKey(stack.getItem().getRegistryName()))
            effects.addAll(INSTANCE.itemEffects.get(stack.getItem().getRegistryName()));

        return effects;
    }

    public static boolean hasStackRegisteredTraits(ItemStack stack)
    {
        if (stack.isEmpty()) return false;

        for (Map.Entry<ResourceLocation, List<Trait>> entry : INSTANCE.oreDictEffects.entrySet())
        {
            Tag<Item> tag = ItemTags.getCollection().get(entry.getKey());
            if (tag != null && tag.contains(stack.getItem()))
                return true;
        }

        return INSTANCE.itemEffects.containsKey(stack.getItem().getRegistryName());
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
        NBTTagCompound nbt = stack.getTag();
        if (nbt == null || !nbt.hasUniqueId(NBT_TIER))
            return 0;

        return nbt.getInt(NBT_TIER);
    }

    public static NBTTagCompound writeTierToNBT(NBTTagCompound nbt, int tier)
    {
        if (nbt == null)
            nbt = new NBTTagCompound();

        nbt.putInt(NBT_TIER, tier);
        return nbt;
    }

    @SubscribeEvent
    public void addToolTip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        List<ITextComponent> tooltip = event.getToolTip();

        if (hasStackRegisteredTraits(stack))
        {
            //<Hold Shift For More Info>
            if (!GuiScreen.isShiftKeyDown())
            {
                tooltip.add(new TextComponentString(TextFormatting.GOLD + I18n.format("info.mysticalmetallurgy.shift")));
            }
            else
            {
                //Attributes:
                tooltip.add(new TextComponentString(TextFormatting.GOLD + TextFormatting.BOLD.toString() + I18n.format("info.mysticalmetallurgy.attributes")));

                for (Trait trait : getItemRegisteredTraits(stack))
                {
                    tooltip.add(new TextComponentString(String.format("%s %d", trait.getEffect().getAttributeInfo(), trait.level)));
                }
            }
        }
    }
}
