package com.gharielsl.mystworlds.age;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record AgeState(AgeBounds bounds, int weather) {
    @Override
    public @NotNull String toString() {
        return String.format("Bounds: (%d, %d to %d, %d)",
                bounds.minX(), bounds.minZ(), bounds.maxX(), bounds.maxZ());
    }
}
