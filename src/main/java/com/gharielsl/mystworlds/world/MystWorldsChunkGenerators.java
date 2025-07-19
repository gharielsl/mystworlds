package com.gharielsl.mystworlds.world;

import com.gharielsl.mystworlds.MystWorlds;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MystWorldsChunkGenerators {
    public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, MystWorlds.MOD_ID);

    public static final RegistryObject<Codec<MystWorldGenerator>> MYST_WORLD_GENERATOR =
            CHUNK_GENERATORS.register("generator", () -> MystWorldGenerator.CODEC);
}
