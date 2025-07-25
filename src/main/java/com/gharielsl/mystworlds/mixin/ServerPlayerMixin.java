package com.gharielsl.mystworlds.mixin;

import com.gharielsl.mystworlds.age.AgeBounds;
import com.gharielsl.mystworlds.age.AgeManager;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
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
import java.util.Map;
import java.util.Objects;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Shadow public abstract boolean hasDisconnected();

    @Unique
    private Vec3 mystWorlds_Forge1_20_1_$previousPos;
    @Unique
    private String mystWorlds_Forge1_20_1_$previousAge;
    @Unique
    private boolean mystWorlds_Forge1_20_1_$sentPackets = false;

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) throws IOException {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (player.hasDisconnected()) {
            mystWorlds_Forge1_20_1_$sentPackets = false;
        }
        Level level = player.level();
        if (!level.dimension().equals(AgeManager.AGE_DIM_KEY)) {
            if (!AgeManager.isPlayerAgeNull(AgeManager.players.get(player.getStringUUID())) && AgeManager.ready) {
                AgeManager.players.put(player.getStringUUID(), null);
                AgeManager.savePlayers();
            }
        } else {
            if (mystWorlds_Forge1_20_1_$isWithinBounds()) {
                if (AgeManager.players != null) {
                    if (!mystWorlds_Forge1_20_1_$sentPackets || !Objects.equals(AgeManager.players.get(player.getStringUUID()), mystWorlds_Forge1_20_1_$previousAge)) {
                        mystWorlds_Forge1_20_1_$sendClientBorder();
                        mystWorlds_Forge1_20_1_$sentPackets = true;
                    }
                    mystWorlds_Forge1_20_1_$previousAge = AgeManager.players.get(player.getStringUUID());
                }
                mystWorlds_Forge1_20_1_$previousPos = player.position();
            } else {
                if (mystWorlds_Forge1_20_1_$previousPos != null && mystWorlds_Forge1_20_1_$getBounds() != null && mystWorlds_Forge1_20_1_$getBounds().isWithinBounds((int) mystWorlds_Forge1_20_1_$previousPos.x, (int) mystWorlds_Forge1_20_1_$previousPos.z)) {
                    player.teleportTo(mystWorlds_Forge1_20_1_$previousPos.x, mystWorlds_Forge1_20_1_$previousPos.y, mystWorlds_Forge1_20_1_$previousPos.z);
                } else {
                    String currentAge = AgeManager.ageStates.entrySet().stream()
                            .filter(entry -> entry.getValue().bounds().isWithinBounds(player.getBlockX(), player.getBlockZ()))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse(null);
                    AgeManager.players.put(player.getStringUUID(), currentAge);
                    AgeManager.savePlayers();
                }
            }
        }
    }

    @Unique
    private AgeBounds mystWorlds_Forge1_20_1_$getBounds() {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (AgeManager.players == null) return null;
        String age = AgeManager.players.get(player.getStringUUID());
        if (age == null) return null;
        if (AgeManager.ageStates == null) return null;
        return AgeManager.ageStates.get(age).bounds();
    }

    @Unique
    private void mystWorlds_Forge1_20_1_$sendClientBorder() {
        ServerPlayer player = (ServerPlayer) (Object) this;
        AgeBounds area = mystWorlds_Forge1_20_1_$getBounds();
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
    private boolean mystWorlds_Forge1_20_1_$isWithinBounds() {
        ServerPlayer player = (ServerPlayer) (Object) this;
        AgeBounds area = mystWorlds_Forge1_20_1_$getBounds();
        if (area == null) {
            return true;
        }
        Vec3 pos = player.position();
        return pos.x >= area.minX() && pos.x <= area.maxX() && pos.z >= area.minZ() && pos.z <= area.maxZ();
    }
}
