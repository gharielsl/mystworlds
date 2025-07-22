package com.gharielsl.mystworlds.network;

import com.gharielsl.mystworlds.MystWorlds;
import com.gharielsl.mystworlds.age.AgeBounds;
import com.gharielsl.mystworlds.age.AgeDescription;
import com.gharielsl.mystworlds.age.AgeManager;
import com.gharielsl.mystworlds.item.DescriptionItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class TravelToAgePacket {
    public TravelToAgePacket() {

    }

    public static void encode(TravelToAgePacket packet, FriendlyByteBuf buf) {

    }

    public static TravelToAgePacket decode(FriendlyByteBuf buf) {
        return new TravelToAgePacket();
    }

    public static void handle(TravelToAgePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) {
                return;
            }
            ItemStack stack1 = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack stack2 = player.getItemInHand(InteractionHand.OFF_HAND);
            ItemStack stack = stack1.getItem() instanceof DescriptionItem ? stack1 : stack2;
            if (stack.getItem() instanceof DescriptionItem) {
//                ForgeRegistries.SOUND_EVENTS.getHolder(SoundEvents.PORTAL_TRAVEL).ifPresent(soundEventHolder -> player.connection.send(new ClientboundSoundPacket(
//                        soundEventHolder,
//                        SoundSource.NEUTRAL,
//                        player.getX(), player.getY(), player.getZ(),
//                        1.0f, 1.0f,
//                        player.level().getRandom().nextLong()
//                )));

                AgeDescription description = DescriptionItem.getAgeDescription(stack);
                if (description == null) {
                    return;
                }
                if (player.level().dimension().equals(AgeManager.AGE_DIM_KEY)) {
                    if (description.getAgeName().equals(AgeManager.players.get(player.getStringUUID()))) {
                        
                        teleportToRespawn(player);
                        return;
                    }
                }
                
                AgeManager.createAndOrTeleport(player, description.getAgeName());
            }
        });
        context.get().setPacketHandled(true);
    }

    private static void teleportToRespawn(ServerPlayer player) {
        ServerLevel respawnLevel = player.server.getLevel(player.getRespawnDimension());
        BlockPos respawnPos = player.getRespawnPosition();

        if (respawnPos == null || respawnLevel == null) {
            respawnLevel = player.server.getLevel(Level.OVERWORLD);
            if (respawnLevel == null) {
                return;
            }
            respawnPos = respawnLevel.getSharedSpawnPos();
        }

        BlockPos bestPos = null;

        BlockPos[] preferredOffsets = new BlockPos[] {
                respawnPos.north(),
                respawnPos.south(),
                respawnPos.east(),
                respawnPos.west()
        };

        for (BlockPos candidate : preferredOffsets) {
            if (isValidSpawn(respawnLevel, candidate)) {
                bestPos = candidate;
                break;
            }
        }

        if (bestPos == null) {
            for (int x = -2; x <= 2; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -2; z <= 2; z++) {
                        BlockPos offset = respawnPos.offset(x, y, z);
                        if (isValidSpawn(respawnLevel, offset)) {
                            bestPos = offset;
                            break;
                        }
                    }
                    if (bestPos != null) break;
                }
                if (bestPos != null) break;
            }
        }

        if (bestPos == null) {
            bestPos = respawnPos;
        }

        respawnLevel.playSound(player, bestPos, SoundEvents.PORTAL_TRAVEL, SoundSource.NEUTRAL);

        player.teleportTo(respawnLevel, bestPos.getX() + 0.5, bestPos.getY() + 0.5, bestPos.getZ() + 0.5, player.getYRot(), player.getXRot());
    }

    private static boolean isValidSpawn(ServerLevel level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        BlockState at = level.getBlockState(pos);
        BlockState above = level.getBlockState(pos.above());

        return below.isFaceSturdy(level, pos.below(), Direction.UP) &&
                at.isAir() &&
                above.isAir();
    }
}
