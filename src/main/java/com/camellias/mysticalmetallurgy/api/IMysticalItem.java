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

        NBTTagCompound nbt = stack.getTag();
        if (nbt != null && nbt.hasUniqueId(IMysticalItem.NBT_COOLINGTIME))
            return nbt.getInt(IMysticalItem.NBT_COOLINGTIME);
        return 0;
    }

    default void setRemainingCoolingTime(ItemStack stack, int coolingTime)
    {
        if (stack.isEmpty()) return;

        NBTTagCompound nbt = stack.getTag();
        if (nbt != null && nbt.hasUniqueId(IMysticalItem.NBT_COOLINGTIME))
            nbt.putInt(IMysticalItem.NBT_COOLINGTIME, coolingTime);
    }
}
