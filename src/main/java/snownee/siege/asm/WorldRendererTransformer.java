package snownee.siege.asm;

import java.util.Collections;
import java.util.function.BiFunction;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
                MethodDescriptor.of("func_72734_e", // tick
                        Collections.EMPTY_LIST,
                        ClassDescriptor.of(void.class))
        );
        /* on */
    }

    @Override
    protected BiFunction<Integer, MethodVisitor, MethodVisitor> getMethodVisitorCreator() {
        return (v, mv) -> new MethodVisitor(v, mv) {
            private boolean visited = false;

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                super.visitFieldInsn(opcode, owner, name, descriptor);
                if (!visited && opcode == Opcodes.PUTFIELD && "net/minecraft/client/renderer/WorldRenderer".equals(owner) && "I".equals(descriptor)) {
                    super.visitInsn(Opcodes.RETURN);
                    visited = true;
                }
            }
        };
    }

}
