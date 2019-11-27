package snownee.siege.block;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.network.NetworkChannel;
import snownee.kiwi.schedule.Scheduler;
import snownee.kiwi.schedule.impl.SimpleGlobalTask;
import snownee.siege.SiegeCapabilities;
import snownee.siege.SiegeConfig;
import snownee.siege.block.capability.BlockProgressProvider;
import snownee.siege.block.capability.DefaultBlockProgress;
import snownee.siege.block.capability.IBlockProgress;
import snownee.siege.block.client.SiegePlayerController;
import snownee.siege.block.impl.BlockInfo;
import snownee.siege.block.network.SyncBlockInfoPacket;
import snownee.siege.block.network.SyncBlockProgressPacket;

@SuppressWarnings("unused")
@KiwiModule(name = "block")
@KiwiModule.Subscriber
@KiwiModule.Optional
public class BlockModule extends AbstractModule {

    public static BlockModule INSTANCE;

    public static final ToolType hammerToolType = ToolType.get("engineering_hammer");

    @Override
    protected void init(FMLCommonSetupEvent event) {
        DefaultBlockProgress.register();
        NetworkChannel.register(SyncBlockInfoPacket.class, new SyncBlockInfoPacket.Handler());
        NetworkChannel.register(SyncBlockProgressPacket.class, new SyncBlockProgressPacket.Handler());
        MinecraftForge.EVENT_BUS.register(BlockRecoverHandler.class);
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
        ResourceLocation id = event.getObject().getWorld().getDimension().getType().getRegistryName();
        if (SiegeConfig.blacklistWorlds.contains(id)) {
            return;
        }
        event.addCapability(DefaultBlockProgress.PROGRESS_ID, new BlockProgressProvider(event.getObject()));
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        if (SiegeConfig.projectileDamage <= 0) {
            return;
        }
        Entity entity = event.getEntity();
        World world = entity.world;
        if (world.isRemote) {
            return;
        }
        if (event.getRayTraceResult().getType() != RayTraceResult.Type.BLOCK) {
            return;
        }
        BlockRayTraceResult trace = (BlockRayTraceResult) event.getRayTraceResult();
        BlockState state = world.getBlockState(trace.getPos());
        if (!canDamage(state)) {
            return;
        }
        Chunk chunk = world.getChunkAt(entity.getPosition());
        chunk.getCapability(SiegeCapabilities.BLOCK_PROGRESS).ifPresent(data -> {
            double vel = entity.getMotion().squareDistanceTo(Vec3d.ZERO) * SiegeConfig.projectileDamage;
            data.destroy(trace.getPos(), (float) vel * SiegeConfig.projectileDamageFactors.getOrDefault(entity.getType().getRegistryName(), 1));
            if (entity instanceof AbstractArrowEntity) {
                Scheduler.add(new SimpleGlobalTask(LogicalSide.SERVER, Phase.END, s -> {
                    ((AbstractArrowEntity) entity).inBlockState = null;
                    return true;
                }));
            }
        });
    }

    public static void onBlockAdded(Chunk chunk, BlockPos pos, BlockState oldState, BlockState newState) {
        if (INSTANCE == null || !canDamage(oldState)) {
            return;
        }
        if (oldState.getBlock() == newState.getBlock()) {
            return;
        }
        chunk.getCapability(SiegeCapabilities.BLOCK_PROGRESS).ifPresent(data -> {
            if (!data.isInitialized()) {
                return;
            }
            data.getInfo(pos).ifPresent(info -> {
                if (chunk.getWorld().isRemote && info.breakerID < 0) {
                    sendBreakAnimation(info.breakerID, pos, -1);
                }
                data.emptyInfo(pos);
            });
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendBreakAnimation(int breakerID, BlockPos pos, int progress) {
        Minecraft mc = Minecraft.getInstance();
        //System.out.println(progress + " " + pos + " " + breakerID);
        mc.runAsync(() -> mc.world.sendBlockBreakProgress(breakerID, pos, progress));
    }

    public static boolean canDamage(BlockState state) {
        return !state.isAir() && state.getBlock().blockHardness >= 0;
    }

    @SubscribeEvent
    public void createPlayer(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            GameType gameType = player.interactionManager.getGameType();
            player.interactionManager = new SiegeInteractionManager((ServerWorld) entity.world);
            player.interactionManager.player = player;
            player.interactionManager.setGameType(gameType);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void playerLogIn(ClientPlayerNetworkEvent.LoggedInEvent event) {
        Minecraft mc = Minecraft.getInstance();
        GameType gameType = mc.playerController.getCurrentGameType();
        mc.playerController = new SiegePlayerController(mc, mc.getConnection());
        mc.playerController.setGameType(gameType);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void playerLogOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        mc.worldRenderer.damagedBlocks.clear();
    }

    @SubscribeEvent
    public void playerWatchChunk(ChunkWatchEvent.Watch event) {
        new SyncBlockProgressPacket().send(event.getWorld().getChunk(event.getPos().x, event.getPos().z), event.getPlayer());
    }

}
