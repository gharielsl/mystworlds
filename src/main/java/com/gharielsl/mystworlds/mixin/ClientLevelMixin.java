package com.gharielsl.mystworlds.mixin;

import com.gharielsl.mystworlds.age.AgeDescription;
import com.gharielsl.mystworlds.age.AgeManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
    private void getSkyColor(Vec3 pos, float particleTicks, CallbackInfoReturnable<Vec3> cir) {
        ClientLevel level = (ClientLevel) ((Object) this);
        if (!level.dimension().equals(AgeManager.AGE_DIM_KEY)) {
            return;
        }
        BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
        String age = AgeManager.ageStates.entrySet().stream()
                .filter(entry -> entry.getValue().bounds().isWithinBounds(blockPos.getX(), blockPos.getZ()))
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
        if (description.getSky() == AgeDescription.SKY_DEFAULT) {
            return;
        }
        if (description.getSky() == AgeDescription.SKY_RED) {
            cir.setReturnValue(new Vec3(1, 0, 0));
        }
        if (description.getSky() == AgeDescription.SKY_GREEN) {
            cir.setReturnValue(new Vec3(0, 1, 0));
        }
        if (description.getSky() == AgeDescription.SKY_BLUE) {
            cir.setReturnValue(new Vec3(0, 0, 1));
        }
        if (description.getSky() == AgeDescription.SKY_CHAOS) {
            cir.setReturnValue(new Vec3(level.random.nextFloat(), level.random.nextFloat(), level.random.nextFloat()));
        }
    }
}
