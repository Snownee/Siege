package snownee.siege.asm;

import java.util.function.BiFunction;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.google.common.collect.ImmutableList;

import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.descriptor.MethodDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.transformer.SingleTargetMethodTransformer;

public final class ChunkTransformer extends SingleTargetMethodTransformer {

    public ChunkTransformer() {
        /* off */
        super(
                TransformerData.Builder.create()
                        .setOwningPluginId(SiegeLaunchPlugin.ID)
                        .setName("chunk")
                        .setDescription("Add hook when blockstate changed.")
                        .build(),
                ClassDescriptor.of("net.minecraft.world.chunk.Chunk"),
                MethodDescriptor.of("setBlockState",
                        ImmutableList.of(
                                ClassDescriptor.of("net.minecraft.util.math.BlockPos"),
                                ClassDescriptor.of("net.minecraft.block.BlockState"),
                                ClassDescriptor.of(boolean.class)),
                        ClassDescriptor.of("net.minecraft.block.BlockState"))
        );
        /* on */
    }

    @Override
    protected BiFunction<Integer, MethodVisitor, MethodVisitor> getMethodVisitorCreator() {
        return (v, mv) -> new MethodVisitor(v, mv) {
            @Override
            public void visitLineNumber(int line, org.objectweb.asm.Label start) {
                super.visitLineNumber(line, start);
                if (line == 298) {
                    super.visitVarInsn(Opcodes.ALOAD, 0);
                    super.visitVarInsn(Opcodes.ALOAD, 1);
                    super.visitVarInsn(Opcodes.ALOAD, 9);
                    super.visitVarInsn(Opcodes.ALOAD, 2);
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "snownee/siege/block/BlockModule", "onBlockAdded", "(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V", false);
                }
            };
        };
    }

}
