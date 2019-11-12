package snownee.siege.block.capability;

import net.minecraft.util.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import snownee.siege.SiegeCapabilities;
import snownee.siege.block.impl.BlockProgress;

public class BlockProgressProvider implements ICapabilityProvider {

    private final Chunk chunk;
    private BlockProgress progress;

    public BlockProgressProvider(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return SiegeCapabilities.BLOCK_PROGRESS.orEmpty(cap, LazyOptional.of(() -> {
            if (progress == null) {
                progress = new BlockProgress(chunk);
            }
            return progress;
        }));
    }

}
