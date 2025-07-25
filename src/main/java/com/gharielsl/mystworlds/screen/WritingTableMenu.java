package com.gharielsl.mystworlds.screen;

import com.gharielsl.mystworlds.age.AgeDescription;
import com.gharielsl.mystworlds.age.AgeManager;
import com.gharielsl.mystworlds.block.MystWorldsBlocks;
import com.gharielsl.mystworlds.block.entity.WritingTableBlockEntity;
import com.gharielsl.mystworlds.item.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;

public class WritingTableMenu extends AbstractContainerMenu {
    public final WritingTableBlockEntity blockEntity;

    private final Level level;
    private int previousPage = 0;

    public WritingTableMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId,
                inventory.player.getInventory(),
                inventory.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(6 + 3 * 6));
    }

    public WritingTableMenu(int containerId, Inventory inventory, BlockEntity be, ContainerData data) {
        super(MystWorldsMenus.WRITING_TABLE_MENU.get(), containerId);
        checkContainerSize(inventory, 6 + 3 * 6);
        blockEntity = ((WritingTableBlockEntity) be);
        blockEntity.setCurrentPage(0);
        level = inventory.player.level();

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            addSlot(new PaperSlot(iItemHandler, 0, 8, 95));
            addSlot(new UnlinkedDescriptionSlot(iItemHandler, 1, 26, 95));
            addSlot(new OutputSlot(iItemHandler, 2, 80, 95));
            addSlot(new InkSlot(iItemHandler, 3, 116, 95, this));
            addSlot(new InkSlot(iItemHandler, 4, 134, 95, this));
            addSlot(new InkSlot(iItemHandler, 5, 152, 95, this));
            addDynamicSlots(iItemHandler);
        });

        addDataSlots(data);
    }

    RuneSlot[] runeSlots = new RuneSlot[6 + 3 * 6];

    private void addDynamicSlots(IItemHandler handler) {
        for (int i = 0; i < 3; i++) {
            int index = 6 + i * 6;
            if (runeSlots[i * 6] == null) {
                runeSlots[i * 6] = new RuneSlot(handler, index, 8, 27);
                addSlot(runeSlots[i * 6]);
            }
            if (runeSlots[i * 6 + 1] == null) {
                runeSlots[i * 6 + 1] = new RuneSlot(handler, index + 1, 8, 45);
                addSlot(runeSlots[i * 6 + 1]);
            }
            if (runeSlots[i * 6 + 2] == null) {
                runeSlots[i * 6 + 2] = new RuneSlot(handler, index + 2, 8, 63);
                addSlot(runeSlots[i * 6 + 2]);
            }
            if (runeSlots[i * 6 + 3] == null) {
                runeSlots[i * 6 + 3] = new RuneSlot(handler, index + 3, 152, 27);
                addSlot(runeSlots[i * 6 + 3]);
            }
            if (runeSlots[i * 6 + 4] == null) {
                runeSlots[i * 6 + 4] = new RuneSlot(handler, index + 4, 152, 45);
                addSlot(runeSlots[i * 6 + 4]);
            }
            if (runeSlots[i * 6 + 5] == null) {
                runeSlots[i * 6 + 5] = new RuneSlot(handler, index + 5, 152, 63);
                addSlot(runeSlots[i * 6 + 5]);
            }
            for (int j = i * 6; j < i * 6 + 6; j++) {
                runeSlots[j].setActive(blockEntity.getCurrentPage() == i);
            }

        }
    }

    private void updateDynamicSlots() {
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(this::addDynamicSlots);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + i * 9 + 9, 8 + l * 18, 117 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 175));
        }
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int PLAYER_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int PLAYER_FIRST_INDEX = 0;
    private static final int CONTAINER_FIRST_INDEX = PLAYER_FIRST_INDEX + PLAYER_SLOT_COUNT;
    private static final int CONTAINER_SLOT_COUNT = 6 + 6 * 3;

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < PLAYER_FIRST_INDEX + PLAYER_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, CONTAINER_FIRST_INDEX, CONTAINER_FIRST_INDEX
                    + CONTAINER_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < CONTAINER_FIRST_INDEX + CONTAINER_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, PLAYER_FIRST_INDEX, PLAYER_FIRST_INDEX + PLAYER_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, MystWorldsBlocks.WRITING_TABLE_BLOCK.get());
    }

    public void tick() {
        if (previousPage != blockEntity.getCurrentPage()) {
            previousPage = blockEntity.getCurrentPage();
            updateDynamicSlots();
        }
    }

    public void nextPage() {
        int page = blockEntity.getCurrentPage();
        if (page < 2) {
            blockEntity.setCurrentPage(page + 1);
        }
    }

    public void previousPage() {
        int page = blockEntity.getCurrentPage();
        if (page > 0) {
            blockEntity.setCurrentPage(page - 1);
        }
    }

    public String validateCrafting(String title) {
        if (title.isEmpty()) {
            return "Enter a title";
        }
        if (title.equals("null")) {
            return "That's still an issue, choose a different name";
        }
        if (title.length() > 24) {
            return "That's too long";
        }
        if (AgeManager.ageStates.size() > 2000) {
            return "You already have 2000 ages, that's enough!";
        }
        for (char c : title.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return "Invalid title";
            }
        }
        if (blockEntity.itemHandler.getStackInSlot(0).getCount() < 3) {
            return "Missing paper";
        }
        if (!blockEntity.itemHandler.getStackInSlot(1).is(MystWorldsItems.UNLINKED_DESCRIPTION.get())) {
            return "Missing book";
        }
        boolean hasInk = false;
        for (int i = 3; i < 6 && !hasInk; i++) {
            if (blockEntity.itemHandler.getStackInSlot(i).getItem() instanceof MysticalInkItem) {
                hasInk = true;
            }
        }
        if (!hasInk) {
            return "Missing ink";
        }
        if (!blockEntity.itemHandler.getStackInSlot(2).isEmpty()) {
            return "";
        }
        if (AgeManager.ageStates.keySet().contains(title)) {
            return "Age with this name already exists";
        }
        int runeCount = 0;
        int greaterRunes = 0;
        for (int i = 6; i < 6 + 6 * 3; i++) {
            if (!blockEntity.itemHandler.getStackInSlot(i).isEmpty()) {
                runeCount++;
            }
            if (blockEntity.itemHandler.getStackInSlot(i).getItem() instanceof GreaterRuneItem) {
                greaterRunes++;
            }
        }
        if (runeCount < 3 && greaterRunes == 0) {
            return "Must place at least 3 runes";
        }
        return null;
    }

    public void startCrafting(String title) {
        if (validateCrafting(title) != null) {
            return;
        }

        List<RuneItem> runes = new ArrayList<>();
        for (int i = 6; i < 6 + 6 * 3; i++) {
            if (!blockEntity.itemHandler.getStackInSlot(i).isEmpty()) {
                runes.add((RuneItem) blockEntity.itemHandler.getStackInSlot(i).getItem());
                blockEntity.itemHandler.getStackInSlot(i).shrink(1);
            }
        }
        AgeDescription description = new AgeDescription(
                runes,
                blockEntity.itemHandler.getStackInSlot(3).getItem() instanceof MysticalInkItem ink1 ? ink1 : null,
                blockEntity.itemHandler.getStackInSlot(4).getItem() instanceof MysticalInkItem ink2 ? ink2 : null,
                blockEntity.itemHandler.getStackInSlot(5).getItem() instanceof MysticalInkItem ink3 ? ink3 : null,
                title
        );
        blockEntity.itemHandler.getStackInSlot(3).shrink(1);
        blockEntity.itemHandler.getStackInSlot(4).shrink(1);
        blockEntity.itemHandler.getStackInSlot(5).shrink(1);
        DescriptionItem descriptionItem;
        if (description.getChaosAmount() == 1) {
            descriptionItem = (DescriptionItem) MystWorldsItems.DESCRIPTION_STABLE.get();
        } else if (description.getChaosAmount() < 4) {
            descriptionItem = (DescriptionItem) MystWorldsItems.DESCRIPTION_NEUTRAL.get();
        } else {
            descriptionItem = (DescriptionItem) MystWorldsItems.DESCRIPTION_UNSTABLE.get();
        }
        blockEntity.itemHandler.getStackInSlot(0).shrink(3);
        blockEntity.itemHandler.getStackInSlot(1).shrink(1);
        ItemStack result = new ItemStack(descriptionItem);
        result.setHoverName(Component.literal(title).setStyle(Style.EMPTY.withItalic(false)));

        DescriptionItem.setAgeDescription(result, description);
        blockEntity.itemHandler.setStackInSlot(2, result);
    }

    private static class RuneSlot extends SlotItemHandler {
        private boolean isActive = true;

        public RuneSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return isActive && stack.getItem() instanceof RuneItem;
        }

        @Override
        public int getMaxStackSize() {
            return isActive ? 1 : 0;
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return isActive ? 1 : 0;
        }
    }

    private static class InkSlot extends SlotItemHandler {
        private final Level level;
        private ItemStack lastStack = ItemStack.EMPTY;
        private WritingTableMenu menu;
        private boolean isFirstSoundInk = true;

        public InkSlot(IItemHandler handler, int index, int x, int y, WritingTableMenu menu) {
            super(handler, index, x, y);
            this.level = menu.level;
            this.menu = menu;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof MysticalInkItem;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return 1;
        }

        @Override
        public void set(ItemStack stack) {
            if (isFirstSoundInk) {
                isFirstSoundInk = false;
            } else if (!ItemStack.matches(stack, lastStack) && !stack.isEmpty()) {
                if (level.isClientSide && Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.playSound(SoundEvents.BREWING_STAND_BREW, 1.0F, 1.0F);
                }
            }
            lastStack = stack.copy();
            super.set(stack);
        }
    }

    private static class PaperSlot extends SlotItemHandler {
        public PaperSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() == Items.PAPER;
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }

    private static class UnlinkedDescriptionSlot extends SlotItemHandler {
        public UnlinkedDescriptionSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() == MystWorldsItems.UNLINKED_DESCRIPTION.get();
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return 1;
        }
    }

    private static class OutputSlot extends SlotItemHandler {
        public OutputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}