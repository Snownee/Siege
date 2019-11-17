package snownee.siege;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;

@EventBusSubscriber(bus = Bus.MOD)
public final class SiegeConfig {

    static final ForgeConfigSpec spec;

    private static BooleanValue blockRecoveryVal;

    private static BooleanValue explosionDamageVal;
    private static DoubleValue explosionDamageFactorVal;

    private static BooleanValue projectileDamageVal;
    private static ConfigValue<List<? extends String>> projectileDamageFactorsVal;

    public static boolean blockRecovery;
    public static boolean explosionDamage;
    public static float explosionDamageFactor;
    public static boolean projectileDamage;
    public static final Object2FloatMap<ResourceLocation> projectileDamageFactors = new Object2FloatArrayMap<>();

    static {
        final Pair<SiegeConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(SiegeConfig::new);
        spec = specPair.getRight();
    }

    private SiegeConfig(ForgeConfigSpec.Builder builder) {
        builder.push("block");
        blockRecoveryVal = builder.define("blockRecovery", true);
        explosionDamageVal = builder.define("explosionDamage", true);
        explosionDamageFactorVal = builder.defineInRange("explosionDamageFactor", 3f, 0, 100);
        projectileDamageVal = builder.define("projectileDamage", true);
        projectileDamageFactorsVal = builder.defineList("projectileDamageFactors", () -> Arrays.asList("arrow=1", "snowball=0.1"), $ -> {
            if ($ == null || $.getClass() != String.class) {
                return false;
            }
            String[] parts = ((String) $).split("=");
            if (parts.length != 2) {
                return false;
            }
            if (!ResourceLocation.isResouceNameValid(parts[0])) {
                return false;
            }
            try {
                float f = Float.parseFloat(parts[1]);
                if (f < 0 || Float.isNaN(f) || Float.isInfinite(f)) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        });
    }

    @SubscribeEvent
    public static void onFileChange(ModConfig.ConfigReloading event) {
        ((CommentedFileConfig) event.getConfig().getConfigData()).load();
        refresh();
    }

    public static void refresh() {
        blockRecovery = blockRecoveryVal.get();
        explosionDamage = explosionDamageVal.get();
        explosionDamageFactor = explosionDamageFactorVal.get().floatValue();
        projectileDamage = projectileDamageVal.get();
        projectileDamageFactors.clear();
        projectileDamageFactorsVal.get().forEach(s -> {
            String[] parts = s.split("=");
            if (parts.length != 2 || !ResourceLocation.isResouceNameValid(parts[0])) {
                return;
            }
            try {
                float f = Float.parseFloat(parts[1]);
                if (f < 0 || Float.isNaN(f) || Float.isInfinite(f)) {
                    return;
                }
                projectileDamageFactors.put(new ResourceLocation(parts[0]), f);
            } catch (Exception e) {}
        });
    }
}
