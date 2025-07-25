package com.gharielsl.mystworlds.screen;

import com.gharielsl.mystworlds.block.MystWorldsBlocks;
import com.gharielsl.mystworlds.block.entity.RuneCarvingStationBlockEntity;
import com.gharielsl.mystworlds.item.GreaterRuneItem;
import com.gharielsl.mystworlds.item.MystWorldsItems;
import com.gharielsl.mystworlds.item.RuneItem;
import com.gharielsl.mystworlds.item.UncarvedRuneItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RuneCarvingMenu extends AbstractContainerMenu {
    public final RuneCarvingStationBlockEntity blockEntity;
    private final Level level;

    public RuneCarvingMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId,
                inventory.player.getInventory(),
                inventory.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(8));
    }

    public RuneCarvingMenu(int containerId, Inventory inventory, BlockEntity be, ContainerData data) {
        super(MystWorldsMenus.RUNE_CARVING_MENU.get(), containerId);
        checkContainerSize(inventory, 8);
        blockEntity = ((RuneCarvingStationBlockEntity) be);
        level = inventory.player.level();

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            addSlot(new RuneSlot(iItemHandler, 0, 26, 22));
            addSlot(new AmethystSlot(iItemHandler, 1, 26, 48));

            addSlot(new OutputSlot(iItemHandler, 2, 80, 22));
            addSlot(new OutputSlot(iItemHandler, 3, 106, 22));
            addSlot(new OutputSlot(iItemHandler, 4, 132, 22));
            addSlot(new OutputSlot(iItemHandler, 5, 80, 48));
            addSlot(new OutputSlot(iItemHandler, 6, 106, 48));
            addSlot(new OutputSlot(iItemHandler, 7, 132, 48));
        });

        addDataSlots(data);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int PLAYER_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int PLAYER_FIRST_INDEX = 0;
    private static final int CONTAINER_FIRST_INDEX = PLAYER_FIRST_INDEX + PLAYER_SLOT_COUNT;
    private static final int CONTAINER_SLOT_COUNT = 8;

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

    public boolean validateCrafting() {
        boolean hasRune = !blockEntity.itemHandler.getStackInSlot(0).isEmpty();
        boolean hasAmethyst = !blockEntity.itemHandler.getStackInSlot(1).isEmpty();
        boolean hasEmptySlot = false;
        for (int i = 2; i < 8 && !hasEmptySlot; i++) {
            if (blockEntity.itemHandler.getStackInSlot(i).isEmpty()) {
                hasEmptySlot = true;
            }
        }
        return hasRune && hasAmethyst && hasEmptySlot;
    }

    public void startCrafting() {
        while (validateCrafting()) {
            ItemStack uncarvedRune = blockEntity.itemHandler.getStackInSlot(0);
            ItemStack[] result = new ItemStack[2];
            if (!(uncarvedRune.getItem() instanceof UncarvedRuneItem uncarvedRuneItem)) {
                if (uncarvedRune.getItem() == MystWorldsItems.GREATER_RUNE.get()) {
                    List<Item> optionsUncarved = List.of(
                            MystWorldsItems.TERRAIN_RUNE.get(),
                            MystWorldsItems.SKY_RUNE.get(),
                            MystWorldsItems.STABILITY_RUNE.get(),
                            MystWorldsItems.TIME_RUNE.get());
                    List<Item> rareRunes = List.of(
                            MystWorldsItems.GREATER_RUNE_FLAT.get(),
                            MystWorldsItems.GREATER_RUNE_SKYBLOCK.get(),
                            MystWorldsItems.GREATER_RUNE_SKYGRID.get(),
                            MystWorldsItems.GREATER_RUNE_VOID.get());
                    if (level.random.nextInt(2) == 0) {
                        result[0] = new ItemStack(optionsUncarved.get(level.random.nextInt(optionsUncarved.size())));
                    } else {
                        result[0] = new ItemStack(rareRunes.get(level.random.nextInt(rareRunes.size())));
                    }
                    result[1] = new ItemStack(Items.AMETHYST_SHARD, level.random.nextInt(4));
                } else if (uncarvedRune.getItem() == MystWorldsItems.MEMORY_STONE.get()) {
                    if (level.random.nextInt(12) == 0) {
                        result[0] = new ItemStack(MystWorldsItems.GREATER_RUNE.get());
                    } else {
                        result[0] = new ItemStack(Items.AIR);
                    }
                    result[1] = new ItemStack(Items.AMETHYST_SHARD, level.random.nextInt(2));
                } else {
                    return;
                }
            } else {
                result = uncarvedRuneItem.carveRandom(level.random);
            }
            uncarvedRune.shrink(1);
            blockEntity.itemHandler.getStackInSlot(1).shrink(1);
            moveItemStackTo(result[0], CONTAINER_FIRST_INDEX + 2, CONTAINER_FIRST_INDEX + 8, false);
            moveItemStackTo(result[1], CONTAINER_FIRST_INDEX + 2, CONTAINER_FIRST_INDEX + 8, false);
        }
        level.playSound(null, blockEntity.getBlockPos(), SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.NEUTRAL);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, MystWorldsBlocks.RUNE_CARVING_STATION.get());
    }

    private static class RuneSlot extends SlotItemHandler {
        public RuneSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.getItem() instanceof UncarvedRuneItem ||
                    stack.is(MystWorldsItems.GREATER_RUNE.get()) ||
                    stack.is(MystWorldsItems.MEMORY_STONE.get());
        }
    }

    private static class AmethystSlot extends SlotItemHandler {
        public AmethystSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.is(Items.AMETHYST_SHARD);
        }
    }

    private static class OutputSlot extends SlotItemHandler {
        public OutputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.getItem() instanceof RuneItem ||
                    stack.is(MystWorldsItems.MEMORY_STONE.get()) ||
                    stack.is(Items.REDSTONE) ||
                    stack.is(Items.LAPIS_LAZULI) ||
                    stack.is(Items.AMETHYST_SHARD) ||
                    stack.is(MystWorldsItems.GREATER_RUNE.get()) ||
                    stack.getItem() instanceof GreaterRuneItem ||
                    stack.getItem() instanceof UncarvedRuneItem;
        }
    }
}
