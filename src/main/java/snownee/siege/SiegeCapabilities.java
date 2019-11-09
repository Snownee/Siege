package snownee.siege;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import snownee.siege.block.EmptyBlockProgress;
import snownee.siege.block.IBlockProgress;

public class SiegeCapabilities {
    public static final ResourceLocation PROGRESS_ID = new ResourceLocation(Siege.MODID, "progress");

    @CapabilityInject(IBlockProgress.class)
    public static Capability<IBlockProgress> BLOCK_PROGRESS = null;

    public static void init() {
        CapabilityManager.INSTANCE.register(IBlockProgress.class, new Capability.IStorage<IBlockProgress>() {

            @Override
            public INBT writeNBT(Capability<IBlockProgress> capability, IBlockProgress instance, Direction side) {
                return new CompoundNBT();
            }

            @Override
            public void readNBT(Capability<IBlockProgress> capability, IBlockProgress instance, Direction side, INBT nbt) {
                // TODO Auto-generated method stub
            }
        }, () -> EmptyBlockProgress.INSTANCE);
    }
}
