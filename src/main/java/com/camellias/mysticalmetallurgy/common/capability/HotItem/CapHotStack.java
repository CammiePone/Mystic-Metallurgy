package com.camellias.mysticalmetallurgy.common.capability.HotItem;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.api.ConfigValues;
import com.camellias.mysticalmetallurgy.api.IMysticalItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
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
public class CapHotStack implements ICapabilitySerializable<NBTBase>
{
    private static final ResourceLocation HOT_STACK_CAPLOC = new ResourceLocation(Main.MODID, "hotstack");

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
                        NBTTagCompound tag = new NBTTagCompound();
                        tag.setInteger("temp", instance.getTemp());
                        tag.setBoolean("canCool", instance.canCool());
                        return tag;
                    }

                    @Override
                    public void readNBT(Capability<IHotStack> capability, IHotStack instance, EnumFacing side, NBTBase nbt)
                    {
                        instance.setTemp(((NBTTagCompound)nbt).getInteger("temp"));
                        instance.setCanCool(((NBTTagCompound)nbt).getBoolean("canCool"));
                    }
                },
                IHotStack.Impl::new);
    }

    @SubscribeEvent
    public static void attachStackCapability(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack stack = event.getObject();
        if (stack.getItem() instanceof IMysticalItem)
            event.addCapability(CapHotStack.HOT_STACK_CAPLOC, new CapHotStack());
        else
        {
            for (ItemStack stack2 : ConfigValues.Heat.HotItemStacks)
            {
                if (stack.isItemEqual(stack2))
                {
                    CapHotStack cap = new CapHotStack();
                    cap.instance.setTemp(350);
                    cap.instance.setCanCool(false);
                    event.addCapability(CapHotStack.HOT_STACK_CAPLOC, cap);
                }
            }
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing)
    {
        return capability == HOT_STACK_CAPABILITY;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing)
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
