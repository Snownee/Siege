package snownee.siege.block.capability;

import java.util.Map;
import java.util.Optional;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import snownee.siege.block.impl.BlockInfo;

public interface IBlockProgress extends INBTSerializable<CompoundNBT> {

    Optional<BlockInfo> getInfo(BlockPos pos);

    BlockInfo getOrCreateInfo(BlockPos pos);

    void emptyInfo(BlockPos pos);

    default boolean recover(BlockPos pos, float f) {
        return recover(pos, f, true);
    }

    boolean recover(BlockPos pos, float f, boolean sync);

    default boolean destroy(BlockPos pos, float f) {
        return destroy(pos, f, true);
    }

    boolean destroy(BlockPos pos, float f, boolean sync);

    Map<BlockPos, BlockInfo> getAllData();

    void sync(BlockPos pos, BlockInfo info);

}
