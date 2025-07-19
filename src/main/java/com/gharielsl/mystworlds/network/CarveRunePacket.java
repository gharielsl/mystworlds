package com.gharielsl.mystworlds.network;

import com.gharielsl.mystworlds.screen.RuneCarvingMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CarveRunePacket {
    public CarveRunePacket() {

    }

    public static void encode(CarveRunePacket packet, FriendlyByteBuf buf) {

    }

    public static CarveRunePacket decode(FriendlyByteBuf buf) {
        return new CarveRunePacket();
    }

    public static void handle(CarveRunePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.containerMenu instanceof RuneCarvingMenu menu) {
                menu.startCrafting();
            }
        });
        context.get().setPacketHandled(true);
    }
}
