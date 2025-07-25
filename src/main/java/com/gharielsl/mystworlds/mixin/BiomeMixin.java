package com.gharielsl.mystworlds.mixin;

import com.gharielsl.mystworlds.age.AgeBiome;
import com.gharielsl.mystworlds.age.AgeDescription;
import com.gharielsl.mystworlds.age.AgeManager;
import com.gharielsl.mystworlds.item.MysticalInkItem;
import com.gharielsl.mystworlds.world.MystWorldGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Biome.class)
public class BiomeMixin {
    @Inject(method = "getGrassColor", at = @At("HEAD"), cancellable = true)
    private void getGrassColor(double x, double z, CallbackInfoReturnable<Integer> cir) {
        Biome biome = (Biome) ((Object) this);
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        if (Minecraft.getInstance().player == null) {
            return;
        }
        ClientLevel level = (ClientLevel) Minecraft.getInstance().player.level();
        if (!level.dimension().equals(AgeManager.AGE_DIM_KEY)) {
            return;
        }
        String age = AgeManager.ageStates.entrySet().stream()
                .filter(entry -> entry.getValue().bounds().isWithinBounds((int) x, (int) z))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        if (age == null) {
            return;
        }
        AgeDescription description = AgeManager.getDescription(age);
        if (description == null) {
            return;
        }
        AgeBiome ageBiome = description.getBiome(MystWorldGenerator.getBiome((int) x, (int) z));
        if (ageBiome.getColor() == null || ageBiome.getColor() == MysticalInkItem.MysticalInkColor.TRANSPARENT) {
            return;
        }

        cir.setReturnValue(MysticalInkItem.inkToColor(ageBiome.getColor()));
    }
}
