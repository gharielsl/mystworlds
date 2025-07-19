package com.gharielsl.mystworlds.screen.overlay;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

public class ScreenshotOverlay {
    public static DynamicTexture capturedTexture = null;
    public static ResourceLocation capturedTextureLocation = null;
    public static long animationStartTime = 0;
    public static boolean active = false;

    public static void startAnimation(NativeImage image) {
        if (capturedTexture != null) {
            capturedTexture.close();
            Minecraft.getInstance().getTextureManager().release(capturedTextureLocation);
        }

        capturedTexture = new DynamicTexture(image);
        capturedTextureLocation = Minecraft.getInstance().getTextureManager().register("screenshot_overlay", capturedTexture);
        animationStartTime = System.currentTimeMillis();
        active = true;
    }
}
