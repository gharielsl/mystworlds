package com.gharielsl.mystworlds.item;

import com.gharielsl.mystworlds.age.AgeDescription;
import com.gharielsl.mystworlds.screen.LinkingBookScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DescriptionItem extends Item {
    private final String stability;

    public DescriptionItem(Properties properties, String stability) {
        super(properties);
        this.stability = stability;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        ItemStack stack = player.getItemInHand(hand);
        AgeDescription description = getAgeDescription(stack);
        if (description == null) {
            return result;
        }
        if (level.isClientSide) {
            Minecraft.getInstance().setScreen(new LinkingBookScreen(Component.literal(""), description));
            return result;
        }
        try {
            AgeDescription.saveToFile(level.getServer(), description);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendSystemMessage(Component.literal(e.getMessage()));
            return result;
        }

//        AgeManager.createAndOrTeleport((ServerPlayer) player, description.getAgeName());
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    public static void setAgeDescription(ItemStack stack, AgeDescription description) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.put("AgeDescription", description.save());
    }

    public static AgeDescription getAgeDescription(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("AgeDescription")) {
            CompoundTag descTag = stack.getTag().getCompound("AgeDescription");
            AgeDescription description = new AgeDescription(descTag);
            description.load(descTag);
            return description;
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);

        components.add(Component.literal(stability)
                .withStyle(Map.of(
                        "Stable", ChatFormatting.GREEN,
                        "Unstable", ChatFormatting.RED,
                        "Neutral", ChatFormatting.YELLOW
                ).get(stability))
        );

        AgeDescription description = getAgeDescription(stack);
        if (description != null) {
//            components.add(Component.literal("Time: " + timeName(description.getTime())).withStyle(ChatFormatting.GRAY));
//            components.add(Component.literal("Fire Amount: " + description.getFireAmount()).withStyle(ChatFormatting.GRAY));
//            components.add(Component.literal("Water Amount: " + description.getLiquidY()).withStyle(ChatFormatting.GRAY));
//            components.add(Component.literal("Explosion Amount: " + description.getExplosionAmount()).withStyle(ChatFormatting.GRAY));
//            components.add(Component.literal("Chaos Amount: " + description.getChaosAmount()).withStyle(ChatFormatting.GRAY));
//            components.add(Component.literal("Carving Threshold: " + description.getCarvingThreshold()).withStyle(ChatFormatting.GRAY));
//            components.add(Component.literal("Terrain Threshold: " + description.getTerrainThreshold()).withStyle(ChatFormatting.GRAY));
        }
    }

    private String skyName(int sky) {
        return switch (sky) {
            case AgeDescription.WEATHER_CLEAR -> "Clear";
            case AgeDescription.WEATHER_RAIN -> "Rain";
            case AgeDescription.WEATHER_STORM -> "Storm";
            case AgeDescription.WEATHER_NORMAL -> "Normal";
            case AgeDescription.WEATHER_CHAOS -> "Chaos";
            default -> "Unknown";
        };
    }

    private String timeName(int time) {
        return switch (time) {
            case AgeDescription.TIME_DAY -> "Day";
            case AgeDescription.TIME_NIGHT -> "Night";
            case AgeDescription.TIME_NORMAL -> "Normal";
            case AgeDescription.TIME_CHAOS -> "Chaos";
            default -> "Unknown";
        };
    }
}
