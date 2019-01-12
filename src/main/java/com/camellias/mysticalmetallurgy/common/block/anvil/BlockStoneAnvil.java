package com.camellias.mysticalmetallurgy.common.block.anvil;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.common.item.tool.ItemHammer;
import com.camellias.mysticalmetallurgy.library.utils.AABBUtils;
import com.camellias.mysticalmetallurgy.library.utils.ItemUtils;
import com.camellias.mysticalmetallurgy.network.NetworkHandler;
import com.camellias.mysticalmetallurgy.network.packet.PlaySoundPacket;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockStoneAnvil extends Block
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "stone_anvil");

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.186D, 0.0D, 0.254D, 0.81D, 0.435D, 0.748D);

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
                ItemStack stack = playerIn.getHeldItem(hand);
                if (stack.getItem() instanceof ItemHammer)
                {
                    if (tile.tryHammer())
                    {
                        stack.damageItem(1, playerIn);
                        NetworkHandler.sendAround(new PlaySoundPacket(pos, SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.AMBIENT, 1F, 1.0F), pos, playerIn.dimension);
                    }
                }
                else
                {
                    TileStoneAnvil.InventorySlotTyped slot = tile.getSlotHit(state.getValue(FACING), hitX, hitZ,
                            tile.hasOutput() ?
                                    TileStoneAnvil.InventorySlotTyped.SlotType.OUTPUT :
                                    TileStoneAnvil.InventorySlotTyped.SlotType.INPUT);

                    if (slot != null)
                    {
                        if (!playerIn.isSneaking() && !stack.isEmpty())
                        {
                            if (!tile.hasOutput())
                                playerIn.setHeldItem(hand, slot.insert(stack, false));
                        }
                        else if (playerIn.isSneaking())
                        {
                            if (tile.canExtract(slot))
                            {
                                ItemStack slotStack = slot.extract( 1, true);
                                if (!slotStack.isEmpty() && ItemUtils.giveStack(playerIn, slotStack).isEmpty())
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
    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state)
    {
        TileStoneAnvil tile = getTile(worldIn, pos);
        if (tile != null)
            tile.getSlots().forEach(slot -> InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), slot.getStack()));
        super.breakBlock(worldIn, pos, state);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        switch (state.getValue(FACING).getAxis())
        {
            case Z:
                return AABB;
            case X:
                return AABBUtils.rotateH90(AABB);
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
