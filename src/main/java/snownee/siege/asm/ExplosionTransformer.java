package snownee.siege.asm;

import java.util.function.BiFunction;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.google.common.collect.ImmutableList;

import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.descriptor.MethodDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.transformer.SingleTargetMethodTransformer;

public final class ExplosionTransformer extends SingleTargetMethodTransformer {

    public ExplosionTransformer(String name, String className) {
        /* off */
        super(
                TransformerData.Builder.create()
                        .setOwningPluginId(SiegeLaunchPlugin.ID)
                        .setName(name)
                        .setDescription("Replace the default explosion with our own one.")
                        .build(),
                ClassDescriptor.of(className),
                MethodDescriptor.of("createExplosion",
                        ImmutableList.of(
                                ClassDescriptor.of("net.minecraft.entity.Entity"),
                                ClassDescriptor.of("net.minecraft.util.DamageSource"),
                                ClassDescriptor.of(double.class),
                                ClassDescriptor.of(double.class),
                                ClassDescriptor.of(double.class),
                                ClassDescriptor.of(float.class),
                                ClassDescriptor.of(boolean.class),
                                ClassDescriptor.of("net.minecraft.world.Explosion$Mode")),
                        ClassDescriptor.of("net.minecraft.world.Explosion"))
        );
        /* on */
    }

    @Override
    protected BiFunction<Integer, MethodVisitor, MethodVisitor> getMethodVisitorCreator() {
        return (v, mv) -> new MethodVisitor(v, mv) {
            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                if (opcode == Opcodes.INVOKESPECIAL && "net/minecraft/world/Explosion".equals(owner) && "<init>".equals(name) && "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;DDDFZLnet/minecraft/world/Explosion$Mode;)V".equals(descriptor)) {
                    super.visitMethodInsn(opcode, "snownee/siege/block/SiegeExplosion", name, descriptor, isInterface);
                } else {
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                }
            }

            @Override
            public void visitTypeInsn(int opcode, String type) {
                if (opcode == Opcodes.NEW && "net/minecraft/world/Explosion".equals(type)) {
                    super.visitTypeInsn(opcode, "snownee/siege/block/SiegeExplosion");
                } else {
                    super.visitTypeInsn(opcode, type);
                }
            }
        };
    }

}
