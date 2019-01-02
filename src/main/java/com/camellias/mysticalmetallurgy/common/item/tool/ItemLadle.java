package com.camellias.mysticalmetallurgy.common.item.tool;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.api.Effect;
import com.camellias.mysticalmetallurgy.common.effect.EffectHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemLadle extends ItemFluidContainer
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "ladle");
    public static final int CAPACITY = 144;

    public ItemLadle()
    {
        super(CAPACITY);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        IFluidHandler handler = FluidUtil.getFluidHandler(stack);
        assert handler != null;

        IFluidTankProperties props = handler.getTankProperties()[0];
        if (props.getContents() != null && props.getContents().tag != null)
        {
            for (EffectHandler.EffectLevelPair effectPair : EffectHandler.readEffectsFromNBT(props.getContents().tag))
            {
                tooltip.add(String.format("%s %d",Effect.getEffect(effectPair.effect).getAttributeInfo(), effectPair.level));
            }
        }
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack)
    {
        IFluidHandler handler = FluidUtil.getFluidHandler(stack);
        assert handler != null;

        IFluidTankProperties props = handler.getTankProperties()[0];
        String itemStackDisplayName = super.getItemStackDisplayName(stack);
        if (props.getContents() != null)
            itemStackDisplayName += " " + props.getContents().getLocalizedName();

        return itemStackDisplayName;
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, NBTTagCompound nbt) {
        return new FluidHandlerItemStack(stack, capacity) {
            @Override
            public int fill(FluidStack resource, boolean doFill) {
                if (container.getCount() != 1 || resource == null || resource.amount < capacity || getFluid() != null || !canFillFluidType(resource))
                    return 0;

                if (doFill)
                {
                    FluidStack filled = resource.copy();
                    filled.amount = 144;
                    setFluid(filled);
                }

                return capacity;
            }

            @Nullable
            @Override
            public FluidStack drain(FluidStack resource, boolean doDrain)
            {
                if (resource == null || resource.amount < capacity)
                    return null;

                FluidStack fluidStack = getFluid();
                if (fluidStack != null && fluidStack.isFluidEqual(resource)) {
                    if (doDrain)
                        setContainerToEmpty();
                    return fluidStack;
                }

                return null;
            }

            @Override
            public FluidStack drain(int maxDrain, boolean doDrain) {
                if (container.getCount() != 1 || maxDrain <= 0)
                    return null;

                FluidStack contained = getFluid();
                if (contained == null || contained.amount <= 0 || !canDrainFluidType(contained))
                    return null;
                if (maxDrain < capacity)
                    return null;

                final int drainAmount = Math.min(contained.amount, maxDrain);

                FluidStack drained = contained.copy();
                drained.amount = drainAmount;

                if (doDrain) {
                    contained.amount -= drainAmount;
                    if (contained.amount == 0)
                        setContainerToEmpty();
                    else
                        setFluid(contained);
                }

                return drained;
            }
        };
    }
}
