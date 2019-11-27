package snownee.siege.block.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;
import snownee.kiwi.network.NetworkChannel;
import snownee.kiwi.network.Packet;
import snownee.siege.SiegeCapabilities;

public class SyncBlockProgressPacket extends Packet {

    private int chunkX;
    private int chunkZ;
    private CompoundNBT data;

    public void send(Chunk chunk) {
        this.chunkX = chunk.getPos().x;
        this.chunkZ = chunk.getPos().z;
        chunk.getCapability(SiegeCapabilities.BLOCK_PROGRESS).ifPresent($ -> {
            if ($.isInitialized()) {
                this.data = $.serializeNBT();
            }
        });
        if (data != null) {
            NetworkChannel.INSTANCE.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), this);
        }
    }

    public void send(Chunk chunk, ServerPlayerEntity player) {
        this.chunkX = chunk.getPos().x;
        this.chunkZ = chunk.getPos().z;
        chunk.getCapability(SiegeCapabilities.BLOCK_PROGRESS).ifPresent($ -> {
            if ($.isInitialized()) {
                this.data = $.serializeNBT();
            }
        });
        if (data != null) {
            NetworkChannel.INSTANCE.channel.send(PacketDistributor.PLAYER.with(() -> player), this);
        }
    }

    public static class Handler extends PacketHandler<SyncBlockProgressPacket> {
        @Override
        public SyncBlockProgressPacket decode(PacketBuffer buffer) {
            SyncBlockProgressPacket pkt = new SyncBlockProgressPacket();
            pkt.chunkX = buffer.readInt();
            pkt.chunkZ = buffer.readInt();
            pkt.data = buffer.readCompoundTag();
            return pkt;
        }

        @Override
        public void encode(SyncBlockProgressPacket msg, PacketBuffer buffer) {
            buffer.writeInt(msg.chunkX);
            buffer.writeInt(msg.chunkZ);
            buffer.writeCompoundTag(msg.data);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void handle(SyncBlockProgressPacket msg, Supplier<Context> ctx) {
            ctx.get().enqueueWork(() -> {
                World world = Minecraft.getInstance().world;
                Chunk chunk = world.getChunk(msg.chunkX, msg.chunkZ);
                chunk.getCapability(SiegeCapabilities.BLOCK_PROGRESS).ifPresent($ -> $.deserializeNBT(msg.data));
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
