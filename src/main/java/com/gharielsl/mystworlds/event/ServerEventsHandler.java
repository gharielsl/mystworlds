package com.gharielsl.mystworlds.event;

import com.gharielsl.mystworlds.age.AgeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventsHandler {
    public static MinecraftServer SERVER;

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) throws IOException {
        SERVER = event.getServer();
        AgeManager.load(SERVER);
        AgeManager.ready = true;
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        SERVER = null;
        AgeManager.ready = false;
        AgeManager.saveFileAge = null;
        AgeManager.saveFilePlayers = null;
    }
}
