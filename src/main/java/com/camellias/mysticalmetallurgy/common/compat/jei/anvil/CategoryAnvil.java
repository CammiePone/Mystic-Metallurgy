package com.camellias.mysticalmetallurgy.common.compat.jei.anvil;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.common.compat.CompatJEI;
import com.camellias.mysticalmetallurgy.common.compat.jei.BaseCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class CategoryAnvil extends BaseCategory<WrapperAnvil> {

    private final IDrawable background;

    public CategoryAnvil(IGuiHelper guiHelper) {
        super("jei.category.anvil", CompatJEI.AnvilCatId);

        ResourceLocation location = new ResourceLocation(Main.MODID, "textures/gui/jei/recipeAnvil.png");

        background = guiHelper.createDrawable(location, 0,0, 116, 162);
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull WrapperAnvil recipeWrapper, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, false, 48, 18);

        group.init(1, true, 30, 76);
        group.init(2, true, 49, 76);
        group.init(3, true, 68, 76);

        group.set(ingredients);
    }
}
