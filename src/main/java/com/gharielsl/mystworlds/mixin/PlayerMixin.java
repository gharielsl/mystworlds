package com.gharielsl.mystworlds.mixin;

import com.gharielsl.mystworlds.age.AgeDescription;
import com.gharielsl.mystworlds.age.AgeManager;
import com.gharielsl.mystworlds.screen.WritingTableMenu;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Unique
    private long lastWeatherUpdateTime = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.containerMenu instanceof WritingTableMenu menu) {
            menu.tick();
        }
        if (player.level() instanceof ClientLevel level) {
            long currentTime = level.getGameTime();
            AgeDescription description = AgeManager.getDescription(AgeManager.players.get(player.getStringUUID()));
            if (description == null) {
                return;
            }
            if (currentTime - lastWeatherUpdateTime >= 100 && description != null) {
                if (description.getWeather() == AgeDescription.WEATHER_CLEAR) {
                    level.setRainLevel(0);
                    level.setThunderLevel(0);
                } else if (description.getWeather() == AgeDescription.WEATHER_RAIN) {
                    level.setRainLevel(1);
                    level.setThunderLevel(0);
                } else if (description.getWeather() == AgeDescription.WEATHER_STORM) {
                    level.setRainLevel(1);
                    level.setThunderLevel(1);
                } else if (description.getWeather() == AgeDescription.WEATHER_CHAOS) {
                    if (level.random.nextBoolean()) {
                        level.setRainLevel(0);
                        level.setThunderLevel(0);
                    } else {
                        level.setRainLevel(1);
                        level.setThunderLevel(1);
                    }
                }

                if (description.getTime() == AgeDescription.TIME_CHAOS) {
                    level.setDayTime(level.random.nextBoolean() ? 1000 : 18000);
                }
                lastWeatherUpdateTime = currentTime;
            }
        }
    }
}
