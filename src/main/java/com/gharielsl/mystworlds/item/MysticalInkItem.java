package com.gharielsl.mystworlds.item;

import com.gharielsl.mystworlds.age.AgeBiome;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class MysticalInkItem extends Item {
    private final Map<AgeBiome.BaseBiome, AgeBiome> biomeOverrides = new HashMap<>();

    public MysticalInkItem(Properties properties, MysticalInkColor color) {
        super(properties);
        biomeOverrides.put(AgeBiome.BaseBiome.DESERT, new AgeBiome(AgeBiome.BaseBiome.DESERT));

        switch (color) {
            case RED -> {
                AgeBiome desert = biomeOverrides.get(AgeBiome.BaseBiome.DESERT);
                desert.getLayers().layer1().add(Blocks.RED_SAND);
                desert.getLayers().layer1().add(Blocks.RED_CONCRETE_POWDER);

                desert.getLayers().layer2().add(Blocks.RED_SANDSTONE);
                desert.getLayers().layer2().add(Blocks.RED_TERRACOTTA);

                desert.getLayers().layer3().add(Blocks.NETHERRACK);

                desert.getLayers().layer4().add(Blocks.NETHER_BRICKS);

                desert.getOres().put(Blocks.REDSTONE_ORE, 5);
                desert.getOres().put(Blocks.COPPER_ORE, 2);
                desert.getOres().put(Blocks.NETHER_GOLD_ORE, 2);
            }
            case BLUE -> {

            }
            case GREEN -> {

            }
            case PURPLE -> {

            }
            case YELLOW -> {

            }
            case ORANGE -> {

            }
            case BROWN -> {

            }
            case BLACK -> {

            }
            case WHITE -> {

            }
        }
    }

    public enum MysticalInkColor { RED, BLUE, GREEN, PURPLE, YELLOW, ORANGE, BROWN, BLACK, WHITE }
}
