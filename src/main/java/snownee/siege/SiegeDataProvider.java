package snownee.siege;

import net.minecraft.util.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class SiegeDataProvider implements ICapabilityProvider {

    private final Chunk chunk;
    private SiegeData progress;

    public SiegeDataProvider(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return SiegeCapabilities.BREAKING_PROGRESS.orEmpty(cap, LazyOptional.of(() -> {
            if (progress == null) {
                progress = new SiegeData(chunk);
            }
            return progress;
        }));
    }

}
