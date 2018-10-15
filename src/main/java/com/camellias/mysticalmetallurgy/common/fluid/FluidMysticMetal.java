package com.camellias.mysticalmetallurgy.common.fluid;

import com.camellias.mysticalmetallurgy.Main;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidMysticMetal extends Fluid
{
    public static final ResourceLocation ID = new ResourceLocation(Main.MODID, "mystical_liquid_metal");
    public static final ResourceLocation STILL = new ResourceLocation(Main.MODID, "blocks/mystic_metal_still");
    public static final ResourceLocation FLOW = new ResourceLocation(Main.MODID, "blocks/mystic_metal_flow");

    public FluidMysticMetal(String fluidName)
    {
        super(fluidName, STILL, FLOW);

        setDensity(2000); // thicker than a bowl of oatmeal
        setViscosity(10000); // sloooow moving
        setTemperature(1000); // not exactly lava, but still hot. Should depend on the material
        setLuminosity(10); // glowy by default!

        // rare by default
        setRarity(EnumRarity.UNCOMMON);
    }

    @Override
    public int getColor()
    {
        return super.getColor();//0x551A8B;//super.getColor();
    }
}
