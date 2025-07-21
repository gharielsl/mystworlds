package com.gharielsl.mystworlds.screen;

import com.gharielsl.mystworlds.MystWorlds;
import com.gharielsl.mystworlds.age.AgeDescription;
import com.gharielsl.mystworlds.age.AgeManager;
import com.gharielsl.mystworlds.item.MemoryStoneItem;
import com.gharielsl.mystworlds.network.MystWorldsChannels;
import com.gharielsl.mystworlds.network.TravelToAgePacket;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LinkingBookScreen extends Screen {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MystWorlds.MOD_ID, "textures/gui/book.png");

    private static final ResourceLocation TEXTURE_OVERWORLD =
            new ResourceLocation(MystWorlds.MOD_ID, "textures/gui/overworld.png");

    private static final int IMAGE_WIDTH = 192;
    private static final int IMAGE_HEIGHT = 144;

    private AgeDescription description;
    private DynamicTexture screenshotTexture;
    private ResourceLocation screenshotLocation;

    public LinkingBookScreen(Component component, AgeDescription description) {
        super(component);
        this.description = description;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);

        int x = (width - IMAGE_WIDTH) / 2;
        int y = (height - IMAGE_HEIGHT) / 2 - 16;

        graphics.blit(TEXTURE, x, y, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        int centerX = x + (11 + 94) / 2;
        int centerY = y + (19 + 11) / 2;

        int textWidth = this.font.width(description.getAgeName());
        graphics.drawString(this.font, description.getAgeName(), centerX - textWidth / 2, centerY, 0x404040, false);

        ResourceLocation tex = screenshotLocation;
        String currentPlayerAge = null;
        if (Minecraft.getInstance().player != null) {
            currentPlayerAge = AgeManager.players.get(Minecraft.getInstance().player.getStringUUID());
        }
        if (currentPlayerAge != null && currentPlayerAge.equals(description.getAgeName())) {
            tex = TEXTURE_OVERWORLD;
        }
        if (tex != null) {
            int imageX = (width - IMAGE_WIDTH) / 2 + 111;
            int imageY = (height - IMAGE_HEIGHT) / 2 - 16 + 25;
            Minecraft.getInstance().getTextureManager().bindForSetup(tex);
            graphics.blit(tex, imageX, imageY, 0, 0, 56, 25, 56, 25);
        }

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.literal("Close"), button -> {
            onClose();
        }).size(IMAGE_WIDTH, 24).pos((width - IMAGE_WIDTH) / 2, (height - IMAGE_HEIGHT) / 2 + IMAGE_HEIGHT).build());
        Button link = Button.builder(Component.literal(""), button -> {
            MystWorldsChannels.INSTANCE.sendToServer(new TravelToAgePacket());
        }).size(56, 25).pos((width - IMAGE_WIDTH) / 2 + 111, (height - IMAGE_HEIGHT) / 2 - 16 + 25).build();
        link.setTooltip(Tooltip.create(Component.literal("Click to travel")));
        link.setAlpha(0);
        Path screenshot = MemoryStoneItem.findAgeScreenshot(description.getAgeName());
        if (screenshot != null && Files.exists(screenshot)) {
            NativeImage nativeImage = null;
            try {
                nativeImage = NativeImage.read(Files.newInputStream(screenshot));
                screenshotTexture = new DynamicTexture(nativeImage);
                screenshotLocation = Minecraft.getInstance().getTextureManager().register("age_shot_" + System.nanoTime(), screenshotTexture);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        addRenderableWidget(link);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (screenshotTexture != null) {
            screenshotTexture.close();
            Minecraft.getInstance().getTextureManager().release(screenshotLocation);
            screenshotTexture = null;
            screenshotLocation = null;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean keyPressed(int p_97765_, int p_97766_, int p_97767_) {
        InputConstants.Key mouseKey = InputConstants.getKey(p_97765_, p_97766_);
        if (super.keyPressed(p_97765_, p_97766_, p_97767_)) {
            return true;
        } else if (minecraft != null && minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
            onClose();
            return true;
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
