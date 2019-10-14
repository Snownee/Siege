package snownee.siege.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import snownee.siege.BreakingProgress;
import snownee.siege.SiegeCapabilities;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class ClientDamageManager {
//    @SubscribeEvent
//    public static void tick(RenderWorldLastEvent event) {
//        Minecraft mc = Minecraft.getInstance();
//        ClientWorld world = mc.world;
//        Chunk chunk = world.getChunkAt(mc.player.getPosition());
//        BreakingProgress progress = chunk.getCapability(MBPCapabilities.BREAKING_PROGRESS).orElse(null);
//        if (progress != null) {
//            progress.progressData.forEach((pos, data) -> {
//                int i = MathHelper.clamp((int) data.progress * 10, 0, 9);
//                world.sendBlockBreakProgress(data.getEntityID(), pos, i);
//            });
//        }
//    }
}
