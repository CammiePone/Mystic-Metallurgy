package com.camellias.mysticalmetallurgy.api.recipe;

import com.camellias.mysticalmetallurgy.api.IMysticalItem;
import com.camellias.mysticalmetallurgy.api.effect.Effect;
import com.camellias.mysticalmetallurgy.api.effect.EffectLinker;
import com.camellias.mysticalmetallurgy.api.effect.Trait;
import com.camellias.mysticalmetallurgy.common.item.tool.ItemMysticalTool;
import com.camellias.mysticalmetallurgy.library.utils.RecipeUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AnvilRecipe
{
    private ItemStack result;
    private int swings;

    private ItemStack printStack;
    private List<ItemStack> inputStack;
    private List<ItemStack> extraStack;
    private List<ItemStack> progressStack = new ArrayList<>();

    public AnvilRecipe(@Nonnull ItemStack craftResult, int hammerSwings, @Nullable Object print, @Nonnull Object input, @Nullable Object extra, Object... progress)
    {
        if (craftResult.isEmpty())
            throw new IllegalArgumentException("invalid stone anvil recipe without output");
        result = craftResult;
        if (hammerSwings <= 0)
            throw new IllegalArgumentException("invalid stone anvil recipe with no hammerswings: " + craftResult.getDisplayName());
        swings = hammerSwings;

        List<ItemStack> temp = RecipeUtil.getStacksFromObject(print);
        if (temp == null || temp.size() <= 0)
            printStack = ItemStack.EMPTY;
        else if (temp.size() > 1)
            throw new IllegalArgumentException("invalid input for print ingredient in anvil recipe: " + craftResult.getDisplayName());
        else
        {
            printStack = temp.get(0).copy();
            printStack.setCount(1);
        }

        temp = RecipeUtil.getStacksFromObject(input);
        if (temp == null || temp.size() <= 0)
            throw new IllegalArgumentException("invalid second input ingredient in anvil recipe: " + craftResult.getDisplayName());
        else
        {
            inputStack = temp;
            inputStack.forEach(stack -> stack.setCount(1));
        }

        temp = RecipeUtil.getStacksFromObject(extra);
        if (temp == null || temp.size() <= 0)
            extraStack = new ArrayList<>();
        else
        {
            extraStack = temp;
            extraStack.forEach(stack -> stack.setCount(1));
        }

        for (Object prog : progress)
        {
            List<ItemStack> l = RecipeUtil.getStacksFromObject(prog);
            if (l != null && l.size() > 0)
                progressStack.add(l.get(0));
        }
    }

    public ItemStack getResult() { return result.copy(); }

    public int getSwings() { return swings; }
    public ItemStack getSwingStack(int swing)
    {
        if (swing >= progressStack.size())
            swing = progressStack.size() - 1;
        return swing < 0 ? result : progressStack.get(swing);
    }

    public boolean match(ItemStack print, ItemStack input, ItemStack extra)
    {
        if (!printStack.isItemEqual(print))
            return false;

        boolean contains = false;
        for (ItemStack stack : inputStack)
        {
            if (stack.isItemEqual(input))
            {
                contains = true;
                break;
            }
        }

        if (!contains)
            return false;

        contains = false;
        if (extraStack.size() == 0)
            contains = extra.isEmpty();
        else
        {
            for (ItemStack stack : extraStack)
            {
                if (stack.isItemEqual(extra))
                {
                    contains = true;
                    break;
                }
            }
        }

        return contains;
    }

    private static List<AnvilRecipe> recipes = new ArrayList<>();
    public static void register(AnvilRecipe recipe)
    {
        recipes.add(recipe);
    }

    @Nullable
    public static AnvilRecipe getMatching(@Nonnull ItemStack print, @Nonnull ItemStack input, @Nonnull ItemStack extra)
    {
        for (AnvilRecipe recipe : recipes)
            if (recipe.match(print, input, extra))
                return recipe;
        return null;
    }

    public ItemStack craft(@Nonnull ItemStack input, @Nonnull ItemStack extra)
    {
        ItemStack res = getResult();
        if (res.getItem() instanceof IMysticalItem)
        {
            NBTTagCompound nbtResult = new NBTTagCompound();

            int diminish = 0;
            List<Trait> traits = new ArrayList<>();
            NBTTagCompound nbtIn = input.getTagCompound();
            if (nbtIn != null)
                traits.addAll(Trait.fromNBT(nbtIn));

            NBTTagCompound nbtExtra = extra.getTagCompound();
            if (nbtExtra != null)
            {
                int cnt = traits.size();
                traits.addAll(Trait.fromNBT(nbtExtra));
                if (cnt > 0 && traits.size() > cnt)
                    diminish = 1;
            }

            Trait.toNBT(nbtResult, Trait.combine(traits, diminish));
            res.setTagCompound(nbtResult);
        }

        return res;
    }
}
