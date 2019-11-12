package snownee.siege.block.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class BlockProgressStorage implements Capability.IStorage<IBlockProgress> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IBlockProgress> capability, IBlockProgress instance, Direction side) {
        return null;
    }

    @Override
    public void readNBT(Capability<IBlockProgress> capability, IBlockProgress instance, Direction side, INBT nbt) {

    }
}
