package com.gharielsl.mystworlds;

import com.gharielsl.mystworlds.age.AgeManager;
import com.gharielsl.mystworlds.block.MystWorldsBlocks;
import com.gharielsl.mystworlds.block.entity.MystWorldsBlockEntities;
import com.gharielsl.mystworlds.item.MystWorldsItemGroups;
import com.gharielsl.mystworlds.item.MystWorldsItems;
import com.gharielsl.mystworlds.network.MystWorldsChannels;
import com.gharielsl.mystworlds.recipe.MystWorldsRecipes;
import com.gharielsl.mystworlds.renderer.WritingTableRenderer;
import com.gharielsl.mystworlds.screen.MystWorldsMenus;
import com.gharielsl.mystworlds.screen.RuneCarvingScreen;
import com.gharielsl.mystworlds.screen.WritingTableScreen;
import com.gharielsl.mystworlds.world.MystWorldsChunkGenerators;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MystWorlds.MOD_ID)
public class MystWorlds {
    public static final String MOD_ID = "mystworlds";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public MystWorlds() {
        init(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public MystWorlds(FMLJavaModLoadingContext context) {
        init(context.getModEventBus());
    }

    public MystWorlds(IEventBus bus, ModContainer container) {
        init(bus);
    }

    private void init(IEventBus bus) {
        MystWorldsChannels.register();
        MystWorldsBlocks.BLOCKS.register(bus);
        MystWorldsItems.ITEMS.register(bus);
        MystWorldsRecipes.RECIPE_SERIALIZERS.register(bus);
        MystWorldsItemGroups.ITEM_GROUPS.register(bus);
        MystWorldsMenus.MENUS.register(bus);
        MystWorldsBlockEntities.BLOCK_ENTITIES.register(bus);
        MystWorldsChunkGenerators.CHUNK_GENERATORS.register(bus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        AgeManager.register(event.getDispatcher());
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class MystWorldsClient {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(MystWorldsMenus.WRITING_TABLE_MENU.get(), WritingTableScreen::new);
            MenuScreens.register(MystWorldsMenus.RUNE_CARVING_MENU.get(), RuneCarvingScreen::new);
            BlockEntityRenderers.register(
                    MystWorldsBlockEntities.WRITING_TABLE_BLOCK_ENTITY.get(),
                    WritingTableRenderer::new
            );
        }
    }
}
