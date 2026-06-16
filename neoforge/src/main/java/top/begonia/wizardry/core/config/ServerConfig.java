package top.begonia.wizardry.core.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

@EventBusSubscriber(modid = Wizardry.MODID)
public final class ServerConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.IntValue MANA_PER_SHARD;
    private static final ModConfigSpec.IntValue MANA_PER_CRYSTAL;
    private static final ModConfigSpec.IntValue GRAND_CRYSTAL_MANA;
    private static final ModConfigSpec.IntValue UPGRADE_STACK_LIMIT;
    public static final ModConfigSpec.IntValue NON_ELEMENTAL_UPGRADE_BONUS;
    public static final ModConfigSpec.DoubleValue COOLDOWN_REDUCTION_PER_LEVEL;
    public static final ModConfigSpec.DoubleValue STORAGE_INCREASE_PER_LEVEL;
    public static final ModConfigSpec.DoubleValue POTENCY_INCREASE_PER_TIER;
    public static final ModConfigSpec.DoubleValue DURATION_INCREASE_PER_LEVEL;
    public static final ModConfigSpec.DoubleValue RANGE_INCREASE_PER_LEVEL;
    public static final ModConfigSpec.DoubleValue BLAST_RADIUS_INCREASE_PER_LEVEL;
    public static final ModConfigSpec.DoubleValue FROST_SLOWNESS_PER_LEVEL;
    public static final ModConfigSpec.IntValue CONDENSER_TICK_INTERVAL;
    public static final ModConfigSpec.IntValue SIPHON_MANA_PER_LEVEL;
    public static final ModConfigSpec.IntValue BASE_SPELL_SLOTS;
    public static final ModConfigSpec.BooleanValue BONEMEAL_GROWS_CRYSTAL_FLOWERS;
    public static final ModConfigSpec.IntValue RECENT_SPELL_EXPIRY_TIME;
    public static final ModConfigSpec.BooleanValue LEGACY_WAND_LEVELLING;

    static {
        BUILDER.push("Gameplay");

        MANA_PER_SHARD = BUILDER.comment("The amount of mana a crystal shard is worth.")
                .translation("config." + Wizardry.MODID + ".mana_per_shard")
                .defineInRange("manaPerShard", 10, 1, 1000);

        MANA_PER_CRYSTAL = BUILDER.comment("The amount of mana each magic crystal is worth.")
                .translation("config." + Wizardry.MODID + ".mana_per_crystal")
                .defineInRange("manaPerCrystal", 100, 1, 10000);

        GRAND_CRYSTAL_MANA = BUILDER.comment("The amount of mana a grand magic crystal is worth.")
                .translation("config." + Wizardry.MODID + ".grand_crystal_mana")
                .defineInRange("grandCrystalMana", 400, 1, 10000);

        UPGRADE_STACK_LIMIT = BUILDER.comment("The maximum number of one type of wand upgrade which can be applied to a wand.")
                .translation("config." + Wizardry.MODID + ".upgrade_stack_limit")
                .defineInRange("upgradeStackLimit", 3, 1, 10);

        NON_ELEMENTAL_UPGRADE_BONUS = BUILDER.comment("The bonus amount of wand upgrades that can be applied to a non-elemental wand.")
                .translation("config." + Wizardry.MODID + ".non_elemental_upgrade_bonus")
                .defineInRange("nonElementalUpgradeBonus", 3, 0, 10);

        SIPHON_MANA_PER_LEVEL = BUILDER.comment("The amount of mana given for a kill for each level of siphon upgrade.")
                .translation("config." + Wizardry.MODID + ".siphon_mana_per_level")
                .defineInRange("siphonManaPerLevel", 5, 0, 100);

        CONDENSER_TICK_INTERVAL = BUILDER.comment("The number of ticks between each mana increase for wands with the condenser upgrade.")
                .translation("config." + Wizardry.MODID + ".condenser_tick_interval")
                .defineInRange("condenserTickInterval", 50, 1, 1000);

        BUILDER.pop();

        BUILDER.push("Tweaks");

        COOLDOWN_REDUCTION_PER_LEVEL = BUILDER.comment("The fraction by which cooldowns are reduced for each level of cooldown upgrade.")
                .translation("config." + Wizardry.MODID + ".cooldown_reduction_per_level")
                .defineInRange("cooldownReductionPerLevel", 0.15, 0.05, (double) Integer.MAX_VALUE);

        STORAGE_INCREASE_PER_LEVEL = BUILDER.comment("The fraction by which maximum charge is increased for each level of storage upgrade.")
                .translation("config." + Wizardry.MODID + ".storage_increase_per_level")
                .defineInRange("storageIncreasePerLevel", 0.15, 0.05, (double) Integer.MAX_VALUE);

        POTENCY_INCREASE_PER_TIER = BUILDER.comment("The fraction by which potency is increased for each tier of matching wand. May cause extreme lag with high values!")
                .translation("config." + Wizardry.MODID + ".potency_increase_per_tier")
                .defineInRange("potencyIncreasePerTier", 0.15, 0.05, (double) Integer.MAX_VALUE);

        DURATION_INCREASE_PER_LEVEL = BUILDER.comment("The fraction by which spell duration is increased for each level of duration upgrade.")
                .translation("config." + Wizardry.MODID + ".duration_increase_per_level")
                .defineInRange("durationIncreasePerLevel", 0.25, 0.05, (double) Integer.MAX_VALUE);

        RANGE_INCREASE_PER_LEVEL = BUILDER.comment("The fraction by which spell range is increased for each level of range upgrade. May cause extreme lag with high values!")
                .translation("config." + Wizardry.MODID + ".range_increase_per_level")
                .defineInRange("rangeIncreasePerLevel", 0.25, 0.05, (double) Integer.MAX_VALUE);

        BLAST_RADIUS_INCREASE_PER_LEVEL = BUILDER.comment("The fraction by which spell blast is increased for each level of blast upgrade. May cause extreme lag with high values!")
                .translation("config." + Wizardry.MODID + ".blast_increase_per_level")
                .defineInRange("blastIncreasePerLevel", 0.25, 0.05, (double) Integer.MAX_VALUE);

        FROST_SLOWNESS_PER_LEVEL = BUILDER.comment("The fraction by which movement speed is reduced per level of frost effect.")
                .translation("config." + Wizardry.MODID + ".frost_slowness_increase_per_level")
                .defineInRange("frostSlownessIncreasePerLevel", 0.5, 0.05, (double) Integer.MAX_VALUE);

        BUILDER.pop();

        BASE_SPELL_SLOTS = BUILDER
                .comment("The number of spell slots a wand has with no attunement upgrades applied.")
                .translation("config." + Wizardry.MODID + ".base_spell_slots")
                .defineInRange("baseSpellSlots", 5, 1, 5);

        BONEMEAL_GROWS_CRYSTAL_FLOWERS = BUILDER
                .comment("Whether using bonemeal on grass blocks has a chance to grow crystal flowers.")
                .translation("config." + Wizardry.MODID + ".bonemeal_grows_crystal_flowers")
                .define("bonemealGrowsCrystalFlowers", true);

        RECENT_SPELL_EXPIRY_TIME = BUILDER
                .comment("The time in ticks after which recent spell casts expire and no longer count toward progression penalties. Default is 1200 ticks (1 minute). Lower values make progression penalties shorter-lived, higher values make them last longer.")
                .translation("config." + Wizardry.MODID + ".recent_spell_expiry_time")
                .defineInRange("recentSpellExpiryTime", 1200, 60, 72000);

        LEGACY_WAND_LEVELLING = BUILDER
                .comment("Controls whether wands are required to gain progression before they can be upgraded to the next tier. Enable this option to revert to the pre-4.2 system, which only requires tomes of arcana. Wands will still gain progression even when this is enabled, so if you go back to the new system you won't lose any progress.")
                .translation("config." + Wizardry.MODID + ".legacy_wand_levelling")
                .define("legacyWandLevelling", false);

        SPEC = BUILDER.build();
    }

    public static final class Constants {
        public static int MANA_PER_SHARD;
        public static int MANA_PER_CRYSTAL;
        public static int GRAND_CRYSTAL_MANA;
        public static int UPGRADE_STACK_LIMIT;
        public static int NON_ELEMENTAL_UPGRADE_BONUS;
        public static float COOLDOWN_REDUCTION_PER_LEVEL;
        public static float STORAGE_INCREASE_PER_LEVEL;
        public static float POTENCY_INCREASE_PER_TIER;
        public static float DURATION_INCREASE_PER_LEVEL;
        public static float RANGE_INCREASE_PER_LEVEL;
        public static float BLAST_RADIUS_INCREASE_PER_LEVEL;
        public static double FROST_SLOWNESS_PER_LEVEL;
        public static final double DECAY_SLOWNESS_PER_LEVEL = 0.2;
        public static final float FROST_FATIGUE_PER_LEVEL = 0.45f;
        public static int CONDENSER_TICK_INTERVAL;
        public static int SIPHON_MANA_PER_LEVEL;
        public static final int DECAY_SPREAD_INTERVAL = 8;
    }

    public static int baseSpellSlots;
    public static boolean bonemealGrowsCrystalFlowers;
    public static int recentSpellExpiryTime;
    public static boolean legacyWandLevelling;

    private static void valueChange() {
        Constants.MANA_PER_SHARD = MANA_PER_SHARD.get();
        Constants.MANA_PER_CRYSTAL = MANA_PER_CRYSTAL.get();
        Constants.GRAND_CRYSTAL_MANA = GRAND_CRYSTAL_MANA.get();
        Constants.UPGRADE_STACK_LIMIT = UPGRADE_STACK_LIMIT.get();
        Constants.NON_ELEMENTAL_UPGRADE_BONUS = NON_ELEMENTAL_UPGRADE_BONUS.get();
        Constants.SIPHON_MANA_PER_LEVEL = SIPHON_MANA_PER_LEVEL.get();
        Constants.CONDENSER_TICK_INTERVAL = CONDENSER_TICK_INTERVAL.get();

        Constants.COOLDOWN_REDUCTION_PER_LEVEL = COOLDOWN_REDUCTION_PER_LEVEL.get().floatValue();
        Constants.STORAGE_INCREASE_PER_LEVEL = STORAGE_INCREASE_PER_LEVEL.get().floatValue();
        Constants.POTENCY_INCREASE_PER_TIER = POTENCY_INCREASE_PER_TIER.get().floatValue();
        Constants.DURATION_INCREASE_PER_LEVEL = DURATION_INCREASE_PER_LEVEL.get().floatValue();
        Constants.RANGE_INCREASE_PER_LEVEL = RANGE_INCREASE_PER_LEVEL.get().floatValue();
        Constants.BLAST_RADIUS_INCREASE_PER_LEVEL = BLAST_RADIUS_INCREASE_PER_LEVEL.get().floatValue();
        Constants.FROST_SLOWNESS_PER_LEVEL = FROST_SLOWNESS_PER_LEVEL.get();

        baseSpellSlots = BASE_SPELL_SLOTS.get();
        bonemealGrowsCrystalFlowers = BONEMEAL_GROWS_CRYSTAL_FLOWERS.get();
        recentSpellExpiryTime = RECENT_SPELL_EXPIRY_TIME.get();
        legacyWandLevelling = LEGACY_WAND_LEVELLING.get();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.@NonNull Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            valueChange();
            Wizardry.LOGGER.info("Server constants synchronized with config.");
        }
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.@NonNull Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            valueChange();
        }
    }
}
