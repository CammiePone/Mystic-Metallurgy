package com.camellias.mysticalmetallurgy.common.block.basin;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.library.tileslottedinventory.InventorySlot;
import com.camellias.mysticalmetallurgy.library.utils.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
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
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockQuenchingBasin extends Block
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "basin");
    public static final BooleanProperty COOLING = BooleanProperty.create("cooling");
    public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
    private static final VoxelShape AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.07D, 1.0D, 0.62D, 0.93D);

    public BlockQuenchingBasin()
    {
        super(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(5F).needsRandomTick());

        setDefaultState(getDefaultState().with(COOLING, false).with(FACING, EnumFacing.NORTH));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            TileQuenchingBasin tile = getTile(worldIn, pos);

            if (tile != null)
            {
                if (FluidUtil.interactWithFluidHandler(playerIn, hand, tile.tank))
                {
                    worldIn.setBlockState(pos, state.with(COOLING, true));
                }
                else
                {
                    InventorySlot slot = tile.getSlotHit(state.get(FACING), hitX, hitZ);

                    if (slot != null)
                    {
                        ItemStack stack = playerIn.getHeldItem(hand);
                        if (!playerIn.isSneaking() && !stack.isEmpty())
                            playerIn.setHeldItem(hand, tile.insert(slot, stack, false));
                        else if (playerIn.isSneaking())
                        {
                            ItemStack slotStack = tile.extract(slot, true);
                            if (!slotStack.isEmpty() && ItemUtils.giveStack(playerIn, slotStack).isEmpty())
                                tile.extract(slot, false);
                        }
                    }
                }
            }
        }
        return true;
    }

    public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.get(COOLING))
        {
            for (int i = 0; i < 3; i++)
            {
                double ry = rand.nextDouble() * 0.5 + 0.15;
                double rx = rand.nextDouble() * 0.4 + 0.3;
                double rz = rand.nextDouble() * 0.4 + 0.3;

                double vx = rand.nextDouble() * 0.02 - 0.01;
                double vy = rand.nextDouble() * 0.05 + 0.03;
                double vz = rand.nextDouble() * 0.02 - 0.01;
                worldIn.addParticle(Particles.SMOKE, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, vx, vy, vz);
            }
        }
    }


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
        return new TileQuenchingBasin();
    }

    private TileQuenchingBasin getTile(@Nonnull World world, @Nonnull BlockPos pos)
    {
        return (TileQuenchingBasin) world.getTileEntity(pos);
    }
    //endregion

    //region <state>
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(FACING).add(COOLING);
    }

    @Nullable
    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext ctx)
    {
        return getDefaultState().with(FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    //endregion

    //region <other>
    @Override
    public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune)
    {
        TileQuenchingBasin tile = getTile(world, pos);
        if (tile != null)
            tile.getSlots().forEach(slot -> drops.add(slot.getStack()));
        super.getDrops(state, drops, world, pos, fortune);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
    {
        switch (state.get(FACING).getAxis())
        {
            case Z:
                return AABB;
            case X:
                return AABB;//AABBUtils.rotateH90(AABB);
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
    public boolean canCreatureSpawn(IBlockState state, IWorldReaderBase world, BlockPos pos, EntitySpawnPlacementRegistry.SpawnPlacementType type, @Nullable EntityType<? extends EntityLiving> entityType)
    {
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IWorldReaderBase world, BlockPos pos)
    {
        return false;
    }
    //endregion
}
