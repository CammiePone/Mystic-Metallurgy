package com.camellias.mysticalmetallurgy.common.item;


import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.common.entity.EntityClump;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemMetalClump extends ItemVariant
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "metal_clump");
    public static final ResourceLocation CLUMP_0_LOC = new ResourceLocation(Main.MODID, "clump_0");
    public static final ResourceLocation CLUMP_1_LOC = new ResourceLocation(Main.MODID, "clump_1");
    public static final ResourceLocation CLUMP_2_LOC = new ResourceLocation(Main.MODID, "clump_2");

    public ItemMetalClump()
    {
        maxStackSize = 16;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (!playerIn.capabilities.isCreativeMode)
            itemstack.shrink(1);

        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote)
        {
            EntityClump clump = new EntityClump(worldIn, playerIn);
            clump.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.spawnEntity(clump);
        }

        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
    }
}
