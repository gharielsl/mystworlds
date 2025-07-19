package com.gharielsl.mystworlds.block;

import com.gharielsl.mystworlds.block.entity.MystWorldsBlockEntities;
import com.gharielsl.mystworlds.block.entity.WritingTableBlockEntity;
import com.gharielsl.mystworlds.item.MysticalInkItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WritingTableBlock extends BaseEntityBlock {
    public static final EnumProperty<WritingTablePart> PART = EnumProperty.create("part", WritingTablePart.class);
    public static final BooleanProperty PAPER = BooleanProperty.create("paper");
    public static final BooleanProperty BOOK = BooleanProperty.create("book");
    public static final BooleanProperty INK = BooleanProperty.create("ink");
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    protected WritingTableBlock() {
        super(Properties.copy(Blocks.OAK_SLAB).noOcclusion());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (!level.isClientSide) {
            WritingTablePart part = state.getValue(PART);
            Direction facing = state.getValue(FACING);
            BlockPos otherPos = (part == WritingTablePart.RIGHT) ? pos.relative(facing) : pos.relative(facing.getOpposite());

            handleUse(level, part == WritingTablePart.LEFT ? pos : otherPos, (ServerPlayer) player);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    public void handleUse(Level level, BlockPos pos, ServerPlayer player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof WritingTableBlockEntity writingTable) {
            NetworkHooks.openScreen(player, writingTable, pos);
        }

    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getClockWise(); // Rotate 90 degrees
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockPos otherPos = pos.relative(facing);

        if (!level.getBlockState(otherPos).canBeReplaced(context)) {
            return null;
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(PART, WritingTablePart.RIGHT)
                .setValue(INK, false)
                .setValue(PAPER, false)
                .setValue(BOOK, false);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        Direction facing = state.getValue(FACING);
        BlockPos headPos = pos.relative(facing);

        level.setBlock(headPos, state.setValue(PART, WritingTablePart.LEFT), 3);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            WritingTablePart part = state.getValue(PART);
            Direction facing = state.getValue(FACING);
            BlockPos otherPos = (part == WritingTablePart.RIGHT) ? pos.relative(facing) : pos.relative(facing.getOpposite());
            BlockPos leftPos = part == WritingTablePart.RIGHT ? otherPos : pos;
            if (level.getBlockEntity(leftPos) instanceof WritingTableBlockEntity be) {
                be.drop();
            }

            if (level.getBlockState(otherPos).getBlock() == this) {
                level.removeBlock(otherPos, false);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, PAPER, INK, BOOK);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter block, BlockPos pos, CollisionContext collision) {
        return Block.box(0, 0, 0, 16, 14, 16);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WritingTableBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public enum WritingTablePart implements StringRepresentable {
        LEFT("left"),
        RIGHT("right");

        private final String name;

        WritingTablePart(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if(level.isClientSide()) {
            return null;
        }
        if (state.getValue(PART) == WritingTablePart.RIGHT) {
            return null;
        }

        return createTickerHelper(type, MystWorldsBlockEntities.WRITING_TABLE_BLOCK_ENTITY.get(),
                (level1, pos, state1, be) -> be.tick(level1, pos, state1));
    }
}
