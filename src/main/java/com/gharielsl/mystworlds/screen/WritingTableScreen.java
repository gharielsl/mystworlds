package com.gharielsl.mystworlds.screen;

import com.gharielsl.mystworlds.MystWorlds;
import com.gharielsl.mystworlds.network.MystWorldsChannels;
import com.gharielsl.mystworlds.network.WritingTableSignPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class WritingTableScreen extends AbstractContainerScreen<WritingTableMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MystWorlds.MOD_ID, "textures/gui/writing.png");

    private static final ResourceLocation TEXTURE_MIDDLE =
            new ResourceLocation(MystWorlds.MOD_ID, "textures/gui/writing_middle.png");

    private static final ResourceLocation TEXTURE_END =
            new ResourceLocation(MystWorlds.MOD_ID, "textures/gui/writing_end.png");

    private static final ResourceLocation TEXTURE_RUNE_HINT =
            new ResourceLocation(MystWorlds.MOD_ID, "textures/gui/rune_hint.png");

    public WritingTableScreen(WritingTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 175;
        imageHeight = 198;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float particleTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (menu.blockEntity.getCurrentPage() == 0) {
            graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        } else if (menu.blockEntity.getCurrentPage() == 1) {
            RenderSystem.setShaderTexture(0, TEXTURE_MIDDLE);
            graphics.blit(TEXTURE_MIDDLE, x, y, 0, 0, imageWidth, imageHeight);
        } else {
            RenderSystem.setShaderTexture(0, TEXTURE_END);
            graphics.blit(TEXTURE_END, x, y, 0, 0, imageWidth, imageHeight);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
        graphics.drawString(font, "Page " + (menu.blockEntity.getCurrentPage() + 1) + "/3",
                leftPos + 91, topPos + 15, 0x404040, false);
    }

    EditBox editBox;
    Button signButton;

    @Override
    protected void init() {
        super.init();
        inventoryLabelY = 10000;
        titleLabelY = 10000;

        Button next = Button.builder(Component.literal(""), button -> {
            if (menu.blockEntity.getCurrentPage() == 2) {
                return;
            }
            signButton.visible = menu.blockEntity.getCurrentPage() == 1;
            editBox.visible = signButton.visible;
            menu.nextPage();
            if (minecraft != null && minecraft.player != null) {
                minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F);
            }
        }).bounds(leftPos + 120, topPos + 64, 18, 10).build();
        next.setAlpha(0);
        addRenderableWidget(next);

        Button prev = Button.builder(Component.literal(""), button -> {
            if (menu.blockEntity.getCurrentPage() == 0) {
                return;
            }
            signButton.visible = false;
            editBox.visible = false;
            menu.previousPage();
            if (minecraft != null && minecraft.player != null) {
                minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F);
            }
        }).bounds(leftPos + 36, topPos + 64, 18, 10).build();
        prev.setAlpha(0);

        addRenderableWidget(prev);

        editBox = new EditBox(font, leftPos + 93, topPos + 28, 42, 12, Component.literal(""));
        editBox.visible = false;
        addRenderableWidget(editBox);

        signButton = Button.builder(Component.literal("Sign"), button -> {
            MystWorldsChannels.INSTANCE.sendToServer(new WritingTableSignPacket(editBox.getValue()));
            menu.startCrafting(editBox.getValue());
            editBox.setValue("");
        }).bounds(leftPos + 92, topPos + 46, 44, 16).build();
        addRenderableWidget(editBox);
        signButton.visible = menu.blockEntity.getCurrentPage() == 2;
        signButton.active = false;

        addRenderableWidget(signButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (minecraft == null) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        if (editBox.isFocused() && keyCode == minecraft.options.keyInventory.getKey().getValue()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result = super.mouseClicked(mouseX, mouseY, button);

        if (editBox.isVisible()) {
            if (editBox.isMouseOver(mouseX, mouseY)) {
                editBox.setFocused(true);
            } else {
                editBox.setFocused(false);
            }
        }

        return result;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (editBox.isFocused()) {
            if (Character.isLetterOrDigit(codePoint)) {
                return editBox.charTyped(codePoint, modifiers);
            } else {
                return true;
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        signButton.visible = menu.blockEntity.getCurrentPage() == 2;
        editBox.visible = menu.blockEntity.getCurrentPage() == 2;
        if (signButton.visible) {
            String message = menu.validateCrafting(editBox.getValue());
            if (message == null) {
                signButton.active = true;
                signButton.setTooltip(null);
            } else {
                signButton.active = false;
                signButton.setTooltip(Tooltip.create(Component.literal(message)));
            }
        }
    }
}
