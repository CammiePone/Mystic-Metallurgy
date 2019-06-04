package com.camellias.mysticalmetallurgy.common.block;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.common.block.crucible.TileCrucible;
import com.camellias.mysticalmetallurgy.network.NetworkHandler;
import com.camellias.mysticalmetallurgy.network.packet.PlaySoundPacket;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockBrazier extends Block
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "brazier");
    public static final PropertyBool COAL = PropertyBool.create("coal");
    public static final PropertyBool LIT = PropertyBool.create("lit");

    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.3D, 0.0D, 0.3D, 0.7D, 0.55D, 0.7D);

    public BlockBrazier()
    {
        super(Material.ROCK);
        setSoundType(SoundType.STONE);
        setHardness(3.0F);
        setResistance(5.0F);

        setDefaultState(blockState.getBaseState().withProperty(COAL, false).withProperty(LIT, false));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            ItemStack stack = playerIn.getHeldItem(hand);
            if (stack.getItem() instanceof ItemFlintAndSteel)
            {
                if (state.getValue(COAL) && !state.getValue(LIT))
                {
                    stack.damageItem(1, playerIn);
                    worldIn.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.4F, 0.7F);
                    worldIn.setBlockState(pos, state.withProperty(LIT, true));
                }
            }
            else if (!state.getValue(COAL) && TileCrucible.isValidFuel(stack))
            {
                stack.shrink(1);
                worldIn.setBlockState(pos, state.withProperty(COAL, true));
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.getValue(LIT))
        {
            for (int i = 0; i < 3; i++)
            {
                double ry = rand.nextDouble() * 0.5 + 0.45;
                double rx = rand.nextDouble() * 0.3 + 0.3;
                double rz = rand.nextDouble() * 0.3 + 0.3;

                double vx = rand.nextDouble() * 0.02 - 0.01;
                double vy = rand.nextDouble() * 0.02;
                double vz = rand.nextDouble() * 0.02 - 0.01;
                worldIn.spawnParticle(EnumParticleTypes.FLAME, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, vx, vy, vz);
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + rx + 0.1, pos.getY() + ry + 0.1, pos.getZ() + rz + 0.1, vx, vy, vz);
            }
        }
    }

    //region <state>
    @Nonnull
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, COAL, LIT);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(COAL, (meta & 1) == 1).withProperty(LIT, (meta >> 3) == 1);//getDefaultState().withProperty(COAL_LEVEL, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(COAL) ? 0 : 1) + ((state.getValue(LIT) ? 0 : 1) << 3);//state.getValue(COAL_LEVEL);
    }
    //endregion



    //region <other>
    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getActualState(world, pos).getValue(LIT) ? 15 : 0;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EntityLiving.SpawnPlacementType type)
    {
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos)
    {
        return false;
    }
    //endregion
}
