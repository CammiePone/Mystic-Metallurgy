package com.camellias.mysticalmetallurgy.common.block.anvil;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.api.utils.ItemUtils;
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
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(Side.CLIENT)
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
            if (facing == EnumFacing.UP && tile != null)
            {
                TileStoneAnvil.SLOTS slot = getSlotHit(state.getValue(FACING), hitX, hitZ);
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
        return true;
    }

    @Nullable
    public static TileStoneAnvil.SLOTS getSlotHit(EnumFacing facing, float x, float z)
    {
        switch (facing)
        {
            case NORTH:
                if (z >= 0.375F && x <= 0.780F && z <= 0.625F && x >= 0.530F)
                    return TileStoneAnvil.SLOTS.PRINT; //0 print
                if (z >= 0.500F && x <= 0.500F && z <= 0.750F && x >= 0.250F)
                    return TileStoneAnvil.SLOTS.INPUT; //1 input
                if (z >= 0.250F && x <= 0.500F && z <= 0.500F && x >= 0.250F)
                    return TileStoneAnvil.SLOTS.EXTRA; //2 extra
                break;
            case SOUTH:
                if (z >= 0.375F && x >= 0.220F && z <= 0.625F && x <= 0.470F)
                    return TileStoneAnvil.SLOTS.PRINT ; //0 print
                if (z >= 0.250F && x >= 0.500F && z <= 0.500F && x <= 0.750F)
                    return TileStoneAnvil.SLOTS.INPUT; //1 input
                if (z >= 0.500F && x >= 0.500F && z <= 0.750F && x <= 0.750F)
                    return TileStoneAnvil.SLOTS.EXTRA; //2 extra
                break;
            case EAST:
                if (x >= 0.375F && z <= 0.780F && x <= 0.625F && z >= 0.530F)
                    return TileStoneAnvil.SLOTS.PRINT; //0 print
                if (x >= 0.250F && z <= 0.500F && x <= 0.500F && z >= 0.250F)
                    return TileStoneAnvil.SLOTS.INPUT; //1 input
                if (x >= 0.500F && z <= 0.500F && x <= 0.750F && z >= 0.250F)
                    return TileStoneAnvil.SLOTS.EXTRA; //2 extra
                break;
            case WEST:
                if (x >= 0.375F && z >= 0.220F && x <= 0.625F && z <= 0.470F)
                    return TileStoneAnvil.SLOTS.PRINT; //0 print
                if (x >= 0.500F && z >= 0.500F && x <= 0.750F && z <= 0.750F)
                    return TileStoneAnvil.SLOTS.INPUT; //1 input
                if (x >= 0.250F && z >= 0.500F && x <= 0.500F && z <= 0.750F)
                    return TileStoneAnvil.SLOTS.EXTRA; //2 extra
                break;
        }
        return null;
    }

    @SubscribeEvent
    public static void DrawBlockHover(DrawBlockHighlightEvent event)
    {
        EntityPlayer player = event.getPlayer();
        BlockPos pos = event.getTarget().getBlockPos();
        IBlockState state = player.world.getBlockState(pos);
        ItemStack stack = player.getHeldItemMainhand();
        TileStoneAnvil tile = getTile(player.world, pos);
        if (tile != null && !stack.isEmpty() && event.getTarget().sideHit == EnumFacing.UP)
        {
            double hitX = event.getTarget().hitVec.x;
            double hitY = event.getTarget().hitVec.y;

            TileStoneAnvil.SLOTS slot = getSlotHit(state.getValue(FACING), (float)hitX, (float)hitY);
            if (slot != null)
            {
                ItemStack slotStack = tile.extract(slot, true);
            }
        }
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

    private static TileStoneAnvil getTile(@Nonnull World world, @Nonnull BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileStoneAnvil ? (TileStoneAnvil)tile : null;
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
