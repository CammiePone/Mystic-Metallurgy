package com.camellias.mysticalmetallurgy.common.capability.HotCarry;

import com.camellias.mysticalmetallurgy.Main;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
@Mod.EventBusSubscriber
public class CapHotCarry implements ICapabilitySerializable<NBTBase>
{
    private static final ResourceLocation HOT_CARRY_CAPLOC = new ResourceLocation(Main.MODID, "hotcarry");

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

    @SubscribeEvent
    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if (!(event.getObject() instanceof EntityPlayer)) return;

        event.addCapability(CapHotCarry.HOT_CARRY_CAPLOC, new CapHotCarry());
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing)
    {
        return capability == HOT_CARRY_CAPABILITY;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing)
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
