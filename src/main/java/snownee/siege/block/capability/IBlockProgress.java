package snownee.siege.block.capability;

import net.minecraft.util.math.BlockPos;
import snownee.siege.block.impl.BlockInfo;

import java.util.Map;
import java.util.Optional;

public interface IBlockProgress {

    Optional<BlockInfo> getInfo(BlockPos pos);

    BlockInfo getOrCreateInfo(BlockPos pos);

    void emptyInfo(BlockPos pos);

    boolean recover(BlockPos pos, float f);

    boolean destroy(BlockPos pos, float f);

    Map<BlockPos, BlockInfo> getAllData();

}
