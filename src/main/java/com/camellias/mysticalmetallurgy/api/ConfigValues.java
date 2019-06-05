package com.camellias.mysticalmetallurgy.api;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Config(modid = Main.MODID)
public final class ConfigValues
{
    @Config.RangeInt(min = 1, max = 5)
    @Config.Comment("How many times alloys can be combined.")
    public static int MaxCombineTier = 1;

    public static CatHeat Heat = new CatHeat();

    public static class CatHeat
    {
        @Config.Comment("Which part of the inventory to check for heat handling")
        public HotCheckType HotHandling = HotCheckType.HAND;

        @Config.Comment("Temperature in K. Every temperature above this is hot.")
        public int TempConsideredCold = 300;

        @Config.RangeInt(min = 0, max = 600)
        @Config.Comment("Drops the item in hand after X seconds (0 = don't drop)")
        public int DropHotAfterSec = 5;

        @Config.RangeInt(min = 0, max = 1000000)
        @Config.Comment("Basically how many seconds the gloves can carry hot items")
        public int GlovesDurablity = 120;

        @Config.RangeInt(min = 0, max = 1000)
        @Config.Comment("How much mB of liquid the Basin drains per Item when it's cooling")
        public int LiquidDrainedPerSec = 5;

        @Config.Comment({
                "Items declared as hot",
                "use the following syntax per line: 'modid:itemname:meta' e.g: minecraft:stone:1",
        })
        public String[] HotItemList = {""};

        @Config.Ignore
        public List<ItemStack> HotItemStacks = parseItemList(HotItemList);
    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void onOnConfigChangedEvent(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Main.MODID)) {
                ConfigManager.sync(Main.MODID, Config.Type.INSTANCE);
                Heat.HotItemStacks = parseItemList(Heat.HotItemList);

                ModItems.GLOVES.setMaxDamage(Heat.GlovesDurablity);
            }
        }
    }

    private static ItemStack parseCfgItem(String s) {
        String[] split = s.split(":");
        if (split.length < 2 || split.length > 3) return ItemStack.EMPTY;
        else {
            Item tmp = Item.getByNameOrId(new ResourceLocation(split[0], split[1]).toString());
            if (tmp == null) return ItemStack.EMPTY;
            if (split.length == 2) return new ItemStack(tmp);
            return new ItemStack(tmp, Integer.getInteger(split[2]));
        }
    }

    private static List<ItemStack> parseItemList(String[] items)
    {
        List<ItemStack> itemStacks = new ArrayList<>(items.length);
        for (String stackString : items)
        {
            ItemStack stack = parseCfgItem(stackString);
            if (stack.isEmpty())
                continue;
            itemStacks.add(stack);
        }

        return itemStacks;
    }

    public enum HotCheckType {
        HAND,
        HOTBAR,
        INVENTORY,
        DISABLED
    }
}
