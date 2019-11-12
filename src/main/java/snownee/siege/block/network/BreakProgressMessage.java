package snownee.siege.block.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BreakProgressMessage {

    private int breakerID;
    private BlockPos pos;
    private int breakProgress; // 0-10

    public BreakProgressMessage(int breakerID, BlockPos pos, int breakProgress) {
        this.breakerID = breakerID;
        this.pos = pos;
        this.breakProgress = breakProgress;
    }

    public static void encode(BreakProgressMessage breakProgressMessage, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(breakProgressMessage.breakerID);
        packetBuffer.writeBlockPos(breakProgressMessage.pos);
        packetBuffer.writeInt(breakProgressMessage.breakProgress);
    }

    public static BreakProgressMessage decode(PacketBuffer packetBuffer) {
        return new BreakProgressMessage(packetBuffer.readInt(), packetBuffer.readBlockPos(), packetBuffer.readInt());
    }

    public static void handle(BreakProgressMessage message, Supplier<NetworkEvent.Context> ctx) {
        Minecraft.getInstance().world.sendBlockBreakProgress(message.breakerID, message.pos, message.breakProgress);
        ctx.get().setPacketHandled(true);
    }
}
