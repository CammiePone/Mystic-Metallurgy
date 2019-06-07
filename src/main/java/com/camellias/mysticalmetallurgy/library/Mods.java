package com.camellias.mysticalmetallurgy.library;

import net.minecraftforge.fml.common.Loader;

public enum Mods {
    JEI("JEI"),
    CRAFTTWEAKER("crafttweaker");


    Mods(String id) {
        modId = id;
        isLoaded = Loader.isModLoaded(modId);
    }

    private final String modId;
    private final boolean isLoaded;

    public boolean isPresent() {
        return isLoaded;
    }
}
