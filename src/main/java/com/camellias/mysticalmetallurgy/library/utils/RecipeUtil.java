package com.camellias.mysticalmetallurgy.library.utils;

import com.camellias.mysticalmetallurgy.library.ItemHandle;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipeUtil
{
    public static ItemHandle getHandleFromObject(Object item)
    {
        if (item instanceof ItemStack)
            return new ItemHandle((ItemStack) item);
        else if (item instanceof Item)
            return new ItemHandle(new ItemStack((Item) item));
        else if (item instanceof Block)
            return new ItemHandle(new ItemStack((Block) item));
        else if (item instanceof String)
            return new ItemHandle((String) item);

        throw new IllegalArgumentException("Unknown type for ingredient parsing");
    }
}
