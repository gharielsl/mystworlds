package com.gharielsl.mystworlds.age;

import com.gharielsl.mystworlds.item.MystWorldsItems;
import com.gharielsl.mystworlds.item.MysticalInkItem;
import com.gharielsl.mystworlds.item.RuneItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AgeDescription {
    public static final int SKY_CLEAR = 1;
    public static final int SKY_RAIN = 2;
    public static final int SKY_STORM = 3;
    public static final int SKY_NORMAL = 4;
    public static final int SKY_CHAOS = 5;

    public static final int TIME_DAY = 1;
    public static final int TIME_NIGHT = 2;
    public static final int TIME_NORMAL = 3;
    public static final int TIME_CHAOS = 4;

    private String ageName;
    private int sky;
    private int time;
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

        int i = 0;
        for (RuneItem item : runes) {
            if (item == MystWorldsItems.RUNE_OF_THESSALY.get()) {
                flatRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_PETRA.get()) {
                caveRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_SANTORINI.get()) {
                islandRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_PHILAE.get()) {
                plainsRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_ARCADIA.get()) {
                forestRunesCount++;
            } else if (item == MystWorldsItems.RUNE_OF_MEMPHIS.get()) {
                desertRunesCount++;
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
            }
            i++;
        }

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
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        tag.putString("ageName", ageName);
        tag.putInt("sky", sky);
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
    }

    public String getAgeName() {
        return ageName;
    }

    public int getSky() {
        return sky;
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
}
