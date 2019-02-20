package com.camellias.mysticalmetallurgy.api.effect;

import com.camellias.mysticalmetallurgy.Main;
import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Effect extends ForgeRegistryEntry<Effect>
{
    public static final int MAX_ID = Integer.MAX_VALUE - 1;
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(Main.MODID, "effects");
    private static IForgeRegistry<Effect> REGISTRY = null;

    public static IForgeRegistry<Effect> getRegistry()
    {
        if (REGISTRY == null)
            REGISTRY = GameRegistry.findRegistry(Effect.class);
        return REGISTRY;
    }

    @Nullable
    public static Effect getEffect(ResourceLocation id)
    {
        if (exists(id))
            return getRegistry().getValue(id);
        return null;
    }

    public static boolean exists(ResourceLocation id)
    {
        return getRegistry().containsKey(id);
    }

    public Effect(@Nonnull ResourceLocation id)
    {
        setRegistryName(id);
    }
    
    @Nonnull
    public String getAttributeInfo()
    {
        return I18n.format("effect." + getRegistryName().toString().replace(":", ".") + ".name");
    }

    /**
     * @return maximum effect level
     */
    public abstract int getMaxLevel();


    /**
     * All methods are called based on priority regardless if it makes sense or not :3
     *
     * @return Priority for effect to take effect :^) (Highest comes first)
     */
    public int getPriority() { return 0; }


    /**
     *
     * @param level effect level
     * @return multiplier for the base attack speed (multipliers are additive)
     */
    public float getAttackSpeedMod(int level) { return 0; }

    /**
     * @param level effect level
     * @return multiplier for the base base attack damage (multipliers are additive)
     */
    public float getAttackMod(int level) { return 0; }

    /**
     * @param level effect level
     * @return multiplier for the base mining speed (multipliers are additive)
     */
    public float getMiningSpeedMod(int level) { return 0; }

    /**
     * @param slot equipment slot
     * @param stack tool item stack
     * @param multimap attribute map
     * @param level effect level
     */
    public void getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack, @Nonnull Multimap<String, AttributeModifier> multimap, int level) {}

    /**
     * Called when a Block is destroyed
     *
     * @param stack tool item stack
     * @param worldIn world
     * @param state target block state
     * @param pos target block pos
     * @param entityLiving destroyer of the block
     * @param success block successfully destroyed
     * @param level effect level
     */
    public void onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, @Nullable EntityLivingBase destroyer, boolean success, int level) {}

    /**
     * Called when the tool gets damaged
     *
     * @param stack tool item stack
     * @param amount base damage amount
     * @param actualAmount actual damage amount / might have been modified by other effects
     * @param entity tool wielder
     * @param level effect level
     * @return new actual amount of tool damage
     */
    public int onToolDamaged(ItemStack stack, int amount, int actualAmount, @Nullable EntityLivingBase entity, int level) { return actualAmount; }

    /**
     * @param stack tool item stack
     * @param attacker attacker?
     * @param target target?
     * @param level effect level
     * @return isCriticalHit?
     */
    public boolean isCriticalHit(ItemStack stack, EntityLivingBase attacker, EntityLivingBase target, int level) { return false; }

    /**
     * @param stack tool item stack
     * @param attacker attacker?
     * @param target target?
     * @param baseDamage base damage...
     * @param actualDamage actual damage
     * @param isCritical is hit critical
     * @param level effect level
     * @return new actual entity damage
     */
    public float calculateDamage(ItemStack stack, EntityLivingBase attacker, EntityLivingBase target, float baseDamage, float actualDamage, boolean isCritical, int level) { return actualDamage;}

    /**
     * @param stack tool item stack
     * @param attacker attacker?
     * @param target target?
     * @param damage damage target receives
     * @param baseKnockBack base knock back
     * @param actualKnockBack actual knock back
     * @param isCritical hit is critical
     * @param level effect level
     * @return new actual knock back
     */
    public float calculateKnockBack(ItemStack stack, EntityLivingBase attacker, EntityLivingBase target, float damage, float baseKnockBack, float actualKnockBack, boolean isCritical, int level) { return actualKnockBack; }

    /**
     * @param stack tool item stack
     * @param attacker attacker
     * @param target target
     * @param damage damage target receives
     * @param isCritical hit is critical
     * @param level effect level
     */
    public void onHit(ItemStack stack, EntityLivingBase attacker, EntityLivingBase target, float damage, boolean isCritical, int level) {}

    /**
     * @param stack tool item stack
     * @param attacker attacker
     * @param target target
     * @param damage damage target receives
     * @param isCritical hit is critical
     * @param isHit is target hit :thinking:
     * @param level effect level
     */
    public void afterHit(ItemStack stack, EntityLivingBase attacker, EntityLivingBase target, float damageDealt, boolean isCritical, boolean isHit, int level) {}
}
