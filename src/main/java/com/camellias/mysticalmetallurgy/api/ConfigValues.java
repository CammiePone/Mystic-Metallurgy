package com.camellias.mysticalmetallurgy.api;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class ConfigValues
{
    public static final Config CONFIG;
    public static final ForgeConfigSpec SPEC;
    static
    {
        final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
        SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    public static int maxCombineTier;

    public static void load()
    {
        maxCombineTier = CONFIG.maxCombineTier.get();
    }

    public static class Config
    {
        ForgeConfigSpec.IntValue maxCombineTier;

        Config(ForgeConfigSpec.Builder builder)
        {
            builder.push("general");
            maxCombineTier = builder
                    .comment("How many times alloys can be combined.")
                    .defineInRange("maxCombineTier", 1, 1, 5);
            builder.pop();
        }
    }
}
