package com.gharielsl.mystworlds.age;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record AgeState(AgeBounds bounds, int weather) {
    @Override
    public @NotNull String toString() {
        return String.format("(%d %d %d %d %s)",
                bounds.minX(), bounds.minZ(), bounds.maxX(), bounds.maxZ(),
                Map.of(AgeManager.WEATHER_CLEAR, "clear", AgeManager.WEATHER_RAIN, "rain", AgeManager.WEATHER_STORM, "storm").get(weather));
    }
}
