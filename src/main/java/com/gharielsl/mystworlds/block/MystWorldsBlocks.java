package com.gharielsl.mystworlds.block;

import com.gharielsl.mystworlds.MystWorlds;
import com.gharielsl.mystworlds.item.MystWorldsItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MystWorldsBlocks {
    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MystWorlds.MOD_ID);

    public static final Supplier<Block> WRITING_TABLE_BLOCK = registerBlockWithItem("writing_table", WritingTableBlock::new, new Item.Properties());
    public static final Supplier<Block> RUNE_CARVING_STATION = registerBlockWithItem("rune_carving_station", RuneCarvingStationBlock::new, new Item.Properties());

    public static Supplier<Block> TERRAIN_RUNE_ORE = registerBlockWithItem("terrain_rune_ore",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE)), new Item.Properties());
    public static Supplier<Block> STABILITY_RUNE_ORE = registerBlockWithItem("stability_rune_ore",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE)), new Item.Properties());
    public static Supplier<Block> SKY_RUNE_ORE = registerBlockWithItem("sky_rune_ore",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE)), new Item.Properties());
    public static Supplier<Block> TIME_RUNE_ORE = registerBlockWithItem("time_rune_ore",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE)), new Item.Properties());

    // END OF BLOCKS

    private static <T extends Block> Supplier<T> registerBlockWithItem(String name, Supplier<T> block, Item.Properties itemProperties) {
        Supplier<T> registryEntry = BLOCKS.register(name, block);
        MystWorldsItems.ITEMS.register(name, () -> new BlockItem(registryEntry.get(), itemProperties));
        return registryEntry;
    }
}
