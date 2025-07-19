package com.gharielsl.mystworlds.screen.overlay;

import com.gharielsl.mystworlds.MystWorlds;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MystWorlds.MOD_ID)
public class ScreenshotOverlayRenderer {
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!ScreenshotOverlay.active || ScreenshotOverlay.capturedTextureLocation == null) return;

        Minecraft mc = Minecraft.getInstance();
        long elapsed = System.currentTimeMillis() - ScreenshotOverlay.animationStartTime;

        int centerHoldTime = 500;
        int animationDuration = 1000;
        int endHoldTime = 1000;
        int totalTime = centerHoldTime + animationDuration + endHoldTime;

        if (elapsed > totalTime) {
            ScreenshotOverlay.active = false;
            return;
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int centerX = screenWidth / 2 - 56;
        int centerY = screenHeight / 2 - 25;

        int targetX = 10;
        int targetY = screenHeight - 25 * 2 - 10;

        int currentX, currentY;
        float alpha = 1.0f;

        if (elapsed < centerHoldTime) {
            currentX = centerX;
            currentY = centerY;
        } else if (elapsed < centerHoldTime + animationDuration) {
            float progress = (elapsed - centerHoldTime) / (float) animationDuration;
            currentX = (int) (centerX + (targetX - centerX) * progress);
            currentY = (int) (centerY + (targetY - centerY) * progress);
        } else {
            currentX = targetX;
            currentY = targetY;
            float fadeElapsed = elapsed - (centerHoldTime + animationDuration);
            alpha = 1.0f - (fadeElapsed / (float) endHoldTime);
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        event.getGuiGraphics().blit(ScreenshotOverlay.capturedTextureLocation, currentX, currentY, 0, 0, 56 * 2, 25 * 2, 56 * 2, 25 * 2);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

}
