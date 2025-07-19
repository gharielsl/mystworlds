package com.gharielsl.mystworlds.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class MystWorldsChannels {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("mymodid", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        INSTANCE.registerMessage(packetId++, WritingTablePagePacket.class,
                WritingTablePagePacket::encode,
                WritingTablePagePacket::decode,
                WritingTablePagePacket::handle);

        INSTANCE.registerMessage(packetId++, WritingTableSignPacket.class,
                WritingTableSignPacket::encode,
                WritingTableSignPacket::decode,
                WritingTableSignPacket::handle);

        INSTANCE.registerMessage(packetId++, TravelToAgePacket.class,
                TravelToAgePacket::encode,
                TravelToAgePacket::decode,
                TravelToAgePacket::handle);

        INSTANCE.registerMessage(packetId++, CarveRunePacket.class,
                CarveRunePacket::encode,
                CarveRunePacket::decode,
                CarveRunePacket::handle);
    }
}
