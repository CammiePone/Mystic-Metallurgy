package com.camellias.mysticalmetallurgy.common.block.anvil;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.common.item.tool.ItemHammer;
import com.camellias.mysticalmetallurgy.library.utils.ItemUtils;
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
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockStoneAnvil extends Block
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "stone_anvil");
    private static final VoxelShape Z_SHAPE = getZShape();
    private static final VoxelShape X_SHAPE = getXShape();

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockStoneAnvil()
    {
        super(Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(4F));

        setDefaultState(stateContainer.getBaseState().with(FACING, EnumFacing.NORTH));

    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            TileStoneAnvil tile = getTile(worldIn, pos);
            if (side == EnumFacing.UP && tile != null)
            {
                ItemStack stack = player.getHeldItem(hand);
                if (stack.getItem() instanceof ItemHammer)
                {
                    if (tile.tryHammer())
                    {
                        stack.damageItem(1, player);
                        NetworkHandler.sendAround(new PlaySoundPacket(pos, SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.AMBIENT, 1F, 1.0F), pos, player.dimension);
                    }
                }
                else
                {
                    TileStoneAnvil.InventorySlotTyped slot = tile.getSlotHit(state.get(FACING), hitX, hitZ,
                            tile.hasOutput() ?
                                    TileStoneAnvil.InventorySlotTyped.SlotType.OUTPUT :
                                    TileStoneAnvil.InventorySlotTyped.SlotType.INPUT);

                    if (slot != null)
                    {
                        if (!player.isSneaking() && !stack.isEmpty())
                        {
                            if (!tile.hasOutput())
                                player.setHeldItem(hand, slot.insert(stack, false));
                        }
                        else if (player.isSneaking())
                        {
                            if (tile.canExtract(slot))
                            {
                                ItemStack slotStack = slot.extract( 1, true);
                                if (!slotStack.isEmpty() && ItemUtils.giveStack(player, slotStack).isEmpty())
                                    slot.extract(1, false);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    //region <state>
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
    {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context)
    {
        return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
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
    public TileEntity createTileEntity(IBlockState state, IBlockReader world)
    {
        return new TileStoneAnvil();
    }

    private static TileStoneAnvil getTile(@Nonnull World world, @Nonnull BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileStoneAnvil ? (TileStoneAnvil)tile : null;
    }
    //endregion

    //region <other>
    @Override
    public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune)
    {
        TileStoneAnvil tile = getTile(world, pos);
        if (tile != null)
            tile.getSlots().forEach( slot -> drops.add(slot.getStack()));
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos)
    {
        switch (state.get(FACING).getAxis())
        {
            case Z:
                return Z_SHAPE;
            case X:
                return X_SHAPE;
        }

        return super.getShape(state, worldIn, pos);
    }

    private static VoxelShape getZShape()
    {
        return VoxelShapes.combineAndSimplify(
                Block.makeCuboidShape(4, 1, 6, 12, 4, 10),
                VoxelShapes.combineAndSimplify(Block.makeCuboidShape(3, 0, 5, 13, 1, 11),
                        VoxelShapes.combineAndSimplify(Block.makeCuboidShape(4, 4, 5, 12, 6, 11),
                                VoxelShapes.combineAndSimplify(Block.makeCuboidShape(3, 6, 4, 13, 7, 12),
                                        VoxelShapes.combineAndSimplify(Block.makeCuboidShape(3, 4, 11, 5, 6, 12),
                                                VoxelShapes.combineAndSimplify(
                                                        Block.makeCuboidShape(11, 4, 11, 13, 6, 12),
                                                        VoxelShapes.combineAndSimplify(Block.makeCuboidShape(11, 4, 4, 13, 6, 5),
                                                                Block.makeCuboidShape(3, 4, 4, 5, 6, 5), IBooleanFunction.OR),
                                                        IBooleanFunction.OR),
                                                IBooleanFunction.OR),
                                        IBooleanFunction.OR),
                                IBooleanFunction.OR),
                        IBooleanFunction.OR),
                IBooleanFunction.OR);
    }

    private static VoxelShape getXShape()
    {
        return VoxelShapes.combineAndSimplify(
                Block.makeCuboidShape(6, 1, 4, 10, 4, 12),
                VoxelShapes.combineAndSimplify(
                        Block.makeCuboidShape(5, 0, 3, 11, 1, 13),
                        VoxelShapes.combineAndSimplify(Block.makeCuboidShape(5, 4, 4, 11, 6, 12),
                                VoxelShapes.combineAndSimplify(
                                        Block.makeCuboidShape(4, 6, 3, 12, 7, 13),
                                        VoxelShapes.combineAndSimplify(
                                                Block.makeCuboidShape(4, 4, 3, 5, 6, 5),
                                                VoxelShapes.combineAndSimplify(
                                                        Block.makeCuboidShape(4, 4, 11, 5, 6, 13),
                                                        VoxelShapes.combineAndSimplify(
                                                                Block.makeCuboidShape(11, 4, 11, 12, 6, 13),
                                                                Block.makeCuboidShape(11, 4, 3, 12, 6, 5),
                                                                IBooleanFunction.OR),
                                                        IBooleanFunction.OR),
                                                IBooleanFunction.OR),
                                        IBooleanFunction.OR),
                                IBooleanFunction.OR),
                        IBooleanFunction.OR),
                IBooleanFunction.OR);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IWorldReaderBase world, BlockPos pos)
    {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IWorldReaderBase world, BlockPos pos, EntitySpawnPlacementRegistry.SpawnPlacementType type, @Nullable EntityType<? extends EntityLiving> entityType) {
        return false;
    }
    //endregion
}
