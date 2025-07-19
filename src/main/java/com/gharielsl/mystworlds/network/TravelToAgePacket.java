package com.gharielsl.mystworlds.network;

import com.gharielsl.mystworlds.age.AgeDescription;
import com.gharielsl.mystworlds.age.AgeManager;
import com.gharielsl.mystworlds.item.DescriptionItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

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
                AgeDescription description = DescriptionItem.getAgeDescription(stack);
                if (description != null) {
                    AgeManager.createAndOrTeleport(player, description.getAgeName());
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
