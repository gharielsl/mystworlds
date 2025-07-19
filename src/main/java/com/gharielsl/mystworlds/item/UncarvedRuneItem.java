package com.gharielsl.mystworlds.item;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class UncarvedRuneItem extends Item {
    private final UncarvedRuneType type;

    public UncarvedRuneItem(Properties properties, UncarvedRuneType type) {
        super(properties);
        this.type = type;
    }

    public UncarvedRuneType getType() {
        return type;
    }

    public ItemStack[] carveRandom(RandomSource random) {
        ItemStack[] result = new ItemStack[2];
        if (type == UncarvedRuneType.TERRAIN || type == UncarvedRuneType.STABILITY) {
            result[1] = new ItemStack(Items.REDSTONE);
        } else {
            result[1] = new ItemStack(Items.LAPIS_LAZULI);
        }
        result[1].setCount(random.nextInt(2) + 1);
        if (random.nextInt(5) == 0) {
            result[0] = new ItemStack(MystWorldsItems.MEMORY_STONE.get());
            return result;
        }
        List<RuneItem> options = switch (type) {
            case TERRAIN -> List.of(
                    (RuneItem) MystWorldsItems.RUNE_OF_ARCADIA.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_MEMPHIS.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_PHILAE.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_NAUCRATIS.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_STAFFA.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_SANTORINI.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_THESSALY.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_OLYMPUS.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_PETRA.get()
            );
            case STABILITY -> List.of(
                    (RuneItem) MystWorldsItems.RUNE_OF_ETNA.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_DELPHI.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_THERA.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_LUXOR.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_ASWAN.get()
            );
            case SKY -> List.of(
                    (RuneItem) MystWorldsItems.RUNE_OF_KNOSSOS.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_DELOS.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_EROS.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_RHODES.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_ARGOS.get()
            );
            case TIME -> List.of(
                    (RuneItem) MystWorldsItems.RUNE_OF_NYX.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_HELIOPOLIS.get(),
                    (RuneItem) MystWorldsItems.RUNE_OF_HADES.get()
            );
        };
        result[0] = new ItemStack(options.get(random.nextInt(options.size())));
        return result;
    }

    public enum UncarvedRuneType {
        TERRAIN,
        STABILITY,
        SKY,
        TIME
    }
}
