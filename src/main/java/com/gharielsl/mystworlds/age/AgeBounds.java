package com.gharielsl.mystworlds.age;

import net.minecraft.core.BlockPos;

public record AgeBounds(int minX, int minZ, int maxX, int maxZ) {
    public BlockPos center() {
        return new BlockPos((minX + maxX) / 2, 0, (minZ + maxZ) / 2);
    }

    public boolean isWithinBounds(int x, int z) {
        return x >= minX && x <= maxX
                && z >= minZ && z <= maxZ;
    }
}