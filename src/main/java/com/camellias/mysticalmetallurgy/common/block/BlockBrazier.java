package com.camellias.mysticalmetallurgy.common.block;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.common.block.crucible.TileCrucible;
import com.camellias.mysticalmetallurgy.network.NetworkHandler;
import com.camellias.mysticalmetallurgy.network.packet.PlaySoundPacket;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockBrazier extends Block
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "brazier");
    public static final BooleanProperty COAL = BooleanProperty.create("coal");
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    private static final VoxelShape AABB = Block.makeCuboidShape(0.3D, 0.0D, 0.3D, 0.7D, 0.55D, 0.7D);

    public BlockBrazier()
    {
        super(Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(3F));

        setDefaultState(getDefaultState().with(COAL, false).with(LIT, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            ItemStack stack = playerIn.getHeldItem(hand);
            if (stack.getItem() instanceof ItemFlintAndSteel)
            {
                if (state.get(COAL) && !state.get(LIT))
                {
                    stack.damageItem(1, playerIn);
                    NetworkHandler.sendAround(new PlaySoundPacket(pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.4F, 0.7F), pos, worldIn.dimension.getType());
                    worldIn.setBlockState(pos, state.with(LIT, true));
                }
            }
            else if (!state.get(COAL) && TileCrucible.isValidFuel(stack))
            {
                stack.shrink(1);
                worldIn.setBlockState(pos, state.with(COAL, true));
            }
        }
        return true;
    }

    @Override
    public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.get(LIT))
        {
            for (int i = 0; i < 3; i++)
            {
                double ry = rand.nextDouble() * 0.5 + 0.45;
                double rx = rand.nextDouble() * 0.3 + 0.3;
                double rz = rand.nextDouble() * 0.3 + 0.3;

                double vx = rand.nextDouble() * 0.02 - 0.01;
                double vy = rand.nextDouble() * 0.02;
                double vz = rand.nextDouble() * 0.02 - 0.01;
                worldIn.addParticle(Particles.FLAME, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, vx, vy, vz);
                worldIn.addParticle(Particles.SMOKE, pos.getX() + rx + 0.1, pos.getY() + ry + 0.1, pos.getZ() + rz + 0.1, vx, vy, vz);
            }
        }
    }

    //region <state>
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(COAL).add(LIT);
    }
    //endregion

    //region <other>

    @Override
    public int getLightValue(IBlockState state, IWorldReader world, BlockPos pos)
    {
        return state.get(LIT) ? 15 : 0;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos) {
        return AABB;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IWorldReaderBase world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IWorldReaderBase world, BlockPos pos, EntitySpawnPlacementRegistry.SpawnPlacementType type, @Nullable EntityType<? extends EntityLiving> entityType) {
        return false;
    }
    //endregion
}
