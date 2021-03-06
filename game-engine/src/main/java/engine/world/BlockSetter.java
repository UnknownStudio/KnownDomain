package engine.world;

import engine.block.state.BlockState;
import engine.event.block.cause.BlockChangeCause;
import engine.math.BlockPos;

import javax.annotation.Nonnull;

public interface BlockSetter {
    @Nonnull
    default BlockState setBlock(@Nonnull BlockPos pos, @Nonnull BlockState block, @Nonnull BlockChangeCause cause) {
        return setBlock(pos, block, cause, true);
    }

    @Nonnull
    BlockState setBlock(@Nonnull BlockPos pos, @Nonnull BlockState block, @Nonnull BlockChangeCause cause, boolean shouldNotify);

    @Nonnull
    BlockState destroyBlock(@Nonnull BlockPos pos, @Nonnull BlockChangeCause cause);
}
