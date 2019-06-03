package com.camellias.mysticalmetallurgy.common.capability.HotCarry;

import com.camellias.mysticalmetallurgy.Main;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;

public class CapHotCarry implements ICapabilitySerializable<NBTBase>
{
    public static final ResourceLocation HOT_CARRY_CAPLOC = new ResourceLocation(Main.MODID, "hotcarry");

    @CapabilityInject(IHotCarry.class)
    public static final Capability<IHotCarry> HOT_CARRY_CAPABILITY = null;

    private IHotCarry instance = HOT_CARRY_CAPABILITY.getDefaultInstance();

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IHotCarry.class, new Capability.IStorage<IHotCarry>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IHotCarry> capability, IHotCarry instance, EnumFacing side)
            {
                return new NBTTagInt(instance.getHotCarryTime());
            }

            @Override
            public void readNBT(Capability<IHotCarry> capability, IHotCarry instance, EnumFacing side, NBTBase nbt)
            {
                instance.set(((NBTTagInt)nbt).getInt());
            }
        },
                IHotCarry.Impl::new);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == HOT_CARRY_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == HOT_CARRY_CAPABILITY ? HOT_CARRY_CAPABILITY.cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT()
    {
        return HOT_CARRY_CAPABILITY.getStorage().writeNBT(HOT_CARRY_CAPABILITY, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        HOT_CARRY_CAPABILITY.getStorage().readNBT(HOT_CARRY_CAPABILITY, this.instance, null, nbt);
    }
}
