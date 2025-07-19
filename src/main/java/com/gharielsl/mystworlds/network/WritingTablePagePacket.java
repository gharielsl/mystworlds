package com.gharielsl.mystworlds.network;

import com.gharielsl.mystworlds.block.entity.WritingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WritingTablePagePacket {
    private final BlockPos pos;
    private final int currentPage;

    public WritingTablePagePacket(BlockPos pos, int currentPage) {
        this.pos = pos;
        this.currentPage = currentPage;
    }

    public static void encode(WritingTablePagePacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeInt(packet.currentPage);
    }

    public static WritingTablePagePacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int currentPage = buf.readInt();
        return new WritingTablePagePacket(pos, currentPage);
    }

    public static void handle(WritingTablePagePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerLevel world = (ServerLevel) context.get().getSender().level();
            if (world.getBlockEntity(packet.pos) instanceof WritingTableBlockEntity table) {
                table.setCurrentPage(packet.currentPage);
            }
        });
        context.get().setPacketHandled(true);
    }
}
