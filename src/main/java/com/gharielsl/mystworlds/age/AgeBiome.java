package com.gharielsl.mystworlds.age;

import com.gharielsl.mystworlds.item.MysticalInkItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AgeBiome {
    private LayersConfiguration layers = new LayersConfiguration(
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    private List<String> features = new ArrayList<>();
    private final BaseBiome baseBiome;
    private final MysticalInkItem.MysticalInkColor color;

    public AgeBiome(BaseBiome baseBiome, MysticalInkItem ink) {
        this.baseBiome = baseBiome == null ? BaseBiome.PLAINS : baseBiome;
        this.color = ink == null ? null : ink.getColor();
        setupLayers(ink == null ? null : ink.getColor());
        setupFeatures();
    }

    public AgeBiome(CompoundTag tag) {
        if (tag.contains("BaseBiome")) {
            String biomeName = tag.getString("BaseBiome");
            BaseBiome parsedBiome;
            try {
                parsedBiome = BaseBiome.valueOf(biomeName);
            } catch (IllegalArgumentException e) {
                parsedBiome = BaseBiome.PLAINS;
            }
            this.baseBiome = parsedBiome;
        } else {
            this.baseBiome = BaseBiome.PLAINS;
        }

        if (tag.contains("Layers")) {
            this.layers = LayersConfiguration.load(tag.getCompound("Layers"));
        } else {
            this.layers = new LayersConfiguration(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        this.features = new ArrayList<>();
        if (tag.contains("Features")) {
            ListTag featuresTag = tag.getList("Features", 8); // 8 = TAG_STRING
            for (int i = 0; i < featuresTag.size(); i++) {
                this.features.add(featuresTag.getString(i));
            }
        }

        if (tag.contains("Color")) {
            this.color = MysticalInkItem.MysticalInkColor.valueOf(tag.getString("Color"));
        } else {
            this.color = null;
        }
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        tag.putString("BaseBiome", baseBiome.name());

        tag.put("Layers", layers.save());

        ListTag featuresTag = new ListTag();
        for (String feature : features) {
            featuresTag.add(StringTag.valueOf(feature));
        }
        tag.put("Features", featuresTag);
        if (color != null) {
            tag.putString("Color", color.name());
        }

        return tag;
    }

    private void setupLayers(@Nullable MysticalInkItem.MysticalInkColor color) {
        if (color == null) {
            layers.layer1().add(Blocks.GRASS_BLOCK);
            layers.layer2().add(Blocks.DIRT);
            layers.layer3().add(Blocks.STONE);
            layers.layer4().add(Blocks.DEEPSLATE);
            return;
        }
        switch (color) {
            case RED -> {
                if (baseBiome == BaseBiome.DESERT) {
                    layers.layer1().add(Blocks.RED_SAND);
                    layers.layer1().add(Blocks.RED_CONCRETE_POWDER);
                    layers.layer2().add(Blocks.RED_SANDSTONE);
                    layers.layer2().add(Blocks.RED_TERRACOTTA);
                    layers.layer3().add(Blocks.NETHERRACK);
                    layers.layer4().add(Blocks.NETHER_BRICKS);
                } else if (baseBiome == BaseBiome.PLAINS) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.NETHERRACK);
                    layers.layer4().add(Blocks.DEEPSLATE);
                } else if (baseBiome == BaseBiome.FOREST || baseBiome == BaseBiome.TAIGA) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.NETHERRACK);
                    layers.layer4().add(Blocks.DEEPSLATE);
                }
            }
            case BLUE -> {
                if (baseBiome == BaseBiome.DESERT) {
                    layers.layer1().add(Blocks.BLUE_CONCRETE_POWDER);
                    layers.layer2().add(Blocks.SANDSTONE);
                    layers.layer3().add(Blocks.STONE);
                    layers.layer4().add(Blocks.POLISHED_DEEPSLATE);
                } else if (baseBiome == BaseBiome.PLAINS) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.STONE);
                    layers.layer4().add(Blocks.POLISHED_DEEPSLATE);
                } else if (baseBiome == BaseBiome.FOREST || baseBiome == BaseBiome.TAIGA) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.PACKED_ICE);
                    layers.layer4().add(Blocks.POLISHED_DEEPSLATE);
                }
            }
            case GREEN -> {
                if (baseBiome == BaseBiome.DESERT) {
                    layers.layer1().add(Blocks.GREEN_CONCRETE_POWDER);
                    layers.layer2().add(Blocks.TERRACOTTA);
                    layers.layer3().add(Blocks.MOSSY_COBBLESTONE);
                    layers.layer3().add(Blocks.STONE);
                    layers.layer4().add(Blocks.DEEPSLATE);
                } else if (baseBiome == BaseBiome.PLAINS) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.MOSSY_COBBLESTONE);
                    layers.layer3().add(Blocks.STONE);
                    layers.layer4().add(Blocks.DEEPSLATE);
                } else if (baseBiome == BaseBiome.FOREST || baseBiome == BaseBiome.TAIGA) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer1().add(Blocks.MOSS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.STONE);
                    layers.layer3().add(Blocks.MOSSY_COBBLESTONE);
                    layers.layer4().add(Blocks.DEEPSLATE);
                }
            }
            case PURPLE -> {
                if (baseBiome == BaseBiome.DESERT) {
                    layers.layer1().add(Blocks.PURPLE_CONCRETE_POWDER);
                    layers.layer2().add(Blocks.PURPLE_TERRACOTTA);
                    layers.layer3().add(Blocks.END_STONE);
                    layers.layer4().add(Blocks.POLISHED_DEEPSLATE);
                } else if (baseBiome == BaseBiome.PLAINS) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.PURPUR_BLOCK);
                    layers.layer4().add(Blocks.POLISHED_DEEPSLATE);
                } else if (baseBiome == BaseBiome.FOREST || baseBiome == BaseBiome.TAIGA) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.END_STONE);
                    layers.layer4().add(Blocks.POLISHED_DEEPSLATE);
                }
            }
            case YELLOW -> {
                if (baseBiome == BaseBiome.DESERT) {
                    layers.layer1().add(Blocks.SAND);
                    layers.layer2().add(Blocks.YELLOW_TERRACOTTA);
                    layers.layer3().add(Blocks.END_STONE);
                    layers.layer4().add(Blocks.DEEPSLATE);
                } else if (baseBiome == BaseBiome.PLAINS) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.END_STONE);
                    layers.layer4().add(Blocks.DEEPSLATE);
                } else if (baseBiome == BaseBiome.FOREST || baseBiome == BaseBiome.TAIGA) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.SANDSTONE);
                    layers.layer4().add(Blocks.DEEPSLATE);
                }
            }
            case ORANGE -> {
                if (baseBiome == BaseBiome.DESERT) {
                    layers.layer1().add(Blocks.ORANGE_CONCRETE_POWDER);
                    layers.layer2().add(Blocks.RED_SANDSTONE);
                    layers.layer3().add(Blocks.GRANITE);
                    layers.layer4().add(Blocks.POLISHED_DEEPSLATE);
                } else if (baseBiome == BaseBiome.PLAINS) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.GRANITE);
                    layers.layer4().add(Blocks.POLISHED_DEEPSLATE);
                } else if (baseBiome == BaseBiome.FOREST || baseBiome == BaseBiome.TAIGA) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.ORANGE_CONCRETE);
                    layers.layer4().add(Blocks.POLISHED_DEEPSLATE);
                }
            }
            case BROWN -> {
                if (baseBiome == BaseBiome.DESERT) {
                    layers.layer1().add(Blocks.BROWN_CONCRETE_POWDER);
                    layers.layer2().add(Blocks.TERRACOTTA);
                    layers.layer3().add(Blocks.MUD_BRICKS);
                    layers.layer4().add(Blocks.DEEPSLATE);
                } else if (baseBiome == BaseBiome.PLAINS) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.PODZOL);
                    layers.layer3().add(Blocks.MUD_BRICKS);
                    layers.layer4().add(Blocks.DEEPSLATE);
                } else if (baseBiome == BaseBiome.FOREST || baseBiome == BaseBiome.TAIGA) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer1().add(Blocks.COARSE_DIRT);
                    layers.layer2().add(Blocks.COARSE_DIRT);
                    layers.layer3().add(Blocks.MUD_BRICKS);
                    layers.layer4().add(Blocks.DEEPSLATE);
                }
            }
            case BLACK -> {
                if (baseBiome == BaseBiome.DESERT) {
                    layers.layer1().add(Blocks.BLACK_CONCRETE_POWDER);
                    layers.layer2().add(Blocks.GRAY_TERRACOTTA);
                    layers.layer3().add(Blocks.BASALT);
                    layers.layer4().add(Blocks.BLACKSTONE);
                } else if (baseBiome == BaseBiome.PLAINS) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.BASALT);
                    layers.layer4().add(Blocks.BLACKSTONE);
                } else if (baseBiome == BaseBiome.FOREST || baseBiome == BaseBiome.TAIGA) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.MUD);
                    layers.layer3().add(Blocks.BASALT);
                    layers.layer4().add(Blocks.BLACKSTONE);
                }
            }
            case WHITE -> {
                if (baseBiome == BaseBiome.DESERT) {
                    layers.layer1().add(Blocks.SAND);
                    layers.layer2().add(Blocks.WHITE_TERRACOTTA);
                    layers.layer3().add(Blocks.CALCITE);
                    layers.layer3().add(Blocks.DIORITE);
                    layers.layer4().add(Blocks.POLISHED_DEEPSLATE);
                } else if (baseBiome == BaseBiome.PLAINS) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.CALCITE);
                    layers.layer3().add(Blocks.DIORITE);
                    layers.layer4().add(Blocks.POLISHED_DEEPSLATE);
                } else if (baseBiome == BaseBiome.FOREST || baseBiome == BaseBiome.TAIGA) {
                    layers.layer1().add(Blocks.GRASS_BLOCK);
                    layers.layer2().add(Blocks.DIRT);
                    layers.layer3().add(Blocks.CALCITE);
                    layers.layer3().add(Blocks.DIORITE);
                    layers.layer4().add(Blocks.POLISHED_DEEPSLATE);
                }
            }
        }
    }

    private void setupFeatures() {
        features.add("amethyst_geode");
        features.add("glow_lichen");
        features.add("monster_room");
        features.add("monster_room_deep");
        features.add("lake_lava_underground");
        features.add("ore_dirt");
        features.add("ore_gravel");
        features.add("ore_granite_upper");
        features.add("ore_granite_lower");
        features.add("ore_diorite_upper");
        features.add("ore_diorite_lower");
        features.add("ore_andesite_upper");
        features.add("ore_andesite_lower");
        features.add("ore_tuff");
        features.add("ore_coal_upper");
        features.add("ore_coal_lower");
        features.add("ore_iron_upper");
        features.add("ore_iron_middle");
        features.add("ore_iron_small");
        features.add("ore_gold");
        features.add("ore_gold_lower");
        features.add("ore_redstone");
        features.add("ore_redstone_lower");
        features.add("ore_diamond");
        features.add("ore_diamond_large");
        features.add("ore_diamond_buried");
        features.add("ore_lapis");
        features.add("ore_lapis_buried");
        features.add("ore_copper");
        features.add("underwater_magma");
        features.add("disk_sand");
        features.add("disk_clay");
        features.add("disk_gravel");
        features.add("spring_water");
        features.add("spring_lava");
        if (baseBiome == BaseBiome.FOREST) {
            features.add("trees_birch_and_oak");
            features.add("forest_flowers");
            features.add("trees_birch_and_oak_leaf_litter");
            features.add("patch_bush");
            features.add("flower_default");
            features.add("patch_grass_forest");
            features.add("brown_mushroom_normal");
            features.add("red_mushroom_normal");
            features.add("patch_pumpkin");
            features.add("patch_sugar_cane");
            features.add("patch_firefly_bush_near_water");
        } else if (baseBiome == BaseBiome.DESERT) {
            features.add("spring_water");
            features.add("spring_lava");
            features.add("desert_well");
        } else if (baseBiome == BaseBiome.PLAINS) {
            features.add("patch_grass_plain");
            features.add("patch_tall_grass_2");
            features.add("patch_large_fern");
            features.add("trees_plains");
        } else if (baseBiome == BaseBiome.TAIGA) {
            features.add("trees_taiga");
            features.add("patch_large_fern");
            features.add("flower_default");
            features.add("patch_grass_taiga_2");
            features.add("brown_mushroom_taiga");
            features.add("red_mushroom_taiga");
            features.add("patch_berry_common");
            features.add("patch_pumpkin");
            features.add("patch_sugar_cane");
        }
    }

    public BaseBiome getBaseBiome() {
        return baseBiome;
    }

    public LayersConfiguration getLayers() {
        return layers;
    }

    public List<String> getFeatures() {
        return features;
    }

    public MysticalInkItem.MysticalInkColor getColor() {
        return color;
    }

    public record LayersConfiguration(
            List<Block> layer1,
            List<Block> layer2,
            List<Block> layer3,
            List<Block> layer4) {

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();

            ListTag layer1Tag = new ListTag();
            for (Block block : layer1) {
                ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
                if (key == null) {
                    key = ForgeRegistries.BLOCKS.getKey(Blocks.AIR);
                }
                layer1Tag.add(StringTag.valueOf(key.toString()));
            }
            tag.put("Layer1", layer1Tag);

            ListTag layer2Tag = new ListTag();
            for (Block block : layer2) {
                ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
                if (key == null) {
                    key = ForgeRegistries.BLOCKS.getKey(Blocks.AIR);
                }
                layer2Tag.add(StringTag.valueOf(key.toString()));
            }
            tag.put("Layer2", layer2Tag);

            ListTag layer3Tag = new ListTag();
            for (Block block : layer3) {
                ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
                if (key == null) {
                    key = ForgeRegistries.BLOCKS.getKey(Blocks.AIR);
                }
                layer3Tag.add(StringTag.valueOf(key.toString()));
            }
            tag.put("Layer3", layer3Tag);

            ListTag layer4Tag = new ListTag();
            for (Block block : layer4) {
                ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
                if (key == null) {
                    key = ForgeRegistries.BLOCKS.getKey(Blocks.AIR);
                }
                layer4Tag.add(StringTag.valueOf(key.toString()));
            }
            tag.put("Layer4", layer4Tag);

            return tag;
        }

        public static LayersConfiguration load(CompoundTag tag) {
            List<Block> layer1 = new ArrayList<>();
            List<Block> layer2 = new ArrayList<>();
            List<Block> layer3 = new ArrayList<>();
            List<Block> layer4 = new ArrayList<>();

            ListTag layer1Tag = tag.getList("Layer1", Tag.TAG_STRING);
            for (Tag blockTag : layer1Tag) {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockTag.getAsString()));
                if (block != null) layer1.add(block);
            }

            ListTag layer2Tag = tag.getList("Layer2", Tag.TAG_STRING);
            for (Tag blockTag : layer2Tag) {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockTag.getAsString()));
                if (block != null) layer2.add(block);
            }

            ListTag layer3Tag = tag.getList("Layer3", Tag.TAG_STRING);
            for (Tag blockTag : layer3Tag) {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockTag.getAsString()));
                if (block != null) layer3.add(block);
            }

            ListTag layer4Tag = tag.getList("Layer4", Tag.TAG_STRING);
            for (Tag blockTag : layer4Tag) {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockTag.getAsString()));
                if (block != null) layer4.add(block);
            }

            return new LayersConfiguration(layer1, layer2, layer3, layer4);
        }
    }

    public enum BaseBiome { DESERT, FOREST, PLAINS, TAIGA }
}
