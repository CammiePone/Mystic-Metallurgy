package com.camellias.mysticalmetallurgy.common.compat.jei.anvil;

import com.camellias.mysticalmetallurgy.api.recipe.AnvilRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class WrapperAnvil implements IRecipeWrapper {

    private final AnvilRecipe recipe;

    public WrapperAnvil(AnvilRecipe recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        List<List<ItemStack>> inputs = new ArrayList<>(3);

        inputs.add(recipe.printHandle.getApplicableItems());
        inputs.add(recipe.inputHandle.getApplicableItems());
        inputs.add(recipe.extraHandle.getApplicableItems());
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);

        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }
}
