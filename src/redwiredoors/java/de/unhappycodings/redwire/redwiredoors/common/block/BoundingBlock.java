package de.unhappycodings.redwire.redwiredoors.common.block;

import de.unhappycodings.redwire.redwiredoors.common.blockentity.BigSlidingDoorEntity;
import de.unhappycodings.redwire.redwiredoors.common.blockentity.BoundingBlockEntity;
import de.unhappycodings.redwire.redwiredoors.common.blockentity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BoundingBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    boolean lastState = false;

    public BoundingBlock() {
        super(Properties.copy(Blocks.STONE).sound(new SoundType(0.0F, 0.0F, SoundEvents.STONE_BREAK,
                SoundEvents.STONE_STEP, SoundEvents.STONE_PLACE, SoundEvents.STONE_HIT, SoundEvents.STONE_FALL)).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.INVISIBLE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        //TODO
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext context) {
        int x1 = 0;
        int y1 = 0;

        BlockEntity blockEntityBounding = blockGetter.getBlockEntity(blockPos);
        if (!(blockEntityBounding instanceof BoundingBlockEntity boundingBlockEntity)) return Shapes.empty();
        CompoundTag tagBounding = new CompoundTag();
        boundingBlockEntity.saveAdditional(tagBounding);

        int x = tagBounding.getInt("origin-x");
        int y = tagBounding.getInt("origin-y");
        int z = tagBounding.getInt("origin-z");
        BlockPos blockPosDoor = new BlockPos(x, y, z);

        BlockEntity blockEntityDoor = blockGetter.getBlockEntity(blockPosDoor);
        if (!(blockEntityDoor instanceof BigSlidingDoorEntity doorBlockEntity)) return Shapes.block();
        CompoundTag tagDoor = new CompoundTag();
        doorBlockEntity.saveAdditional(tagDoor);

        Direction direction = blockState.getValue(FACING);
        if (direction == Direction.SOUTH || direction == Direction.WEST) {
            switch (tagBounding.getByte("pos")) {
                case 2 -> {
                    x1 = 16;
                    y1 = -16;
                }
                case 3 -> y1 = -16;
                case 4 -> {
                    x1 = -16;
                    y1 = -16;
                }
                case 5 -> x1 = -16;
                default -> x1 = 16;
            }
        }
        if (direction == Direction.NORTH || direction == Direction.EAST) {
            switch (tagBounding.getByte("pos")) {
                case 2 -> {
                    x1 = -16;
                    y1 = -16;
                }
                case 3 -> y1 = -16;
                case 4 -> {
                    x1 = 16;
                    y1 = -16;
                }
                case 5 -> x1 = 16;
                default -> x1 = -16;
            }
        }

        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            byte state = tagDoor.getByte("state");
            if (doorBlockEntity.isLast()) {
                if (state >= 20) {
                    if (state == 38)
                        return Shapes.or(Block.box(-16 + x1, y1, 7, -11.15 + x1, 32 + y1, 9), Block.box(27.15 + x1, y1, 7, 32 + x1, 32 + y1, 9));
                    return Shapes.or(Block.box(-16 + x1, y1, 7, 8 + x1 - ((state - 20) * 1.12), 32 + y1, 9), Block.box(8 + x1 + ((state - 20) * 1.12), y1, 7, 32 + x1, 32 + y1, 9));
                } else {
                    return Shapes.or(Block.box(-16 + x1, y1, 7, 8 + x1, 32 + y1, 9), Block.box(8 + x1, y1, 7, 32 + x1, 32 + y1, 9));
                }
            } else {
                if (state <= 18)
                    return Shapes.or(Block.box(-16 + x1, y1, 7, -11.15 + x1 + (state * 1.12), 32 + y1, 9), Block.box(27.15 + x1 - (state * 1.12), y1, 7, 32 + x1, 32 + y1, 9));
                else
                    return Shapes.or(Block.box(-16 + x1, y1, 7, 8 + x1, 32 + y1, 9), Block.box(8 + x1, y1, 7, 32 + x1, 32 + y1, 9));
            }
        }

        byte state = tagDoor.getByte("state");
        if (doorBlockEntity.isLast()) {
            if (state >= 20) {
                if (state == 38)
                    return Shapes.or(Block.box(7, y1, 27.15 + x1, 9, 32 + y1, 32 + x1), Block.box(7, y1, -16 + x1, 9, 32 + y1, -11.15 + x1));
                return Shapes.or(Block.box(7, y1, 8 + x1 + ((state - 20) * 1.12), 9, 32 + y1, 32 + x1), Block.box(7, y1, -16 + x1, 9, 32 + y1, 8.0 + x1 - ((state - 20) * 1.12)));
            } else {
                return Shapes.or(Block.box(7, y1, 8 + x1, 9, 32 + y1, 32 + x1), Block.box(7, y1, -16 + x1, 9, 32 + y1, 8.0 + x1));
            }

        } else {
            if (state <= 18)
                return Shapes.or(Block.box(7, y1, 8 + x1 - ((state - 20) * 1.12), 9, 32 + y1, 32 + x1), Block.box(7, y1, -16 + x1, 9, 32 + y1, 8.0 + x1 + ((state - 20) * 1.12)));
            else
                return Shapes.or(Block.box(7, y1, 8 + x1, 9, 32 + y1, 32 + x1), Block.box(7, y1, -16 + x1, 9, 32 + y1, 8.0 + x1));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> type) {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return ModBlockEntities.BOUNDING_BLOCK.get().create(pos, state);
    }

}
