package snownee.siege;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;

@KiwiModule
@KiwiModule.Subscriber
public class SiegeModule extends AbstractModule {

    @Override
    protected void init(FMLCommonSetupEvent event) {
        SiegeCapabilities.init();
    }

    @Nullable
    public static Optional<BlockInfo> getProgress(World world, BlockPos pos) {
        Chunk chunk = world.getChunkAt(pos);
        IBreakingProgress progress = chunk.getCapability(SiegeCapabilities.BREAKING_PROGRESS).orElse(null);
        if (progress != null) {
            //return progress.progressData.get(pos);
        }
        return null;
    }

    @SubscribeEvent
    public void attachCap(AttachCapabilitiesEvent<Chunk> event) {
        event.addCapability(SiegeCapabilities.PROGRESS_ID, new BreakingProgressProvider(event.getObject()));
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity entity = event.getEntity();
        World world = entity.world;
        if (event.getRayTraceResult().getType() != RayTraceResult.Type.BLOCK) {
            return;
        }
        BlockRayTraceResult trace = (BlockRayTraceResult) event.getRayTraceResult();
        Chunk chunk = world.getChunkAt(entity.getPosition());
        BlockState state = chunk.getBlockState(trace.getPos());
        if (!state.isSolid()) {
            return;
        }
        IBreakingProgress progress = chunk.getCapability(SiegeCapabilities.BREAKING_PROGRESS).orElse(null);
        if (progress != null) {
            double vel = entity.getMotion().squareDistanceTo(Vec3d.ZERO);
            //progress.destroy(trace.getPos(), (float) vel * .1f);
            progress.destroy(trace.getPos(), (float) vel);
            if (entity instanceof AbstractArrowEntity) {
                event.setCanceled(true);
                //((AbstractArrowEntity) entity).inBlockState = state;
            }
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END && event.world.getWorldInfo().getGameTime() % 20 == 1) {
            //WorldTickHandler.tick(event);
        }
    }
}
