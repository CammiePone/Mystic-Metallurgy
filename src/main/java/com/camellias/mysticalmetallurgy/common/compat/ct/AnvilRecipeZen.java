package com.camellias.mysticalmetallurgy.common.compat.ct;

import com.camellias.mysticalmetallurgy.api.recipe.AnvilRecipe;
import com.camellias.mysticalmetallurgy.library.ItemHandle;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenClass("mods.mysticalmetallurgy.anvil")
public class AnvilRecipeZen extends BaseZen {

    private static final String name = "MysticalMetallurgy Anvil";

    @ZenMethod
    public static void remove(IItemStack output)
    {
        ItemStack stack = convertToItemStack(output);
        int before = AnvilRecipe.recipes.size();

        AnvilRecipe.recipes.entrySet().removeIf(entry -> entry.getValue().getOutput().isItemEqual(stack));

        CraftTweakerAPI.logInfo("[" + name + "] removed " + (before - AnvilRecipe.recipes.size()) + " recipes with output " + output.getName());
    }

    @ZenMethod
    public static void remove(String recipeName)
    {
        if (!AnvilRecipe.recipes.containsKey(recipeName))
        {
            CraftTweakerAPI.logInfo("[" + name + "] failed removing recipe " + recipeName + " because it does not exist");
            return;
        }

        AnvilRecipe.recipes.remove(recipeName);
    }

    @ZenMethod
    public static void add(String recipeName, IItemStack output, int swings, IIngredient print, IIngredient input, IIngredient extra, IItemStack... progress)
    {
        if (!AnvilRecipe.recipes.containsKey(recipeName))
        {
            CraftTweakerAPI.logError("[" + name + "] skipping duplicate recipe name: " + recipeName);
            return;
        }

        ItemStack out = convertToItemStack(output);
        if (out.isEmpty())
        {
            CraftTweakerAPI.logError("[" + name + "] skipping invalid anvil recipe without output");
            return;
        }
        if (swings <= 0)
        {
            CraftTweakerAPI.logError("[" + name + "] skipping invalid anvil recipe with no hammerswings: " + out.getDisplayName());
            return;
        }

        ItemHandle inputHandle = convertToHandle(input);
        if (inputHandle == null)
        {
            CraftTweakerAPI.logError("[" + name + "] skipping invalid anvil recipe without input:" + out.getDisplayName());
            return;
        }

        List<ItemStack> progressStacks = new ArrayList<>();
        for (IItemStack prog : progress) {
            progressStacks.add(convertToItemStack(prog));
        }

        AnvilRecipe.recipes.put(recipeName, new AnvilRecipe(convertToItemStack(output), swings, convertToHandle(print), inputHandle, convertToHandle(extra), progressStacks.toArray()));
    }
}
