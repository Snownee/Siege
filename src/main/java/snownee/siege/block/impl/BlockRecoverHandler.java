package snownee.siege.block.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import snownee.siege.Siege;
import snownee.siege.SiegeCapabilities;
import snownee.siege.block.network.BreakProgressMessage;
import snownee.siege.block.network.NetworkHandler;

public class BlockRecoverHandler {

    private static Method METHOD;

    static {
        try {
            METHOD = ObfuscationReflectionHelper.findMethod(ChunkManager.class, "func_223491_f");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void recoverAllBlocks(ServerWorld world) {
        if (METHOD == null) {
            return;
        }
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
            chunk.getCapability(SiegeCapabilities.BLOCK_PROGRESS).ifPresent(progress -> progress.getAllData().entrySet().removeIf(e -> progress.recover(e.getKey(), .05f)));
        });
    }
}
