package com.camellias.mysticalmetallurgy.common.block.rack;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.library.utils.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockRack extends Block
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "rack");
    public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;

    public BlockRack()
    {
        super(Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(2F));

        setDefaultState(getDefaultState().with(FACING, EnumFacing.NORTH));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
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
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlacementHorizontalFacing());
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
    public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
        return new TileRack();
    }

    private TileRack getTile(@Nonnull World world, @Nonnull BlockPos pos)
    {
        return (TileRack) world.getTileEntity(pos);
    }
    //endregion

    //region <other>
    @Override
    public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
        TileRack tile = getTile(world, pos);
        if (tile != null)
            drops.add(tile.inventory.getStackInSlot(0));
        super.getDrops(state, drops, world, pos, fortune);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
        EnumFacing side = state.get(FACING);
        return canAttachTo(worldIn, pos.offset(side), side.getOpposite());
    }

    public boolean canAttachTo(@Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, EnumFacing side) {
        IBlockState state = worldIn.getBlockState(pos);
        boolean flag = isExceptBlockForAttachWithPiston(state.getBlock());
        return !flag && state.getBlockFaceShape(worldIn, pos, side) == BlockFaceShape.SOLID;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote) {
            EnumFacing facing = state.get(FACING);
            if (!canAttachTo(worldIn, pos, facing.getOpposite()) && !canAttachTo(worldIn, pos.up(), EnumFacing.DOWN)) {
                state.dropBlockAsItem(worldIn, pos, 0);
                worldIn.removeBlock(pos);
            }
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos) {
        switch (state.get(FACING))
        {
            case NORTH:
                return Block.makeCuboidShape(0, 14, 0, 16, 16, 2);
            case EAST:
                return Block.makeCuboidShape(14, 14, 0, 16, 16, 16);
            case SOUTH:
                return Block.makeCuboidShape(0, 14, 14, 16, 16, 16);
            case WEST:
                return Block.makeCuboidShape(0, 14, 0, 2, 16, 16);
        }
        return super.getShape(state, source, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IWorldReaderBase world, BlockPos pos, EntitySpawnPlacementRegistry.SpawnPlacementType type, @Nullable EntityType<? extends EntityLiving> entityType) {
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IWorldReaderBase world, BlockPos pos) {
        return false;
    }
    //endregion
}
