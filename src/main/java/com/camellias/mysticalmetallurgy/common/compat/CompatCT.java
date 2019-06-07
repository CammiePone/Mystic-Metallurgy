package com.camellias.mysticalmetallurgy.common.compat;

import com.camellias.mysticalmetallurgy.api.recipe.AnvilRecipe;
import crafttweaker.CraftTweakerAPI;

public class CompatCT {

    public static CompatCT INSTANCE = new CompatCT();

    private CompatCT() {}

    public void load()
    {
        CraftTweakerAPI.registerClass(AnvilRecipe.class);
    }
}
