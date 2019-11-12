package snownee.siege.block.network;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import snownee.siege.Siege;

public enum NetworkHandler {
    INSTANCE;

    private SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Siege.MODID, "channel_1"))
            .networkProtocolVersion(() -> "1")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

    public void register() {
        CHANNEL.registerMessage(1, BreakProgressMessage.class, BreakProgressMessage::encode, BreakProgressMessage::decode, BreakProgressMessage::handle);
    }

    public <M> void sendToNear(M message, BlockPos center, double radius, DimensionType dimension) {
        CHANNEL.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(center.getX() + 0.5D, center.getY() + 0.5D, center.getZ() + 0.5D, radius, dimension)), message);
    }
}
