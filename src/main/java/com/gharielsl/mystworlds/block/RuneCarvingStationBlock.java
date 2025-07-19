package com.gharielsl.mystworlds.block;

import com.gharielsl.mystworlds.block.entity.MystWorldsBlockEntities;
import com.gharielsl.mystworlds.block.entity.RuneCarvingStationBlockEntity;
import com.gharielsl.mystworlds.block.entity.WritingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class RuneCarvingStationBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public RuneCarvingStationBlock() {
        super(Properties.copy(Blocks.OAK_SLAB).noOcclusion());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (!level.isClientSide) {
            handleUse(level, pos, (ServerPlayer) player);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public void handleUse(Level level, BlockPos pos, ServerPlayer player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RuneCarvingStationBlockEntity runeCarver) {
            NetworkHooks.openScreen(player, runeCarver, pos);
        }

    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (level.isClientSide) {
            return;
        }
        if (level.getBlockEntity(pos) instanceof RuneCarvingStationBlockEntity be) {
            be.drop();
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter block, BlockPos pos, CollisionContext collision) {
        return Block.box(0, 0, 0, 16, 11, 16);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RuneCarvingStationBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if(level.isClientSide()) {
            return null;
        }

        return createTickerHelper(type, MystWorldsBlockEntities.RUNE_CARVING_STATION_BLOCK_ENTITY.get(),
                (level1, pos, state1, be) -> be.tick(level1, pos, state1));
    }
}
