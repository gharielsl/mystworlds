package com.gharielsl.mystworlds.world;

import com.gharielsl.mystworlds.age.AgeDescription;
import com.gharielsl.mystworlds.age.AgeManager;
import com.gharielsl.mystworlds.event.ServerEventsHandler;
import com.gharielsl.mystworlds.util.FastNoiseLite;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MystWorldGenerator extends NoiseBasedChunkGenerator {
//    public static final Codec<MystWorldGenerator> CODEC = RecordCodecBuilder.create((p_255577_) -> {
//        return p_255577_.group(FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(FlatLevelSource::settings)).apply(p_255577_, p_255577_.stable(MystWorldGenerator::new));
//    });

    public static final Codec<MystWorldGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                BiomeSource.CODEC.fieldOf("biome_source").forGetter(nbChunkGen -> {
                    try {
                        Field field = NoiseBasedChunkGenerator.class.getDeclaredField("biomeSource");
                        field.setAccessible(true);
                        return (BiomeSource) field.get(nbChunkGen);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }),
                NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(nbChunkGen -> {
                    try {
                        Field field = NoiseBasedChunkGenerator.class.getDeclaredField("settings");
                        field.setAccessible(true);
                        return (Holder<NoiseGeneratorSettings>) field.get(nbChunkGen);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
        ).apply(instance, instance.stable(MystWorldGenerator::new));
    });

    public MystWorldGenerator(BiomeSource p_256415_, Holder<NoiseGeneratorSettings> p_256182_) {
        super(p_256415_, p_256182_);
    }

//    public MystWorldGenerator(FlatLevelGeneratorSettings settings) {
//        super(settings);
//    }

    private void setBiome(ChunkAccess chunk, BlockPos posInChunk, Holder<Biome> biomeHolder) {
        BlockState oldState = chunk.getBlockState(posInChunk);
        if (oldState.isAir()) {
            chunk.setBlockState(posInChunk, Blocks.BARRIER.defaultBlockState(), false);
        }

        try {
            int sectionIndex = chunk.getSectionIndex(posInChunk.getY());
            LevelChunkSection section = chunk.getSections()[sectionIndex];

            Field biomesField = LevelChunkSection.class.getDeclaredField("biomes");
            biomesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            PalettedContainer<Holder<Biome>> biomes = (PalettedContainer<Holder<Biome>>) biomesField.get(section);

            int biomeX = (posInChunk.getX() & 15) >> 2;
            int biomeY = (posInChunk.getY() & 15) >> 2;
            int biomeZ = (posInChunk.getZ() & 15) >> 2;

            biomes.set(biomeX, biomeY, biomeZ, biomeHolder);

            if (chunk instanceof net.minecraft.world.level.chunk.LevelChunk levelChunk) {
                levelChunk.setUnsaved(true);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            chunk.setBlockState(posInChunk, oldState, false);
        }
    }

    public int getBaseHeight(int x, int z, Heightmap.Types types, LevelHeightAccessor heightAccessor, RandomState randomState) {
//        if (ServerEventsHandler.SERVER == null) {
//            return getSpawnHeight(heightAccessor);
//        }
//        ServerLevel level = ServerEventsHandler.SERVER.getLevel(AgeManager.AGE_DIM_KEY);
//        if (level == null) {
//            return getSpawnHeight(heightAccessor);
//        }
//        return level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);

        String matchingAgeState = AgeManager.ageStates.entrySet().stream()
                .filter(entry -> entry.getValue().bounds().isWithinBounds(x, z))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        AgeDescription description = null;

        if (matchingAgeState != null) {
            try {
                description = AgeDescription.loadFromFile(ServerEventsHandler.SERVER, matchingAgeState);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (description == null) {
            return getSpawnHeight(heightAccessor);
        }

        FastNoiseLite noise = new FastNoiseLite((int) getSeed());
        float scale = description.getChaosAmount() / 2f;
        float terrainHeightNoise = noise.GetNoise(x * scale / description.getNoiseScaleX(), z * scale / description.getNoiseScaleZ());
        float terrainHeightNoise2 = noise.GetNoise(x * scale / description.getNoiseScaleX() * 2f + 1000f, z * scale / description.getNoiseScaleZ() * 2f + 1000f);
        int terrainHeight = (int) (description.getTerrainThreshold() * terrainHeightNoise * 16) + 34;
        int terrainHeight2 = (int) (description.getTerrainThreshold() * terrainHeightNoise2 * 21) + 35;
        return Math.max(terrainHeight, terrainHeight2);
    }

    @Override
    public int getSpawnHeight(LevelHeightAccessor p_158279_) {
        return 64;
    }

    private CompletableFuture<ChunkAccess> fillFlat(ChunkAccess chunkAccess, Heightmap heightmap, Heightmap heightmap1) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = 0; x < 16; ++x) {
            for (int y = chunkAccess.getMinBuildHeight(); y < 14; ++y) {
                for (int z = 0; z < 16; ++z) {
                    BlockState state = Blocks.AIR.defaultBlockState();

                    if (y == 0) {
                        state = Blocks.BEDROCK.defaultBlockState();
                    }
                    else if (y < 9) {
                        state = Blocks.STONE.defaultBlockState();
                    }
                    else if (y < 12) {
                        state = Blocks.DIRT.defaultBlockState();
                    }
                    else if (y == 12) {
                        state = Blocks.GRASS_BLOCK.defaultBlockState();
                    }

                    chunkAccess.setBlockState(pos.set(x, y, z), state, false);
                    heightmap.update(x, y, z, state);
                    heightmap1.update(x, y, z, state);
                }
            }
        }

        return CompletableFuture.completedFuture(chunkAccess);
    }

    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess) {
        Heightmap heightmap = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        Random random = new Random();
        Holder<Biome> riverBiome = null;

        if (ServerEventsHandler.SERVER != null) {
            Registry<Biome> biomeRegistry = ServerEventsHandler.SERVER.registryAccess().registryOrThrow(Registries.BIOME);
            try { riverBiome = biomeRegistry.getHolderOrThrow(Biomes.RIVER); } catch (Exception e) {}
        }

        ChunkPos chunkPos = chunkAccess.getPos();
        int blockX = chunkPos.x << 4;
        int blockZ = chunkPos.z << 4;

        String matchingAgeState = AgeManager.ageStates.entrySet().stream()
                .filter(entry -> entry.getValue().bounds().isWithinBounds(blockX, blockZ))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        AgeDescription description = null;

        if (matchingAgeState != null) {
            try {
                description = AgeDescription.loadFromFile(ServerEventsHandler.SERVER, matchingAgeState);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (description == null) {
            return fillFlat(chunkAccess, heightmap, heightmap1);
        }

        Block liquid = description.isLava() ? Blocks.LAVA : Blocks.WATER;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        FastNoiseLite noise = new FastNoiseLite((int) getSeed());

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                int worldX = blockX + x;
                int worldZ = blockZ + z;

                float scale = description.getChaosAmount() / 2f;
                float terrainHeightNoise;
                float terrainHeightNoise2;
                int terrainHeight = 0;
                int terrainHeight2 = 0;
                if (!description.isFloatingIslands()) {
                    terrainHeightNoise = noise.GetNoise(worldX * scale / description.getNoiseScaleX(), worldZ * scale / description.getNoiseScaleZ());
                    terrainHeightNoise2 = noise.GetNoise(worldX * scale / description.getNoiseScaleX() * 2f + 1000f, worldZ * scale / description.getNoiseScaleZ() * 2f + 1000f);
                    terrainHeight = (int) (description.getTerrainThreshold() * terrainHeightNoise * 16) + 34;
                    terrainHeight2 = (int) (description.getTerrainThreshold() * terrainHeightNoise2 * 21) + 35;
                }
                for (int y = chunkAccess.getMinBuildHeight(); y < chunkAccess.getMaxBuildHeight() - 1; ++y) {
                    pos.set(x, y, z);
                    if (description.isFloatingIslands()) {
                        terrainHeightNoise = noise.GetNoise(worldX * scale / description.getNoiseScaleX(), y, worldZ * scale / description.getNoiseScaleZ());
                        terrainHeightNoise2 = noise.GetNoise(worldX * scale / description.getNoiseScaleX() * 2f + 1000f, y, worldZ * scale / description.getNoiseScaleZ() * 2f + 1000f);
                        terrainHeight = (int) (description.getTerrainThreshold() * terrainHeightNoise * 16) + 34;
                        terrainHeight2 = (int) (description.getTerrainThreshold() * terrainHeightNoise2 * 21) + 35;
                    }
                    int caveNoise = (int) (noise.GetNoise(worldX, y, worldZ) * 16);
                    int terrainHeightWithOffset = terrainHeight;// - (int)((caveNoise / 2f) * description.getChaosAmount());
                    int combinedHeight = Math.max(terrainHeightWithOffset, terrainHeight2);
                    if (description.getChaosAmount() > 5) {
                        terrainHeight -= (int)((caveNoise / 16f) * 2);
                    }
                    if ((y <= combinedHeight && !description.isFloatingIslands()) || (combinedHeight > 50 && description.isFloatingIslands())) {
                        chunkAccess.setBlockState(pos, Blocks.STONE.defaultBlockState(), true);
                    }
                    if (!description.isFloatingIslands() && combinedHeight < description.getLiquidY() && riverBiome != null && y < description.getLiquidY() + 12 && y > combinedHeight) {
                        setBiome(chunkAccess, pos, riverBiome);
                    }
                }
            }
        }

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                boolean foundHeight = false;
                int deep = 0;
                for (int y = chunkAccess.getMaxBuildHeight() - 1; y >= chunkAccess.getMinBuildHeight(); --y) {
                    pos.set(x, y, z);
                    BlockState state = chunkAccess.getBlockState(pos);
                    BlockState stateAbove = chunkAccess.getBlockState(pos.above());
                    if (!state.isAir() && (stateAbove.isAir() || stateAbove.is(liquid))) {
                        chunkAccess.setBlockState(pos, getSurfaceBlock(y, description.getLiquidY()), true);
                        if (!foundHeight) {
                            foundHeight = true;
                        }
                    }
                    if (!state.isAir() && foundHeight) {
                        if (deep == 0) {
                            if (y > 100) {
                                chunkAccess.setBlockState(pos, Blocks.SNOW_BLOCK.defaultBlockState(), true);
                            } else {
                                BlockState surface = getSurfaceBlock(y, description.getLiquidY());
                                chunkAccess.setBlockState(pos, surface, true);
                            }
                        } else if (deep <= 3) {
                            if (stateAbove.is(Blocks.SAND)) {
                                chunkAccess.setBlockState(pos, Blocks.SAND.defaultBlockState(), true);
                            } else {
                                chunkAccess.setBlockState(pos, Blocks.DIRT.defaultBlockState(), true);
                            }
                        } else if (deep <= 40) {
                            chunkAccess.setBlockState(pos, Blocks.STONE.defaultBlockState(), true);
                        } else {
                            chunkAccess.setBlockState(pos, Blocks.DEEPSLATE.defaultBlockState(), true);
                        }
                    } else if (y < description.getLiquidY()) {
                        chunkAccess.setBlockState(pos, liquid.defaultBlockState(), true);
                    }
                    if (foundHeight) {
                        deep++;
                    }
                }
                if (!foundHeight) {

                }
            }
        }

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                int worldX = blockX + x;
                int worldZ = blockZ + z;

                for (int y = chunkAccess.getMaxBuildHeight() - 1; y >= chunkAccess.getMinBuildHeight(); --y) {
                    pos.set(x, y, z);
                    BlockState state = chunkAccess.getBlockState(pos);
                    BlockState stateAbove = chunkAccess.getBlockState(pos.above());
                    BlockState stateBelow = Blocks.AIR.defaultBlockState();
                    if (y > chunkAccess.getMinBuildHeight()) {
                        stateBelow = chunkAccess.getBlockState(pos.below());
                    }

                    int caveNoise = (int) (16 * (noise.GetNoise(worldX * 2f, y * 6f, worldZ * 2f)));
                    if (caveNoise > 12 && !state.is(liquid) && !stateAbove.is(liquid)) {
                        chunkAccess.setBlockState(pos, Blocks.AIR.defaultBlockState(), true);
                        if (stateBelow.is(Blocks.DIRT)) {
                            chunkAccess.setBlockState(pos.below(), Blocks.GRASS_BLOCK.defaultBlockState(), true);
                        }
                    }
                    if (y < chunkAccess.getMinBuildHeight() + description.getBottomBedrockLayers() * 2 && random.nextBoolean()) {
                        chunkAccess.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), true);
                    }
                    heightmap.update(x, y, z, chunkAccess.getBlockState(pos));
                    heightmap1.update(x, y, z, chunkAccess.getBlockState(pos));
                    if (y == chunkAccess.getMinBuildHeight() && description.getBottomBedrockLayers() > 0) {
                        chunkAccess.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), true);
                    }
                    if (y == chunkAccess.getMaxBuildHeight() - 1 && description.getTopBedrockLayers() > 0) {
                        chunkAccess.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(chunkAccess);
    }

    private BlockState getSurfaceBlock(int y, int seaLevel) {
        if (y < seaLevel - 3) {
            return Blocks.DIRT.defaultBlockState();
        }
        if (y < seaLevel + 1) {
            return Blocks.SAND.defaultBlockState();
        }
        return Blocks.GRASS_BLOCK.defaultBlockState();
    }

    private long getSeed() {
        MinecraftServer server = ServerEventsHandler.SERVER;
        if (server == null) {
            return 0;
        }
        ServerLevel overworld = server.getLevel(ServerLevel.OVERWORLD);
        if (overworld != null) {
            return overworld.getSeed();
        }
        return 0;
    }
}
