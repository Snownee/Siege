package snownee.siege.block;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.INBTSerializable;

public class BlockProgress implements IBlockProgress, INBTSerializable<CompoundNBT> {

    public final Map<BlockPos, BlockInfo> progressData = Maps.newLinkedHashMap();
    private final Chunk chunk;

    public BlockProgress(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        System.out.println(nbt);
    }

    @Override
    public Optional<BlockInfo> getInfo(BlockPos pos) {
        return Optional.fromNullable(progressData.get(pos));
    }

    @Override
    public BlockInfo getOrCreateInfo(BlockPos pos) {
        return progressData.computeIfAbsent(pos, $ -> new BlockInfo());
    }

    @Override
    public boolean destroy(BlockPos pos, float f) {
        if (f == 0) {
            return false;
        }
        BlockInfo info = null;
        if (f > 0) {
            info = getOrCreateInfo(pos);
        } else {
            info = getInfo(pos).orNull();
        }
        if (info == null) {
            return false;
        }
        BlockState state = info.getBlockState(chunk, pos);
        if (!state.isSolid() || state.getBlockHardness(chunk.getWorld(), pos) < 0) {
            return false;
        }
        float hardness = state.getBlockHardness(chunk.getWorld(), pos);
        float progress = info.getProgress() + f * hardness;
        if (progress <= 0) {
            return true;
        } else if (progress < 1) {
            info.setProgress(progress);
            if (chunk.getWorld().isRemote) {
                System.out.println(progress);
                Minecraft.getInstance().world.sendBlockBreakProgress(info.breakerID, pos, info.getProgressInt());
            }
        } else {
            chunk.getWorld().destroyBlock(pos, true);
            emptyInfo(pos);
        }
        return false;
    }

    @Override
    public boolean recover(BlockPos pos, float f) {
        return destroy(pos, -f);
    }

    @Override
    public void emptyInfo(BlockPos pos) {
        progressData.remove(pos);
    }

    @Override
    public Map<BlockPos, BlockInfo> getAllData() {
        return progressData;
    }
}
