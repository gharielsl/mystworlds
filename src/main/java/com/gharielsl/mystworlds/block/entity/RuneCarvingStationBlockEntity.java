package com.gharielsl.mystworlds.block.entity;

import com.gharielsl.mystworlds.block.MystWorldsBlocks;
import com.gharielsl.mystworlds.block.WritingTableBlock;
import com.gharielsl.mystworlds.item.MysticalInkItem;
import com.gharielsl.mystworlds.network.MystWorldsChannels;
import com.gharielsl.mystworlds.network.WritingTablePagePacket;
import com.gharielsl.mystworlds.screen.RuneCarvingMenu;
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

public class RuneCarvingStationBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(8);
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final ContainerData data;

    public RuneCarvingStationBlockEntity(BlockPos pos, BlockState state) {
        super(MystWorldsBlockEntities.RUNE_CARVING_STATION_BLOCK_ENTITY.get(), pos, state);
        data = new SimpleContainerData(0);
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
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("Items", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Rune Carving");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new RuneCarvingMenu(containerId, inventory, this, data);
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
    }
}
