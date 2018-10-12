package com.camellias.mysticalmetallurgy.api;

import com.camellias.mysticalmetallurgy.Main;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Effect extends IForgeRegistryEntry.Impl<Effect> implements INBTSerializable<NBTTagCompound>
{
    public static final int MAX_ID = Integer.MAX_VALUE;
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(Main.MODID, "effects");
    public static final RegistryNamespacedDefaultedByKey<ResourceLocation, Effect> REGISTRY = GameData.getWrapperDefaulted(Effect.class);
    
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
            return REGISTRY.getObject(id);
        return null;
    }
    
    public static boolean exists(ResourceLocation id)
    {
        return REGISTRY.containsKey(id);
    }
}
