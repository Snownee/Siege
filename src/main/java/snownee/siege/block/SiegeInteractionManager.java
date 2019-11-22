package snownee.siege.block;

import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerDiggingPacket;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import snownee.siege.SiegeConfig;
import snownee.siege.block.capability.IBlockProgress;
import snownee.siege.block.impl.BlockInfo;

//TODO: patch PlayerController and break sound
public class SiegeInteractionManager extends PlayerInteractionManager {

    public SiegeInteractionManager(ServerWorld world) {
        super(world);
    }

    @Override
    public void tick() {
        if (this.receivedFinishDiggingPacket || this.isDestroyingBlock) {
            BlockState state = this.world.getBlockState(this.destroyPos);
            if (state.isAir(world, destroyPos)) {
                this.isDestroyingBlock = false;
                return;
            } else {
                float f = this.destory(player.getHeldItemMainhand(), state, this.destroyPos);
                if (f <= 0) {
                    this.isDestroyingBlock = false;
                    this.receivedFinishDiggingPacket = false;
                } else if (f >= 1.0F) {
                    this.isDestroyingBlock = false;
                    this.receivedFinishDiggingPacket = false;
                    this.tryHarvestBlock(this.destroyPos);
                }
            }
        }
    }

    /*
     * process digging
     */
    public void func_225416_a(BlockPos pos, CPlayerDiggingPacket.Action action, Direction direction, int heightLimit) {
        double d0 = this.player.posX - ((double) pos.getX() + 0.5D);
        double d1 = this.player.posY - ((double) pos.getY() + 0.5D) + 1.5D;
        double d2 = this.player.posZ - ((double) pos.getZ() + 0.5D);
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
        double dist = player.getAttribute(net.minecraft.entity.player.PlayerEntity.REACH_DISTANCE).getValue() + 1;
        net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event = net.minecraftforge.common.ForgeHooks.onLeftClickBlock(player, pos, direction);
        if (event.isCanceled() || (!this.isCreative() && event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY)) { // Restore block and te data
            player.connection.sendPacket(new SPlayerDiggingPacket(pos, world.getBlockState(pos), action, false));
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            return;
        }
        dist *= dist;
        if (d3 > dist) {
            this.player.connection.sendPacket(new SPlayerDiggingPacket(pos, this.world.getBlockState(pos), action, false));
        } else if (pos.getY() >= heightLimit) {
            this.player.connection.sendPacket(new SPlayerDiggingPacket(pos, this.world.getBlockState(pos), action, false));
        } else {
            ItemStack stack = player.getHeldItemMainhand();
            if (action == CPlayerDiggingPacket.Action.START_DESTROY_BLOCK) {
                if (!this.world.isBlockModifiable(this.player, pos)) {
                    this.player.connection.sendPacket(new SPlayerDiggingPacket(pos, this.world.getBlockState(pos), action, false));
                    return;
                }

                if (this.isCreative()) {
                    if (!this.world.extinguishFire((PlayerEntity) null, pos, direction)) {
                        this.func_225415_a(pos, action);
                    } else {
                        this.player.connection.sendPacket(new SPlayerDiggingPacket(pos, this.world.getBlockState(pos), action, true));
                    }

                    return;
                }

                if (this.player.func_223729_a(this.world, pos, this.gameType)) {
                    this.player.connection.sendPacket(new SPlayerDiggingPacket(pos, this.world.getBlockState(pos), action, false));
                    return;
                }

                this.world.extinguishFire((PlayerEntity) null, pos, direction);
                //this.initialDamage = this.ticks;
                float f = 1.0F;
                BlockState blockstate = this.world.getBlockState(pos);
                if (!blockstate.isAir(world, pos)) {
                    if (event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY)
                        blockstate.onBlockClicked(this.world, pos, this.player);
                    f = getNewProgress(stack, blockstate, pos);
                }

                if (!blockstate.isAir(world, pos) && f >= 1.0F) {
                    this.func_225415_a(pos, action);
                } else {
                    this.isDestroyingBlock = true;
                    this.destroyPos = pos;
                    int i = (int) (f * 10.0F);
                    //this.world.sendBlockBreakProgress(this.player.getEntityId(), p_225416_1_, i);
                    this.player.connection.sendPacket(new SPlayerDiggingPacket(pos, this.world.getBlockState(pos), action, true));
                    //this.durabilityRemainingOnBlock = i;
                }
            } else if (action == CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK) {
                if (pos.equals(this.destroyPos)) {
                    //int j = this.ticks - this.initialDamage;
                    BlockState blockstate1 = this.world.getBlockState(pos);
                    if (!blockstate1.isAir()) {

                        float f1 = getNewProgress(stack, blockstate1, pos);
                        Optional<BlockInfo> info = BlockModule.getInfo(world, destroyPos);
                        if (info.isPresent()) {
                            f1 += info.get().getProgress();
                        }
                        if (f1 >= 0.7F) {
                            this.isDestroyingBlock = false;
                            //this.world.sendBlockBreakProgress(this.player.getEntityId(), p_225416_1_, -1);
                            this.func_225415_a(pos, action);
                            return;
                        }

                        if (!this.receivedFinishDiggingPacket) {
                            this.isDestroyingBlock = false;
                            this.receivedFinishDiggingPacket = true;
                            //this.delayedDestroyPos = pos;
                            //this.initialBlockDamage = this.initialDamage;
                        }
                    }
                }

                this.player.connection.sendPacket(new SPlayerDiggingPacket(pos, this.world.getBlockState(pos), action, true));
            } else if (action == CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK) {
                this.isDestroyingBlock = false;
                //this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, -1);
                this.player.connection.sendPacket(new SPlayerDiggingPacket(pos, this.world.getBlockState(pos), action, true));
            }

        }
    }

    private float destory(ItemStack stack, BlockState state, BlockPos pos) {
        IBlockProgress data = BlockModule.getBlockProgress(this.player.world, pos);
        BlockInfo info = data.getOrCreateInfo(pos);
        float f = getNewProgress(stack, state, pos);
        System.out.println(f);
        f += info.getProgress();
        if (f <= 0) {
            data.emptyInfo(pos);
            data.sync(pos, null);
        } else {
            info.setProgress(f, this.player.world);
            data.sync(pos, info);
        }
        return f;
    }

    private float getNewProgress(ItemStack stack, BlockState state, BlockPos pos) {
        if (stack.getToolTypes().contains(SiegeConfig.hammerToolType)) {
            float f = state.getBlockHardness(player.world, pos);
            if (f == -1) {
                return 0;
            } else {
                return player.getDigSpeed(state, pos) / -f * SiegeConfig.hammerRepairingSpeed;
            }
        } else {
            return state.getPlayerRelativeBlockHardness(this.player, this.player.world, pos);
        }
    }
}
