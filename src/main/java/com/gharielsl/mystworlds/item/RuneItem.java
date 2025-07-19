package com.gharielsl.mystworlds.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RuneItem extends Item {
    private final String clazz;
    private final String effect;
    private final ChatFormatting effectColor;

    public RuneItem(Properties properties, String clazz, String effect, ChatFormatting effectColor) {
        super(properties);
        this.clazz = clazz;
        this.effect = effect;
        this.effectColor = effectColor;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        components.add(Component.literal("Class: " + clazz).withStyle(ChatFormatting.GRAY));
        components.add(Component.empty().append(Component.literal("Effect: ").withStyle(ChatFormatting.GRAY).append(Component.literal(effect).withStyle(effectColor))));
    }

    public String getClazz() {
        return clazz;
    }

    public String getEffect() {
        return effect;
    }

    public ChatFormatting getEffectColor() {
        return effectColor;
    }
}
