package com.camellias.mysticalmetallurgy.api.recipe;

import com.camellias.mysticalmetallurgy.api.IMysticalItem;
import com.camellias.mysticalmetallurgy.api.effect.Trait;
import com.camellias.mysticalmetallurgy.library.ItemHandle;
import com.camellias.mysticalmetallurgy.library.utils.HotUtils;
import com.camellias.mysticalmetallurgy.library.utils.RecipeUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnvilRecipe
{
    private ItemStack result;
    private int swings;

    public ItemHandle printHandle;
    public ItemHandle inputHandle;
    public ItemHandle extraHandle;
    private List<ItemStack> progressStack = new ArrayList<>();

    public AnvilRecipe(@Nonnull ItemStack craftResult, int hammerSwings, @Nullable ItemHandle print, @Nonnull ItemHandle input, @Nullable ItemHandle extra, ItemStack... progress)
    {
        if (craftResult.isEmpty())
            throw new IllegalArgumentException("invalid anvil recipe without output");
        result = craftResult;
        if (hammerSwings <= 0)
            throw new IllegalArgumentException("invalid anvil recipe with no hammerswings: " + craftResult.getDisplayName());
        swings = hammerSwings;

        printHandle = print;
        inputHandle = input;
        extraHandle = extra;

        if (progress != null)
            progressStack = Arrays.asList(progress);
    }

    public AnvilRecipe(@Nonnull ItemStack craftResult, int hammerSwings, @Nullable Object print, @Nonnull Object input, @Nullable Object extra, Object... progress)
    {
        if (craftResult.isEmpty())
            throw new IllegalArgumentException("invalid anvil recipe: no output set");
        result = craftResult;
        if (hammerSwings <= 0)
            throw new IllegalArgumentException("invalid anvil recipe: no hammer swings set");
        swings = hammerSwings;

        printHandle = RecipeUtil.getHandleFromObject(print);
        inputHandle = RecipeUtil.getHandleFromObject(input);
        extraHandle = RecipeUtil.getHandleFromObject(extra);

        if (progress != null)
        {
            for (Object prog : progress)
            {
                ItemHandle progressHandle = RecipeUtil.getHandleFromObject(prog);
                if (progressHandle.handleType != ItemHandle.Type.STACK)
                    throw new IllegalArgumentException("invalid anvil recipe: invalid type for progress items (valid: BLOCKS, ITEMS, ITEMSTACKS)");
                progressStack.add(progressHandle.getApplicableItems().get(0));
            }
        }
    }

    public ItemStack getOutput() { return result.copy(); }

    public int getSwings() { return swings; }
    public ItemStack getSwingStack(int swing)
    {
        if (swing >= progressStack.size())
            swing = progressStack.size() - 1;
        return swing < 0 ? result : progressStack.get(swing);
    }

    public boolean match(ItemStack print, ItemStack input, ItemStack extra)
    {
        if (printHandle != null && !printHandle.matchCrafting(print))
            return false;

        if (!inputHandle.matchCrafting(input))
            return false;

        if (extraHandle != null && !extraHandle.matchCrafting(extra))
            return false;

        return true;
    }

    public static Map<String, AnvilRecipe> recipes = new HashMap<>();

    @Nullable
    public static AnvilRecipe getMatching(@Nonnull ItemStack print, @Nonnull ItemStack input, @Nonnull ItemStack extra)
    {
        for (AnvilRecipe recipe : recipes.values())
            if (recipe.match(print, input, extra))
                return recipe;
        return null;
    }

    public ItemStack craft(@Nonnull ItemStack input, @Nonnull ItemStack extra)
    {
        ItemStack res = getOutput();
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
            HotUtils.makeHot(res, 400);
        }

        return res;
    }
}
