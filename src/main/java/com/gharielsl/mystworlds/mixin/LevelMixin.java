package com.gharielsl.mystworlds.mixin;

import com.gharielsl.mystworlds.age.AgeDescription;
import com.gharielsl.mystworlds.age.AgeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Shadow @Final public boolean isClientSide;

    @Shadow public abstract ResourceKey<Level> dimension();

    @Inject(method = "getDayTime", at = @At("RETURN"), cancellable = true)
    private void getDayTime(CallbackInfoReturnable<Long> ci) {
        if (isClientSide) {
            return;
        }
        if (!dimension().equals(AgeManager.AGE_DIM_KEY)) {
            return;
        }
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        AgeDescription description = AgeManager.getDescription(AgeManager.players.get(player.getStringUUID()));
        if (description == null) {
            return;
        }
        if (description.getTime() == AgeDescription.TIME_NORMAL || description.getTime() == AgeDescription.TIME_CHAOS) {
            return;
        }
        if (description.getTime() == AgeDescription.TIME_DAY) {
            ci.setReturnValue(1000L);
        } else if (description.getTime() == AgeDescription.TIME_NIGHT) {
            ci.setReturnValue(18000L);
        }
    }
}
