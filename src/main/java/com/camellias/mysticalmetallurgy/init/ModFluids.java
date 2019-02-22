package com.camellias.mysticalmetallurgy.init;

import com.camellias.mysticalmetallurgy.common.fluid.FluidMysticMetal;
import net.minecraft.fluid.FlowingFluid;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("mysticalmetallurgy")
public class ModFluids
{
    public static final FlowingFluid MYSTICAL_METAL = new FluidMysticMetal.Source();
    public static final FlowingFluid MYSTICAL_METAL_FLOWING = new FluidMysticMetal.Flowing();
}
