package com.gharielsl.mystworlds.age;

import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgeBiome {
    private final List<Block> foliage = new ArrayList<>();
    private final Map<Block, Integer> ores = new HashMap<>();
    private final LayersConfiguration layers = new LayersConfiguration(
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    private final BaseBiome baseBiome;

    public AgeBiome(BaseBiome baseBiome) {
        this.baseBiome = baseBiome;
    }

    public BaseBiome getBaseBiome() {
        return baseBiome;
    }

    public LayersConfiguration getLayers() {
        return layers;
    }

    public List<Block> getFoliage() {
        return foliage;
    }

    public Map<Block, Integer> getOres() {
        return ores;
    }

    public record LayersConfiguration(
            List<Block> layer1,
            List<Block> layer2,
            List<Block> layer3,
            List<Block> layer4) {

    }

    public enum BaseBiome { DESERT, FOREST, PLAINS }
}
