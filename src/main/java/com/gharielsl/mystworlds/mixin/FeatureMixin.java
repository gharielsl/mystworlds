package com.gharielsl.mystworlds.mixin;

import com.gharielsl.mystworlds.age.AgeBiome;
import com.gharielsl.mystworlds.age.AgeDescription;
import com.gharielsl.mystworlds.age.AgeManager;
import com.gharielsl.mystworlds.event.ServerEventsHandler;
import com.gharielsl.mystworlds.world.MystWorldGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.Map;

@Mixin(Feature.class)
public class FeatureMixin<FC extends FeatureConfiguration> {

    @Inject(
            method = "place(Lnet/minecraft/world/level/levelgen/feature/configurations/FeatureConfiguration;" +
                    "Lnet/minecraft/world/level/WorldGenLevel;" +
                    "Lnet/minecraft/world/level/chunk/ChunkGenerator;" +
                    "Lnet/minecraft/util/RandomSource;" +
                    "Lnet/minecraft/core/BlockPos;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void place(
            FC p_225029_, WorldGenLevel p_225030_, ChunkGenerator p_225031_, RandomSource p_225032_, BlockPos p_225033_, CallbackInfoReturnable<Boolean> cir
    ) {
        if (!p_225030_.equals(AgeManager.AGE_DIM_KEY)) {
            return;
        }
        String age = AgeManager.ageStates.entrySet().stream()
                .filter(entry -> entry.getValue().bounds().isWithinBounds(p_225033_.getX(), p_225033_.getZ()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        if (age == null) {
            return;
        }
        if (ServerEventsHandler.SERVER == null) {
            return;
        }
        AgeDescription description = AgeManager.getDescription(age);
        if (description == null) {
            return;
        }

        Feature<?> feature = (Feature<?>) (Object) this;
        ResourceLocation id = ForgeRegistries.FEATURES.getKey(feature);
        if (id == null) return;
        int biome = MystWorldGenerator.getBiome(p_225033_.getX(), p_225033_.getZ());
        AgeBiome ageBiome = description.getBiome(biome);

        if (id.getNamespace().equals("minecraft")) {
            String path = id.getPath();
            if (!ageBiome.getFeatures().contains(path)) {
                cir.setReturnValue(false);
            }
        }
    }
}