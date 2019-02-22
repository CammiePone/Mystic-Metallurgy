package com.camellias.mysticalmetallurgy.common.block.crucible;


import com.camellias.mysticalmetallurgy.Main;
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
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockCrucible extends Block
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "crucible");
    public static final IntegerProperty COAL_LEVEL = IntegerProperty.create("coallevel", 0, 4);
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    private static final VoxelShape SHAPE = getShape();


    public BlockCrucible()
    {
        super(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(5F));

        setDefaultState(getDefaultState().with(COAL_LEVEL, 0).with(LIT, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            TileCrucible tile = getTile(worldIn, pos);
            if (tile != null)
            {
                ItemStack stack = playerIn.getHeldItem(hand);
                if (stack.getItem() instanceof ItemFlintAndSteel)
                {
                    if (tile.canLight())
                    {
                        tile.setLit();
                        stack.damageItem(1, playerIn);
                        NetworkHandler.sendAround(new PlaySoundPacket(pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.4F, 0.7F), pos, worldIn.dimension.getType());
                        tile.markDirty();
                    }
                }
                else
                    InsertExtract(tile, playerIn, hand, stack);
            }
        }
        return true;
    }

    private void InsertExtract(TileCrucible tile, EntityPlayer playerIn, EnumHand hand, ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            if (FluidUtil.getFluidHandler(stack).isPresent())
            {
                FluidUtil.interactWithFluidHandler(playerIn, hand, tile.output);
                tile.markDirty();
            }
            else if (!playerIn.isSneaking())
            {
                if (TileCrucible.isValidFuel(stack))
                {
                    ItemStack fuelStack = stack.copy();
                    fuelStack.setCount(1);
                    if (tile.input.insertItem(TileCrucible.FUEL_SLOT, fuelStack, false).isEmpty())
                        stack.shrink(1);

                }
                else
                {
                    for (int slot = 0; slot < TileCrucible.INPUT_SLOTS; slot++)
                    {
                        if (tile.input.getStackInSlot(slot).isEmpty())
                        {
                            playerIn.setHeldItem(hand, tile.input.insertItem(slot, stack, false));
                            break;
                        }
                    }
                }
            }
        }
        else if (playerIn.isSneaking())
        {
            for (int slot = TileCrucible.INPUT_SLOTS - 1; slot >= 0; slot--)
            {
                ItemStack slotStack = tile.input.extractItem(slot, 1, true);
                if (!slotStack.isEmpty())
                {
                    if (ItemUtils.giveStack(playerIn, slotStack).isEmpty())
                        tile.input.extractItem(slot, 1, false);
                    break;
                }
            }
        }
    }

    @Override
    public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.get(LIT))
        {
            for (int i = 0; i < 3; i++)
            {
                double ry = rand.nextDouble() * 0.5 + 0.15;
                double rx = rand.nextDouble() * 0.4 + 0.3;
                double rz = rand.nextDouble() * 0.4 + 0.3;

                double vx = rand.nextDouble() * 0.02 - 0.01;
                double vy = rand.nextDouble() * 0.05 + 0.03;
                double vz = rand.nextDouble() * 0.02 - 0.01;
                worldIn.addParticle(Particles.FLAME, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, vx, vy, vz);
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
    public TileEntity createTileEntity(@Nonnull IBlockState state, @Nonnull IBlockReader world)
    {
        return new TileCrucible();
    }

    private TileCrucible getTile(@Nonnull World world, @Nonnull BlockPos pos)
    {
        return (TileCrucible) world.getTileEntity(pos);
    }
    //endregion

    //region <state>
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(COAL_LEVEL).add(LIT);
    }
    //endregion

    //region <other>
    @Override
    public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune)
    {
        TileCrucible tile = getTile(world, pos);
        if (tile != null)
        {
            for (int slot = 0; slot < tile.input.getSlots(); slot++)
            {
                ItemStack stack = tile.input.getStackInSlot(slot);
                if (slot == TileCrucible.FUEL_SLOT && state.get(LIT))
                    stack.shrink(1);
                drops.add(stack);
            }
        }
    }

    @Override
    public int getLightValue(IBlockState state, IWorldReader world, BlockPos pos)
    {
        return state.get(LIT) ? 15 : 0;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
    {
        return SHAPE;
    }

    private static VoxelShape getShape()
    {
        return VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 3, 2, 14, 14, 14), VoxelShapes.combineAndSimplify(Block.makeCuboidShape(12, 0, 2, 13, 3, 3), VoxelShapes.combineAndSimplify(Block.makeCuboidShape(13, 0, 3, 14, 3, 4), VoxelShapes.combineAndSimplify(Block.makeCuboidShape(13, 0, 2, 14, 3, 3), VoxelShapes.combineAndSimplify(Block.makeCuboidShape(3, 0, 2, 4, 3, 3), VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 0, 3, 3, 3, 4), VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 0, 2, 3, 3, 3), VoxelShapes.combineAndSimplify(Block.makeCuboidShape(3, 0, 13, 4, 3, 14), VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 0, 13, 3, 3, 14), VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 0, 12, 3, 3, 13), VoxelShapes.combineAndSimplify(Block.makeCuboidShape(13, 0, 13, 14, 3, 14), VoxelShapes.combineAndSimplify(Block.makeCuboidShape(12, 0, 13, 13, 3, 14), Block.makeCuboidShape(13, 0, 12, 14, 3, 13), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR);
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
