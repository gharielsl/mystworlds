package com.gharielsl.mystworlds.world;

import com.gharielsl.mystworlds.MystWorlds;
import com.gharielsl.mystworlds.age.AgeBiome;
import com.gharielsl.mystworlds.age.AgeDescription;
import com.gharielsl.mystworlds.age.AgeManager;
import com.gharielsl.mystworlds.event.ServerEventsHandler;
import com.gharielsl.mystworlds.mixin.ChunkGeneratorAccessor;
import com.gharielsl.mystworlds.mixin.LevelChunkSectionAccessor;
import com.gharielsl.mystworlds.mixin.NoiseBasedChunkGeneratorAccessor;
import com.gharielsl.mystworlds.util.FastNoiseLite;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MystWorldGenerator extends NoiseBasedChunkGenerator {
//    public static final Codec<MystWorldGenerator> CODEC = RecordCodecBuilder.create((p_255577_) -> {
//        return p_255577_.group(FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(FlatLevelSource::settings)).apply(p_255577_, p_255577_.stable(MystWorldGenerator::new));
//    });

    public static final Codec<MystWorldGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                BiomeSource.CODEC.fieldOf("biome_source").forGetter(nbChunkGen -> {
                    return ((ChunkGeneratorAccessor) nbChunkGen).getBiomeSource();
                }),
                NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(nbChunkGen -> {
                    return ((NoiseBasedChunkGeneratorAccessor) nbChunkGen).getSettings();
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

            @SuppressWarnings("unchecked")
            PalettedContainer<Holder<Biome>> biomes = (PalettedContainer) ((LevelChunkSectionAccessor) section).getBiomes();

            int biomeX = (posInChunk.getX() & 15) >> 2;
            int biomeY = (posInChunk.getY() & 15) >> 2;
            int biomeZ = (posInChunk.getZ() & 15) >> 2;

            biomes.set(biomeX, biomeY, biomeZ, biomeHolder);

            if (chunk instanceof LevelChunk levelChunk) {
                levelChunk.setUnsaved(true);
            }
        } catch (Exception e) {
//            MystWorlds.LOGGER.error(e.toString());
            e.printStackTrace();
        } finally {
            chunk.setBlockState(posInChunk, oldState, false);
        }
    }

    public static int getBiome(int x, int z) {
        FastNoiseLite noise = new FastNoiseLite((int) getSeed());
        float n1 = noise.GetNoise(x / 2f, z / 2f + 2000);
        float n2 = noise.GetNoise(x + 2000, z);
        if (n1 <= 0 && n2 >= 0) return 0;
        if (n1 >= 0 && n2 <= 0) return 1;
        return 2;
    }

    public int getBaseHeight(int x, int z, Heightmap.Types types, LevelHeightAccessor heightAccessor, RandomState randomState) {
        String matchingAgeState = AgeManager.ageStates.entrySet().stream()
                .filter(entry -> entry.getValue().bounds().isWithinBounds(x, z))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        AgeDescription description = null;

        if (matchingAgeState != null) {
            description = AgeManager.getDescription(matchingAgeState);
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
        Holder<Biome> desertBiome = null;
        Holder<Biome> plainsBiome = null;
        Holder<Biome> forestBiome = null;
        Holder<Biome> taigaBiome = null;
        Holder<Biome> snowyTaigaBiome = null;
        

        if (ServerEventsHandler.SERVER != null) {
            Registry<Biome> biomeRegistry = ServerEventsHandler.SERVER.registryAccess().registryOrThrow(Registries.BIOME);
            try { riverBiome = biomeRegistry.getHolderOrThrow(Biomes.RIVER); } catch (Exception e) {}
            try { desertBiome = biomeRegistry.getHolderOrThrow(Biomes.DESERT); } catch (Exception e) {}
            try { plainsBiome = biomeRegistry.getHolderOrThrow(Biomes.PLAINS); } catch (Exception e) {}
            try { forestBiome = biomeRegistry.getHolderOrThrow(Biomes.FOREST); } catch (Exception e) {}
            try { taigaBiome = biomeRegistry.getHolderOrThrow(Biomes.TAIGA); } catch (Exception e) {}
            try { snowyTaigaBiome = biomeRegistry.getHolderOrThrow(Biomes.SNOWY_TAIGA); } catch (Exception e) {}
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
            description = AgeManager.getDescription(matchingAgeState);
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
                int biome = getBiome(worldX, worldZ);
                AgeBiome.BaseBiome baseBiome = description.getBiome(biome).getBaseBiome();

                float scale = description.getChaosAmount() / 2f;
                float terrainHeightNoise;
                float terrainHeightNoise2;
                float terrainHeight = 0;
                float terrainHeight2 = 0;
                if (!description.isFloatingIslands()) {
                    terrainHeightNoise = noise.GetNoise(worldX * scale / description.getNoiseScaleX(), worldZ * scale / description.getNoiseScaleZ());
                    terrainHeightNoise2 = noise.GetNoise(worldX * scale / description.getNoiseScaleX() * 2f + 1000f, worldZ * scale / description.getNoiseScaleZ() * 2f + 1000f);
                    terrainHeight = (description.getTerrainThreshold() * terrainHeightNoise * 16) + 34;
                    terrainHeight2 = (description.getTerrainThreshold() * terrainHeightNoise2 * 21) + 35;
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
                    float terrainHeightWithOffset = terrainHeight;// - (int)((caveNoise / 2f) * description.getChaosAmount());
                    float combinedHeight = Math.max(terrainHeightWithOffset, terrainHeight2);
                    if (description.getChaosAmount() > 5) {
                        terrainHeight -= (int)((caveNoise / 16f) * 2);
                    }
                    if ((y <= combinedHeight && !description.isFloatingIslands()) || (combinedHeight > 70 && description.isFloatingIslands())) {
                        chunkAccess.setBlockState(pos, Blocks.STONE.defaultBlockState(), false);
                    }
                    if ((y <= combinedHeight + 2 && y > combinedHeight - 4 && !description.isFloatingIslands()) || (description.isFloatingIslands() && combinedHeight > 50)) {
                        if (baseBiome == AgeBiome.BaseBiome.DESERT && desertBiome != null) {
                            setBiome(chunkAccess, pos, desertBiome);
                        } else if (baseBiome == AgeBiome.BaseBiome.PLAINS && plainsBiome != null) {
                            setBiome(chunkAccess, pos, plainsBiome);
                        } else if (baseBiome == AgeBiome.BaseBiome.FOREST && forestBiome != null) {
                            setBiome(chunkAccess, pos, forestBiome);
                        } else if (baseBiome == AgeBiome.BaseBiome.TAIGA && taigaBiome != null) {
                            if (y > Math.max(description.getLiquidY(), 0) + 66) {
                                setBiome(chunkAccess, pos, snowyTaigaBiome);
                            } else {
                                setBiome(chunkAccess, pos, taigaBiome);
                            }
                        }
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
                int worldX = blockX + x;
                int worldZ = blockZ + z;
                int biome = getBiome(worldX, worldZ);
                Block GRASS = description.getBiome(biome).getLayers().layer1().get(0);
                Block DIRT = description.getBiome(biome).getLayers().layer2().get(0);
                Block SAND = description.getBiome(biome).getBaseBiome() == AgeBiome.BaseBiome.DESERT ? description.getBiome(biome).getLayers().layer2().get(0) : Blocks.SAND;
                Block STONE = description.getBiome(biome).getLayers().layer3().get(0);
                Block DEEPSLATE = description.getBiome(biome).getLayers().layer4().get(0);

                for (int y = chunkAccess.getMaxBuildHeight() - 1; y >= chunkAccess.getMinBuildHeight(); --y) {
                    pos.set(x, y, z);
                    BlockState state = chunkAccess.getBlockState(pos);
                    BlockState stateAbove = chunkAccess.getBlockState(pos.above());
                    if (!state.isAir() && (stateAbove.isAir() || stateAbove.is(liquid))) {
                        chunkAccess.setBlockState(pos, getSurfaceBlock(y, description.getLiquidY(), DIRT, SAND, GRASS), false);
                        if (!foundHeight) {
                            foundHeight = true;
                        }
                    }
                    if (!state.isAir() && foundHeight) {
                        if (deep == 0) {
                            if (y > Math.max(description.getLiquidY(), 0) + 80) {
                                chunkAccess.setBlockState(pos, Blocks.SNOW_BLOCK.defaultBlockState(), false);
                            } else {
                                BlockState surface = getSurfaceBlock(y, description.getLiquidY(), DIRT, SAND, GRASS);
                                chunkAccess.setBlockState(pos, surface, false);
                            }
                            if (random.nextInt(300) < description.getFireAmount()) {
                                chunkAccess.setBlockState(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), false);
                            } else if (random.nextInt(300) < description.getFireAmount()) {
                                chunkAccess.setBlockState(pos, Blocks.NETHERRACK.defaultBlockState(), false);
                                chunkAccess.setBlockState(pos.above(), Blocks.FIRE.defaultBlockState(), false);
                            }
                        } else if (deep <= 3) {
                            if (stateAbove.is(SAND)) {
                                chunkAccess.setBlockState(pos, SAND.defaultBlockState(), false);
                            } else {
                                chunkAccess.setBlockState(pos, DIRT.defaultBlockState(), false);
                            }
                        } else if (deep <= 40) {
                            chunkAccess.setBlockState(pos, STONE.defaultBlockState(), false);
                        } else {
                            chunkAccess.setBlockState(pos, DEEPSLATE.defaultBlockState(), false);
                        }
                    } else if (y < description.getLiquidY()) {
                        chunkAccess.setBlockState(pos, liquid.defaultBlockState(), false);
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
                int biome = getBiome(worldX, worldZ);
                Block GRASS = description.getBiome(biome).getLayers().layer1().get(0);
                Block DIRT = description.getBiome(biome).getLayers().layer2().get(0);

                for (int y = chunkAccess.getMaxBuildHeight() - 1; y >= chunkAccess.getMinBuildHeight(); --y) {
                    pos.set(x, y, z);
                    BlockState state = chunkAccess.getBlockState(pos);
                    BlockState stateAbove = chunkAccess.getBlockState(pos.above());
                    BlockState stateBelow = Blocks.AIR.defaultBlockState();
                    if (y > chunkAccess.getMinBuildHeight()) {
                        stateBelow = chunkAccess.getBlockState(pos.below());
                    }

                    float distanceToY = Math.abs(y + 30);
                    float scale = 1f + (1f / (1f + distanceToY));
                    float caveNoise = (16 * scale * noise.GetNoise(worldX * 2f, y * 6f, worldZ * 2f));

                    if (caveNoise > description.getCarvingThreshold() && !state.is(liquid) && !stateAbove.is(liquid)) {
                        chunkAccess.setBlockState(pos, description.getCaveBlock().defaultBlockState(), false);
                        if (stateBelow.is(DIRT)) {
                            chunkAccess.setBlockState(pos.below(), GRASS.defaultBlockState(), false);
                        }
                    }
                    if (y < chunkAccess.getMinBuildHeight() + description.getBottomBedrockLayers() * 2 && random.nextBoolean()) {
                        chunkAccess.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
                    }
                    heightmap.update(x, y, z, chunkAccess.getBlockState(pos));
                    heightmap1.update(x, y, z, chunkAccess.getBlockState(pos));
                    if (y == chunkAccess.getMinBuildHeight() && description.getBottomBedrockLayers() > 0) {
                        chunkAccess.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
                    }
                    if (y == chunkAccess.getMaxBuildHeight() - 1 && description.getTopBedrockLayers() > 0) {
                        chunkAccess.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
                    }
                }
            }
        }

        

        return CompletableFuture.completedFuture(chunkAccess);
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel p_223087_, ChunkAccess p_223088_, StructureManager p_223089_) {
        ChunkPos chunkpos = p_223088_.getPos();
        if (!SharedConstants.debugVoidTerrain(chunkpos)) {
            SectionPos sectionpos = SectionPos.of(chunkpos, p_223087_.getMinSection());
            BlockPos blockpos = sectionpos.origin();
            Registry<Structure> registry = p_223087_.registryAccess().registryOrThrow(Registries.STRUCTURE);
            Map<Integer, List<Structure>> map = registry.stream().collect(Collectors.groupingBy((p_223103_) -> {
                return p_223103_.step().ordinal();
            }));
            List<FeatureSorter.StepFeatureData> list = ((ChunkGeneratorAccessor)this).featuresPerStep().get();
            WorldgenRandom worldgenrandom = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
            long i = worldgenrandom.setDecorationSeed(p_223087_.getSeed(), blockpos.getX(), blockpos.getZ());
            Set<Holder<Biome>> set = new ObjectArraySet<>();
            ChunkPos.rangeClosed(sectionpos.chunk(), 1).forEach((p_223093_) -> {
                ChunkAccess chunkaccess = p_223087_.getChunk(p_223093_.x, p_223093_.z);

                for(LevelChunkSection levelchunksection : chunkaccess.getSections()) {
                    levelchunksection.getBiomes().getAll(set::add);
                }

            });
            set.retainAll(this.biomeSource.possibleBiomes());
            int j = list.size();

            try {
                Registry<PlacedFeature> registry1 = p_223087_.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
                int i1 = Math.max(GenerationStep.Decoration.values().length, j);

                for(int k = 0; k < i1; ++k) {
                    int l = 0;
                    if (p_223089_.shouldGenerateStructures()) {
                        for(Structure structure : map.getOrDefault(k, Collections.emptyList())) {
                            worldgenrandom.setFeatureSeed(i, l, k);
                            Supplier<String> supplier = () -> {
                                return registry.getResourceKey(structure).map(Object::toString).orElseGet(structure::toString);
                            };

                            try {
                                p_223087_.setCurrentlyGenerating(supplier);
                                p_223089_.startsForStructure(sectionpos, structure).forEach((p_223086_) -> {
                                    p_223086_.placeInChunk(p_223087_, p_223089_, this, worldgenrandom, ((ChunkGeneratorAccessor)this).getWritableArea(p_223088_), chunkpos);
                                });
                            } catch (Exception exception) {
                                CrashReport crashreport1 = CrashReport.forThrowable(exception, "Feature placement");
                                crashreport1.addCategory("Feature").setDetail("Description", supplier::get);
                                throw new ReportedException(crashreport1);
                            }

                            ++l;
                        }
                    }

                    if (k < j) {
                        IntSet intset = new IntArraySet();

                        for(Holder<Biome> holder : set) {
                            List<HolderSet<PlacedFeature>> list1 = ((ChunkGeneratorAccessor)this).generationSettingsGetter().apply(holder).features();
                            if (k < list1.size()) {
                                HolderSet<PlacedFeature> holderset = list1.get(k);
                                FeatureSorter.StepFeatureData featuresorter$stepfeaturedata1 = list.get(k);
                                holderset.stream().map(Holder::value).forEach((p_223174_) -> {
                                    intset.add(featuresorter$stepfeaturedata1.indexMapping().applyAsInt(p_223174_));
                                });
                            }
                        }

                        int j1 = intset.size();
                        int[] aint = intset.toIntArray();
                        Arrays.sort(aint);
                        FeatureSorter.StepFeatureData featuresorter$stepfeaturedata = list.get(k);

                        String age = AgeManager.ageStates.entrySet().stream()
                                .filter(entry -> entry.getValue().bounds().isWithinBounds(blockpos.getX(), blockpos.getZ()))
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElse(null);
                        if (age == null) {
                            return;
                        }
                        if (ServerEventsHandler.SERVER == null) {
                            return;
                        }
                        AgeDescription description = AgeManager.getDescription(age);
                        if (description == null) {
                            return;
                        }
                        int biome = MystWorldGenerator.getBiome(blockpos.getX(), blockpos.getZ());
                        AgeBiome ageBiome = description.getBiome(biome);

                        for (int k1 = 0; k1 < j1; ++k1) {
                            int l1 = aint[k1];
                            PlacedFeature placedfeature = featuresorter$stepfeaturedata.features().get(l1);

                            Optional<ResourceKey<PlacedFeature>> keyOptional = registry1.getResourceKey(placedfeature);
                            Supplier<String> supplier1 = () -> keyOptional.map(ResourceKey::toString).orElseGet(placedfeature::toString);

                            if (keyOptional.isEmpty() || !ageBiome.getFeatures().contains(keyOptional.get().location().getPath())) {
                                continue;
                            }

                            worldgenrandom.setFeatureSeed(i, l1, k);

                            try {
                                p_223087_.setCurrentlyGenerating(supplier1);
                                placedfeature.placeWithBiomeCheck(p_223087_, this, worldgenrandom, blockpos);
                            } catch (Exception exception1) {
                                CrashReport crashreport2 = CrashReport.forThrowable(exception1, "Feature placement");
                                crashreport2.addCategory("Feature").setDetail("Description", supplier1::get);
                                throw new ReportedException(crashreport2);
                            }
                        }
                    }
                }

                p_223087_.setCurrentlyGenerating((Supplier<String>)null);
            } catch (Exception exception2) {
                CrashReport crashreport = CrashReport.forThrowable(exception2, "Biome decoration");
                crashreport.addCategory("Generation").setDetail("CenterX", chunkpos.x).setDetail("CenterZ", chunkpos.z).setDetail("Seed", i);
                throw new ReportedException(crashreport);
            }
        }
    }

    private BlockState getSurfaceBlock(int y, int seaLevel, Block dirt, Block sand, Block grass) {
        if (y < seaLevel - 3) {
            return dirt.defaultBlockState();
        }
        if (y < seaLevel + 1) {
            return sand.defaultBlockState();
        }
        return grass.defaultBlockState();
    }

    private static long getSeed() {
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
