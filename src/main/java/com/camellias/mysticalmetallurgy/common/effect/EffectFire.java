package com.camellias.mysticalmetallurgy.common.effect;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.api.effect.Effect;
import net.minecraft.util.ResourceLocation;

public class EffectFire extends Effect
{
    public static final ResourceLocation ID = new ResourceLocation(Main.MODID, "fire");

    public EffectFire()
    {
        super(ID);
    }

    @Override
    public int getMaxLevel()
    {
        return 1;
    }
}
