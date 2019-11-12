package snownee.siege.block.capability;

import net.minecraft.block.Blocks;
import snownee.siege.block.impl.BlockInfo;

public class DefaultBlockInfo extends BlockInfo {

    public static final DefaultBlockInfo INSTANCE = new DefaultBlockInfo();

    private DefaultBlockInfo() {
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
