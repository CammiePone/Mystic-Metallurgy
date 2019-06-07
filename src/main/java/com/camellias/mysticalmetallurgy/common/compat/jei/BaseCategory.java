package com.camellias.mysticalmetallurgy.common.compat.jei;

import com.camellias.mysticalmetallurgy.Main;
import com.google.common.collect.Lists;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class BaseCategory<T extends IRecipeWrapper> implements IRecipeCategory<T> {

    private final String locTitle, uid;

    public BaseCategory(String unlocTitle, String uid) {
        this.locTitle = I18n.format(unlocTitle);
        this.uid = uid;
    }

    @Nonnull
    @Override
    public String getModName() {
        return Main.NAME;
    }

    @Nonnull
    @Override
    public String getUid() {
        return uid;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return locTitle;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Nonnull
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Lists.newArrayList();
    }

}