package com.camellias.mysticalmetallurgy.common.fluid;

import com.camellias.mysticalmetallurgy.Main;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class MythicFluid extends Fluid
{
    public static final ResourceLocation ID = new ResourceLocation(Main.MODID, "mythical_fluid");

    public MythicFluid(String fluidName, ResourceLocation still, ResourceLocation flowing)
    {
        super(fluidName, still, flowing);
    }
}
