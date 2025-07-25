package com.gharielsl.mystworlds.item;

import com.gharielsl.mystworlds.MystWorlds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MystWorldsItems {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MystWorlds.MOD_ID);

    public static RegistryObject<Item> RUNE_OF_ARCADIA = ITEMS.register("rune_of_arcadia", () -> new RuneItem(new Item.Properties(), "Terrain", "Forest", ChatFormatting.GREEN));
    public static RegistryObject<Item> RUNE_OF_BORNEO = ITEMS.register("rune_of_borneo", () -> new RuneItem(new Item.Properties(), "Terrain", "Jungle", ChatFormatting.DARK_GREEN));
    public static RegistryObject<Item> RUNE_OF_HALLERBOS = ITEMS.register("rune_of_hallerbos", () -> new RuneItem(new Item.Properties(), "Terrain", "Dark Forest", ChatFormatting.DARK_BLUE));
    public static RegistryObject<Item> RUNE_OF_SAYA = ITEMS.register("rune_of_saya", () -> new RuneItem(new Item.Properties(), "Terrain", "Taiga", ChatFormatting.GREEN));
    public static RegistryObject<Item> RUNE_OF_MEMPHIS = ITEMS.register("rune_of_memphis", () -> new RuneItem(new Item.Properties(), "Terrain", "Desert", ChatFormatting.YELLOW));
    public static RegistryObject<Item> RUNE_OF_PHILAE = ITEMS.register("rune_of_philae", () -> new RuneItem(new Item.Properties(), "Terrain", "Plains", ChatFormatting.YELLOW));
    public static RegistryObject<Item> RUNE_OF_NAUCRATIS = ITEMS.register("rune_of_naucratis", () -> new RuneItem(new Item.Properties(), "Terrain", "Water", ChatFormatting.BLUE));
    public static RegistryObject<Item> RUNE_OF_STAFFA = ITEMS.register("rune_of_staffa", () -> new RuneItem(new Item.Properties(), "Terrain", "Lava", ChatFormatting.RED));
    public static RegistryObject<Item> RUNE_OF_SANTORINI = ITEMS.register("rune_of_santorini", () -> new RuneItem(new Item.Properties(), "Terrain", "Island", ChatFormatting.YELLOW));
    public static RegistryObject<Item> RUNE_OF_THESSALY = ITEMS.register("rune_of_thessaly", () -> new RuneItem(new Item.Properties(), "Terrain", "Flat", ChatFormatting.YELLOW));
    public static RegistryObject<Item> RUNE_OF_OLYMPUS = ITEMS.register("rune_of_olympus", () -> new RuneItem(new Item.Properties(), "Terrain", "Mountain", ChatFormatting.BLUE));
    public static RegistryObject<Item> RUNE_OF_PETRA = ITEMS.register("rune_of_petra", () -> new RuneItem(new Item.Properties(), "Terrain", "Cave", ChatFormatting.DARK_PURPLE));

    public static RegistryObject<Item> RUNE_OF_ETNA = ITEMS.register("rune_of_etna", () -> new RuneItem(new Item.Properties(), "Stability", "Fire", ChatFormatting.RED));
    public static RegistryObject<Item> RUNE_OF_DELPHI = ITEMS.register("rune_of_delphi", () -> new RuneItem(new Item.Properties(), "Stability", "Stable", ChatFormatting.GREEN));
    public static RegistryObject<Item> RUNE_OF_THERA = ITEMS.register("rune_of_thera", () -> new RuneItem(new Item.Properties(), "Stability", "Explosion", ChatFormatting.RED));
    public static RegistryObject<Item> RUNE_OF_LUXOR = ITEMS.register("rune_of_luxor", () -> new RuneItem(new Item.Properties(), "Stability", "Unstable", ChatFormatting.RED));
    public static RegistryObject<Item> RUNE_OF_ASWAN = ITEMS.register("rune_of_aswan", () -> new RuneItem(new Item.Properties(), "Stability", "Bedrock", ChatFormatting.GREEN));

    public static RegistryObject<Item> RUNE_OF_KNOSSOS = ITEMS.register("rune_of_knossos", () -> new RuneItem(new Item.Properties(), "Sky", "Rain", ChatFormatting.BLUE));
    public static RegistryObject<Item> RUNE_OF_DELOS = ITEMS.register("rune_of_delos", () -> new RuneItem(new Item.Properties(), "Sky", "Blue", ChatFormatting.BLUE));
    public static RegistryObject<Item> RUNE_OF_EROS = ITEMS.register("rune_of_eros", () -> new RuneItem(new Item.Properties(), "Sky", "Red", ChatFormatting.RED));
    public static RegistryObject<Item> RUNE_OF_RHODES = ITEMS.register("rune_of_rhodes", () -> new RuneItem(new Item.Properties(), "Sky", "Clear", ChatFormatting.WHITE));
    public static RegistryObject<Item> RUNE_OF_ARGOS = ITEMS.register("rune_of_argos", () -> new RuneItem(new Item.Properties(), "Sky", "Storm", ChatFormatting.DARK_BLUE));
    public static RegistryObject<Item> RUNE_OF_SKIATHOS = ITEMS.register("rune_of_skiathos", () -> new RuneItem(new Item.Properties(), "Sky", "Green", ChatFormatting.GREEN));

    public static RegistryObject<Item> RUNE_OF_NYX = ITEMS.register("rune_of_nyx", () -> new RuneItem(new Item.Properties(), "Time", "Night", ChatFormatting.DARK_PURPLE));
    public static RegistryObject<Item> RUNE_OF_HELIOPOLIS = ITEMS.register("rune_of_heliopolis", () -> new RuneItem(new Item.Properties(), "Time", "Day", ChatFormatting.BLUE));
    public static RegistryObject<Item> RUNE_OF_HADES = ITEMS.register("rune_of_hades", () -> new RuneItem(new Item.Properties(), "Time", "Unstable", ChatFormatting.RED));

    public static RegistryObject<Item> DESCRIPTION_NEUTRAL = ITEMS.register("description_neutral", () -> new DescriptionItem(new Item.Properties().stacksTo(1), "Neutral"));
    public static RegistryObject<Item> DESCRIPTION_STABLE = ITEMS.register("description_stable", () -> new DescriptionItem(new Item.Properties().stacksTo(1), "Stable"));
    public static RegistryObject<Item> DESCRIPTION_UNSTABLE = ITEMS.register("description_unstable", () -> new DescriptionItem(new Item.Properties().stacksTo(1), "Unstable"));

    public static RegistryObject<Item> RED_MYSTICAL_INK = ITEMS.register("red_mystical_ink", () -> new MysticalInkItem(new Item.Properties(), MysticalInkItem.MysticalInkColor.RED));
    public static RegistryObject<Item> BLUE_MYSTICAL_INK = ITEMS.register("blue_mystical_ink", () -> new MysticalInkItem(new Item.Properties(), MysticalInkItem.MysticalInkColor.BLUE));
    public static RegistryObject<Item> GREEN_MYSTICAL_INK = ITEMS.register("green_mystical_ink", () -> new MysticalInkItem(new Item.Properties(), MysticalInkItem.MysticalInkColor.GREEN));
    public static RegistryObject<Item> PURPLE_MYSTICAL_INK = ITEMS.register("purple_mystical_ink", () -> new MysticalInkItem(new Item.Properties(), MysticalInkItem.MysticalInkColor.PURPLE));
    public static RegistryObject<Item> YELLOW_MYSTICAL_INK = ITEMS.register("yellow_mystical_ink", () -> new MysticalInkItem(new Item.Properties(), MysticalInkItem.MysticalInkColor.YELLOW));
    public static RegistryObject<Item> ORANGE_MYSTICAL_INK = ITEMS.register("orange_mystical_ink", () -> new MysticalInkItem(new Item.Properties(), MysticalInkItem.MysticalInkColor.ORANGE));
    public static RegistryObject<Item> BROWN_MYSTICAL_INK = ITEMS.register("brown_mystical_ink", () -> new MysticalInkItem(new Item.Properties(), MysticalInkItem.MysticalInkColor.BROWN));
    public static RegistryObject<Item> BLACK_MYSTICAL_INK = ITEMS.register("black_mystical_ink", () -> new MysticalInkItem(new Item.Properties(), MysticalInkItem.MysticalInkColor.BLACK));
    public static RegistryObject<Item> WHITE_MYSTICAL_INK = ITEMS.register("white_mystical_ink", () -> new MysticalInkItem(new Item.Properties(), MysticalInkItem.MysticalInkColor.WHITE));
    public static RegistryObject<Item> TRANSPARENT_MYSTICAL_INK = ITEMS.register("transparent_mystical_ink", () -> new MysticalInkItem(new Item.Properties(), MysticalInkItem.MysticalInkColor.TRANSPARENT));

    public static RegistryObject<Item> UNLINKED_DESCRIPTION = ITEMS.register("unlinked_description", () -> new Item(new Item.Properties()));
    public static RegistryObject<Item> TERRAIN_RUNE = ITEMS.register("terrain_rune", () -> new UncarvedRuneItem(new Item.Properties(), UncarvedRuneItem.UncarvedRuneType.TERRAIN));
    public static RegistryObject<Item> STABILITY_RUNE = ITEMS.register("stability_rune", () -> new UncarvedRuneItem(new Item.Properties(), UncarvedRuneItem.UncarvedRuneType.STABILITY));
    public static RegistryObject<Item> SKY_RUNE = ITEMS.register("sky_rune", () -> new UncarvedRuneItem(new Item.Properties(), UncarvedRuneItem.UncarvedRuneType.SKY));
    public static RegistryObject<Item> TIME_RUNE = ITEMS.register("time_rune", () -> new UncarvedRuneItem(new Item.Properties(), UncarvedRuneItem.UncarvedRuneType.TIME));

    public static RegistryObject<Item> MEMORY_STONE = ITEMS.register("memory_stone", () -> new MemoryStoneItem(new Item.Properties()));

    public static RegistryObject<Item> GREATER_RUNE = ITEMS.register("greater_rune", () -> new Item(new Item.Properties()));
    public static RegistryObject<Item> GREATER_RUNE_SKYBLOCK = ITEMS.register("greater_rune_skyblock", () -> new GreaterRuneItem(new Item.Properties(), "Sky Block", ChatFormatting.BLUE));
    public static RegistryObject<Item> GREATER_RUNE_SKYGRID = ITEMS.register("greater_rune_skygrid", () -> new GreaterRuneItem(new Item.Properties(), "Sky Grid", ChatFormatting.BLUE));
    public static RegistryObject<Item> GREATER_RUNE_FLAT = ITEMS.register("greater_rune_flat", () -> new GreaterRuneItem(new Item.Properties(), "Flat World", ChatFormatting.GREEN));
    public static RegistryObject<Item> GREATER_RUNE_VOID = ITEMS.register("greater_rune_void", () -> new GreaterRuneItem(new Item.Properties(), "Void", ChatFormatting.DARK_PURPLE));

    // END OF ITEMS
}
