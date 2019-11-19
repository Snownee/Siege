package snownee.siege.fire_charge;

import java.util.Random;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;

@KiwiModule(name = "projectile")
@KiwiModule.Optional
@KiwiModule.Subscriber
public class ProjectileModule extends AbstractModule {

    @SubscribeEvent
    public void rightClick(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != Items.FIRE_CHARGE) {
            return;
        }
        PlayerEntity player = event.getPlayer();
        Random rand = player.getRNG();
        float f = -MathHelper.sin(player.rotationYaw * ((float) Math.PI / 180F)) * MathHelper.cos(player.rotationPitch * ((float) Math.PI / 180F));
        float f1 = -MathHelper.sin(player.rotationPitch * ((float) Math.PI / 180F));
        float f2 = MathHelper.cos(player.rotationYaw * ((float) Math.PI / 180F)) * MathHelper.cos(player.rotationPitch * ((float) Math.PI / 180F));
        SmallFireballEntity fireball = new SmallFireballEntity(event.getWorld(), player, f, f1, f2);
        Vec3d acel = new Vec3d(f + rand.nextGaussian() * 0.05, f1 + rand.nextGaussian() * 0.05, f2 + rand.nextGaussian() * 0.05).normalize().scale(0.2);
        fireball.accelerationX = acel.x + player.getMotion().x;
        fireball.accelerationY = acel.y + player.getMotion().y;
        fireball.accelerationZ = acel.z + player.getMotion().z;
        event.getWorld().playSound(null, player.getPosition().up(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
        fireball.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        event.getWorld().addEntity(Util.make(fireball, e -> e.setStack(stack)));
        event.setCancellationResult(ActionResultType.SUCCESS);
        if (!player.isCreative()) {
            stack.shrink(1);
        }
    }
}
