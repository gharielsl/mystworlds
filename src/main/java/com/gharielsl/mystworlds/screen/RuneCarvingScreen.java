package com.gharielsl.mystworlds.screen;

import com.gharielsl.mystworlds.MystWorlds;
import com.gharielsl.mystworlds.network.CarveRunePacket;
import com.gharielsl.mystworlds.network.MystWorldsChannels;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RuneCarvingScreen extends AbstractContainerScreen<RuneCarvingMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MystWorlds.MOD_ID, "textures/gui/rune_carving.png");

    private Button carve;
    private long carvePressedTime = -1L;
    private boolean isPressed = false;

    public RuneCarvingScreen(RuneCarvingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float particleTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        graphics.blit(TEXTURE, leftPos + 51 + 2, topPos + 33 + 2, carve.active ? 176 : 192, 0, 16, 16);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        carve = Button.builder(Component.literal(""), button -> {
            carvePressedTime = System.currentTimeMillis() + 500;
            button.active = false;
            isPressed = true;
            MystWorldsChannels.INSTANCE.sendToServer(new CarveRunePacket());
        }).pos(leftPos + 51, topPos + 33).size(20, 20).build();
        testActive();
        addRenderableWidget(carve);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        testActive();
    }

    private void testActive() {
        if (carvePressedTime > 0 && System.currentTimeMillis() >= carvePressedTime) {
            carve.setFocused(false);
            isPressed = false;
            carvePressedTime = -1L;
        } else if (!isPressed) {
            carve.active = menu.validateCrafting();
        }
    }
}
