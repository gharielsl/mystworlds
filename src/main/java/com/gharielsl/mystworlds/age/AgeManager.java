package com.gharielsl.mystworlds.age;

import com.gharielsl.mystworlds.MystWorlds;
import com.gharielsl.mystworlds.event.ServerEventsHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.LevelResource;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class AgeManager {
    public static boolean ready = false;
    public static Path saveFileAge;
    public static Path saveFilePlayers;
    private static final int WORLD_SIZE = 30000000;
    private static final int AGE_AREA_X = 1000000;
    private static final int AGE_AREA_Z = 1000000;

    public static final int WEATHER_CLEAR = 1;
    public static final int WEATHER_RAIN = 2;
    public static final int WEATHER_STORM = 3;

    public static ResourceKey<Level> AGE_DIM_KEY = ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION,
            new ResourceLocation(MystWorlds.MOD_ID, "mystworld")
    );

    public static Map<String, AgeState> ageStates = new HashMap<>();
    public static Map<String, String> players = new HashMap<>();
    private static Map<String, AgeDescription> descriptions = new HashMap<>();
    public static Set<String> playersAllowedTravel = new HashSet<>();

    public static void allocateNextArea(String name) throws IOException {
        int spacing = 10000;
        int dimensionCount = ageStates.size();
        int totalAreaX = AGE_AREA_X + spacing;
        int totalAreaZ = AGE_AREA_Z + spacing;
        int areasPerRow = WORLD_SIZE / totalAreaX;
        int row = dimensionCount / areasPerRow;
        int col = dimensionCount % areasPerRow;
        int minX = -WORLD_SIZE / 2 + col * totalAreaX;
        int minZ = -WORLD_SIZE / 2 + row * totalAreaZ;
        int maxX = minX + AGE_AREA_X - 1;
        int maxZ = minZ + AGE_AREA_Z - 1;
        AgeBounds area = new AgeBounds(minX, minZ, maxX, maxZ);
        ageStates.put(name, new AgeState(area, WEATHER_CLEAR));
        saveAges();
    }

    public static AgeDescription getDescription(String ageName) {
        if (ageName == null) {
            return null;
        }
        if (descriptions.containsKey(ageName)) {
            return descriptions.get(ageName);
        }
        if (ServerEventsHandler.SERVER == null) {
            return null;
        }
        try {
            descriptions.put(ageName, AgeDescription.loadFromFile(ServerEventsHandler.SERVER, ageName));
            return descriptions.get(ageName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
//        dispatcher.register(Commands.literal("gendim")
//                .then(Commands.argument("name", StringArgumentType.word())
//                        .executes(context -> {
//                            String name = StringArgumentType.getString(context, "name");
//                            return execute(context.getSource(), name);
//                        })));

        dispatcher.register(Commands.literal("age")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            try {
                                load(context.getSource().getServer());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            if (player == null) {
                                return 0;
                            }

                            String message = "age: ";
                            String dim = players.get(player.getStringUUID());
                            message += dim + ", " + ageStates.get(dim);

                            String finalMessage = message;
                            context.getSource().sendSuccess(() -> Component.literal(finalMessage), true);
                            return 1;
                        }));
    }

    private static int execute(CommandSourceStack source, String name) {
        try {
            return createAndOrTeleport(source.getPlayerOrException(), name);
        } catch (Exception e) {
            source.sendSystemMessage(Component.literal("Not a player"));
            return 0;
        }
    }

    public static int createAndOrTeleport(@Nullable ServerPlayer player, String ageName) {
        try {
            MinecraftServer server = player.getServer();
            load(server);
            if (players.containsKey(player.getStringUUID()) && players.get(player.getStringUUID()).equals(ageName)) {
                player.sendSystemMessage(Component.literal("Already in " + ageName));
                return 0;
            }

            ServerLevel level = server.getLevel(AGE_DIM_KEY);
            if (level != null) {
                if (!ageStates.containsKey(ageName)) {
                    allocateNextArea(ageName);
                }
                AgeBounds area = ageStates.get(ageName).bounds();
                playersAllowedTravel.add(player.getStringUUID());
                BlockPos center = area.center();
                int ySpawn;
                for (ySpawn = level.getMaxBuildHeight(); ySpawn > level.getMinBuildHeight(); ySpawn--) {
                    BlockPos pos = new BlockPos(center.getX(), ySpawn, center.getZ());
                    if (level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP)) {
                        ySpawn++;
                        break;
                    }
                }
                
                player.teleportTo(level, center.getX(), ySpawn, center.getZ(), player.getYRot(), player.getXRot());
                level.playSound(player, new BlockPos(center.getX(), ySpawn, center.getZ()), SoundEvents.PORTAL_TRAVEL, SoundSource.NEUTRAL);
                players.put(player.getStringUUID(), ageName);
                savePlayers();
                player.sendSystemMessage(Component.literal("Teleported to age: " + ageName));
            } else {
                player.sendSystemMessage(Component.literal("Dimension not found: " + AGE_DIM_KEY));
            }
            return 1;
        } catch (Exception e) {
            MystWorlds.LOGGER.error(e.getMessage());
            if (player != null) {
                player.sendSystemMessage(Component.literal(e.getMessage()));
                playersAllowedTravel.remove(player.getStringUUID());
            }
            return 0;
        }
    }

    public static void load(MinecraftServer server) throws IOException {
        saveFileAge = server.getWorldPath(LevelResource.ROOT).resolve("mystworlds.csv");
        if (!Files.exists(saveFileAge)) {
            saveAges();
        }
        saveFilePlayers = server.getWorldPath(LevelResource.ROOT).resolve("mystworlds_players.csv");
        if (!Files.exists(saveFilePlayers)) {
            savePlayers();
        }
        loadAges();
        loadPlayers();
    }

    public static void saveAges() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(saveFileAge)) {
            writer.write("age,minX,minZ,maxX,maxZ,weather\n");
            for (Map.Entry<String, AgeState> entry : ageStates.entrySet()) {
                AgeBounds area = entry.getValue().bounds();
                writer.write(String.format(Locale.ROOT, "%s,%d,%d,%d,%d,%d\n",
                        entry.getKey(), area.minX(), area.minZ(), area.maxX(), area.maxZ(), entry.getValue().weather()));
            }
        }
    }

    public static void savePlayers() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(saveFilePlayers)) {
            writer.write("age,minX,minZ,maxX,maxZ\n");
            for (Map.Entry<String, String> entry : players.entrySet()) {
                writer.write(String.format(Locale.ROOT, "%s,%s\n", entry.getKey(), entry.getValue()));
            }
        }
    }

    public static void loadAges() throws IOException {
        ageStates = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(saveFileAge)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String name = parts[0];
                    int minX = Integer.parseInt(parts[1]);
                    int minZ = Integer.parseInt(parts[2]);
                    int maxX = Integer.parseInt(parts[3]);
                    int maxZ = Integer.parseInt(parts[4]);
                    int weather = Integer.parseInt(parts[5]);

                    ageStates.put(name, new AgeState(new AgeBounds(minX, minZ, maxX, maxZ), weather));
                }
            }
        }
    }

    public static void loadPlayers() throws IOException {
        players = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(saveFilePlayers)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String uuid = parts[0];
                    String age = parts[1];

                    players.put(uuid, age);
                }
            }
        }
    }
}