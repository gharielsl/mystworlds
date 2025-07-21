package com.gharielsl.mystworlds.item;

import com.gharielsl.mystworlds.age.AgeManager;
import com.gharielsl.mystworlds.age.AgeState;
import com.gharielsl.mystworlds.screen.overlay.ScreenshotOverlay;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MemoryStoneItem extends Item {
    public MemoryStoneItem(Properties properties) {
        super(properties.durability(8));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 50000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        super.releaseUsing(stack, level, entity, timeLeft);
        if (timeLeft > 49986) {
            return;
        }
        if (!(entity instanceof Player player)) {
            return;
        }
        if (!level.dimension().equals(AgeManager.AGE_DIM_KEY)) {
            level.playSound(player, player.getOnPos().above(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            player.displayClientMessage(Component.literal("Only works inside of ages"), true);
            return;
        }
        if (!level.isClientSide) {
            level.playSound(null, player.getOnPos().above(), SoundEvents.ENDER_EYE_DEATH, SoundSource.NEUTRAL, 1.0F, 1.0F);
            player.getCooldowns().addCooldown(this, 40);
            if (!player.isCreative()) {
                stack.hurtAndBreak(1, entity, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
            }
            return;
        }
        takeAgeScreenshot((boolean success) -> {
            if (success) {
                NativeImage image;
                Path shotPath = findAgeScreenshot();
                if (shotPath == null) {
                    return;
                }
                try (InputStream inputStream = new FileInputStream(shotPath.toFile())) {
                    image = NativeImage.read(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                ScreenshotOverlay.startAnimation(image);
            }
        });
    }

    public interface AgeScreenshotConsumer {
        void onComplete(boolean success);
    }

    public static Path findAgeScreenshot(String age) {
        Minecraft mc = Minecraft.getInstance();
        String worldId;
        if (Minecraft.getInstance().getSingleplayerServer() != null) {
            worldId = Minecraft.getInstance().getSingleplayerServer().getWorldData().getLevelName();
        } else if (Minecraft.getInstance().getCurrentServer() != null) {
            worldId = Minecraft.getInstance().getCurrentServer().ip;
        } else {
            return null;
        }
        if (age == null) {
            return null;
        }
        AgeState state = AgeManager.ageStates.get(age);
        if (state == null) {
            return null;
        }
        Path folder = mc.gameDirectory.toPath().resolve("mystworlds").resolve(worldId);
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return folder.resolve(age + ".png");
    }

    public static Path findAgeScreenshot() {
        Minecraft mc = Minecraft.getInstance();
        String worldId;
        if (Minecraft.getInstance().getSingleplayerServer() != null) {
            worldId = Minecraft.getInstance().getSingleplayerServer().getWorldData().getLevelName();
        } else if (Minecraft.getInstance().getCurrentServer() != null) {
            worldId = Minecraft.getInstance().getCurrentServer().ip;
        } else {
            return null;
        }
        Player player = mc.player;
        if (player == null) {
            return null;
        }

        String age = AgeManager.players.get(player.getStringUUID());
        if (age == null) {
            return null;
        }
        AgeState state = AgeManager.ageStates.get(age);
        if (state == null) {
            return null;
        }
        Path folder = mc.gameDirectory.toPath().resolve("mystworlds").resolve(worldId);
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return folder.resolve(age + ".png");
    }

    public static void takeAgeScreenshot(AgeScreenshotConsumer consumer) {
        Minecraft mc = Minecraft.getInstance();
        int windowWidth = mc.getWindow().getGuiScaledWidth();
        int windowHeight = mc.getWindow().getGuiScaledHeight();
        if (windowWidth >= 800 && windowHeight >= 600) {
            consumer.onComplete(false);
            return;
        }
        Path out = findAgeScreenshot();
        if (out == null) {
            consumer.onComplete(false);
            return;
        }

        mc.execute(() -> {
            boolean oldHideGui = mc.options.hideGui;
            mc.options.hideGui = true;
            queueNextFrameScreenshot(mc, out, consumer, oldHideGui);
        });
    }

    private static void queueNextFrameScreenshot(Minecraft mc, Path out, AgeScreenshotConsumer consumer, boolean oldHideGui) {
        RenderSystem.recordRenderCall(() -> {
            try {
                NativeImage image = Screenshot.takeScreenshot(mc.getMainRenderTarget());

                int screenshotWidth = 560;
                int screenshotHeight = 250;
                NativeImage cropped = new NativeImage(NativeImage.Format.RGBA, screenshotWidth, screenshotHeight, false);

                int offsetX = (image.getWidth() - screenshotWidth) / 2;
                int offsetY = (image.getHeight() - screenshotHeight) / 2;

                for (int y = 0; y < screenshotHeight; y++) {
                    for (int x = 0; x < screenshotWidth; x++) {
                        cropped.setPixelRGBA(x, y, image.getPixelRGBA(x + offsetX, y + offsetY));
                    }
                }

                cropped.writeToFile(out.toFile());
                consumer.onComplete(true);

                cropped.close();
                image.close();
            } catch (Exception e) {
                e.printStackTrace();
                consumer.onComplete(false);
            } finally {
                mc.options.hideGui = oldHideGui;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal("Use inside an age to set cover image").withStyle(ChatFormatting.GRAY));
    }
}
