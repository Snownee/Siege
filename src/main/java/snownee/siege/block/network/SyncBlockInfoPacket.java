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
import snownee.siege.block.BlockModule;
import snownee.siege.block.capability.IBlockProgress;
import snownee.siege.block.impl.BlockInfo;

public class SyncBlockInfoPacket extends Packet {

    private BlockPos pos;
    private long lastMine;
    private float progress;

    public SyncBlockInfoPacket(BlockPos pos, long lastMine, float progress) {
        this.pos = pos;
        this.lastMine = lastMine;
        this.progress = progress;
    }

    public void send(World world) {
        this.send(world.getDimension().getType());
    }

    public void send(DimensionType dimensionType) {
        NetworkChannel.INSTANCE.channel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 64, dimensionType)), this);
    }

    public static class Handler extends PacketHandler<SyncBlockInfoPacket> {
        @Override
        public SyncBlockInfoPacket decode(PacketBuffer buffer) {
            return new SyncBlockInfoPacket(buffer.readBlockPos(), buffer.readLong(), buffer.readFloat());
        }

        @Override
        public void encode(SyncBlockInfoPacket msg, PacketBuffer buffer) {
            buffer.writeBlockPos(msg.pos);
            buffer.writeLong(msg.lastMine);
            buffer.writeFloat(msg.progress);
        }

        @Override
        public void handle(SyncBlockInfoPacket msg, Supplier<Context> ctx) {
            ctx.get().enqueueWork(() -> {
                World world = Minecraft.getInstance().world;
                IBlockProgress data = BlockModule.getBlockProgress(world, msg.pos);
                if (msg.progress <= 0) {
                    data.emptyInfo(msg.pos);
                } else {
                    BlockInfo info = data.getOrCreateInfo(msg.pos);
                    info.setProgress(msg.progress, msg.lastMine);
                    world.sendBlockBreakProgress(info.breakerID, msg.pos, info.getProgressInt());
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
