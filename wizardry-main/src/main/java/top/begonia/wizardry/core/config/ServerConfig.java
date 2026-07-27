package top.begonia.wizardry.core.config;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.constants.ConfigCategory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = Wizardry.MODID)
public final class ServerConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue WANDS_MUST_BE_HELD_TO_DECREMENT_COOLDOWN;
    private static final ModConfigSpec.BooleanValue DISPENSER_BLOCK_DAMAGE;
    private static final ModConfigSpec.BooleanValue PLAYER_BLOCK_DAMAGE;
    private static final ModConfigSpec.BooleanValue PLAYERS_MOVE_EACH_OTHER;
    private static final ModConfigSpec.BooleanValue MINION_REVENGE_TARGETING;
    private static final ModConfigSpec.IntValue MANA_PER_SHARD;
    private static final ModConfigSpec.IntValue MANA_PER_CRYSTAL;
    private static final ModConfigSpec.IntValue GRAND_CRYSTAL_MANA;
    private static final ModConfigSpec.IntValue UPGRADE_STACK_LIMIT;
    private static final ModConfigSpec.IntValue NON_ELEMENTAL_UPGRADE_BONUS;
    private static final ModConfigSpec.DoubleValue COOLDOWN_REDUCTION_PER_LEVEL;
    private static final ModConfigSpec.DoubleValue STORAGE_INCREASE_PER_LEVEL;
    private static final ModConfigSpec.DoubleValue POTENCY_INCREASE_PER_TIER;
    private static final ModConfigSpec.DoubleValue DURATION_INCREASE_PER_LEVEL;
    private static final ModConfigSpec.DoubleValue RANGE_INCREASE_PER_LEVEL;
    private static final ModConfigSpec.DoubleValue BLAST_RADIUS_INCREASE_PER_LEVEL;
    private static final ModConfigSpec.DoubleValue FROST_SLOWNESS_PER_LEVEL;
    private static final ModConfigSpec.IntValue CONDENSER_TICK_INTERVAL;
    private static final ModConfigSpec.IntValue SIPHON_MANA_PER_LEVEL;
    private static final ModConfigSpec.IntValue BASE_SPELL_SLOTS;
    private static final ModConfigSpec.BooleanValue BONEMEAL_GROWS_CRYSTAL_FLOWERS;
    private static final ModConfigSpec.IntValue RECENT_SPELL_EXPIRY_TIME;
    private static final ModConfigSpec.BooleanValue LEGACY_WAND_LEVELLING;
    private static final ModConfigSpec.BooleanValue PREVENT_BINDING_SAME_SPELL_TWICE_TO_WANDS;
    private static final ModConfigSpec.BooleanValue SINGLE_USE_SPELL_BOOKS;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> FLOWER_DIMENSIONS;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> ORE_DIMENSIONS;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> SUMMONED_CREATURE_TARGETS_WHITELIST;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> SUMMONED_CREATURE_TARGETS_BLACKLIST;
    public static List<Identifier> summonedCreatureTargetsBlacklist;
    public static List<Identifier> summonedCreatureTargetsWhitelist;
    public static boolean playersMoveEachOther;
    public static int baseSpellSlots;
    public static boolean bonemealGrowsCrystalFlowers;
    public static int recentSpellExpiryTime;
    public static boolean legacyWandLevelling;
    public static boolean preventBindingSameSpellTwiceToWands;
    public static boolean singleUseSpellBooks;
    public static List<String> flowerDimensions;
    public static List<String> oreDimensions;
    public static boolean wandsMustBeHeldToDecrementCooldown;
    public static boolean dispenserBlockDamage;
    public static boolean playerBlockDamage;
    public static boolean minionRevengeTargeting;

    static {
        BUILDER
                .comment("Global settings that affect game mechanics. In multiplayer, the server/LAN host settings will apply. Please note that changing some of these settings may make the mod very difficult to play.")
                .translation("config." + Wizardry.MODID + ".category." + ConfigCategory.GAMEPLAY_CATEGORY)
                .push("Gameplay");

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

        BUILDER
                .comment("Assorted settings for tweaking the mod's behaviour. In multiplayer, the server/LAN host settings will apply.")
                .translation("config." + Wizardry.MODID + ".category." + ConfigCategory.TWEAKS_CATEGORY)
                .push("Tweaks");

        COOLDOWN_REDUCTION_PER_LEVEL = BUILDER.comment("The fraction by which cooldowns are reduced for each level of cooldown upgrade.")
                .translation("config." + Wizardry.MODID + ".cooldown_reduction_per_level")
                .defineInRange("cooldownReductionPerLevel", 0.15, 0.05, Integer.MAX_VALUE);

        STORAGE_INCREASE_PER_LEVEL = BUILDER.comment("The fraction by which maximum charge is increased for each level of storage upgrade.")
                .translation("config." + Wizardry.MODID + ".storage_increase_per_level")
                .defineInRange("storageIncreasePerLevel", 0.15, 0.05, Integer.MAX_VALUE);

        POTENCY_INCREASE_PER_TIER = BUILDER.comment("The fraction by which potency is increased for each tier of matching wand. May cause extreme lag with high values!")
                .translation("config." + Wizardry.MODID + ".potency_increase_per_tier")
                .defineInRange("potencyIncreasePerTier", 0.15, 0.05, Integer.MAX_VALUE);

        DURATION_INCREASE_PER_LEVEL = BUILDER.comment("The fraction by which spell duration is increased for each level of duration upgrade.")
                .translation("config." + Wizardry.MODID + ".duration_increase_per_level")
                .defineInRange("durationIncreasePerLevel", 0.25, 0.05, Integer.MAX_VALUE);

        RANGE_INCREASE_PER_LEVEL = BUILDER.comment("The fraction by which spell range is increased for each level of range upgrade. May cause extreme lag with high values!")
                .translation("config." + Wizardry.MODID + ".range_increase_per_level")
                .defineInRange("rangeIncreasePerLevel", 0.25, 0.05, Integer.MAX_VALUE);

        BLAST_RADIUS_INCREASE_PER_LEVEL = BUILDER.comment("The fraction by which spell blast is increased for each level of blast upgrade. May cause extreme lag with high values!")
                .translation("config." + Wizardry.MODID + ".blast_increase_per_level")
                .defineInRange("blastIncreasePerLevel", 0.25, 0.05, Integer.MAX_VALUE);

        FROST_SLOWNESS_PER_LEVEL = BUILDER.comment("The fraction by which movement speed is reduced per level of frost effect.")
                .translation("config." + Wizardry.MODID + ".frost_slowness_increase_per_level")
                .defineInRange("frostSlownessIncreasePerLevel", 0.5, 0.05, Integer.MAX_VALUE);

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

        PREVENT_BINDING_SAME_SPELL_TWICE_TO_WANDS = BUILDER
                .comment("Whether to prevent binding the same spell to a wand multiple times")
                .translation("config." + Wizardry.MODID + ".prevent_binding_same_spell_twice_to_wands")
                .define("preventBindingSameSpellTwiceToWands", false);

        SINGLE_USE_SPELL_BOOKS = BUILDER
                .comment("Whether spell books are consumed when they are bound to a wand.")
                .translation("config." + Wizardry.MODID + ".single_use_spell_books")
                .define("singleUseSpellBooks", false);

        FLOWER_DIMENSIONS = BUILDER
                .comment("List of dimension ids in which crystal flowers will generate.")
                .translation("config." + Wizardry.MODID + ".flower_dimensions")
                .worldRestart()
                .defineListAllowEmpty(
                        "flowerDimensions",
                        List.of("minecraft:overworld"),
                        () -> "minecraft:overworld",
                        level -> level instanceof String str && Identifier.tryParse(str) != null
                );

        ORE_DIMENSIONS = BUILDER
                .comment("List of dimension ids in which crystal ore will generate. Note that removing the overworld (id 0) from this list will make the mod VERY difficult to play!")
                .translation("config." + Wizardry.MODID + ".ore_dimensions")
                .worldRestart()
                .defineListAllowEmpty(
                        "flowerDimensions",
                        List.of("minecraft:overworld"),
                        () -> "minecraft:overworld",
                        value -> value instanceof String str && Identifier.tryParse(str) != null
                );
        WANDS_MUST_BE_HELD_TO_DECREMENT_COOLDOWN = BUILDER
                .comment("Whether wands only decrement their cooldowns if a player holds them.")
                .translation("config." + Wizardry.MODID + ".wands_must_be_held_to_decrement_cooldown")
                .define("wandsMustBeHeldToDecrementCooldown", false);
        DISPENSER_BLOCK_DAMAGE = BUILDER
                .comment("Whether spells cast by dispensers can destroy blocks in the world. Wizardry makes every attempt to respect protection mods and plugins, but cannot guarantee it will work in all cases for every mod. If you need absolutely watertight anti-grief, disable this setting.")
                .translation("config." + Wizardry.MODID + ".dispenser_block_damage")
                .define("dispenserBlockDamage", true);
        PLAYER_BLOCK_DAMAGE = BUILDER
                .comment("Whether spells cast by players can destroy blocks in the world. Wizardry makes every attempt to respect protection mods and plugins, but cannot guarantee it will work in all cases for every mod. If you need absolutely watertight anti-grief, disable this setting. (N.B. This setting only affects players. To prevent mobs from destroying blocks with magic, use the mobGriefing gamerule.)")
                .translation("config." + Wizardry.MODID + ".player_block_damage")
                .define("playerBlockDamage", true);
        PLAYERS_MOVE_EACH_OTHER = BUILDER
                .comment("Whether to allow players to move other players around using magic.")
                .translation("config." + Wizardry.MODID + ".players_move_each_other")
                .define("playersMoveEachOther", true);
        MINION_REVENGE_TARGETING = BUILDER
                .comment("Whether summoned creatures can revenge attack their owner if their owner attacks them.")
                .translation("config." + Wizardry.MODID + ".minion_revenge_targeting")
                .define("minionRevengeTargeting", true);
        SUMMONED_CREATURE_TARGETS_WHITELIST = BUILDER
                .comment("List of names of entities which summoned creatures and wizards are allowed to attack, in addition to the defaults. Add mod creatures to this list if you want summoned creatures to attack them and they aren't already doing so. SoundLoopSpellEntity names are not case sensitive. For mod entities, prefix with the mod ID (e.g. " + Wizardry.MODID + ":wizard).")
                .translation("config." + Wizardry.MODID + ".summoned_creature_targets_whitelist")
                .defineListAllowEmpty(
                        "summonedCreatureTargetsWhitelist",
                        List.of(),
                        () -> "",
                        value -> value instanceof String str && Identifier.tryParse(str) != null
                );
        SUMMONED_CREATURE_TARGETS_BLACKLIST = BUILDER
                .comment("List of names of entities which summoned creatures and wizards are specifically not allowed to attack, overriding the defaults and the whitelist. Add creatures to this list if allowing them to be attacked causes problems or is too destructive (removing creepers from this list is done at your own risk!). SoundLoopSpellEntity names are not case sensitive. For mod entities, prefix with the mod ID (e.g. " + Wizardry.MODID + ":wizard).")
                .translation("config." + Wizardry.MODID + ".summoned_creature_targets_blacklist")
                .defineListAllowEmpty(
                        "summonedCreatureTargetsBlacklist",
                        List.of(),
                        () -> "",
                        value -> value instanceof String str && Identifier.tryParse(str) != null
                );

        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.@NonNull Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            valueChange();
            Wizardry.LOGGER.info("服务器常量与配置同步.");
        }
    }

    private static void valueChange() {
        Constants.manaPerShard = MANA_PER_SHARD.get();
        Constants.manaPerCrystal = MANA_PER_CRYSTAL.get();
        Constants.grandCrystalMana = GRAND_CRYSTAL_MANA.get();
        Constants.upgradeStackLimit = UPGRADE_STACK_LIMIT.get();
        Constants.nonElementalUpgradeBonus = NON_ELEMENTAL_UPGRADE_BONUS.get();
        Constants.siphonManaPerLevel = SIPHON_MANA_PER_LEVEL.get();
        Constants.condenserTickInterval = CONDENSER_TICK_INTERVAL.get();

        Constants.cooldownReductionPerLevel = COOLDOWN_REDUCTION_PER_LEVEL.get().floatValue();
        Constants.storageIncreasePerLevel = STORAGE_INCREASE_PER_LEVEL.get().floatValue();
        Constants.potencyIncreasePerTier = POTENCY_INCREASE_PER_TIER.get().floatValue();
        Constants.durationIncreasePerLevel = DURATION_INCREASE_PER_LEVEL.get().floatValue();
        Constants.rangeIncreasePerLevel = RANGE_INCREASE_PER_LEVEL.get().floatValue();
        Constants.blastRadiusIncreasePerLevel = BLAST_RADIUS_INCREASE_PER_LEVEL.get().floatValue();
        Constants.frostSlownessPerLevel = FROST_SLOWNESS_PER_LEVEL.get();

        baseSpellSlots = BASE_SPELL_SLOTS.get();
        bonemealGrowsCrystalFlowers = BONEMEAL_GROWS_CRYSTAL_FLOWERS.get();
        recentSpellExpiryTime = RECENT_SPELL_EXPIRY_TIME.get();
        legacyWandLevelling = LEGACY_WAND_LEVELLING.get();
        preventBindingSameSpellTwiceToWands = PREVENT_BINDING_SAME_SPELL_TWICE_TO_WANDS.get();
        singleUseSpellBooks = SINGLE_USE_SPELL_BOOKS.get();
        flowerDimensions = FLOWER_DIMENSIONS.get().stream().collect(Collectors.toUnmodifiableList());
        oreDimensions = ORE_DIMENSIONS.get().stream().collect(Collectors.toUnmodifiableList());
        wandsMustBeHeldToDecrementCooldown = WANDS_MUST_BE_HELD_TO_DECREMENT_COOLDOWN.get();
        dispenserBlockDamage = DISPENSER_BLOCK_DAMAGE.get();
        playerBlockDamage = PLAYER_BLOCK_DAMAGE.get();
        playersMoveEachOther = PLAYERS_MOVE_EACH_OTHER.get();
        minionRevengeTargeting = MINION_REVENGE_TARGETING.get();
        summonedCreatureTargetsWhitelist = SUMMONED_CREATURE_TARGETS_WHITELIST.get().stream().map(Identifier::tryParse).filter(Objects::nonNull).collect(Collectors.toList());
        summonedCreatureTargetsBlacklist = SUMMONED_CREATURE_TARGETS_BLACKLIST.get().stream().map(Identifier::tryParse).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.@NonNull Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            valueChange();
        }
    }

    public static final class Constants {
        public static final double DECAY_SLOWNESS_PER_LEVEL = 0.2;
        public static final float FROST_FATIGUE_PER_LEVEL = 0.45f;
        public static final int DECAY_SPREAD_INTERVAL = 8;
        public static int manaPerShard;
        public static int manaPerCrystal;
        public static int grandCrystalMana;
        public static int upgradeStackLimit;
        public static int nonElementalUpgradeBonus;
        public static float cooldownReductionPerLevel;
        public static float storageIncreasePerLevel;
        public static float potencyIncreasePerTier;
        public static float durationIncreasePerLevel;
        public static float rangeIncreasePerLevel;
        public static float blastRadiusIncreasePerLevel;
        public static double frostSlownessPerLevel;
        public static int condenserTickInterval;
        public static int siphonManaPerLevel;
    }
}
