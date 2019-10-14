package snownee.siege;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class SiegeCapabilities {
    public static final ResourceLocation PROGRESS_ID = new ResourceLocation(Siege.MODID, "progress");

    @CapabilityInject(IBreakingProgress.class)
    public static Capability<IBreakingProgress> BREAKING_PROGRESS = null;

    public static void init() {
        CapabilityManager.INSTANCE.register(IBreakingProgress.class, new Capability.IStorage<IBreakingProgress>() {

            @Override
            public INBT writeNBT(Capability<IBreakingProgress> capability, IBreakingProgress instance, Direction side) {
                return new CompoundNBT();
            }

            @Override
            public void readNBT(Capability<IBreakingProgress> capability, IBreakingProgress instance, Direction side, INBT nbt) {
                // TODO Auto-generated method stub
            }
        }, () -> VoidBreakingProgress.INSTANCE);
    }
}
