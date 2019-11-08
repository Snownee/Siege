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

    @CapabilityInject(ISiegeData.class)
    public static Capability<ISiegeData> BREAKING_PROGRESS = null;

    public static void init() {
        CapabilityManager.INSTANCE.register(ISiegeData.class, new Capability.IStorage<ISiegeData>() {

            @Override
            public INBT writeNBT(Capability<ISiegeData> capability, ISiegeData instance, Direction side) {
                return new CompoundNBT();
            }

            @Override
            public void readNBT(Capability<ISiegeData> capability, ISiegeData instance, Direction side, INBT nbt) {
                // TODO Auto-generated method stub
            }
        }, () -> EmptySiegeData.INSTANCE);
    }
}
