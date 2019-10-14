package snownee.siege;

import net.minecraft.block.Blocks;

public class VoidBlockInfo extends BlockInfo {
    public static final VoidBlockInfo INSTANCE = new VoidBlockInfo();

    private VoidBlockInfo() {
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
