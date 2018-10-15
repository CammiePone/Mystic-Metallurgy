package com.camellias.mysticalmetallurgy.api;

import com.camellias.mysticalmetallurgy.Main;
import net.minecraftforge.common.config.Config;

@Config(modid = Main.MODID)
public final class ConfigValues
{
    @Config.RangeInt(min = 1, max = 5)
    @Config.Comment("How many times alloys can be combined.")
    public static int MaxCombineTier = 1;
}
