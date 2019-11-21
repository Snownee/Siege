package snownee.siege.asm;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.thesilkminer.mc.fermion.asm.api.PluginMetadata.Builder;
import net.thesilkminer.mc.fermion.asm.prefab.AbstractLaunchPlugin;

public final class SiegeLaunchPlugin extends AbstractLaunchPlugin {

    public static final String ID = "siege.asm";

    public SiegeLaunchPlugin() {
        super(ID);
        registerTransformer(new ExplosionTransformer("client_explosion", "net.minecraft.world.World"));
        registerTransformer(new ExplosionTransformer("server_explosion", "net.minecraft.world.server.ServerWorld"));
        registerTransformer(new ChunkTransformer());
        registerTransformer(new WorldRendererTransformer());
    }

    @Override
    public Set<String> getRootPackages() {
        return ImmutableSet.of("snownee.siege");
    }

    @Override
    protected void populateMetadata(Builder metadataBuilder) {
        /* off */
        metadataBuilder.setVersion("0.0.0")
                .setName("Siege Core")
                .setDescription("Siege Coremod")
                .addAuthor("Snownee");
        /* on */
    }

}
