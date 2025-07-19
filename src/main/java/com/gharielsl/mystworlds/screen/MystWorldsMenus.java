package com.gharielsl.mystworlds.screen;

import com.gharielsl.mystworlds.MystWorlds;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MystWorldsMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, MystWorlds.MOD_ID);

    public static final RegistryObject<MenuType<WritingTableMenu>> WRITING_TABLE_MENU =
            MENUS.register("writing_table_menu", () -> IForgeMenuType.create(WritingTableMenu::new));

    public static final RegistryObject<MenuType<RuneCarvingMenu>> RUNE_CARVING_MENU =
            MENUS.register("rune_carving_menu", () -> IForgeMenuType.create(RuneCarvingMenu::new));
}
