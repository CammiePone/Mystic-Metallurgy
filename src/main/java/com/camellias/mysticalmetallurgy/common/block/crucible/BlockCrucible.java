package com.camellias.mysticalmetallurgy.common.block.crucible;


import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.api.utils.ItemUtils;
import com.camellias.mysticalmetallurgy.network.NetworkHandler;
import com.camellias.mysticalmetallurgy.network.packet.PlaySoundPacket;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockCrucible extends Block
{
    public static final ResourceLocation LOC = new ResourceLocation(Main.MODID, "crucible");
    public static final PropertyInteger COAL_LEVEL = PropertyInteger.create("coallevel", 0, 4);
    public static final PropertyBool LIT = PropertyBool.create("lit");

    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.875D, 0.875D);

    public BlockCrucible()
    {
        super(Material.IRON);
        setSoundType(SoundType.METAL);
        setResistance(5.0F);
        setHardness(4.0F);

        setDefaultState(blockState.getBaseState().withProperty(COAL_LEVEL, 0).withProperty(LIT, false));
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
        if (!stack.isEmpty())
        {
            IFluidHandler fluidHandler = FluidUtil.getFluidHandler(stack);
            if (fluidHandler != null)
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

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.getValue(LIT))
        {
            for (int i = 0; i < 3; i++)
            {
                double ry = rand.nextDouble() * 0.5 + 0.15;
                double rx = rand.nextDouble() * 0.4 + 0.3;
                double rz = rand.nextDouble() * 0.4 + 0.3;

                double vx = rand.nextDouble() * 0.02 - 0.01;
                double vy = rand.nextDouble() * 0.05 + 0.03;
                double vz = rand.nextDouble() * 0.02 - 0.01;
                worldIn.spawnParticle(EnumParticleTypes.FLAME, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, vx, vy, vz);
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
        return new BlockStateContainer(this, COAL_LEVEL, LIT);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(COAL_LEVEL, meta & 7).withProperty(LIT, (meta >> 3) == 1);//getDefaultState().withProperty(COAL_LEVEL, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(COAL_LEVEL)) + ((state.getValue(LIT) ? 0 : 1) << 3);//state.getValue(COAL_LEVEL);
    }
    //endregion

    //region <other>
    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state)
    {
        TileCrucible tile = getTile(worldIn, pos);
        if (tile != null)
        {
            for (int slot = 0; slot < tile.input.getSlots(); slot++)
            {
                ItemStack stack = tile.input.getStackInSlot(slot);
                if (slot == TileCrucible.FUEL_SLOT && state.getValue(LIT))
                    stack.shrink(1);
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getActualState(world, pos).getValue(LIT) ? 15 : 0;
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
