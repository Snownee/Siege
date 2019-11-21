package snownee.siege.asm;

import java.util.Collections;
import java.util.function.BiFunction;

import org.objectweb.asm.MethodVisitor;

import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.descriptor.MethodDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.transformer.SingleTargetMethodTransformer;

public final class WorldRendererTransformer extends SingleTargetMethodTransformer {

    public WorldRendererTransformer() {
        /* off */
        super(
                TransformerData.Builder.create()
                        .setOwningPluginId(SiegeLaunchPlugin.ID)
                        .setName("world_renderer")
                        .setDescription("Disable auto cleanup of damaged blocks.")
                        .build(),
                ClassDescriptor.of("net.minecraft.client.renderer.WorldRenderer"),
                MethodDescriptor.of("tick",
                        Collections.EMPTY_LIST,
                        ClassDescriptor.of(void.class))
        );
        /* on */
    }

    @Override
    protected BiFunction<Integer, MethodVisitor, MethodVisitor> getMethodVisitorCreator() {
        return (v, mv) -> new MethodVisitor(v, mv) {

            private boolean visit = true;

            @Override
            public void visitLineNumber(int line, org.objectweb.asm.Label start) {
                visit = line != 944;
                super.visitLineNumber(line, start);
            }

            @Override
            public void visitInsn(int opcode) {
                if (visit)
                    super.visitInsn(opcode);
            }

            @Override
            public void visitVarInsn(int opcode, int var) {
                if (visit)
                    super.visitVarInsn(opcode, var);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                if (visit)
                    super.visitFieldInsn(opcode, owner, name, descriptor);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                if (visit)
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }
        };
    }

}
