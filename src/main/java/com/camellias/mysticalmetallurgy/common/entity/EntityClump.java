package com.camellias.mysticalmetallurgy.common.entity;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.init.ModEntities;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Particles;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityClump extends EntityThrowable
{
    public static final ResourceLocation ID = new ResourceLocation(Main.MODID, "clump");

    public EntityClump(World worldIn)
    {
        super(ModEntities.CLUMP, worldIn);
    }

    public EntityClump(EntityLivingBase throwerIn, World worldIn)
    {
        super(ModEntities.CLUMP, throwerIn, worldIn);
    }

    public EntityClump(double x, double y, double z, World worldIn)
    {
        super(ModEntities.CLUMP, x, y, z, worldIn);
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    @Override
    public void handleStatusUpdate(byte id)
    {
        if (id == 3)
        {
            for (int i = 0; i < 8; ++i)
            {
                //this.world.addParticle(Particles.FALLING_DUST, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onImpact(@Nonnull RayTraceResult result)
    {
        if (result.entity != null)
            result.entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.3F);

        if (!this.world.isRemote)
        {
            this.world.setEntityState(this, (byte)3);
            this.remove();
        }
    }
}
