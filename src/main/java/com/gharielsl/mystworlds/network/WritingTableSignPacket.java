package com.gharielsl.mystworlds.network;

import com.gharielsl.mystworlds.screen.WritingTableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WritingTableSignPacket {
    private final String title;

    public WritingTableSignPacket(FriendlyByteBuf buf) {
        this.title = buf.readUtf(256);
    }

    public WritingTableSignPacket(String text) {
        this.title = text;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(title);
    }

    public static WritingTableSignPacket decode(FriendlyByteBuf buf) {
        String title = buf.readUtf();
        return new WritingTableSignPacket(title);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.containerMenu instanceof WritingTableMenu menu) {
                menu.startCrafting(title);
            }
        });
        context.get().setPacketHandled(true);
    }
}
