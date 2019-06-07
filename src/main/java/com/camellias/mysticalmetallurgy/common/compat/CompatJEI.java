package com.camellias.mysticalmetallurgy.common.compat;

import com.camellias.mysticalmetallurgy.api.recipe.AnvilRecipe;
import com.camellias.mysticalmetallurgy.common.compat.jei.anvil.CategoryAnvil;
import com.camellias.mysticalmetallurgy.common.compat.jei.anvil.WrapperAnvil;
import com.camellias.mysticalmetallurgy.init.ModBlocks;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class CompatJEI implements IModPlugin {

    public static final String AnvilCatId = "mysticalmetallurgy.anvil";

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();

        registry.addRecipeCategories(new CategoryAnvil(helper));
    }

    @Override
    public void register(IModRegistry registry)
    {
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.STONE_ANVIL), AnvilCatId);
        registry.handleRecipes(AnvilRecipe.class, WrapperAnvil::new, AnvilCatId);
        registry.addRecipes(AnvilRecipe.recipes.values(), AnvilCatId);

        hideItems(registry.getJeiHelpers().getIngredientBlacklist());
    }


    private void hideItems(IIngredientBlacklist blacklist) {
        //blacklist.addIngredientToBlacklist(new ItemStack());
    }
}
