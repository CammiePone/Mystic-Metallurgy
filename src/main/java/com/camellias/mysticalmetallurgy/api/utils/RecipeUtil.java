package com.camellias.mysticalmetallurgy.api.utils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collections;
import java.util.List;

public class RecipeUtil
{
    public static List<ItemStack> getStacksFromObject(Object item)
    {
        List<ItemStack> result = null;
        if (item instanceof ItemStack)
        {
            ItemStack stack = (ItemStack) item;
            int stackSize = stack.getCount();
            stack.setCount(1);
            for (int i = 0; i < stackSize; i++)
                result = Collections.singletonList(stack);
        }
        else if (item instanceof Item)
            result = Collections.singletonList(new ItemStack((Item) item));
        else if (item instanceof Block)
            result = Collections.singletonList(new ItemStack((Block) item));
        else if (item instanceof String)
        {
            List<ItemStack> oreDicts = OreDictionary.getOres((String) item);
            if (oreDicts == null || oreDicts.size() <= 0)
                oreDicts = null;
            result = oreDicts;
        }
        return result;
    }
}
