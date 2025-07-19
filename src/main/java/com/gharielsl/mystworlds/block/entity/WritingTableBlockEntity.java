package com.gharielsl.mystworlds.block.entity;

import com.gharielsl.mystworlds.block.MystWorldsBlocks;
import com.gharielsl.mystworlds.block.WritingTableBlock;
import com.gharielsl.mystworlds.item.MystWorldsItems;
import com.gharielsl.mystworlds.item.MysticalInkItem;
import com.gharielsl.mystworlds.network.MystWorldsChannels;
import com.gharielsl.mystworlds.network.WritingTablePagePacket;
import com.gharielsl.mystworlds.screen.WritingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WritingTableBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(6 + 6 * 3);
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final ContainerData data;
    private int currentPage = 0;

    public WritingTableBlockEntity(BlockPos pos, BlockState state) {
        super(MystWorldsBlockEntities.WRITING_TABLE_BLOCK_ENTITY.get(), pos, state);
        data = new SimpleContainerData(0);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        if (level != null && level.isClientSide) {
            MystWorldsChannels.INSTANCE.sendToServer(new WritingTablePagePacket(worldPosition, currentPage));
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Items"));
        currentPage = 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("Items", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Writing Desk");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new WritingTableMenu(containerId, inventory, this, data);
    }

    public void drop() {
        if (level == null) {
            return;
        }
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(level, worldPosition, inventory);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        setChanged();
        if (!level.isClientSide && state.is(MystWorldsBlocks.WRITING_TABLE_BLOCK.get())) {
            Direction facing = state.getValue(WritingTableBlock.FACING);
            BlockPos rightPos = pos.relative(facing.getOpposite());
            BlockState rightState = level.getBlockState(rightPos);

            if (!rightState.is(MystWorldsBlocks.WRITING_TABLE_BLOCK.get())) {
                return;
            }
            boolean rightNeedsUpdate = false;

            if (itemHandler.getStackInSlot(0).isEmpty() == rightState.getValue(WritingTableBlock.PAPER)) {
                rightState = rightState.setValue(WritingTableBlock.PAPER, !rightState.getValue(WritingTableBlock.PAPER));
                rightNeedsUpdate = true;
            }
            if (itemHandler.getStackInSlot(1).isEmpty() == rightState.getValue(WritingTableBlock.BOOK)) {
                rightState = rightState.setValue(WritingTableBlock.BOOK, !rightState.getValue(WritingTableBlock.BOOK));
                rightNeedsUpdate = true;
            }

            boolean hasInk = false;
            for (int i = 3; i < 6 && !hasInk; i++) {
                if (itemHandler.getStackInSlot(i).getItem() instanceof MysticalInkItem) {
                    hasInk = true;
                }
            }
            if (hasInk != state.getValue(WritingTableBlock.INK)) {
                state = state.setValue(WritingTableBlock.INK, !state.getValue(WritingTableBlock.INK));
                level.setBlock(pos, state, 3);
            }

            if (rightNeedsUpdate) {
                level.setBlock(rightPos, rightState, 3);
            }
        }
    }
}