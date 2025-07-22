package com.gharielsl.mystworlds.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {
    @Accessor("biomeSource")
    BiomeSource getBiomeSource();
    @Accessor("featuresPerStep")
    Supplier<List<FeatureSorter.StepFeatureData>> featuresPerStep();
    @Accessor("generationSettingsGetter")
    Function<Holder<Biome>, BiomeGenerationSettings> generationSettingsGetter();
    @Invoker("getWritableArea")
    BoundingBox getWritableArea(ChunkAccess p_187718_);
}
