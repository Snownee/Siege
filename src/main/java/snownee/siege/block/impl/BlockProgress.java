package snownee.siege.block.impl;

import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.INBTSerializable;
import snownee.siege.block.capability.IBlockProgress;
import snownee.siege.block.network.BreakProgressMessage;
import snownee.siege.block.network.NetworkHandler;

import java.util.Map;
import java.util.Optional;

public class BlockProgress implements IBlockProgress, INBTSerializable<CompoundNBT> {

    private final Map<BlockPos, BlockInfo> progressData = Maps.newLinkedHashMap();
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
        return Optional.ofNullable(progressData.get(pos));
    }

    @Override
    public BlockInfo getOrCreateInfo(BlockPos pos) {
        return progressData.computeIfAbsent(pos, s -> new BlockInfo());
    }

    @Override
    public boolean destroy(BlockPos pos, float f) {
        if (f == 0) {
            return false;
        }
        BlockInfo info = f > 0 ? getOrCreateInfo(pos) : getInfo(pos).orElse(null);
        if (info == null) {
            return false;
        }
        BlockState state = info.getBlockState(chunk, pos);
        World world = chunk.getWorld();
        if (!state.isSolid() || state.getBlockHardness(world, pos) < 0) {
            return false;
        }
        float hardness = state.getBlockHardness(world, pos);
        float progress = info.getProgress() + f * hardness;
        if (progress <= 0) {
            NetworkHandler.INSTANCE.sendToNear(new BreakProgressMessage(info.breakerID, pos, -1), pos, 64, chunk.getWorld().getDimension().getType());
            return true;
        } else if (progress < 1) {
            info.setProgress(progress);
            NetworkHandler.INSTANCE.sendToNear(new BreakProgressMessage(info.breakerID, pos, info.getProgressInt()), pos, 64, chunk.getWorld().getDimension().getType());
        } else {
            world.destroyBlock(pos, true);
            NetworkHandler.INSTANCE.sendToNear(new BreakProgressMessage(info.breakerID, pos, -1), pos, 64, chunk.getWorld().getDimension().getType());
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
