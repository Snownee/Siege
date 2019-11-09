package snownee.siege.block;

import java.util.Collections;
import java.util.Map;

import com.google.common.base.Optional;

import net.minecraft.util.math.BlockPos;

public final class EmptyBlockProgress implements IBlockProgress {
    public static final EmptyBlockProgress INSTANCE = new EmptyBlockProgress();

    private EmptyBlockProgress() {}

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
        return EmptyBlockInfo.INSTANCE;
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
