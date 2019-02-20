package com.camellias.mysticalmetallurgy.common.item.tool;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.api.effect.Trait;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
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
import java.util.Properties;

public class ItemLadle extends ItemFluidContainer
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "ladle");
    public static final int CAPACITY = 144;

    public ItemLadle(Properties props)
    {
        super(props, CAPACITY);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        IFluidHandler handler = FluidUtil.getFluidHandler(stack).orElse(null);
        assert handler != null;

        IFluidTankProperties props = handler.getTankProperties()[0];
        if (props.getContents() != null && props.getContents().tag != null)
        {
            for (Trait trait : Trait.fromNBT(props.getContents().tag))
                tooltip.add(new TextComponentString(String.format("%s %d", trait.getEffect().getAttributeInfo(), trait.level)));
        }
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(@Nonnull ItemStack stack)
    {
        IFluidHandler handler = FluidUtil.getFluidHandler(stack).orElse(null);
        assert handler != null;

        IFluidTankProperties props = handler.getTankProperties()[0];
        ITextComponent displayName = super.getDisplayName(stack);
        if (props.getContents() != null)
            displayName.appendText(" " + props.getContents().getLocalizedName());

        return displayName;
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, NBTTagCompound nbt)
    {
        return new FluidHandlerItemStack(stack, capacity)
        {
            @Override
            public int fill(FluidStack resource, boolean doFill)
            {
                if (container.getCount() != 1 || resource == null || resource.amount < capacity || getFluid() != null || !canFillFluidType(resource))
                    return 0;

                if (doFill)
                {
                    FluidStack filled = resource.copy();
                    filled.amount = CAPACITY;
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
                if (fluidStack != null && fluidStack.isFluidEqual(resource))
                {
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

                if (doDrain)
                {
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
