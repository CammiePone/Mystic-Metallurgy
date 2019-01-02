package com.camellias.mysticalmetallurgy.common.block.basin;

import com.camellias.mysticalmetallurgy.api.utils.ItemUtils;
import com.camellias.mysticalmetallurgy.common.item.tool.ItemMysticalTool;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

public class TileQuenchingBasin extends TileEntity implements ITickable
{
    ItemStackHandler inventory = new ItemStackHandler(3);
    FluidTank tank = new FluidTank(1000);

    @Override
    public void update()
    {
        if (world.isRemote) return;
        if (world.getTotalWorldTime() % 10 != 0) return;
        if (ItemUtils.getFirstOccupiedSlot(inventory) < 0) return;

        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemMysticalTool)
            {

            }
        }
    }
}
