package snownee.siege.block.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import snownee.siege.SiegeCapabilities;
import snownee.siege.block.impl.BlockProgress;

public class BlockProgressProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {

    private final Chunk chunk;
    private BlockProgress progress;

    public BlockProgressProvider(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return SiegeCapabilities.BLOCK_PROGRESS.orEmpty(cap, LazyOptional.of(this::getProgress));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return getProgress().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        getProgress().deserializeNBT(nbt);
    }

    public BlockProgress getProgress() {
        if (progress == null) {
            progress = new BlockProgress(chunk);
        }
        return progress;
    }

}
