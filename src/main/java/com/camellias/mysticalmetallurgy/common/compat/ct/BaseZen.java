package com.camellias.mysticalmetallurgy.common.compat.ct;

import com.camellias.mysticalmetallurgy.library.ItemHandle;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BaseZen {

    @Nonnull
    public static ItemStack convertToItemStack(IItemStack stack) {
        return CraftTweakerMC.getItemStack(stack);
    }


    @Nullable
    public static ItemHandle convertToHandle(IIngredient obj) {
        if(obj == null) {
            return null;
        }
        if(obj instanceof IItemStack) {
            ItemStack ret = convertToItemStack((IItemStack) obj);
            if(ret.isEmpty()) return null;
            return new ItemHandle(ret);
//        } else if(obj instanceof ILiquidStack) {
//            FluidStack ret = convertToFluidStack((ILiquidStack) obj, true);
//            if (ret == null) return null;
//            return new ItemHandle(ret);
        } else if(obj instanceof IOreDictEntry) {
            return new ItemHandle(((IOreDictEntry) obj).getName());
        } else {
            NonNullList<ItemStack> stacks = NonNullList.create();
            for (IItemStack in : obj.getItems()) {
                ItemStack real = convertToItemStack(in);
                if (!real.isEmpty()) {
                    stacks.add(real);
                }
            }
            return new ItemHandle(stacks);
        }
    }
}
