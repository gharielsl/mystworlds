package com.gharielsl.mystworlds.block.entity;

import com.gharielsl.mystworlds.MystWorlds;
import com.gharielsl.mystworlds.block.MystWorldsBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MystWorldsBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MystWorlds.MOD_ID);

    public static final RegistryObject<BlockEntityType<WritingTableBlockEntity>> WRITING_TABLE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("writing_table_block_entity", () ->
                    BlockEntityType.Builder.of(WritingTableBlockEntity::new,
                            MystWorldsBlocks.WRITING_TABLE_BLOCK.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<RuneCarvingStationBlockEntity>> RUNE_CARVING_STATION_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rune_carving_station_block_entity", () ->
                    BlockEntityType.Builder.of(RuneCarvingStationBlockEntity::new,
                            MystWorldsBlocks.RUNE_CARVING_STATION.get()
                    ).build(null));
}
