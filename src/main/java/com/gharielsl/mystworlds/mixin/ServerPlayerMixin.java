package com.gharielsl.mystworlds.mixin;

import com.gharielsl.mystworlds.age.AgeBounds;
import com.gharielsl.mystworlds.age.AgeManager;
import com.gharielsl.mystworlds.screen.WritingTableMenu;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Objects;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Shadow public abstract boolean hasDisconnected();

    @Unique
    private Vec3 previousPos;
    @Unique
    private String previousAge;
    @Unique
    private boolean sentPackets = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) throws IOException {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (player.hasDisconnected()) {
            sentPackets = false;
        }
        Level level = player.level();
        if (!level.dimension().equals(AgeManager.AGE_DIM_KEY)) {
            if (!Objects.equals(AgeManager.players.get(player.getStringUUID()), null) && AgeManager.ready) {
                AgeManager.players.put(player.getStringUUID(), null);
                AgeManager.savePlayers();
            }
        } else {
            if (isWithinBounds()) {
                if (AgeManager.players != null) {
                    if (!sentPackets || !Objects.equals(AgeManager.players.get(player.getStringUUID()), previousAge)) {
                        sendClientBorder();
                        sentPackets = true;
                    }
                    previousAge = AgeManager.players.get(player.getStringUUID());
                }
                previousPos = player.position();
            } else {
                if (previousPos != null) {
                    player.teleportTo(previousPos.x, previousPos.y, previousPos.z);
                } else {
                    // todo also if previousPos is out of bounds or null teleport player to the fucking backrooms
                }
            }
        }
    }

    @Unique
    private AgeBounds getBounds() {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (AgeManager.players == null) return null;
        String age = AgeManager.players.get(player.getStringUUID());
        if (age == null) return null;
        if (AgeManager.ageStates == null) return null;
        return AgeManager.ageStates.get(age).bounds();
    }

    @Unique
    private void sendClientBorder() {
        ServerPlayer player = (ServerPlayer) (Object) this;
        AgeBounds area = getBounds();
        if (area == null) {
            return;
        }
        WorldBorder border = new WorldBorder();
        double centerX = (area.minX() + area.maxX()) / 2.0;
        double centerZ = (area.minZ() + area.maxZ()) / 2.0;
        double sizeX = area.maxX() - area.minX();
        double sizeZ = area.maxZ() - area.minZ();
        double size = Math.max(sizeX, sizeZ);
        border.setCenter(centerX, centerZ);
        border.setSize(size);
        ClientboundInitializeBorderPacket packet = new ClientboundInitializeBorderPacket(border);
        player.connection.send(packet);
    }

    @Unique
    private boolean isWithinBounds() {
        ServerPlayer player = (ServerPlayer) (Object) this;
        AgeBounds area = getBounds();
        if (area == null) {
            return true;
        }
        Vec3 pos = player.position();
        return pos.x >= area.minX() && pos.x <= area.maxX() && pos.z >= area.minZ() && pos.z <= area.maxZ();
    }
}
