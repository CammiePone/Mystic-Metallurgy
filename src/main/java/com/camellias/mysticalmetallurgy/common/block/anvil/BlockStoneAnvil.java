package com.camellias.mysticalmetallurgy.common.block.anvil;

import com.camellias.mysticalmetallurgy.Main;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockStoneAnvil extends Block
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "stone_anvil");

    private static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private static final AxisAlignedBB AABB_Z = new AxisAlignedBB(0.186D, 0.0D, 0.254D, 0.81D, 0.435D, 0.748D);
    private static final AxisAlignedBB AABB_X = new AxisAlignedBB(0.254D, 0.0D, 0.186D, 0.748D, 0.435D, 0.81D);

    public BlockStoneAnvil()
    {
        super(Material.ROCK);
        setSoundType(SoundType.STONE);
        setHardness(4.0F);
        setResistance(5.0F);

        setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            TileStoneAnvil tile = getTile(worldIn, pos);
            if (tile != null)
            {
                ItemStack stack = playerIn.getHeldItem(hand);
                if (!tile.insertPrint(stack, false).isEmpty())
                    if(!tile.insertStick(stack, false).isEmpty())
                        tile.insertMetal(stack, false);
            }
        }
        return true;
    }

    //region <state>
    @Nonnull
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Nonnull
    @Override
    public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(FACING,  EnumFacing.HORIZONTALS[meta & 3]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).ordinal() - 2;
    }
    //endregion

    //region <tileentity>
    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state)
    {
        return new TileStoneAnvil();
    }

    private TileStoneAnvil getTile(@Nonnull World world, @Nonnull BlockPos pos)
    {
        return (TileStoneAnvil) world.getTileEntity(pos);
    }
    //endregion

    //region <other>
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        switch (state.getValue(FACING).getAxis())
        {
            case X:
                return AABB_X;
            case Z:
                return AABB_Z;
        }
        return FULL_BLOCK_AABB;
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