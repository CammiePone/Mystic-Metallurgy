package com.camellias.mysticalmetallurgy.api.recipe;

import com.camellias.mysticalmetallurgy.api.utils.RecipeUtil;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AnvilRecipe
{
    private ItemStack result;
    private int swings;
    private final List<List<ItemStack>> recipe = new ArrayList<>();

    ItemStack printStack;
    List<ItemStack> inputStack;
    List<ItemStack> extraStack;

    public AnvilRecipe(ItemStack craftResult, int hammerSwings, @Nullable Object print, Object input, Object extra)
    {
        result = craftResult;
        swings = hammerSwings;

        List<ItemStack> temp = RecipeUtil.getStacksFromObject(print);
        if (temp == null || temp.size() <= 0)
            printStack = ItemStack.EMPTY;
        else if (temp.size() > 1)
            throw new IllegalArgumentException("invalid input for print ingredient in anvil recipe: " + craftResult.getDisplayName());
        else
        {
            printStack = temp.get(0);
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
            extraStack = null;
        else
        {
            extraStack = temp;
            extraStack.forEach(stack -> stack.setCount(1));
        }
    }

}
