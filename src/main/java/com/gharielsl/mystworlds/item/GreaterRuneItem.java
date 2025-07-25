package com.gharielsl.mystworlds.item;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;

public class GreaterRuneItem extends RuneItem {
    public GreaterRuneItem(Properties properties, String effect, ChatFormatting effectColor) {
        super(properties.rarity(Rarity.UNCOMMON), "Greater Rune", effect, effectColor);
    }
}
