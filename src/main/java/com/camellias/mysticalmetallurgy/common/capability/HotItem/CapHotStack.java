package com.camellias.mysticalmetallurgy.common.capability.HotItem;

import com.camellias.mysticalmetallurgy.Main;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;

public class CapHotStack implements ICapabilitySerializable<NBTBase>
{
    public static final ResourceLocation HOT_STACK_CAPLOC = new ResourceLocation(Main.MODID, "hotstack");

    @CapabilityInject(IHotStack.class)
    public static final Capability<IHotStack> HOT_STACK_CAPABILITY = null;

    private IHotStack instance = HOT_STACK_CAPABILITY.getDefaultInstance();

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IHotStack.class, new Capability.IStorage<IHotStack>()
                {
                    @Nullable
                    @Override
                    public NBTBase writeNBT(Capability<IHotStack> capability, IHotStack instance, EnumFacing side)
                    {
                        return new NBTTagShort((short) (instance.isHot() ? 0 : 1));
                    }

                    @Override
                    public void readNBT(Capability<IHotStack> capability, IHotStack instance, EnumFacing side, NBTBase nbt)
                    {
                        if (((NBTTagShort)nbt).getShort() == 0)
                            instance.setCold();
                        else
                            instance.setHot();
                    }
                },
                IHotStack.Impl::new);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == HOT_STACK_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == HOT_STACK_CAPABILITY ? HOT_STACK_CAPABILITY.cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT()
    {
        return HOT_STACK_CAPABILITY.getStorage().writeNBT(HOT_STACK_CAPABILITY, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        HOT_STACK_CAPABILITY.getStorage().readNBT(HOT_STACK_CAPABILITY, this.instance, null, nbt);
    }
}
