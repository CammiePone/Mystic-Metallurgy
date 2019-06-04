package com.camellias.mysticalmetallurgy.library.utils;

import com.camellias.mysticalmetallurgy.api.ConfigValues;
import com.camellias.mysticalmetallurgy.common.capability.HotCarry.CapHotCarry;
import com.camellias.mysticalmetallurgy.common.capability.HotCarry.IHotCarry;
import com.camellias.mysticalmetallurgy.common.capability.HotItem.CapHotStack;
import com.camellias.mysticalmetallurgy.common.capability.HotItem.IHotStack;
import com.camellias.mysticalmetallurgy.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.IItemHandler;

@SuppressWarnings("ConstantConditions")
public class HotUtils
{
    public static DamageSource hotItem = new DamageSource("hotItem").setFireDamage();

    public static void hotItemCheck(final EntityPlayer player)
    {
        if (player.world.isRemote)
            return;

        // Exit early if creative mode.
        if (player.isCreative())
            return;

        if (ConfigValues.Heat.HotHandling == ConfigValues.HotCheckType.DISABLED)
            return;

        if (player.isImmuneToFire())
            return;

        if (player.world.getTotalWorldTime() % 20 != 0)
            return;

        IItemHandler itemHandler = ItemUtils.getItemHandler(player);

        ItemStack gloves = getGlovesStack(player, itemHandler);
        IHotCarry hotCarry = player.getCapability(CapHotCarry.HOT_CARRY_CAPABILITY, null);


        int hotSlot = getHotSlot(itemHandler);
        if (hotSlot < 0 || !gloves.isEmpty())
        {
            hotCarry.carryCool();
            if (!gloves.isEmpty())
                gloves.damageItem(1, player);
        }
        else
        { //when we dont have heat gloves we take damage and eventually drop the hot item
            hotCarry.carryHot();
            if (shouldDropHotItem(hotCarry.getHotCarryTime()))
            {
                ItemStack hotStack = itemHandler.extractItem(hotSlot, Integer.MAX_VALUE, false);
                player.dropItem(hotStack, false);
                player.sendMessage(new TextComponentTranslation("message.mysticalmetallurgy.toohot"));
            }

            player.attackEntityFrom(hotItem, 0.5F);
        }
    }

    public static ItemStack getGlovesStack(EntityPlayer player, IItemHandler itemHandler)
    {
        ItemStack gloves = ItemStack.EMPTY;
        switch (ConfigValues.Heat.HotHandling)
        {
            case HAND:
                gloves = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
                if (gloves.isEmpty() || gloves.getItem() != ModItems.GLOVES)
                    gloves = player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
                break;
            case HOTBAR:
                for (int i = 0; i < 9; i++)
                {
                    gloves = itemHandler.getStackInSlot(i);
                    if (gloves.getItem() == ModItems.GLOVES)
                        break;
                }
                break;
            case INVENTORY:
                for (int i = 0; i < itemHandler.getSlots(); i++)
                {
                    gloves = itemHandler.getStackInSlot(i);
                    if (gloves.getItem() == ModItems.GLOVES)
                        break;
                }
                break;
        }

        return gloves.getItem() == ModItems.GLOVES ? gloves : ItemStack.EMPTY;
    }

    public static int getHotSlot(IItemHandler itemHandler)
    {
        for (int i = 0; i< itemHandler.getSlots(); i++)
        {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (isItemHot(stack))
                return i;
        }

        return -1;
    }

    public static boolean isItemHot(ItemStack stack)
    {
        FluidStack fluid = FluidUtil.getFluidContained(stack);
        if (fluid != null && fluid.getFluid().getTemperature() > ConfigValues.Heat.TempConsideredCold)
            return true;
        else
        {
            IHotStack hotStack = getHotStackCap(stack);
            if (hotStack != null)
                return hotStack.isHot();
        }

        return false;
    }

    public static void makeHot(ItemStack stack, int temp)
    {
        IHotStack hotStack = getHotStackCap(stack);
        if(hotStack != null)
            hotStack.setTemp(temp);
    }

    public static IHotStack getHotStackCap(ItemStack stack)
    {
        return stack.getCapability(CapHotStack.HOT_STACK_CAPABILITY, null);
    }

    public static boolean shouldDropHotItem(int carryTime)
    {
        return ConfigValues.Heat.DropHotAfterSec > 0 && carryTime > ConfigValues.Heat.DropHotAfterSec;
    }
}
