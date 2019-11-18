package snownee.siege.block;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import snownee.siege.SiegeCapabilities;
import snownee.siege.SiegeConfig;

public class BlockRecoverHandler {

    private static final Multimap<IWorld, Chunk> chunks = LinkedHashMultimap.create();
    private static int tickClient = 0;
    private static int tickServer = 0;

    @SubscribeEvent
    public static void loadChunk(ChunkEvent.Load event) {
        if (event.getChunk() instanceof Chunk) {
            chunks.put(event.getWorld(), ((Chunk) event.getChunk()));
            //System.out.println(chunks.size());
        }
    }

    @SubscribeEvent
    public static void unloadChunk(ChunkEvent.Unload event) {
        if (event.getChunk() instanceof Chunk) {
            chunks.remove(event.getWorld(), (Chunk) event.getChunk());
            //System.out.println(chunks.size());
        }
    }

    @SubscribeEvent
    public static void unloadWorld(WorldEvent.Unload event) {
        if (event.getWorld().isRemote()) {
            chunks.clear();
        } else {
            chunks.removeAll(event.getWorld());
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void tickClient(TickEvent.ClientTickEvent event) {
        if (SiegeConfig.blockRecoverySpeed > 0 && event.phase == Phase.END && Minecraft.getInstance().player != null && !Minecraft.getInstance().isGamePaused() && ++tickClient % 20 == 1) {
            recoverAllBlocks();
        }
    }

    @SubscribeEvent
    public static void tickServer(TickEvent.ServerTickEvent event) {
        if (SiegeConfig.blockRecoverySpeed > 0 && event.phase == Phase.END && ++tickServer % 20 == 1) {
            recoverAllBlocks();
        }
    }

    public static void recoverAllBlocks() {
        chunks.values().stream().filter(c -> !c.isEmpty()).forEach(c -> c.getCapability(SiegeCapabilities.BLOCK_PROGRESS).ifPresent(progress -> progress.getAllData().entrySet().removeIf(e -> {
            boolean end = progress.recover(e.getKey(), SiegeConfig.blockRecoverySpeed, false);
            if (c.getWorld().isRemote && e.getValue().breakerID < 0) {
                Minecraft.getInstance().world.sendBlockBreakProgress(e.getValue().breakerID, e.getKey(), e.getValue().getProgressInt());
            }
            return end;
        })));
    }
}
