package snownee.siege;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(Siege.MODID)
public class Siege {
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "siege";

    public Siege() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SiegeConfig.spec, MODID + ".toml");
    }
}
