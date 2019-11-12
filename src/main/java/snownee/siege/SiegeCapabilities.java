package snownee.siege;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import snownee.siege.block.capability.IBlockProgress;

public class SiegeCapabilities {

    @CapabilityInject(IBlockProgress.class)
    public static Capability<IBlockProgress> BLOCK_PROGRESS = null;

}
