package com.camellias.mysticalmetallurgy.api.effect;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Trait implements INBTSerializable<NBTTagCompound>
{
    private static final String NBT_TRAITS = "mystic_traits";

    private static final String NBT_EFFECT = "effect_id";
    private static final String NBT_LEVEL = "effect_level";

    ResourceLocation effect;
    private int priority;
    public int level;

    Trait(ResourceLocation effect, int level)
    {
        this.effect = effect;
        this.level = level;
        this.priority = Effect.getEffect(effect).getPriority();
    }

    private Trait(NBTTagCompound nbt) { deserializeNBT(nbt); }

    public Effect getEffect() { return Effect.getEffect(effect); }

    public int getPriority()
    {
        return priority;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == this) return true;
        if (!(other instanceof Trait))return false;
        Trait otherMyClass = (Trait)other;
        return otherMyClass.effect.equals(effect);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(NBT_EFFECT, effect.toString());
        tag.setInteger(NBT_LEVEL, level);
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        effect = new ResourceLocation(nbt.getString(NBT_EFFECT));
        level = nbt.getInteger(NBT_LEVEL);
    }

    @Nonnull
    public static List<Trait> fromNBT(@Nonnull NBTTagCompound tag)
    {
        List<Trait> list = new ArrayList<>();
        if (tag.hasKey(NBT_TRAITS))
        {
            NBTTagList tagList = (NBTTagList)tag.getTag(NBT_TRAITS);
            tagList.forEach(nbt ->
                    list.add(new Trait((NBTTagCompound) nbt)));

            list.sort(Comparator.comparing(Trait::getPriority).reversed());
        }
        return list;
    }

    public static void toNBT(@Nonnull NBTTagCompound tag, @Nonnull List<Trait> effects)
    {
        NBTTagList tagList = new NBTTagList();
        for (Trait trait : effects)
            tagList.appendTag(trait.serializeNBT());

        tag.setTag(NBT_TRAITS, tagList);
    }

    public static List<Trait> combine(List<Trait> traits, int diminish)
    {
        traits.sort((o1, o2) -> Integer.compare(o2.level, o1.level));

        List<Trait> combinedEffects = new ArrayList<>();
        for (Trait pair : traits)
        {
            if (!combinedEffects.contains(pair))
            {
                int freq = Collections.frequency(traits, pair);
                int newLevel = freq > 1 ? pair.level + freq - 1 : pair.level - diminish;
                if (newLevel > 0)
                    combinedEffects.add(new Trait(pair.effect, newLevel));
            }
        }

        return combinedEffects;
    }
}

