package snownee.siege.block.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;
import snownee.kiwi.network.NetworkChannel;
import snownee.kiwi.network.Packet;

public class BreakProgressPacket extends Packet {

    private int breakerID;
    private BlockPos pos;
    private int breakProgress; // 0-10

    public BreakProgressPacket(int breakerID, BlockPos pos, int breakProgress) {
        this.breakerID = breakerID;
        this.pos = pos;
        this.breakProgress = breakProgress;
    }

    public void send(World world) {
        this.send(world.getDimension().getType());
    }

    public void send(DimensionType dimensionType) {
        NetworkChannel.INSTANCE.channel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 64, dimensionType)), this);
    }

    public static class Handler extends PacketHandler<BreakProgressPacket> {
        @Override
        public BreakProgressPacket decode(PacketBuffer buffer) {
            return new BreakProgressPacket(buffer.readInt(), buffer.readBlockPos(), buffer.readInt());
        }

        @Override
        public void encode(BreakProgressPacket msg, PacketBuffer buffer) {
            buffer.writeInt(msg.breakerID);
            buffer.writeBlockPos(msg.pos);
            buffer.writeInt(msg.breakProgress);
        }

        @Override
        public void handle(BreakProgressPacket msg, Supplier<Context> ctx) {
            Minecraft.getInstance().world.sendBlockBreakProgress(msg.breakerID, msg.pos, msg.breakProgress);
            ctx.get().setPacketHandled(true);
        }
    }
}
