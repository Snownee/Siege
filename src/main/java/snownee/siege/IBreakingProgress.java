package snownee.siege;

import java.util.Map;

import com.google.common.base.Optional;

import net.minecraft.util.math.BlockPos;

public interface IBreakingProgress {

    Optional<BlockInfo> getInfo(BlockPos pos);

    BlockInfo getOrCreateInfo(BlockPos pos);

    void emptyInfo(BlockPos pos);

    boolean recover(BlockPos pos, float f);

    boolean destroy(BlockPos pos, float f);

    Map<BlockPos, BlockInfo> getAllData();

}
