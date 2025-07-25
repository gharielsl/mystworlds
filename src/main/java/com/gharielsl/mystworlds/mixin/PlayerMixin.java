package com.gharielsl.mystworlds.mixin;

import com.gharielsl.mystworlds.age.AgeDescription;
import com.gharielsl.mystworlds.age.AgeManager;
import com.gharielsl.mystworlds.item.MemoryStoneItem;
import com.gharielsl.mystworlds.screen.WritingTableMenu;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.Map;

@Mixin(Player.class)
public class PlayerMixin {
    @Unique
    private long mystWorlds_Forge1_20_1_$lastWeatherUpdateTime = 0;
    private ResourceKey<Level> previousLevel;
    private int delayToScreenshot = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.containerMenu instanceof WritingTableMenu menu) {
            menu.tick();
        }
        if (player.level() instanceof ClientLevel level) {
            long currentTime = level.getGameTime();
            AgeDescription description = AgeManager.getDescription(AgeManager.players.get(player.getStringUUID()));
            if (previousLevel == null || (!previousLevel.equals(AgeManager.AGE_DIM_KEY) && level.dimension().equals(AgeManager.AGE_DIM_KEY))) {
                String age = AgeManager.ageStates.entrySet().stream()
                        .filter(entry -> entry.getValue().bounds().isWithinBounds(player.getBlockX(), player.getBlockZ()))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null);
                if (age != null) {
                    Path screenshotPath = MemoryStoneItem.findAgeScreenshot();
                    if (screenshotPath != null && !screenshotPath.toFile().exists()) {
                        delayToScreenshot = 1;
                    }
                }
            } else if (delayToScreenshot != 0) {
                delayToScreenshot++;
                if (delayToScreenshot > 50) {
                    MemoryStoneItem.takeAgeScreenshot((success -> {}));
                    delayToScreenshot = 0;
                }
            }
            previousLevel = level.dimension();
            if (description == null) {
                return;
            }
            if (currentTime - mystWorlds_Forge1_20_1_$lastWeatherUpdateTime >= 100 && description != null) {
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
                mystWorlds_Forge1_20_1_$lastWeatherUpdateTime = currentTime;
            }
        }
    }
}
