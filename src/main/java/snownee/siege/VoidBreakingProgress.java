package snownee.siege;

import java.util.Collections;
import java.util.Map;

import com.google.common.base.Optional;

import net.minecraft.util.math.BlockPos;

public final class VoidBreakingProgress implements IBreakingProgress {
    public static final VoidBreakingProgress INSTANCE = new VoidBreakingProgress();

    private VoidBreakingProgress() {}

    @Override
    public boolean destroy(BlockPos pos, float f) {
        return false;
    }

    @Override
    public Optional<BlockInfo> getInfo(BlockPos pos) {
        return Optional.absent();
    }

    @Override
    public BlockInfo getOrCreateInfo(BlockPos pos) {
        return VoidBlockInfo.INSTANCE;
    }

    @Override
    public boolean recover(BlockPos pos, float f) {
        return false;
    }

    @Override
    public void emptyInfo(BlockPos pos) {}

    @Override
    public Map<BlockPos, BlockInfo> getAllData() {
        return Collections.EMPTY_MAP;
    }
}
