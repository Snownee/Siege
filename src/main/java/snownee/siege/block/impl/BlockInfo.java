package snownee.siege.block.impl;

import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class BlockInfo {
    // In order not to conflict with Create, start from a negative number
    // https://github.com/simibubi/Create/blob/master/src/main/java/com/simibubi/create/modules/contraptions/receivers/DrillTileEntity.java
    private static final AtomicInteger NEXT_ID = new AtomicInteger(-114514);

    protected BlockState blockstate;
    private float progress;
    public int lastMine;
    public final int breakerID;

    public BlockInfo() {
        breakerID = EffectiveSide.get().isServer() ? 0 : NEXT_ID.decrementAndGet();
    }

    public BlockState getBlockState(Chunk chunk, BlockPos pos) {
        if (blockstate == null) {
            blockstate = chunk.getBlockState(pos);
        }
        return blockstate;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = MathHelper.clamp(progress, 0, 1);
    }

    public int getProgressInt() {
        return MathHelper.floor(progress * 10) - 1;
    }
}
