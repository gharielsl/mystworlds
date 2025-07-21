package com.gharielsl.mystworlds.item;

import net.minecraft.world.item.Item;

public class MysticalInkItem extends Item {
    private final MysticalInkColor color;

    public MysticalInkItem(Properties properties, MysticalInkColor color) {
        super(properties);
        this.color = color;
    }

    public MysticalInkColor getColor() {
        return color;
    }

    public enum MysticalInkColor { RED, BLUE, GREEN, PURPLE, YELLOW, ORANGE, BROWN, BLACK, WHITE }
}
