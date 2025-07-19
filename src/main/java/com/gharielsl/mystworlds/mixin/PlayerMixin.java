package com.gharielsl.mystworlds.mixin;

import com.gharielsl.mystworlds.screen.WritingTableMenu;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.containerMenu instanceof WritingTableMenu menu) {
            menu.tick();
        }
    }
}
