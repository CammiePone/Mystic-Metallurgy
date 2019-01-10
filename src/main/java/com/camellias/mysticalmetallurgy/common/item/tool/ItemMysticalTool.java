package com.camellias.mysticalmetallurgy.common.item.tool;

import com.camellias.mysticalmetallurgy.api.IMysticalItem;
import com.camellias.mysticalmetallurgy.api.effect.Effect;
import com.camellias.mysticalmetallurgy.api.effect.Trait;
import com.camellias.mysticalmetallurgy.network.NetworkHandler;
import com.camellias.mysticalmetallurgy.network.packet.ToolBreakAnimationPacket;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemMysticalTool extends Item implements IMysticalItem
{

    @Override
    public int getMaxDamage(ItemStack stack)
    {
        return ToolStats.get(stack).durability;
    }

    @Override
    public void setDamage(ItemStack stack, int damage)
    {
        int max = getMaxDamage(stack);
        super.setDamage(stack, Math.min(max, damage));

        if (getDamage(stack) == max)
            setBroken(stack, null);
    }

    @Override
    public boolean isDamageable()
    {
        return true;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return super.showDurabilityBar(stack) && !ToolStats.get(stack).broken;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state)
    {
        if (state == null)
            return 0f;

        if (!stack.hasTagCompound())
            return 1f;

        ToolStats stats = ToolStats.get(stack);

        if (stats.broken)
            return 0.3f;

        // check if the tool has the correct class and harvest level
        if (!canHarvestBlock(state, stack))
            return 0f;

        // calculate attackSpeed depending on stats
        return stats.getActualMiningSpeed();
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        return attackEntity(stack, player, entity, null, true);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        ToolStats stats = ToolStats.get(stack);

        float speed = stats.getActualAttackSpeed();
        int time = Math.round(20f / speed);
        if (time < target.hurtResistantTime / 2)
        {
            target.hurtResistantTime = (target.hurtResistantTime + time) / 2;
            target.hurtTime = (target.hurtTime + time) / 2;
        }
        return super.hitEntity(stack, target, attacker);
    }

    @Nonnull
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

        ToolStats stats = ToolStats.get(stack);

        if (slot == EntityEquipmentSlot.MAINHAND && !stats.broken)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", stats.getActualAttack(), 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", stats.getActualAttackSpeed() - 4d, 0));
        }

        stats.traits.forEach(trait -> trait.getEffect().getAttributeModifiers(slot, stack, multimap, trait.level));

        return multimap;
    }

    @Override
    public int getHarvestLevel(ItemStack stack, @Nonnull String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState)
    {
        ToolStats stats = ToolStats.get(stack);

        if (stats.broken)
            return -1;

        if (getToolClasses(stack).contains(toolClass))
        {
            // will return 0 if the tag has no info anyway
            return Math.max(stats.harvestLevel, 0);
        }

        return super.getHarvestLevel(stack, toolClass, player, blockState);
    }

    @Override
    public boolean canHarvestBlock(@Nonnull IBlockState state, ItemStack stack)
    {
        Block block = state.getBlock();

        // doesn't require a tool
        if (state.getMaterial().isToolNotRequired())
            return true;

        String type = block.getHarvestTool(state);
        int level = block.getHarvestLevel(state);

        return stack.getItem().getHarvestLevel(stack, type, null, state) >= level;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving)
    {
        ToolStats stats = ToolStats.get(stack);
        if (stats.broken)
            return false;

        boolean effective = canHarvestBlock(state, stack);

        stats.traits.forEach(trait -> trait.getEffect().onBlockDestroyed(stack, worldIn, state, pos, entityLiving, effective, trait.level));

        onDamage(stack, effective ? 1 : 2, entityLiving, stats);

        return true;
    }

    /**
     * Damages the tool. Entity is only needed in case the tool breaks for rendering the break effect.
     */
    protected void onDamage(ItemStack stack, int amount, @Nullable EntityLivingBase entity, @Nonnull ToolStats stats)
    {
        if (amount == 0 || stats.broken)
            return;

        int actualAmount = amount;

        for (Trait trait : stats.traits)
            actualAmount = trait.getEffect().onToolDamaged(stack, amount, actualAmount, entity, trait.level);

        // ensure we never deal more damage than durability
        actualAmount = Math.min(actualAmount, getDurability(stack));
        stack.setItemDamage(stack.getItemDamage() + actualAmount);

        if (getDurability(stack) == 0)
            setBroken(stack, entity);
    }

    public int getDurability(ItemStack stack)
    {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public void setBroken(ItemStack stack, @Nullable EntityLivingBase entity)
    {
        ToolStats stats = ToolStats.get(stack);
        stats.broken = true;
        ToolStats.write(stack, stats);

        if (entity instanceof EntityPlayerMP)
            NetworkHandler.sendTo(new ToolBreakAnimationPacket(stack), (EntityPlayerMP) entity);
    }

    /**
     * Makes all the calls to attack an entity. Takes enchantments and potions and traits into account. Basically call this when a tool deals damage.
     * Most of this function is the same as {@link EntityPlayer#attackTargetEntityWithCurrentItem(Entity targetEntity)}
     */
    public boolean attackEntity(ItemStack stack, @Nullable EntityLivingBase attacker, @Nullable Entity targetEntity, @Nullable Entity projectileEntity, boolean applyCooldown)
    {
        // nothing to do, no target?
        if (targetEntity == null || !targetEntity.canBeAttackedWithItem() || targetEntity.hitByEntity(attacker) || !stack.hasTagCompound())
            return false;
        ToolStats stats = ToolStats.get(stack);

        if (stats.broken)
            return false;

        if (attacker == null)
            return false;

        boolean isProjectile = projectileEntity != null;

        EntityLivingBase target = null;
        EntityPlayer player = null;
        if (targetEntity instanceof EntityLivingBase)
            target = (EntityLivingBase) targetEntity;

        if (attacker instanceof EntityPlayer)
        {
            player = (EntityPlayer) attacker;
            if (target instanceof EntityPlayer)
            {
                if (!player.canAttackPlayer((EntityPlayer) target))
                    return false;
            }
        }

        // players base damage (includes tools damage stat)
        float baseDamage = (float) attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();

        float baseKnockback = attacker.isSprinting() ? 1 : 0;

        // calculate if it's a critical hit
        boolean isCritical = attacker.fallDistance > 0.0F &&
                !attacker.onGround && !attacker.isOnLadder() &&
                !attacker.isInWater() &&
                !attacker.isPotionActive(MobEffects.BLINDNESS) &&
                !attacker.isRiding();

        if (!isCritical)
        {
            for (Trait trait : stats.traits)
                if (trait.getEffect().isCriticalHit(stack, attacker, target, trait.level))
                {
                    isCritical = true;
                    break;
                }
        }

        // calculate actual damage
        float damage = baseDamage;
        if (target != null)
        {
            for (Trait trait : stats.traits)
                damage = trait.getEffect().calculateDamage(stack, attacker, target, baseDamage, damage, isCritical, trait.level);
        }

        // apply critical damage
        if (isCritical)
            damage *= 1.5f;

        // calculate diminishing returns
        damage = calcDiminishingDamage(damage, damageCutoff());

        // calculate actual knockBack
        float knockBack = baseKnockback;
        if (target != null)
        {
            for (Trait trait : stats.traits)
                knockBack = trait.getEffect().calculateKnockBack(stack, attacker, target, damage, baseKnockback, knockBack, isCritical, trait.level);
        }

        float oldHP = 0;

        double oldVelX = targetEntity.motionX;
        double oldVelY = targetEntity.motionY;
        double oldVelZ = targetEntity.motionZ;

        if (target != null)
            oldHP = target.getHealth();

        // apply cooldown damage decrease
        if (player != null)
        {
            float coolDown = ((EntityPlayer) attacker).getCooledAttackStrength(0.5F);
            damage *= (0.2F + coolDown * coolDown * 0.8F);
        }

        // deal the damage
        if (target != null)
        {
            int hurtResistantTime = target.hurtResistantTime;
            for (Trait trait : stats.traits)
            {
                trait.getEffect().onHit(stack, attacker, target, damage, isCritical, trait.level);
                // reset hurt reristant time
                target.hurtResistantTime = hurtResistantTime;
            }
        }

        boolean hit = false;
        //if (isProjectile && tool instanceof IProjectile)
        //    hit = ((IProjectile) tool).dealDamageRanged(stack, projectileEntity, attacker, targetEntity, damage);
        //else
            hit = dealDamage(stack, attacker, targetEntity, damage);

        // did we hit?
        if (hit && target != null)
        {
            // actual damage dealt
            float damageDealt = oldHP - target.getHealth();

            // apply knockback modifier
            oldVelX = target.motionX = oldVelX + (target.motionX - oldVelX) * knockBack();
            oldVelY = target.motionY = oldVelY + (target.motionY - oldVelY) * knockBack() / 3f;
            oldVelZ = target.motionZ = oldVelZ + (target.motionZ - oldVelZ) * knockBack();

            // apply knockback
            if (knockBack > 0f)
            {
                double velX = -MathHelper.sin(attacker.rotationYaw * (float) Math.PI / 180.0F) * knockBack * 0.5F;
                double velZ = MathHelper.cos(attacker.rotationYaw * (float) Math.PI / 180.0F) * knockBack * 0.5F;
                targetEntity.addVelocity(velX, 0.1d, velZ);

                // slow down player
                attacker.motionX *= 0.6f;
                attacker.motionZ *= 0.6f;
                attacker.setSprinting(false);
            }

            // Send movement changes caused by attacking directly to hit players.
            // I guess this is to allow better handling at the hit players side? No idea why it resets the motion though.
            if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged)
            {
                NetworkHandler.sendPacket(player, new SPacketEntityVelocity(targetEntity));
                targetEntity.velocityChanged = false;
                targetEntity.motionX = oldVelX;
                targetEntity.motionY = oldVelY;
                targetEntity.motionZ = oldVelZ;
            }

            if (player != null)
            {
                // vanilla critical callback
                if (isCritical)
                    player.onCriticalHit(target);

                // "magical" critical damage? (aka caused by modifiers)
                if (damage > baseDamage)
                {
                    // this usually only displays some particles :)
                    player.onEnchantmentCritical(targetEntity);
                }

                // TODO: Achievements?
            }
            attacker.setLastAttackedEntity(target);
            // Damage indicator particles

            // we don't support vanilla thorns or antispider enchantments
            //EnchantmentHelper.applyThornEnchantments(target, player);
            //EnchantmentHelper.applyArthropodEnchantments(player, target);

            // call post-hit callbacks before reducing the durability
            EntityLivingBase finalTarget = target;
            boolean finalIsCritical = isCritical;
            stats.traits.forEach(trait -> trait.getEffect().afterHit(stack, attacker, finalTarget, damageDealt, finalIsCritical, true, trait.level));

            // damage the tool
            if (player != null)
            {
                stack.hitEntity(target, player);
                if (!player.capabilities.isCreativeMode && !isProjectile)
                    reduceDurabilityOnHit(stack, player, damage);

                player.addStat(StatList.DAMAGE_DEALT, Math.round(damageDealt * 10f));
                player.addExhaustion(0.3f);

                if (player.getEntityWorld() instanceof WorldServer && damageDealt > 2f)
                {
                    int k = (int) (damageDealt * 0.5);
                    ((WorldServer) player.getEntityWorld()).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + targetEntity.height * 0.5F, targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                }

                // cooldown for non-projectiles
                if (!isProjectile && applyCooldown)
                    player.resetCooldown();
            }
            else if (!isProjectile)
                reduceDurabilityOnHit(stack, null, damage);
        }

        return true;
    }

    /**
     * A fixed damage value where the calculations start to apply dimishing returns.
     * Basically if you'd hit more than that damage with this tool, the damage is gradually reduced depending on how much the cutoff is exceeded.
     */
    protected float damageCutoff() {
        return 15.0f; // in general this should be sufficient and only needs increasing if it's a stronger weapon
        // fun fact: diamond sword with sharpness V has 15 damage
    }


    protected float calcDiminishingDamage(float damage, float cutoff)
    {
        float p = 1f;
        float d = damage;
        damage = 0f;
        while (d > cutoff)
        {
            damage += p * cutoff;
            // safety for ridiculous values
            if (p > 0.001f)
                p *= 0.9f;
            else
            {
                damage += p * cutoff * ((d / cutoff) - 1f);
                return damage;
            }

            d -= cutoff;
        }

        damage += p * d;

        return damage;
    }

    /**
     * Actually deal damage to the entity we hit. Can be overridden for special behaviour
     *
     * @return True if the entity was hit. Usually the return value of {@link Entity#attackEntityFrom(DamageSource, float)}
     */
    protected boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage)
    {
        if (player instanceof EntityPlayer)
            return entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
        return entity.attackEntityFrom(DamageSource.causeMobDamage(player), damage);
    }

    /**
     * KnockBack modifier. Basically this takes the vanilla knockBack on hit and modifies it by this factor.
     */
    public float knockBack()
    {
        return 1.0f;
    }

    /**
     * Called when an entity is getting damaged with the tool.
     * Reduce the tools durability accordingly
     * player can be null!
     */
    public void reduceDurabilityOnHit(ItemStack stack, @Nullable EntityPlayer player, float damage) {
        damage = Math.max(1f, damage / 10f);
        //if(!hasCategory(Category.WEAPON)) {
        //    damage *= 2;
        //}
        onDamage(stack, (int) damage, player, ToolStats.get(stack));
    }

    public static class ToolStats implements INBTSerializable<NBTTagCompound>
    {
        private static final String NBT_DATA = "tool_data";

        private static final String NBT_DURABILITY = "durability";
        private static final String NBT_ATTACK = "attack";
        private static final String NBT_ASPEED = "attackSpeed";
        private static final String NBT_MSPEED = "miningSpeed";
        private static final String NBT_LEVEL = "level";
        private static final String NBT_BROKEN = "broken";

        int durability = 10;
        float attack = 1;
        float attackSpeed = 3;
        float miningSpeed = 3;
        short harvestLevel = 0;

        boolean broken = false;

        List<Trait> traits;

        ToolStats()
        {
        }

        ToolStats(NBTTagCompound nbt)
        {
            deserializeNBT(nbt);
        }

        public static ToolStats get(NBTTagCompound nbt)
        {
            return new ToolStats(nbt);
        }

        public static ToolStats get(ItemStack stack)
        {
            if (stack.isEmpty() || !stack.hasTagCompound())
                return new ToolStats();

            return get(stack.getTagCompound().getCompoundTag(NBT_DATA));
        }

        public float getActualAttack()
        {
            float attack = this.attack;

            for (Trait trait : traits)
            {
                Effect effect = trait.getEffect();
                if (effect != null)
                    attack += this.attackSpeed * effect.getAttackMod(trait.level);
            }

            return attack;
        }

        public float getActualMiningSpeed()
        {
            float speed = this.miningSpeed;

            for (Trait trait : traits)
            {
                Effect effect = trait.getEffect();
                if (effect != null)
                    speed += this.miningSpeed * effect.getMiningSpeedMod(trait.level);
            }

            return speed;
        }

        public float getActualAttackSpeed()
        {
            float speed = this.attackSpeed;

            for (Trait trait : traits)
            {
                Effect effect = trait.getEffect();
                if (effect != null)
                    speed += this.attackSpeed * effect.getAttackSpeedMod(trait.level);
            }

            return speed;
        }

        public static void write(ItemStack stack, ToolStats stats)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            if (!stack.isEmpty() && stack.hasTagCompound())
                nbt = stack.getTagCompound();

            nbt.setTag(NBT_DATA, stats.serializeNBT());
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger(NBT_DURABILITY, durability);
            nbt.setFloat(NBT_ATTACK, attack);
            nbt.setFloat(NBT_ASPEED, attackSpeed);
            nbt.setFloat(NBT_MSPEED, miningSpeed);
            nbt.setBoolean(NBT_BROKEN, broken);
            nbt.setShort(NBT_LEVEL, harvestLevel);
            Trait.toNBT(nbt, traits);
            return nbt;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            if (nbt.hasKey(NBT_DURABILITY)) durability = nbt.getInteger(NBT_DURABILITY);
            if (nbt.hasKey(NBT_ATTACK)) attack = nbt.getFloat(NBT_ATTACK);
            if (nbt.hasKey(NBT_ASPEED)) attackSpeed = nbt.getFloat(NBT_ASPEED);
            if (nbt.hasKey(NBT_MSPEED)) miningSpeed = nbt.getFloat(NBT_MSPEED);
            if (nbt.hasKey(NBT_BROKEN)) broken = nbt.getBoolean(NBT_BROKEN);
            if (nbt.hasKey(NBT_LEVEL)) harvestLevel = nbt.getShort(NBT_LEVEL);
            traits = Trait.fromNBT(nbt);
        }
    }
}
