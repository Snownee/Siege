package snownee.siege.block.client;

import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import snownee.siege.block.BlockModule;
import snownee.siege.block.SiegeInteractionManager;
import snownee.siege.block.impl.BlockInfo;

@OnlyIn(Dist.CLIENT)
public class SiegePlayerController extends PlayerController {

    private boolean repairing;

    public SiegePlayerController(Minecraft mcIn, ClientPlayNetHandler netHandler) {
        super(mcIn, netHandler);
    }

    @Override
    public boolean clickBlock(BlockPos loc, Direction face) {
        if (this.mc.player.func_223729_a(this.mc.world, loc, this.currentGameType)) {
            return false;
        } else if (!this.mc.world.getWorldBorder().contains(loc)) {
            return false;
        } else {
            if (this.currentGameType.isCreative()) {
                BlockState blockstate = this.mc.world.getBlockState(loc);
                this.mc.getTutorial().onHitBlock(this.mc.world, loc, blockstate, 1.0F);
                this.func_225324_a(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, loc, face);
                if (!net.minecraftforge.common.ForgeHooks.onLeftClickBlock(this.mc.player, loc, face).isCanceled())
                    clickBlockCreative(this.mc, this, loc, face);
                this.blockHitDelay = 5;
            } else if (!this.isHittingBlock || !this.isHittingPosition(loc)) {
                if (this.isHittingBlock) {
                    this.func_225324_a(CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, this.currentBlock, face);
                }
                net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event = net.minecraftforge.common.ForgeHooks.onLeftClickBlock(this.mc.player, loc, face);

                BlockState blockstate1 = this.mc.world.getBlockState(loc);
                this.mc.getTutorial().onHitBlock(this.mc.world, loc, blockstate1, 0.0F);
                this.func_225324_a(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, loc, face);
                boolean flag = !blockstate1.isAir();
                if (flag) {
                    if (event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY)
                        blockstate1.onBlockClicked(this.mc.world, loc, this.mc.player);
                }

                if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY)
                    return true;

                float p = 0;
                Optional<BlockInfo> result = BlockModule.getInfo(mc.player.world, loc);
                if (result.isPresent()) {
                    p = result.get().getProgress();
                }
                ItemStack stack = mc.player.getHeldItemMainhand();
                repairing = stack.getToolTypes().contains(BlockModule.hammerToolType);
                p += SiegeInteractionManager.getNewProgress(mc.player, stack, blockstate1, loc);

                if (flag && !repairing && p >= 1.0F) {
                    this.onPlayerDestroyBlock(loc);
                } else {
                    this.isHittingBlock = true;
                    this.currentBlock = loc;
                    this.currentItemHittingBlock = stack;
                    this.curBlockDamageMP = p;
                    this.stepSoundTickCounter = 0.0F;
                    //this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, (int) (this.curBlockDamageMP * 10.0F) - 1);
                }
            }

            return true;
        }
    }

    public void resetBlockRemoving() {
        if (this.isHittingBlock) {
            BlockState blockstate = this.mc.world.getBlockState(this.currentBlock);
            this.mc.getTutorial().onHitBlock(this.mc.world, this.currentBlock, blockstate, -1.0F);
            this.func_225324_a(CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, this.currentBlock, Direction.DOWN);
            this.isHittingBlock = false;
            this.curBlockDamageMP = 0.0F;
            //this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, -1);
            this.mc.player.resetCooldown();
        }

    }

    public boolean onPlayerDamageBlock(BlockPos posBlock, Direction directionFacing) {
        this.syncCurrentPlayItem();
        if (this.blockHitDelay > 0) {
            --this.blockHitDelay;
            return true;
        } else if (this.currentGameType.isCreative() && this.mc.world.getWorldBorder().contains(posBlock)) {
            this.blockHitDelay = 5;
            BlockState blockstate1 = this.mc.world.getBlockState(posBlock);
            this.mc.getTutorial().onHitBlock(this.mc.world, posBlock, blockstate1, 1.0F);
            this.func_225324_a(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, posBlock, directionFacing);
            if (!net.minecraftforge.common.ForgeHooks.onLeftClickBlock(this.mc.player, posBlock, directionFacing).isCanceled())
                clickBlockCreative(this.mc, this, posBlock, directionFacing);
            return true;
        } else if (this.isHittingPosition(posBlock)) {
            BlockState blockstate = this.mc.world.getBlockState(posBlock);
            if (blockstate.isAir(this.mc.world, posBlock)) {
                this.isHittingBlock = false;
                return false;
            } else {
                this.curBlockDamageMP += SiegeInteractionManager.getNewProgress(mc.player, currentItemHittingBlock, blockstate, posBlock);
                if (this.stepSoundTickCounter % 4.0F == 0.0F) {
                    SoundType soundtype = blockstate.getSoundType(this.mc.world, posBlock, this.mc.player);
                    this.mc.getSoundHandler().play(new SimpleSound(soundtype.getHitSound(), SoundCategory.NEUTRAL, (soundtype.getVolume() + 1.0F) / 8.0F, soundtype.getPitch() * 0.5F, posBlock));
                }

                ++this.stepSoundTickCounter;
                this.mc.getTutorial().onHitBlock(this.mc.world, posBlock, blockstate, MathHelper.clamp(this.curBlockDamageMP, 0.0F, 1.0F));
                if (net.minecraftforge.common.ForgeHooks.onLeftClickBlock(this.mc.player, posBlock, directionFacing).getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY)
                    return true;
                //System.out.println(curBlockDamageMP);
                if (this.curBlockDamageMP >= 1.0F) {
                    this.isHittingBlock = false;
                    this.func_225324_a(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, posBlock, directionFacing);
                    this.onPlayerDestroyBlock(posBlock);
                    this.curBlockDamageMP = 0.0F;
                    this.stepSoundTickCounter = 0.0F;
                    this.blockHitDelay = 5;
                }

                //this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, (int) (this.curBlockDamageMP * 10.0F) - 1);
                return true;
            }
        } else {
            return this.clickBlock(posBlock, directionFacing);
        }
    }
}
