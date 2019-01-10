package com.camellias.mysticalmetallurgy.common.block.rack;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.library.utils.AABBUtils;
import com.camellias.mysticalmetallurgy.library.utils.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
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

public class BlockRack extends Block
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "rack");
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.875D, 0.0D, 1.0D, 1.0D, 0.125D);

    public BlockRack()
    {
        super(Material.WOOD);
        setSoundType(SoundType.WOOD);
        setResistance(2.0F);
        setHardness(1.0F);

        setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileRack tile = getTile(worldIn, pos);
        if (tile != null)
        {
            ItemStack stack = playerIn.getHeldItem(hand);
            if (!playerIn.isSneaking() && !stack.isEmpty())
                playerIn.setHeldItem(hand, tile.inventory.insertItem(0, stack, false));
            else if (playerIn.isSneaking())
            {
                ItemStack slotStack = tile.inventory.extractItem(0, 1, true);
                if (!slotStack.isEmpty() && ItemUtils.giveStack(playerIn, slotStack).isEmpty())
                    tile.inventory.extractItem(0, 1, false);
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
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
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
        return new TileRack();
    }

    private TileRack getTile(@Nonnull World world, @Nonnull BlockPos pos)
    {
        return (TileRack) world.getTileEntity(pos);
    }
    //endregion

    //region <other>
    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state)
    {
        TileRack tile = getTile(worldIn, pos);
        if (tile != null)
            InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.inventory.getStackInSlot(0));
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean canPlaceBlockOnSide(@Nonnull World worldIn, @Nonnull BlockPos pos, EnumFacing side) {
        BlockPos adjacent = pos.offset(side.getOpposite());
        IBlockState state = worldIn.getBlockState(adjacent);
        return (state.isSideSolid(worldIn, adjacent, side) || state.getMaterial().isSolid()) && !BlockRedstoneDiode.isDiode(state) && side != EnumFacing.UP; //side.getAxis().isHorizontal();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote) {
            EnumFacing facing = state.getValue(FACING);
            if (!canPlaceBlockOnSide(worldIn, pos, facing.getOpposite()) && !canPlaceBlockOnSide(worldIn, pos.up(), EnumFacing.DOWN)) {
                dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        switch (state.getValue(FACING))
        {
            case NORTH:
                return AABB;
            case EAST:
                return AABBUtils.rotateH90(AABB);
            case SOUTH:
                return AABBUtils.rotateH180(AABB);
            case WEST:
                return AABBUtils.rotateH270(AABB);
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
