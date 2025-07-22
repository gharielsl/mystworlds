package com.gharielsl.mystworlds.item;

import com.gharielsl.mystworlds.MystWorlds;
import com.gharielsl.mystworlds.block.MystWorldsBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MystWorldsItemGroups {
    public static final DeferredRegister<CreativeModeTab> ITEM_GROUPS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MystWorlds.MOD_ID);

    public static final Supplier<CreativeModeTab> ITEMS_GROUP = ITEM_GROUPS.register(MystWorlds.MOD_ID + "_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(MystWorldsItems.DESCRIPTION_NEUTRAL.get()))
                    .title(Component.translatable("tab." + MystWorlds.MOD_ID + ".items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(MystWorldsBlocks.WRITING_TABLE_BLOCK.get());
                        output.accept(MystWorldsBlocks.RUNE_CARVING_STATION.get());

                        output.accept(MystWorldsItems.DESCRIPTION_STABLE.get());
                        output.accept(MystWorldsItems.DESCRIPTION_NEUTRAL.get());
                        output.accept(MystWorldsItems.DESCRIPTION_UNSTABLE.get());
                        output.accept(MystWorldsItems.UNLINKED_DESCRIPTION.get());

                        output.accept(MystWorldsBlocks.TERRAIN_RUNE_ORE.get());
                        output.accept(MystWorldsBlocks.STABILITY_RUNE_ORE.get());
                        output.accept(MystWorldsBlocks.SKY_RUNE_ORE.get());
                        output.accept(MystWorldsBlocks.TIME_RUNE_ORE.get());

                        output.accept(MystWorldsItems.TERRAIN_RUNE.get());
                        output.accept(MystWorldsItems.STABILITY_RUNE.get());
                        output.accept(MystWorldsItems.SKY_RUNE.get());
                        output.accept(MystWorldsItems.TIME_RUNE.get());

                        output.accept(MystWorldsItems.RUNE_OF_ARCADIA.get());
                        output.accept(MystWorldsItems.RUNE_OF_SAYA.get());
                        output.accept(MystWorldsItems.RUNE_OF_MEMPHIS.get());
                        output.accept(MystWorldsItems.RUNE_OF_PHILAE.get());
                        output.accept(MystWorldsItems.RUNE_OF_NAUCRATIS.get());
                        output.accept(MystWorldsItems.RUNE_OF_STAFFA.get());
                        output.accept(MystWorldsItems.RUNE_OF_SANTORINI.get());
                        output.accept(MystWorldsItems.RUNE_OF_THESSALY.get());
                        output.accept(MystWorldsItems.RUNE_OF_OLYMPUS.get());
                        output.accept(MystWorldsItems.RUNE_OF_PETRA.get());

                        output.accept(MystWorldsItems.RUNE_OF_ETNA.get());
                        output.accept(MystWorldsItems.RUNE_OF_DELPHI.get());
                        output.accept(MystWorldsItems.RUNE_OF_THERA.get());
                        output.accept(MystWorldsItems.RUNE_OF_LUXOR.get());
                        output.accept(MystWorldsItems.RUNE_OF_ASWAN.get());

                        output.accept(MystWorldsItems.RUNE_OF_KNOSSOS.get());
                        output.accept(MystWorldsItems.RUNE_OF_DELOS.get());
                        output.accept(MystWorldsItems.RUNE_OF_EROS.get());
                        output.accept(MystWorldsItems.RUNE_OF_RHODES.get());
                        output.accept(MystWorldsItems.RUNE_OF_ARGOS.get());
                        output.accept(MystWorldsItems.RUNE_OF_SKIATHOS.get());

                        output.accept(MystWorldsItems.RUNE_OF_NYX.get());
                        output.accept(MystWorldsItems.RUNE_OF_HELIOPOLIS.get());
                        output.accept(MystWorldsItems.RUNE_OF_HADES.get());

                        output.accept(MystWorldsItems.RED_MYSTICAL_INK.get());
                        output.accept(MystWorldsItems.BLUE_MYSTICAL_INK.get());
                        output.accept(MystWorldsItems.GREEN_MYSTICAL_INK.get());
                        output.accept(MystWorldsItems.PURPLE_MYSTICAL_INK.get());
                        output.accept(MystWorldsItems.YELLOW_MYSTICAL_INK.get());
                        output.accept(MystWorldsItems.ORANGE_MYSTICAL_INK.get());
                        output.accept(MystWorldsItems.BROWN_MYSTICAL_INK.get());
                        output.accept(MystWorldsItems.BLACK_MYSTICAL_INK.get());
                        output.accept(MystWorldsItems.WHITE_MYSTICAL_INK.get());

                        output.accept(MystWorldsItems.MEMORY_STONE.get());
                        // END OF ITEMS
                    }).build());
}
