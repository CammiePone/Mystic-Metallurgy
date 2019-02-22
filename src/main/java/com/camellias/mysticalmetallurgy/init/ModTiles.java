package com.camellias.mysticalmetallurgy.init;

import com.camellias.mysticalmetallurgy.common.block.anvil.TileStoneAnvil;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("mysticalmetallurgy")
public class ModTiles
{
    public static TileEntityType RACK;
    public static TileEntityType BASIN;
    public static TileEntityType CRUCIBLE;
    public static TileEntityType<TileStoneAnvil> STONE_ANVIL;
}
