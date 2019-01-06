package com.camellias.mysticalmetallurgy.common.block.basin;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.api.utils.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockQuenchingBasin extends Block
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "basin");
    public static final PropertyBool COOLING = PropertyBool.create("cooling");
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.875D, 0.875D);

    public BlockQuenchingBasin()
    {
        super(Material.IRON);
        setSoundType(SoundType.METAL);
        setResistance(5.0F);
        setHardness(4.0F);

        setDefaultState(blockState.getBaseState().withProperty(COOLING, false).withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            TileQuenchingBasin tile = getTile(worldIn, pos);

            if (tile != null)
            {
                if (FluidUtil.interactWithFluidHandler(playerIn, hand, tile.tank))
                {
                    worldIn.setBlockState(pos, state.withProperty(COOLING, true));
                }
                else
                {
                    TileQuenchingBasin.Slot slot = TileQuenchingBasin.Slot.getSlotHit(state.getValue(FACING), hitX, hitZ);

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

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.getValue(COOLING))
        {
            for (int i = 0; i < 3; i++)
            {
                double ry = rand.nextDouble() * 0.5 + 0.15;
                double rx = rand.nextDouble() * 0.4 + 0.3;
                double rz = rand.nextDouble() * 0.4 + 0.3;

                double vx = rand.nextDouble() * 0.02 - 0.01;
                double vy = rand.nextDouble() * 0.05 + 0.03;
                double vz = rand.nextDouble() * 0.02 - 0.01;
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, vx, vy, vz);
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
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state)
    {
        return new TileQuenchingBasin();
    }

    private TileQuenchingBasin getTile(@Nonnull World world, @Nonnull BlockPos pos)
    {
        return (TileQuenchingBasin) world.getTileEntity(pos);
    }
    //endregion

    //region <state>
    @Nonnull
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, COOLING, FACING);
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
        return getDefaultState().withProperty(FACING, EnumFacing.HORIZONTALS[meta >> 1]).withProperty(COOLING, (meta & 1) == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(COOLING) ? 0 : 1) + ((state.getValue(FACING).ordinal() - 2) << 1);
    }
    //endregion

    //region <other>
    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state)
    {
        TileQuenchingBasin tile = getTile(worldIn, pos);
        if (tile != null)
        {
            for (int slot = 0; slot < tile.inventory.getSlots(); slot++)
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.inventory.getStackInSlot(slot));
        }
        super.breakBlock(worldIn, pos, state);
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
