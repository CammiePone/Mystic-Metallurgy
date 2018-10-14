package com.camellias.mysticalmetallurgy.api;

import com.camellias.mysticalmetallurgy.Main;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Effect extends IForgeRegistryEntry.Impl<Effect> implements INBTSerializable<NBTTagCompound>
{
    public static final int MAX_ID = Integer.MAX_VALUE - 1;
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(Main.MODID, "effects");
    private static IForgeRegistry<Effect> REGISTRY = null;

    public static IForgeRegistry<Effect> getRegistry()
    {
        if (REGISTRY == null)
            REGISTRY = GameRegistry.findRegistry(Effect.class);
        return REGISTRY;
    }

    public Effect(@Nonnull ResourceLocation id)
    {
        setRegistryName(id);
    }
    
    @Nonnull
    public String getAttributeInfo()
    {
        return I18n.format("effect." + getRegistryName().toString().replace(":", ".") + ".name");
    }
    
    public abstract int getMaxLevel();

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT()
    {
        return new NBTTagCompound();
    }
    
    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
    	
    }
    
    @Nullable
    public static Effect getEffect(ResourceLocation id)
    {
        if (exists(id))
            return getRegistry().getValue(id);
        return null;
    }
    
    public static boolean exists(ResourceLocation id)
    {
        return getRegistry().containsKey(id);
    }
}
