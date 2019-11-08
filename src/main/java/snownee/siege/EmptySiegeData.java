package snownee.siege;

import java.util.Collections;
import java.util.Map;

import com.google.common.base.Optional;

import net.minecraft.util.math.BlockPos;
import snownee.siege.block.BlockInfo;
import snownee.siege.block.VoidBlockInfo;

public final class EmptySiegeData implements ISiegeData {
    public static final EmptySiegeData INSTANCE = new EmptySiegeData();

    private EmptySiegeData() {}

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
