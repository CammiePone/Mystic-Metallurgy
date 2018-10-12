package com.camellias.mysticalmetallurgy.common.effect;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.api.Effect;
import net.minecraft.util.ResourceLocation;

public class EffectDense extends Effect
{
    public static final ResourceLocation ID = new ResourceLocation(Main.MODID, "dense");

    public EffectDense()
    {
        super(ID);
    }

    @Override
    public int getMaxLevel()
    {
        return 0;
    }
}
