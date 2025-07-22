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

    public static int inkToColor(MysticalInkColor color) {
        return switch(color) {
            case RED -> 0xFF0000;
            case BLUE -> 0x0000FF;
            case GREEN -> 0x00FF00;
            case PURPLE -> 0x800080;
            case YELLOW -> 0xFFFF00;
            case ORANGE -> 0xFFA500;
            case BROWN -> 0x8B4513;
            case BLACK -> 0x000000;
            case WHITE -> 0xFFFFFF;
        };
    }

    public enum MysticalInkColor { RED, BLUE, GREEN, PURPLE, YELLOW, ORANGE, BROWN, BLACK, WHITE }
}
