package com.camellias.mysticalmetallurgy.init;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.api.RegisterItemEffectsEvent;
import com.camellias.mysticalmetallurgy.api.effect.Effect;
import com.camellias.mysticalmetallurgy.api.recipe.AnvilRecipe;
import com.camellias.mysticalmetallurgy.common.block.BlockBrazier;
import com.camellias.mysticalmetallurgy.common.block.anvil.BlockStoneAnvil;
import com.camellias.mysticalmetallurgy.common.block.anvil.TileStoneAnvil;
import com.camellias.mysticalmetallurgy.common.block.basin.BlockQuenchingBasin;
import com.camellias.mysticalmetallurgy.common.block.basin.TileQuenchingBasin;
import com.camellias.mysticalmetallurgy.common.block.crucible.BlockCrucible;
import com.camellias.mysticalmetallurgy.common.block.crucible.TileCrucible;
import com.camellias.mysticalmetallurgy.common.block.rack.BlockRack;
import com.camellias.mysticalmetallurgy.common.block.rack.TileRack;
import com.camellias.mysticalmetallurgy.common.effect.EffectDense;
import com.camellias.mysticalmetallurgy.common.effect.EffectFire;
import com.camellias.mysticalmetallurgy.common.entity.EntityClump;
import com.camellias.mysticalmetallurgy.common.fluid.BlockMetalFluid;
import com.camellias.mysticalmetallurgy.common.fluid.FluidMysticMetal;
import com.camellias.mysticalmetallurgy.common.item.ItemMetalClump;
import com.camellias.mysticalmetallurgy.common.item.tool.ItemHammer;
import com.camellias.mysticalmetallurgy.common.item.tool.ItemLadle;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryBuilder;


public class RegistrationHandler
{
    @SubscribeEvent
    public static void registerRegistry(RegistryEvent.NewRegistry event)
    {
        //noinspection unchecked
        new RegistryBuilder<>().setName(Effect.REGISTRY_NAME).setType((Class) Effect.class).setMaxID(Effect.MAX_ID).create();
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
//        IRegistry.FLUID.put(FluidMysticMetal.ID, ModFluids.MYSTICAL_METAL);
//        IRegistry.FLUID.put(FluidMysticMetal.ID_FLOW, ModFluids.MYSTICAL_METAL_FLOWING);

        event.getRegistry().registerAll(
                asDefault(new BlockCrucible(), BlockCrucible.LOC),
                asDefault(new BlockBrazier(), BlockBrazier.LOC),
                asDefault(new BlockStoneAnvil(), BlockStoneAnvil.LOC),
                asDefault(new BlockQuenchingBasin(), BlockQuenchingBasin.LOC),
                asDefault(new BlockRack(), BlockRack.LOC)
//                asDefault(new BlockMetalFluid(ModFluids.MYSTICAL_METAL,
//                        Block.Properties
//                                .create(Material.LAVA)
//                                .doesNotBlockMovement()
//                                .tickRandomly()
//                                .hardnessAndResistance(100.0F)
//                                .lightValue(15)),
//                        FluidMysticMetal.ID)
        );
    }

    @SubscribeEvent
    public static void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> event)
    {
        ModTiles.RACK = TileEntityType.register(BlockRack.LOC.toString(), TileEntityType.Builder.create(TileRack::new));
        ModTiles.CRUCIBLE = TileEntityType.register(BlockCrucible.LOC.toString(), TileEntityType.Builder.create(TileCrucible::new));
        ModTiles.STONE_ANVIL = TileEntityType.register(BlockStoneAnvil.LOC.toString(), TileEntityType.Builder.create(TileStoneAnvil::new));
        ModTiles.BASIN = TileEntityType.register(BlockQuenchingBasin.LOC.toString(), TileEntityType.Builder.create(TileQuenchingBasin::new));
    }

    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityType<?>> event)
    {
        EntityType.register(EntityClump.ID.toString(), EntityType.Builder.create(EntityClump.class, EntityClump::new));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                //Blocks
                asItem(ModBlocks.CRUCIBLE, BlockCrucible.LOC),
                asItem(ModBlocks.BRAZIER, BlockBrazier.LOC),
                asItem(ModBlocks.STONE_ANVIL, BlockStoneAnvil.LOC),
                asItem(ModBlocks.BASIN, BlockQuenchingBasin.LOC),
                asItem(ModBlocks.RACK, BlockRack.LOC),
                asItem(ModBlocks.MYSTICAL_LIQUID_METAL, FluidMysticMetal.ID),

                //Items
                asDefault(new ItemMetalClump(new Item.Properties().group(ModGroups.MYSTICAL_METALS_ITEMS)), ItemMetalClump.CLUMP_0_LOC),
                asDefault(new ItemMetalClump(new Item.Properties().group(ModGroups.MYSTICAL_METALS_ITEMS)), ItemMetalClump.CLUMP_1_LOC),
                asDefault(new ItemMetalClump(new Item.Properties().group(ModGroups.MYSTICAL_METALS_ITEMS)), ItemMetalClump.CLUMP_2_LOC),
//                asDefault(new ItemBucket(ModFluids.MYSTICAL_METAL,
//                        new Item.Properties().containerItem(Items.BUCKET).
//                                group(ModGroups.MYSTICAL_METALS_ITEMS).
//                                maxStackSize(1)),
//                        new ResourceLocation(Main.MODID, "metal_bucket")),

                //Tools
                asDefault(new ItemLadle(new Item.Properties().group(ModGroups.MYSTICAL_METALS_ITEMS)), ItemLadle.LOC),
                asDefault(new ItemHammer(new Item.Properties().group(ModGroups.MYSTICAL_METALS_ITEMS)), ItemHammer.LOC)
        );
    }

    //@SubscribeEvent
    //public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    //{
    //    AnvilRecipe.register(new AnvilRecipe(new ItemStack(Items.ARROW), 4, Items.PAPER, "ingotIron", null, Items.COAL, Items.BONE, Items.APPLE, Items.BEEF));
    //}

    @SubscribeEvent
    public static void registerEffects(RegistryEvent.Register<Effect> event)
    {
        event.getRegistry().registerAll(
                new EffectFire(),
                new EffectDense()
        );
    }

    @SubscribeEvent
    public static void registerItemEffects(RegisterItemEffectsEvent event)
    {
        event.getRegistry().registerTagWithTrait(new ResourceLocation("forge","ingots/iron"), EffectDense.ID, 3);
        event.getRegistry().registerTagWithTrait(new ResourceLocation("forge","ingots/iron"), EffectFire.ID, 5);
        event.getRegistry().registerTagWithTrait(new ResourceLocation("forge","ingots/gold"), EffectFire.ID, 3);
    }

    //region <helper>
    private static Block asDefault(Block block, ResourceLocation loc)
    {
        return block.setRegistryName(loc);
    }

    private static Item asDefault(Item item, ResourceLocation loc)
    {
        return item.setRegistryName(loc);
    }

    private static Item asItem(Block block, ResourceLocation loc)
    {
        Item ib = new ItemBlock(block, new Item.Properties().group(ModGroups.MYSTICAL_METALS_BLOCKS)).setRegistryName(loc);
        Item.BLOCK_TO_ITEM.put(block, ib);
        return ib;
    }
    //endregion
}
