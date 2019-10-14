package snownee.siege;

import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class BlockInfo {
    // In order not to conflict with Create, start from a negative number
    // https://github.com/simibubi/Create/blob/master/src/main/java/com/simibubi/create/modules/contraptions/receivers/DrillTileEntity.java
    private static final AtomicInteger NEXT_ID = new AtomicInteger(-114514);

    protected BlockState blockstate;
    private float progress;
    public int lastMine;
    public final int breakerID;

    public BlockInfo() {
        breakerID = NEXT_ID.decrementAndGet();
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
        this.progress = progress;
    }

    public int getProgressInt() {
        return (int) (progress * 10);
    }
}
