package com.camellias.mysticalmetallurgy.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IMysticalItem
{
    String NBT_COOLINGTIME = "cooling_time";

    int getCoolingTime();

    default int getRemainingCoolingTime(ItemStack stack)
    {
        if (stack.isEmpty()) return 0;

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(IMysticalItem.NBT_COOLINGTIME))
            return nbt.getInteger(IMysticalItem.NBT_COOLINGTIME);
        return 0;
    }

    default void setRemainingCoolingTime(ItemStack stack, int coolingTime)
    {
        if (stack.isEmpty()) return;

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(IMysticalItem.NBT_COOLINGTIME))
            nbt.setInteger(IMysticalItem.NBT_COOLINGTIME, coolingTime);
    }
}
