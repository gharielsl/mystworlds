package com.gharielsl.mystworlds.event;

import com.gharielsl.mystworlds.age.AgeManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DimensionEventsHandler {
    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ResourceKey<Level> newDimension = player.level().dimension();
        if (!newDimension.equals(AgeManager.AGE_DIM_KEY)) {
            AgeManager.players.put(player.getStringUUID(), "null");
        }
    }

    @SubscribeEvent
    public static void onPlayerEnterPortal(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ResourceKey<Level> from = player.level().dimension();
            ResourceKey<Level> to = event.getDimension();
            if (to.equals(AgeManager.AGE_DIM_KEY) &&
                    !from.equals(AgeManager.AGE_DIM_KEY) &&
                    !AgeManager.playersAllowedTravel.contains(player.getStringUUID())) {
                event.setCanceled(true);
            }
        }
    }
}
