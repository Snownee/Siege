package snownee.siege;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.Sets;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import snownee.kiwi.util.Util;

@EventBusSubscriber(bus = Bus.MOD)
public final class SiegeConfig {

    static final ForgeConfigSpec spec;

    private static ConfigValue<List<? extends String>> blacklistWorldsVal;

    private static IntValue maxDamagedBlockPerChunkVal;

    private static DoubleValue blockRecoverySpeedVal;
    public static IntValue blockRecoveryDelayVal;

    private static DoubleValue blockDropsRateVal;

    private static BooleanValue explosionDamageVal;
    private static DoubleValue explosionDamageFactorVal;

    private static BooleanValue projectileDamageVal;
    private static ConfigValue<List<? extends String>> projectileDamageFactorsVal;

    private static DoubleValue fireballVelocityVal;
    private static DoubleValue fireballInaccuracyVal;

    public static final Set<ResourceLocation> blacklistWorlds = Sets.newHashSet();
    public static int maxDamagedBlockPerChunk;
    public static float blockRecoverySpeed;
    public static int blockRecoveryDelay;
    public static float blockDropsRate;
    public static boolean explosionDamage;
    public static float explosionDamageFactor;
    public static boolean projectileDamage;
    public static float fireballVelocity;
    public static float fireballInaccuracy;
    public static final Object2FloatMap<ResourceLocation> projectileDamageFactors = new Object2FloatArrayMap<>();

    static {
        final Pair<SiegeConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(SiegeConfig::new);
        spec = specPair.getRight();
    }

    private SiegeConfig(ForgeConfigSpec.Builder builder) {
        builder.push("block");
        blacklistWorldsVal = builder.defineList("blacklistWorlds", Collections.EMPTY_LIST, $ -> {
            return $ instanceof String && ResourceLocation.isResouceNameValid((String) $);
        });
        maxDamagedBlockPerChunkVal = builder.defineInRange("maxDamagedBlockPerChunk", 128, 0, 4096);
        blockRecoverySpeedVal = builder.defineInRange("blockRecoverySpeed", .05D, 0, 4096);
        blockRecoveryDelayVal = builder.defineInRange("blockRecoveryDelay", 120, 0, 3600);
        blockDropsRateVal = builder.defineInRange("blockDropsRate", 1D, 0, 1);
        explosionDamageVal = builder.define("explosionDamage", true);
        explosionDamageFactorVal = builder.defineInRange("explosionDamageFactor", 3D, 0, 100);
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

        builder.pop().push("projectile");
        fireballVelocityVal = builder.defineInRange("fireballVelocity", 0.2, 0.001, 10);
        fireballInaccuracyVal = builder.defineInRange("fireballInaccuracy", 0.05, 0, 1);
    }

    @SubscribeEvent
    public static void onFileChange(ModConfig.ConfigReloading event) {
        ((CommentedFileConfig) event.getConfig().getConfigData()).load();
        refresh();
    }

    public static void refresh() {
        blacklistWorlds.clear();
        blacklistWorlds.addAll(blacklistWorldsVal.get().stream().map(Util::RL).collect(Collectors.toSet()));
        maxDamagedBlockPerChunk = maxDamagedBlockPerChunkVal.get();
        blockRecoverySpeed = blockRecoverySpeedVal.get().floatValue();
        blockRecoveryDelay = blockRecoveryDelayVal.get();
        blockDropsRate = blockDropsRateVal.get().floatValue();
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

        fireballVelocity = fireballVelocityVal.get().floatValue();
        fireballInaccuracy = fireballInaccuracyVal.get().floatValue();
    }
}
