package com.gharielsl.mystworlds.age;

import com.gharielsl.mystworlds.item.MystWorldsItems;
import com.gharielsl.mystworlds.item.MysticalInkItem;
import com.gharielsl.mystworlds.item.RuneItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AgeDescription {
    public static final int WEATHER_CLEAR = 1;
    public static final int WEATHER_RAIN = 2;
    public static final int WEATHER_STORM = 3;
    public static final int WEATHER_NORMAL = 4;
    public static final int WEATHER_CHAOS = 5;

    public static final int TIME_DAY = 1;
    public static final int TIME_NIGHT = 2;
    public static final int TIME_NORMAL = 3;
    public static final int TIME_CHAOS = 4;

    public static final int SKY_DEFAULT = 1;
    public static final int SKY_BLUE = 2;
    public static final int SKY_RED = 3;
    public static final int SKY_GREEN = 4;
    public static final int SKY_CHAOS = 5;

    private String ageName;
    private int sky = SKY_DEFAULT;
    private int weather = WEATHER_NORMAL;
    private int time = TIME_NORMAL;
    private int fireAmount;
    private int liquidY = 0;
    private int explosionAmount;
    private int chaosAmount = 1; // higher threshold
    private int carvingThreshold = 15; // no caves
    private int terrainThreshold = 2;
    private int noiseScaleX = 1;
    private int noiseScaleZ = 1;
    private boolean isFloatingIslands = false;
    private boolean isLava = false;
    private int bottomBedrockLayers = 0;
    private int topBedrockLayers = 0;
    private AgeBiome biome1;
    private AgeBiome biome2;
    private AgeBiome biome3;
    private Block caveBlock = Blocks.AIR;

    public AgeDescription(CompoundTag tag) {
        this.load(tag);
    }

    public AgeDescription(List<RuneItem> runes, MysticalInkItem ink1, MysticalInkItem ink2, MysticalInkItem ink3, String ageName) {
        this.ageName = ageName;
        int flatRunesCount = 0;
        int caveRunesCount = 0;
        int islandRunesCount = 0;
        int plainsRunesCount = 0;
        int forestRunesCount = 0;
        int desertRunesCount = 0;
        int mountainRunesCount = 0;
        int waterRunesCount = 0;
        int lavaRunesCount = 0;
        int chaosRunesCount = 0;

        int dayRunesCount = 0;
        int nightRunesCount = 0;
        int chaosTimeRunesCount = 0;

        int clearWeatherCount = 0;
        int rainWeatherCount = 0;
        int thunderWeatherCount = 0;

        Map<Integer, Integer> skyColorScores = new java.util.HashMap<>(Map.of(
                SKY_BLUE, 0,
                SKY_RED, 0,
                SKY_GREEN, 0
        ));
        int skyRunesCount = 0;

        int i = 0;

        List<AgeBiome.BaseBiome> baseBiomes = new ArrayList<>();

        for (RuneItem item : runes) {
            if (item == MystWorldsItems.RUNE_OF_THESSALY.get()) {
                flatRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_PETRA.get()) {
                caveRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_SANTORINI.get()) {
                islandRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_PHILAE.get()) {
                plainsRunesCount++;
                baseBiomes.add(AgeBiome.BaseBiome.PLAINS);
            } else if (item == MystWorldsItems.RUNE_OF_ARCADIA.get()) {
                forestRunesCount++;
                baseBiomes.add(AgeBiome.BaseBiome.FOREST);
            } else if (item == MystWorldsItems.RUNE_OF_SAYA.get()) {
                forestRunesCount++;
                baseBiomes.add(AgeBiome.BaseBiome.TAIGA);
            } else if (item == MystWorldsItems.RUNE_OF_MEMPHIS.get()) {
                desertRunesCount++;
                baseBiomes.add(AgeBiome.BaseBiome.DESERT);
            } else if (item == MystWorldsItems.RUNE_OF_OLYMPUS.get()) {
                mountainRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_LUXOR.get()) {
                chaosRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_DELPHI.get()) {
                chaosRunesCount--;
            } else if (item == MystWorldsItems.RUNE_OF_NAUCRATIS.get()) {
                waterRunesCount++;
            }  else if (item == MystWorldsItems.RUNE_OF_STAFFA.get()) {
                lavaRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_ASWAN.get()) {
                if (i < runes.size() / 2) {
                    bottomBedrockLayers++;
                } else {
                    topBedrockLayers++;
                }
            } else if (item == MystWorldsItems.RUNE_OF_KNOSSOS.get()) {
                rainWeatherCount++;
            } else if (item == MystWorldsItems.RUNE_OF_RHODES.get()) {
                clearWeatherCount++;
            } else if (item == MystWorldsItems.RUNE_OF_ARGOS.get()) {
                thunderWeatherCount++;
            } else if (item == MystWorldsItems.RUNE_OF_NYX.get()) {
                nightRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_HELIOPOLIS.get()) {
                dayRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_HADES.get()) {
                chaosTimeRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_ETNA.get()) {
                fireAmount++;
            } else if (item == MystWorldsItems.RUNE_OF_THERA.get()) {
                explosionAmount++;
                chaosRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_EROS.get()) {
                skyColorScores.put(SKY_RED, skyColorScores.get(SKY_RED) + 1);
                skyRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_DELOS.get()) {
                skyColorScores.put(SKY_BLUE, skyColorScores.get(SKY_BLUE) + 1);
                skyRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_SKIATHOS.get()) {
                skyColorScores.put(SKY_GREEN, skyColorScores.get(SKY_GREEN) + 1);
                skyRunesCount++;
            }
            i++;
        }

        RandomSource random = RandomSource.create();

        biome1 = new AgeBiome((!baseBiomes.isEmpty()) ? baseBiomes.get(0) : null, ink1);
        biome2 = new AgeBiome((1 < baseBiomes.size()) ? baseBiomes.get(1) : null, ink2);
        biome3 = new AgeBiome((2 < baseBiomes.size()) ? baseBiomes.get(2) : null, ink3);

        chaosAmount += chaosRunesCount / 2;
        chaosAmount = Math.max(chaosAmount, 1);
        terrainThreshold += forestRunesCount + (int)(plainsRunesCount / 2f + desertRunesCount / 3f - flatRunesCount * 2f + mountainRunesCount * 3f);
        terrainThreshold = Math.max(terrainThreshold, 1);
        isFloatingIslands = islandRunesCount > flatRunesCount;
        carvingThreshold -= caveRunesCount;
        carvingThreshold = Math.max(carvingThreshold, -15);
        if (flatRunesCount / 2 > plainsRunesCount + forestRunesCount + desertRunesCount && chaosRunesCount < 2) {
            chaosAmount = 0;
        }
        noiseScaleX += flatRunesCount;
        noiseScaleZ += flatRunesCount;
        if (chaosAmount > 1) {
            noiseScaleZ += chaosAmount;
        }
        if (lavaRunesCount > waterRunesCount) {
            isLava = true;
        }
        liquidY = isLava ? lavaRunesCount * 20 : waterRunesCount * 20;
        if (lavaRunesCount + waterRunesCount == 0) {
            liquidY = -1000;
        }
        if (chaosAmount <= 2 && random.nextBoolean()) {
            biome1.getLayers().layer3().set(0, Blocks.STONE);
            biome1.getLayers().layer4().set(0, Blocks.DEEPSLATE);
        }
        if (chaosAmount <= 2 && random.nextBoolean()) {
            biome2.getLayers().layer3().set(0, Blocks.STONE);
            biome2.getLayers().layer4().set(0, Blocks.DEEPSLATE);
        }
        if (chaosAmount <= 2 && random.nextBoolean()) {
            biome3.getLayers().layer3().set(0, Blocks.STONE);
            biome3.getLayers().layer4().set(0, Blocks.DEEPSLATE);
        }
        if (rainWeatherCount + clearWeatherCount + thunderWeatherCount == 0 && chaosAmount <= 2) {
            weather = WEATHER_NORMAL;
        } else if (rainWeatherCount + clearWeatherCount + thunderWeatherCount == 0) {
            weather = WEATHER_CHAOS;
        } else if (clearWeatherCount > rainWeatherCount + thunderWeatherCount && chaosAmount == 1) {
            weather = WEATHER_CLEAR;
        } else if (rainWeatherCount > thunderWeatherCount) {
            weather = WEATHER_RAIN;
        } else {
            weather = WEATHER_STORM;
        }
        if (chaosTimeRunesCount > 0) {
            time = TIME_CHAOS;
        } else if (chaosAmount > 2 && random.nextBoolean()) {
            time = TIME_CHAOS;
        } else if (dayRunesCount + nightRunesCount == 0) {
            time = TIME_NORMAL;
        } else if (dayRunesCount > nightRunesCount) {
            time = TIME_DAY;
        } else {
            time = TIME_NIGHT;
        }
        if (chaosAmount > 3) {
            caveBlock = List.of(Blocks.OAK_LOG, Blocks.PACKED_ICE, Blocks.WHITE_WOOL, Blocks.NETHERRACK, Blocks.GLASS).get(random.nextInt(5));
        }

        sky = skyColorScores.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(SKY_DEFAULT);

        if (chaosAmount > 3 && random.nextBoolean()) {
            sky = SKY_CHAOS;
        }
        if (chaosAmount == 1 && skyRunesCount == 0) {
            sky = SKY_DEFAULT;
        }
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        tag.putString("ageName", ageName);
        tag.putInt("sky", sky);
        tag.putInt("weather", weather);
        tag.putInt("time", time);
        tag.putInt("fireAmount", fireAmount);
        tag.putInt("liquidY", liquidY);
        tag.putInt("explosionAmount", explosionAmount);
        tag.putInt("chaosAmount", chaosAmount);
        tag.putInt("carvingThreshold", carvingThreshold);
        tag.putInt("terrainThreshold", terrainThreshold);
        tag.putInt("noiseScaleX", noiseScaleX);
        tag.putInt("noiseScaleZ", noiseScaleZ);
        tag.putBoolean("isFloatingIslands", isFloatingIslands);
        tag.putBoolean("isLava", isLava);
        tag.putInt("bottomBedrockLayers", bottomBedrockLayers);
        tag.putInt("topBedrockLayers", topBedrockLayers);

        tag.put("biome1", biome1.save());
        tag.put("biome2", biome2.save());
        tag.put("biome3", biome3.save());

        tag.putString("caveBlock", ForgeRegistries.BLOCKS.getKey(caveBlock).toString());

        return tag;
    }

    public static void saveToFile(MinecraftServer server, AgeDescription description) throws IOException {
        String ageName = description.getAgeName();
        if (!AgeManager.ageStates.containsKey(ageName)) {
            CompoundTag tag = new CompoundTag();
            tag.put(ageName, description.save());

            Path saveDir = server.getWorldPath(LevelResource.ROOT).resolve("mystworlds");
            Path saveFile = saveDir.resolve("age_descriptions.nbt");

            Files.createDirectories(saveDir);
            CompoundTag existingTag = Files.exists(saveFile) ?
                    NbtIo.readCompressed(saveFile.toFile()) : new CompoundTag();

            existingTag.put(ageName, description.save());
            NbtIo.writeCompressed(existingTag, saveFile.toFile());
        }
    }

    public static AgeDescription loadFromFile(MinecraftServer server, String ageName) throws IOException {
        Path saveDir = server.getWorldPath(LevelResource.ROOT).resolve("mystworlds");
        Path saveFile = saveDir.resolve("age_descriptions.nbt");

        if (Files.exists(saveFile)) {
            CompoundTag existingTag = NbtIo.readCompressed(saveFile.toFile());

            if (existingTag.contains(ageName)) {
                CompoundTag ageTag = existingTag.getCompound(ageName);
                return new AgeDescription(ageTag);
            }
        }

        return null;
    }

    public void load(CompoundTag tag) {
        ageName = tag.getString("ageName");
        sky = tag.getInt("sky");
        weather = tag.getInt("weather");
        time = tag.getInt("time");
        fireAmount = tag.getInt("fireAmount");
        liquidY = tag.getInt("liquidY");
        explosionAmount = tag.getInt("explosionAmount");
        chaosAmount = tag.getInt("chaosAmount");
        carvingThreshold = tag.getInt("carvingThreshold");
        terrainThreshold = tag.getInt("terrainThreshold");
        noiseScaleX = tag.getInt("noiseScaleX");
        noiseScaleZ = tag.getInt("noiseScaleZ");
        isFloatingIslands = tag.getBoolean("isFloatingIslands");
        isLava = tag.getBoolean("isLava");
        bottomBedrockLayers = tag.getInt("bottomBedrockLayers");
        topBedrockLayers = tag.getInt("topBedrockLayers");

        biome1 = new AgeBiome(tag.getCompound("biome1"));
        biome2 = new AgeBiome(tag.getCompound("biome2"));
        biome3 = new AgeBiome(tag.getCompound("biome3"));

        caveBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("caveBlock")));
    }

    public String getAgeName() {
        return ageName;
    }

    public int getSky() {
        return sky;
    }

    public int getWeather() {
        return weather;
    }

    public int getTime() {
        return time;
    }

    public int getFireAmount() {
        return fireAmount;
    }

    public int getLiquidY() {
        return liquidY;
    }

    public int getExplosionAmount() {
        return explosionAmount;
    }

    public int getChaosAmount() {
        return chaosAmount;
    }

    public int getCarvingThreshold() {
        return carvingThreshold;
    }

    public int getTerrainThreshold() {
        return terrainThreshold;
    }

    public boolean isFloatingIslands() {
        return isFloatingIslands;
    }

    public boolean isLava() {
        return isLava;
    }

    public int getNoiseScaleX() {
        return noiseScaleX;
    }

    public int getNoiseScaleZ() {
        return noiseScaleZ;
    }

    public int getBottomBedrockLayers() {
        return bottomBedrockLayers;
    }

    public int getTopBedrockLayers() {
        return topBedrockLayers;
    }

    public AgeBiome getBiome1() {
        return biome1;
    }

    public AgeBiome getBiome2() {
        return biome2;
    }

    public AgeBiome getBiome3() {
        return biome3;
    }

    public Block getCaveBlock() {
        return caveBlock;
    }

    public AgeBiome getBiome(int index) {
        if (index == 0) {
            return biome1;
        }
        if (index == 1) {
            return biome2;
        }
        return biome3;
    }
}
