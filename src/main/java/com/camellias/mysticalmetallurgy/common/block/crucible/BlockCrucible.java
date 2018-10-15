package com.camellias.mysticalmetallurgy.common.block.crucible;


import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.network.NetworkHandler;
import com.camellias.mysticalmetallurgy.network.packet.PlaySoundPacket;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockCrucible extends Block
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "crucible");
    public static final PropertyInteger COAL_LEVEL = PropertyInteger.create("coallevel", 0, 4);

    public BlockCrucible()
    {
        super(Material.IRON);
        setSoundType(SoundType.METAL);
        setResistance(5.0F);
        setHardness(3.0F);

        setDefaultState(blockState.getBaseState().withProperty(COAL_LEVEL, 0));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
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
                        NetworkHandler.sendAround(new PlaySoundPacket(pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.4F, 0.7F), pos, worldIn.provider.getDimension());
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
        if (!playerIn.isSneaking() && !stack.isEmpty())
        {
            if (TileCrucible.isValidFuel(stack))
                playerIn.setHeldItem(hand, tile.input.insertItem(TileCrucible.FUEL_SLOT, stack, false));
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
        else if (playerIn.isSneaking() && stack.isEmpty())
        {
            for (int slot = TileCrucible.INPUT_SLOTS - 1; slot >= 0; slot--)
            {
                if (!tile.input.getStackInSlot(slot).isEmpty())
                {
                    playerIn.setHeldItem(hand, tile.input.extractItem(slot, 1, false));
                    break;
                }
            }
        }
        tile.markDirty();
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
        return new TileCrucible();
    }

    private TileCrucible getTile(@Nonnull World world, @Nonnull BlockPos pos)
    {
        return (TileCrucible) world.getTileEntity(pos);
    }
    //endregion

    //region <state>
    @Nonnull
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, COAL_LEVEL);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getStateFromMeta(meta).withProperty(COAL_LEVEL, 0);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(COAL_LEVEL, meta & 5);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(COAL_LEVEL);
    }
    //endregion

    //region <other>
    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        return ((tile instanceof TileCrucible) && ((TileCrucible)tile).isLit() ? 15 : 0) * 15;
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
