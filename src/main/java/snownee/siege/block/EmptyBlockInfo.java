package snownee.siege.block;

import net.minecraft.block.Blocks;

public class EmptyBlockInfo extends BlockInfo {
    public static final EmptyBlockInfo INSTANCE = new EmptyBlockInfo();

    private EmptyBlockInfo() {
        blockstate = Blocks.AIR.getDefaultState();
    }

    @Override
    public int getProgressInt() {
        return 0;
    }

    @Override
    public float getProgress() {
        return 0;
    }

    @Override
    public void setProgress(float progress) {}
}
