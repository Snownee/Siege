package snownee.siege.block;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class SiegeExplosion extends Explosion {

    private final Map<BlockPos, Float> breakingMap = Maps.newHashMap();

    public SiegeExplosion(World worldIn, @Nullable Entity exploderIn, double xIn, double yIn, double zIn, float sizeIn, boolean causesFireIn, Explosion.Mode modeIn) {
        super(worldIn, exploderIn, xIn, yIn, zIn, sizeIn, causesFireIn, modeIn);
    }

    public void doExplosionA() {
        Set<BlockPos> set = Sets.newHashSet();
        int i = 16;

        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (double) ((float) j / 15.0F * 2.0F - 1.0F);
                        double d1 = (double) ((float) k / 15.0F * 2.0F - 1.0F);
                        double d2 = (double) ((float) l / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = this.size * (0.7F + this.world.rand.nextFloat() * 0.6F);
                        double d4 = this.x;
                        double d6 = this.y;
                        double d8 = this.z;

                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = new BlockPos(d4, d6, d8);
                            BlockState blockstate = this.world.getBlockState(blockpos);
                            IFluidState ifluidstate = this.world.getFluidState(blockpos);
                            if (!blockstate.isAir(this.world, blockpos) || !ifluidstate.isEmpty()) {
                                float f2 = Math.max(blockstate.getExplosionResistance(this.world, blockpos, exploder, this), ifluidstate.getExplosionResistance(this.world, blockpos, exploder, this));
                                if (this.exploder != null) {
                                    f2 = this.exploder.getExplosionResistance(this, this.world, blockpos, blockstate, ifluidstate, f2);
                                }

                                f -= (f2 + 0.3F) * 0.3F;

                                // Siege patch
                                if (mode != Explosion.Mode.NONE && blockstate.isSolid() && f < 0) {
                                    float progress = breakingMap.getOrDefault(blockpos, 0f);
                                    progress = Math.max(progress, f * -3);
                                    breakingMap.put(blockpos, progress);
                                    System.out.println(progress);
                                }
                            }

                            if (f > 0.0F && (this.exploder == null || this.exploder.canExplosionDestroyBlock(this, this.world, blockpos, blockstate, f))) {
                                set.add(blockpos);
                            }

                            d4 += d0 * (double) 0.3F;
                            d6 += d1 * (double) 0.3F;
                            d8 += d2 * (double) 0.3F;
                        }
                    }
                }
            }
        }

        this.getAffectedBlockPositions().addAll(set);
        float f3 = this.size * 2.0F;
        int k1 = MathHelper.floor(this.x - (double) f3 - 1.0D);
        int l1 = MathHelper.floor(this.x + (double) f3 + 1.0D);
        int i2 = MathHelper.floor(this.y - (double) f3 - 1.0D);
        int i1 = MathHelper.floor(this.y + (double) f3 + 1.0D);
        int j2 = MathHelper.floor(this.z - (double) f3 - 1.0D);
        int j1 = MathHelper.floor(this.z + (double) f3 + 1.0D);
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB((double) k1, (double) i2, (double) j2, (double) l1, (double) i1, (double) j1));
        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.world, this, list, f3);
        Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

        for (int k2 = 0; k2 < list.size(); ++k2) {
            Entity entity = list.get(k2);
            if (!entity.isImmuneToExplosions()) {
                double d12 = (double) (MathHelper.sqrt(entity.getDistanceSq(new Vec3d(this.x, this.y, this.z))) / f3);
                if (d12 <= 1.0D) {
                    double d5 = entity.posX - this.x;
                    double d7 = entity.posY + (double) entity.getEyeHeight() - this.y;
                    double d9 = entity.posZ - this.z;
                    double d13 = (double) MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                    if (d13 != 0.0D) {
                        d5 = d5 / d13;
                        d7 = d7 / d13;
                        d9 = d9 / d13;
                        double d14 = (double) func_222259_a(vec3d, entity);
                        double d10 = (1.0D - d12) * d14;
                        entity.attackEntityFrom(this.getDamageSource(), (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f3 + 1.0D)));
                        double d11 = d10;
                        if (entity instanceof LivingEntity) {
                            d11 = ProtectionEnchantment.getBlastDamageReduction((LivingEntity) entity, d10);
                        }

                        entity.setMotion(entity.getMotion().add(d5 * d11, d7 * d11, d9 * d11));
                        if (entity instanceof PlayerEntity) {
                            PlayerEntity playerentity = (PlayerEntity) entity;
                            if (!playerentity.isSpectator() && (!playerentity.isCreative() || !playerentity.abilities.isFlying)) {
                                this.getPlayerKnockbackMap().put(playerentity, new Vec3d(d5 * d10, d7 * d10, d9 * d10));
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    public void doExplosionB(boolean spawnParticles) {
        super.doExplosionB(spawnParticles);
        breakingMap.forEach((pos, f) -> {
            BlockModule.getBlockProgress(world, pos).destroy(pos, f);
        });
    }
}
