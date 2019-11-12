package snownee.siege.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.schedule.Scheduler;
import snownee.kiwi.schedule.impl.SimpleGlobalTask;
import snownee.siege.SiegeCapabilities;
import snownee.siege.block.capability.BlockProgressProvider;
import snownee.siege.block.capability.DefaultBlockProgress;
import snownee.siege.block.capability.IBlockProgress;
import snownee.siege.block.impl.BlockInfo;
import snownee.siege.block.impl.BlockRecoverHandler;
import snownee.siege.block.network.BreakProgressMessage;
import snownee.siege.block.network.NetworkHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings("unused")
@KiwiModule(name = "block")
@KiwiModule.Subscriber
@KiwiModule.Optional
public class BlockModule extends AbstractModule {

    @Override
    protected void init(FMLCommonSetupEvent event) {
        DefaultBlockProgress.register();
        NetworkHandler.INSTANCE.register();
    }

    @Nonnull
    public static IBlockProgress getBlockProgress(World world, BlockPos pos) {
        Chunk chunk = world.getChunkAt(pos);
        return chunk.getCapability(SiegeCapabilities.BLOCK_PROGRESS).orElse(DefaultBlockProgress.INSTANCE);
    }

    @Nullable
    public static Optional<BlockInfo> getInfo(World world, BlockPos pos) {
        return getBlockProgress(world, pos).getInfo(pos);
    }

    @Nonnull
    public static BlockInfo getOrCreateInfo(World world, BlockPos pos) {
        return getBlockProgress(world, pos).getOrCreateInfo(pos);
    }

    @SubscribeEvent
    public void attachChunkCapability(AttachCapabilitiesEvent<Chunk> event) {
        event.addCapability(DefaultBlockProgress.PROGRESS_ID, new BlockProgressProvider(event.getObject()));
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity entity = event.getEntity();
        World world = entity.world;
        if(world.isRemote) {
            return;
        }
        if (event.getRayTraceResult().getType() != RayTraceResult.Type.BLOCK) {
            return;
        }
        BlockRayTraceResult trace = (BlockRayTraceResult) event.getRayTraceResult();
        BlockState state = world.getBlockState(trace.getPos());
        if (!state.isSolid()) {
            return;
        }
        Chunk chunk = world.getChunkAt(entity.getPosition());
        chunk.getCapability(SiegeCapabilities.BLOCK_PROGRESS).ifPresent(data -> {
            double vel = entity.getMotion().squareDistanceTo(Vec3d.ZERO);
            data.destroy(trace.getPos(), (float) vel);
            if (entity instanceof AbstractArrowEntity) {
                Scheduler.add(new SimpleGlobalTask(LogicalSide.SERVER, Phase.END, s -> {
                    ((AbstractArrowEntity) entity).inBlockState = null;
                    return true;
                }));
            }
        });
    }

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (!event.world.isRemote && event.phase == Phase.END && event.world.getWorldInfo().getGameTime() % 20 == 1) {
            BlockRecoverHandler.recoverAllBlocks((ServerWorld) event.world);
        }
    }
}
