package snownee.siege.block.capability;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.CapabilityManager;
import snownee.siege.Siege;
import snownee.siege.block.impl.BlockInfo;

public class DefaultBlockProgress implements IBlockProgress {

    public static final ResourceLocation PROGRESS_ID = new ResourceLocation(Siege.MODID, "progress");

    public static void register() {
        CapabilityManager.INSTANCE.register(IBlockProgress.class, new BlockProgressStorage(), DefaultBlockProgress::new);
    }

    public static final DefaultBlockProgress INSTANCE = new DefaultBlockProgress();

    private DefaultBlockProgress() {}

    @Override
    public boolean destroy(BlockPos pos, float f, boolean sync) {
        return false;
    }

    @Override
    public Optional<BlockInfo> getInfo(BlockPos pos) {
        return Optional.empty();
    }

    @Override
    public BlockInfo getOrCreateInfo(BlockPos pos) {
        return DefaultBlockInfo.INSTANCE;
    }

    @Override
    public boolean recover(BlockPos pos, float f, boolean sync) {
        return false;
    }

    @Override
    public void emptyInfo(BlockPos pos) {}

    @Override
    public Map<BlockPos, BlockInfo> getAllData() {
        return new HashMap<>();
    }
}
