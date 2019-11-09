package snownee.siege.block;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import snownee.siege.SiegeCapabilities;

public class WorldTickHandler {

    private static Method METHOD;

    static {
        try {
            METHOD = ObfuscationReflectionHelper.findMethod(ChunkManager.class, "func_223491_f");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tick(TickEvent.WorldTickEvent event) {
        if (METHOD == null) {
            return;
        }
        ServerWorld world = (ServerWorld) event.world;
        if (world.getWorldInfo().getGenerator() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            return;
        }
        Iterable<ChunkHolder> holders;
        try {
            holders = (Iterable<ChunkHolder>) METHOD.invoke(world.getChunkProvider().chunkManager);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return;
        }
        holders.forEach(holder -> {
            Chunk chunk = holder.func_219298_c();
            if (chunk == null || !world.getChunkProvider().isChunkLoaded(chunk.getPos())) {
                return;
            }
            chunk.getCapability(SiegeCapabilities.BLOCK_PROGRESS).ifPresent(progress -> {
                Iterator<Entry<BlockPos, BlockInfo>> itr = progress.getAllData().entrySet().iterator();
                while (itr.hasNext()) {
                    Entry<BlockPos, BlockInfo> e = itr.next();
                    if (progress.recover(e.getKey(), .1f)) {
                        itr.remove();
                    }
                }
            });
        });
    }
}
