package com.camellias.mysticalmetallurgy.common.fluid;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.init.ModBlocks;
import com.camellias.mysticalmetallurgy.init.ModFluids;
import com.camellias.mysticalmetallurgy.init.ModItems;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public abstract class FluidMysticMetal extends FlowingFluid
{
    public static final ResourceLocation ID = new ResourceLocation(Main.MODID, "mystical_liquid_metal");
    public static final ResourceLocation ID_FLOW = new ResourceLocation(Main.MODID, "mystical_liquid_metal_flowing");
    public static final ResourceLocation STILL = new ResourceLocation(Main.MODID, "block/mystic_metal_still");
    public static final ResourceLocation FLOW = new ResourceLocation(Main.MODID, "block/mystic_metal_flow");

    public FluidMysticMetal()
    {
        //super(fluidName, STILL, FLOW);

        //setDensity(2000); // thicker than a bowl of oatmeal
        //setViscosity(10000); // sloooow moving
        //setTemperature(1000); // not exactly lava, but still hot. Should depend on the material
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(World worldIn, BlockPos pos, IFluidState state, Random random) {
        BlockPos blockpos = pos.up();
        if (worldIn.isAirBlock(blockpos) && !worldIn.getBlockState(blockpos).isOpaqueCube(worldIn, blockpos)) {
            if (random.nextInt(100) == 0) {
                double d0 = (double)((float)pos.getX() + random.nextFloat());
                double d1 = (double)(pos.getY() + 1);
                double d2 = (double)((float)pos.getZ() + random.nextFloat());
                worldIn.addParticle(Particles.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }

            if (random.nextInt(200) == 0) {
                worldIn.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        }

    }

    //@Override
    //public int getColor()
    //{
    //    return new Color(0x885ead).getRGB();//0x551A8B;//super.getColor();
    //}

    @Nonnull
    @Override
    public Fluid getFlowingFluid() {
        return ModFluids.MYSTICAL_METAL_FLOWING;
    }

    @Nonnull
    @Override
    public Fluid getStillFluid() {
        return ModFluids.MYSTICAL_METAL;
    }

    @Override
    protected boolean canSourcesMultiply() {
        return false;
    }

    @Override
    protected void beforeReplacingBlock(@Nonnull IWorld worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {

    }

    public boolean isEquivalentTo(Fluid fluidIn) {
        return fluidIn == ModFluids.MYSTICAL_METAL || fluidIn == ModFluids.MYSTICAL_METAL_FLOWING;
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public IParticleData getDripParticleData() {
        return Particles.DRIPPING_LAVA;
    }

    @Override
    protected int getSlopeFindDistance(@Nonnull IWorldReaderBase worldIn) {
        return 2;
    }

    @Override
    protected int getLevelDecreasePerBlock(@Nonnull IWorldReaderBase worldIn) {
        return 2;
    }

    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Nonnull
    @Override
    public Item getFilledBucket() {
        return ModItems.METAL_BUCKET;
    }

    @Override
    protected boolean canOtherFlowInto(@Nonnull IFluidState state, @Nonnull Fluid fluidIn, @Nonnull EnumFacing direction) {
        return false;
    }

    @Override
    public int getTickRate(@Nonnull IWorldReaderBase world) {
        return 20;
    }

    @Override
    protected float getExplosionResistance() {
        return 100F;
    }

    @Nonnull
    @Override
    protected IBlockState getBlockState(@Nonnull IFluidState state) {
        return ModBlocks.MYSTICAL_LIQUID_METAL.getDefaultState().with(BlockFlowingFluid.LEVEL, getLevelFromState(state));
    }

    public static class Flowing extends FluidMysticMetal {
        protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        public int getLevel(IFluidState state) {
            return state.get(LEVEL_1_8);
        }

        public boolean isSource(IFluidState state) {
            return false;
        }
    }

    public static class Source extends FluidMysticMetal {
        public int getLevel(IFluidState state) {
            return 8;
        }

        public boolean isSource(IFluidState state) {
            return true;
        }
    }
}
