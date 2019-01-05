package com.camellias.mysticalmetallurgy.common.block.rack;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockRack extends Block
{
    public BlockRack()
    {
        super(Material.WOOD);
        setSoundType(SoundType.WOOD);
        setResistance(2.0F);
        setHardness(1.0F);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return true;
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
        return new TileRack();
    }

    private TileRack getTile(@Nonnull World world, @Nonnull BlockPos pos)
    {
        return (TileRack) world.getTileEntity(pos);
    }
    //endregion
}
