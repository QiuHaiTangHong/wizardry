package top.begonia.wizardry.core.config;

import com.ibm.icu.impl.Pair;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.constants.ConfigCategory;
import top.begonia.wizardry.api.constants.FriendlyFire;

import java.util.*;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = Wizardry.MODID)
public final class SyncedConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    private static final String[] DEFAULT_LOOT_INJECTION_LOCATIONS = {"minecraft:chests/simple_dungeon",
            "minecraft:chests/abandoned_mineshaft", "minecraft:chests/desert_pyramid", "minecraft:chests/jungle_temple",
            "minecraft:chests/stronghold_corridor", "minecraft:chests/stronghold_crossing",
            "minecraft:chests/stronghold_library", "minecraft:chests/igloo_chest", "minecraft:chests/woodland_mansion",
            "minecraft:chests/end_city_treasure"};

    private static final String[] DEFAULT_TREE_BLOCKS = {"dynamictrees:oakbranch", "dynamictrees:sprucebranch",
            "dynamictrees:birchbranch", "dynamictrees:junglebranch", "dynamictrees:darkoakbranch",
            "dynamictrees:acaciabranch", "dynamictrees:cactusbranch", "dynamictrees:leaves0", "dynamictrees:leaves1"};

    //region gameplay 设置
    //@formatter:off
    public static boolean playerBlockDamage = true;
    public static final ModConfigSpec.BooleanValue PLAYER_BLOCK_DAMAGE;

    public static boolean dispenserBlockDamage = true;
    public static final ModConfigSpec.BooleanValue DISPENSER_BLOCK_DAMAGE;

    public static boolean damageTypePerElement = false;
    public static final ModConfigSpec.BooleanValue DAMAGE_TYPE_PER_ELEMENT;

    public static boolean singleUseSpellBooks = false;
    public static final ModConfigSpec.BooleanValue SINGLE_USE_SPELL_BOOKS;

    public static boolean preventBindingSameSpellTwiceToWands = false;
    public static final ModConfigSpec.BooleanValue PREVENT_BINDING_SAME_SPELL_TWICE_TO_WANDS;

    public static int manaPerShard = 10;
    public static final ModConfigSpec.IntValue MANA_PER_SHARD;

    public static int manaPerCrystal = 100;
    public static final ModConfigSpec.IntValue MANA_PER_CRYSTAL;

    public static int grandCrystalMana = 400;
    public static final ModConfigSpec.IntValue GRAND_CRYSTAL_MANA;

    public static int upgradeStackLimit = 3;
    public static final ModConfigSpec.IntValue UPGRADE_STACK_LIMIT;

    public static int nonElementalUpgradeBonus = 3;
    public static final ModConfigSpec.IntValue NON_ELEMENTAL_UPGRADE_BONUS;

    public static int siphonManaPerLevel = 5;
    public static final ModConfigSpec.IntValue SIPHON_MANA_PER_LEVEL;

    public static int condenserTickInterval = 50;
    public static final ModConfigSpec.IntValue CONDENSER_TICK_INTERVAL;

    public static int baseSpellSlots = 5;
    public static final ModConfigSpec.IntValue BASE_SPELL_SLOTS;

    public static boolean playersMoveEachOther = true;
    public static final ModConfigSpec.BooleanValue PLAYERS_MOVE_EACH_OTHER;

    public static boolean telekineticDisarmament = true;
    public static final ModConfigSpec.BooleanValue TELEKINETIC_DISARMAMENT;

    public static boolean teleportThroughUnbreakableBlocks = false;
    public static final ModConfigSpec.BooleanValue TELEPORT_THROUGH_UNBREAKABLE_BLOCKS;

    public static boolean worldTimeManipulation = true;
    public static final ModConfigSpec.BooleanValue WORLD_TIME_MANIPULATION;

    public static boolean bonemealGrowsCrystalFlowers = true;
    public static final ModConfigSpec.BooleanValue BONEMEAL_GROWS_CRYSTAL_FLOWERS;

    public static boolean wandsMustBeHeldToDecrementCooldown = false;
    public static final ModConfigSpec.BooleanValue WANDS_MUST_BE_HELD_TO_DECREMENT_COOLDOWN;

    public static boolean slowTimeAffectsPlayers = true;
    public static final ModConfigSpec.BooleanValue SLOW_TIME_AFFECTS_PLAYERS;

    public static boolean creativeBypassesArcaneLock = true;
    public static final ModConfigSpec.BooleanValue CREATIVE_BYPASSES_ARCANE_LOCK;
    //@formatter:on
    //endregion

    //region difficulty 设置
    //@formatter:off
    public static boolean legacyWandLevelling = false;
    public static final ModConfigSpec.BooleanValue LEGACY_WAND_LEVELLING;

    public static FriendlyFire friendlyFire = FriendlyFire.ALL;
    public static final ModConfigSpec.EnumValue<FriendlyFire> FRIENDLY_FIRE;

    public static boolean minionRevengeTargeting = true;
    public static final ModConfigSpec.BooleanValue MINION_REVENGE_TARGETING;

    public static double playerDamageScale = 1.0;
    public static final ModConfigSpec.DoubleValue PLAYER_DAMAGE_SCALE;

    public static double npcDamageScale = 1.0;
    public static final ModConfigSpec.DoubleValue NPC_DAMAGE_SCALE;

    public static int evilWizardSpawnRate = 3;
    public static final ModConfigSpec.IntValue EVIL_WIZARD_SPAWN_RATE;

    public static int iceWraithSpawnRate = 3;
    public static final ModConfigSpec.IntValue ICE_WRAITH_SPAWN_RATE;

    public static int lightningWraithSpawnRate = 1;
    public static final ModConfigSpec.IntValue LIGHTNING_WRAITH_SPAWN_RATE;

    public static boolean discoveryMode = true;
    public static final ModConfigSpec.BooleanValue DISCOVERY_MODE;

    public static double forfeitChance = 0.2;
    public static final ModConfigSpec.DoubleValue FORFEIT_CHANCE;

    public static int[] progressionRequirements = {1500, 3500, 6000};
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> PROGRESSION_REQUIREMENTS;

    public static int noviceMaxCharge = 700;
    public static final ModConfigSpec.IntValue NOVICE_MAX_CHARGE;

    public static int apprenticeMaxCharge = 1000;
    public static final ModConfigSpec.IntValue APPRENTICE_MAX_CHARGE;

    public static int advancedMaxCharge = 1500;
    public static final ModConfigSpec.IntValue ADVANCED_MAX_CHARGE;

    public static int masterMaxCharge = 2500;
    public static final ModConfigSpec.IntValue MASTER_MAX_CHARGE;

    public static int noviceUpgradeLimit = 3;
    public static final ModConfigSpec.IntValue NOVICE_UPGRADE_LIMIT;

    public static int apprenticeUpgradeLimit = 5;
    public static final ModConfigSpec.IntValue APPRENTICE_UPGRADE_LIMIT;

    public static int advancedUpgradeLimit = 7;
    public static final ModConfigSpec.IntValue ADVANCED_UPGRADE_LIMIT;

    public static int masterUpgradeLimit = 9;
    public static final ModConfigSpec.IntValue MASTER_UPGRADE_LIMIT;

    public static double condenserAmountMultiplier = 1.0;
    public static final ModConfigSpec.DoubleValue CONDENSER_AMOUNT_MULTIPLIER;

    public static boolean fleshSpellsCauseSlowness = true;
    public static final ModConfigSpec.BooleanValue FLESH_SPELLS_CAUSE_SLOWNESS;

    public static double diamondFleshArmorBonus = 4.0;
    public static final ModConfigSpec.DoubleValue DIAMOND_FLESH_ARMOR_BONUS;

    public static double diamondFleshArmorToughnessBonus = 3.0;
    public static final ModConfigSpec.DoubleValue DIAMOND_FLESH_ARMOR_TOUGHNESS_BONUS;

    public static double ironFleshArmorBonus = 4.0;
    public static final ModConfigSpec.DoubleValue IRON_FLESH_ARMOR_BONUS;

    public static double oakFleshArmorBonus = 3.0;
    public static final ModConfigSpec.DoubleValue OAK_FLESH_ARMOR_BONUS;

    public static double oakFleshHealthBonus = 0.2;
    public static final ModConfigSpec.DoubleValue OAK_FLESH_HEALTH_BONUS;
    //@formatter:on
    //endregion

    //region tweaks 设置
    //@formatter:off
    public static boolean blindnessTweak = true;
    public static final ModConfigSpec.BooleanValue BLINDNESS_TWEAK;

    public static boolean injectMobDrops = true;
    public static final ModConfigSpec.BooleanValue INJECT_MOB_DROPS;

    public static int recentSpellExpiryTime = 1200;
    public static final ModConfigSpec.IntValue RECENT_SPELL_EXPIRY_TIME;

    public static Identifier[] mobLootTableWhitelist = {};
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MOB_LOOT_TABLE_WHITELIST;

    public static Identifier[] mobLootTableBlacklist;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MOB_LOOT_TABLE_BLACKLIST;

    public static int[] mobSpawnDimensions = {0};
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> MOB_SPAWN_DIMENSIONS;

    public static Identifier[] mobSpawnBiomeBlacklist;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MOB_SPAWN_BIOME_BLACKLIST;

    public static List<Identifier> summonedCreatureTargetsWhitelist;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SUMMONED_CREATURE_TARGETS_WHITELIST;

    public static List<Identifier> summonedCreatureTargetsBlacklist;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SUMMONED_CREATURE_TARGETS_BLACKLIST;

    public static Identifier[] mindControlTargetsBlacklist = {};
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MIND_CONTROL_TARGETS_BLACKLIST;

    public static Pair<Identifier, Short>[] pocketFurnaceItemBlacklist;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> POCKET_FURNACE_ITEM_BLACKLIST;

    public static Pair<Identifier, Short>[] divinationOreWhitelist;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> DIVINATION_ORE_WHITELIST;

    public static Pair<Identifier, Short>[] swordItemWhitelist;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SWORD_ITEM_WHITELIST;

    public static Pair<Identifier, Short>[] bowItemWhitelist;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> BOW_ITEM_WHITELIST;

    public static Map<Pair<Identifier, Short>, Integer> currencyItems;
    public static Map<Pair<Identifier, Short>, String> currencyItemNbt;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CURRENCY_ITEMS;

    public static double cooldownReductionPerLevel = 0.15;
    public static final ModConfigSpec.DoubleValue COOLDOWN_REDUCTION_PER_LEVEL;

    public static float storageIncreasePerLevel = 0.15f;
    public static final ModConfigSpec.DoubleValue STORAGE_INCREASE_PER_LEVEL;

    public static double potencyIncreasePerTier = 0.15;
    public static final ModConfigSpec.DoubleValue POTENCY_INCREASE_PER_TIER;

    public static double durationIncreasePerLevel = 0.25;
    public static final ModConfigSpec.DoubleValue DURATION_INCREASE_PER_LEVEL;

    public static double rangeIncreasePerLevel = 0.25;
    public static final ModConfigSpec.DoubleValue RANGE_INCREASE_PER_LEVEL;

    public static double blastIncreasePerLevel = 0.25;
    public static final ModConfigSpec.DoubleValue BLAST_INCREASE_PER_LEVEL;

    public static double frostSlownessIncreasePerLevel = 0.5;
    public static final ModConfigSpec.DoubleValue FROST_SLOWNESS_INCREASE_PER_LEVEL;

    public static boolean replaceVanillaFireballs = true;
    public static final ModConfigSpec.BooleanValue REPLACE_VANILLA_FIREBALLS;

    public static boolean replaceVanillaFallDamage = true;
    public static final ModConfigSpec.BooleanValue REPLACE_VANILLA_FALL_DAMAGE;

    public static boolean spellBookColorsRequireArchivistsEyeglass = false;
    public static final ModConfigSpec.BooleanValue SPELL_BOOK_COLORS_REQUIRE_ARCHIVISTS_EYEGLASS;

    public static int bookshelfSearchRadius = 4;
    public static final ModConfigSpec.IntValue BOOKSHELF_SEARCH_RADIUS;

    public static Pair<Identifier, Short>[] bookshelfBlocks;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> BOOKSHELF_BLOCKS;

    public static Pair<Identifier, Short>[] bookItems;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> BOOK_ITEMS;
    //@formatter:on
    //endregion

    //region resistances 设置
    //@formatter:off
    public static String[] mobsImmuneToFire = {};
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MOBS_IMMUNE_TO_FIRE;

    public static String[] mobsImmuneToIce = {};
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MOBS_IMMUNE_TO_ICE;

    public static String[] mobsImmuneToLightning = {};
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MOBS_IMMUNE_TO_LIGHTNING;

    public static String[] mobsImmuneToMagic = {};
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MOBS_IMMUNE_TO_MAGIC;

    public static String[] mobsImmuneToWither = {};
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MOBS_IMMUNE_TO_WITHER;

    public static String[] mobsImmuneToPoison = {};
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MOBS_IMMUNE_TO_POISON;
    //@formatter:on
    //endregion

    //region commands 设置
    //@formatter:off
    public static double maxSpellCommandMultiplier = 20d;
    public static final ModConfigSpec.DoubleValue MAX_SPELL_COMMAND_MULTIPLIER;

    public static String castCommandName = "cast";
    public static final ModConfigSpec.ConfigValue<String> CAST_COMMAND_NAME;

    public static String discoverspellCommandName = "discoverspell";
    public static final ModConfigSpec.ConfigValue<String> DISCOVERSPELL_COMMAND_NAME;

    public static String allyCommandName = "ally";
    public static final ModConfigSpec.ConfigValue<String> ALLY_COMMAND_NAME;

    public static String alliesCommandName = "allies";
    public static final ModConfigSpec.ConfigValue<String> ALLIES_COMMAND_NAME;
    //@formatter:on
    //endregion

    //region worldgen 设置
    //@formatter:off
    public static boolean fastWorldgen = false;
    public static final ModConfigSpec.BooleanValue FAST_WORLDGEN;

    public static int[] towerDimensions = {0};
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> TOWER_DIMENSIONS;

    public static int towerRarity = 700;
    public static final ModConfigSpec.IntValue TOWER_RARITY;

    public static double evilWizardChance = 0.2;
    public static final ModConfigSpec.DoubleValue EVIL_WIZARD_CHANCE;

    public static Identifier[] towerFiles;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> TOWER_FILES;

    public static Identifier[] towerWithChestFiles;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> TOWER_WITH_CHEST_FILES;

    public static int[] obeliskDimensions = {0, -1};
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> OBELISK_DIMENSIONS;

    public static int obeliskRarity = 600;
    public static final ModConfigSpec.IntValue OBELISK_RARITY;

    public static Identifier[] obeliskFiles;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> OBELISK_FILES;

    public static int[] shrineDimensions = {0, -1};
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> SHRINE_DIMENSIONS;

    public static int shrineRarity = 1100;
    public static final ModConfigSpec.IntValue SHRINE_RARITY;

    public static Identifier[] shrineFiles;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SHRINE_FILES;

    public static boolean shrineRegenerationEnabled = true;
    public static final ModConfigSpec.BooleanValue SHRINE_REGENERATION_ENABLED;

    public static int shrineRegenerationTime = 1;
    public static final ModConfigSpec.IntValue SHRINE_REGENERATION_TIME;

    public static boolean shrineAllowMultipleLoot = false;
    public static final ModConfigSpec.BooleanValue SHRINE_ALLOW_MULTIPLE_LOOT;

    public static int[] libraryDimensions = {0};
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> LIBRARY_DIMENSIONS;

    public static int libraryRarity = 900;
    public static final ModConfigSpec.IntValue LIBRARY_RARITY;

    public static Identifier[] libraryFiles;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> LIBRARY_FILES;

    public static Identifier[] undergroundLibraryFiles;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> UNDERGROUND_LIBRARY_FILES;

    public static Pair<Identifier, Short>[] treeBlocks;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> TREE_BLOCKS;

    public static int[] oreDimensions = {0};
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> ORE_DIMENSIONS;

    public static int[] flowerDimensions = {0};
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> FLOWER_DIMENSIONS;

    public static Identifier[] lootInjectionLocation;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> LOOT_INJECTION_LOCATIONS;
    //@formatter:on
    //endregion

    //region compatibility 设置
    //@formatter:off
    public static String[] damageSourceBlacklist = {};
    public static final ModConfigSpec.ConfigValue<List<? extends String>> DAMAGE_SOURCE_BLACKLIST;

    public static boolean compatibilityWarnings = true;
    public static final ModConfigSpec.BooleanValue COMPATIBILITY_WARNINGS;

    public static boolean baublesIntegration = true;
    public static final ModConfigSpec.BooleanValue BAUBLES_INTEGRATION;

    public static boolean jeiIntegration = true;
    public static final ModConfigSpec.BooleanValue JEI_INTEGRATION;

    public static boolean antiqueAtlasIntegration = true;
    public static final ModConfigSpec.BooleanValue ANTIQUE_ATLAS_INTEGRATION;

    public static boolean autoTowerMarkers = true;
    public static final ModConfigSpec.BooleanValue AUTO_TOWER_MARKERS;

    public static boolean autoObeliskMarkers = true;
    public static final ModConfigSpec.BooleanValue AUTO_OBELISK_MARKERS;

    public static boolean autoShrineMarkers = true;
    public static final ModConfigSpec.BooleanValue AUTO_SHRINE_MARKERS;

    public static boolean autoLibraryMarkers = true;
    public static final ModConfigSpec.BooleanValue AUTO_LIBRARY_MARKERS;

    public static boolean autoUndergroundLibraryMarkers = false;
    public static final ModConfigSpec.BooleanValue AUTO_UNDERGROUND_LIBRARY_MARKERS;
    //@formatter:on
    //endregion

    //region spells 设置
    //@formatter:off
    //@formatter:on
    //endregion

    //region spells 设置
    //@formatter:off
    //@formatter:on
    //endregion

    static {
        //region gameplay 设置
        //@formatter:off
        BUILDER.push(ConfigCategory.GAMEPLAY_CATEGORY)
                .comment("Global settings that affect game mechanics. In multiplayer, the server/LAN host settings will apply. Please note that changing some of these settings may make the mod very difficult to play.");
        PLAYER_BLOCK_DAMAGE = BUILDER
                .comment("Whether spells cast by players can destroy blocks in the world. Wizardry makes every attempt to respect protection mods and plugins, but cannot guarantee it will work in all cases for every mod. If you need absolutely watertight anti-grief, disable this setting. (N.B. This setting only affects players. To prevent mobs from destroying blocks with magic, use the mobGriefing gamerule.)")
                .translation("config." + Wizardry.MODID + ".player_block_damage")
                .define("playerBlockDamage", true);

        DISPENSER_BLOCK_DAMAGE = BUILDER
                .comment("Whether spells cast by dispensers can destroy blocks in the world. Wizardry makes every attempt to respect protection mods and plugins, but cannot guarantee it will work in all cases for every mod. If you need absolutely watertight anti-grief, disable this setting.")
                .translation("config." + Wizardry.MODID + ".dispenser_block_damage")
                .define("dispenserBlockDamage", true);

        DAMAGE_TYPE_PER_ELEMENT = BUILDER
                .comment("Whether damage should be registered with the old system (wizardry_magic/indirect_wizardry_magic) prefixed damage with the elements like necromancy_indirect_wizardry_magic, necromancy_wizardry_magic. This is disabled by default to not break existing modpacks. The intention of this setting is to allow differentiating various damage types for e.g. the Distinct Damage Descriptions mod")
                .translation("config." + Wizardry.MODID + ".damage_type_per_element")
                .define("damageTypePerElement", false);

        SINGLE_USE_SPELL_BOOKS = BUILDER
                .comment("Whether spell books are consumed when they are bound to a wand.")
                .translation("config." + Wizardry.MODID + ".single_use_spell_books")
                .define("singleUseSpellBooks", false);

        PREVENT_BINDING_SAME_SPELL_TWICE_TO_WANDS = BUILDER
                .comment("Whether to prevent binding the same spell to a wand multiple times")
                .translation("config." + Wizardry.MODID + ".prevent_binding_same_spell_twice_to_wands")
                .define("preventBindingSameSpellTwiceToWands", false);

        MANA_PER_SHARD = BUILDER
                .comment("The amount of mana a crystal shard is worth.")
                .translation("config." + Wizardry.MODID + ".mana_per_shard")
                .defineInRange("manaPerShard", 10, 1, 1000);

        MANA_PER_CRYSTAL = BUILDER
                .comment("The amount of mana each magic crystal is worth.")
                .translation("config." + Wizardry.MODID + ".mana_per_crystal")
                .defineInRange("manaPerCrystal", 100, 1, 10000);

        GRAND_CRYSTAL_MANA = BUILDER
                .comment("The amount of mana a grand magic crystal is worth.")
                .translation("config." + Wizardry.MODID + ".grand_crystal_mana")
                .defineInRange("grandCrystalMana", 400, 1, 10000);

        UPGRADE_STACK_LIMIT = BUILDER
                .comment("The maximum number of one type of wand upgrade which can be applied to a wand.")
                .translation("config." + Wizardry.MODID + ".upgrade_stack_limit")
                .defineInRange("upgradeStackLimit", 3, 1, 10);

        NON_ELEMENTAL_UPGRADE_BONUS = BUILDER
                .comment("The bonus amount of wand upgrades that can be applied to a non-elemental wand.")
                .translation("config." + Wizardry.MODID + ".non_elemental_upgrade_bonus")
                .defineInRange("nonElementalUpgradeBonus", 3, 0, 10);

        SIPHON_MANA_PER_LEVEL = BUILDER
                .comment("The amount of mana given for a kill for each level of siphon upgrade.")
                .translation("config." + Wizardry.MODID + ".siphon_mana_per_level")
                .defineInRange("siphonManaPerLevel", 5, 0, 100);

        CONDENSER_TICK_INTERVAL = BUILDER
                .comment("The number of ticks between each mana increase for wands with the condenser upgrade.")
                .translation("config." + Wizardry.MODID + ".condenser_tick_interval")
                .defineInRange("condenserTickInterval", 50, 1, 1000);

        BASE_SPELL_SLOTS = BUILDER
                .comment("The number of spell slots a wand has with no attunement upgrades applied.")
                .translation("config." + Wizardry.MODID + ".base_spell_slots")
                .defineInRange("baseSpellSlots", 5, 1, 5);

        PLAYERS_MOVE_EACH_OTHER = BUILDER
                .comment("Whether to allow players to move other players around using magic.")
                .translation("config." + Wizardry.MODID + ".players_move_each_other")
                .define("playersMoveEachOther", true);

        TELEKINETIC_DISARMAMENT = BUILDER
                .comment("Whether to allow players to disarm other players using the telekinesis spell. Set to false to prevent stealing of items.")
                .translation("config." + Wizardry.MODID + ".telekinetic_disarmament")
                .define("telekineticDisarmament", true);

        TELEPORT_THROUGH_UNBREAKABLE_BLOCKS = BUILDER
                .comment("Whether players are allowed to teleport through unbreakable blocks (e.g. bedrock) using the phase step spell.")
                .translation("config." + Wizardry.MODID + ".teleport_through_unbreakable_blocks")
                .define("teleportThroughUnbreakableBlocks", false);

        WORLD_TIME_MANIPULATION = BUILDER
                .comment("Whether players are allowed to change the world time with the speed time spell. If this is false, the speed time spell will not change the world time but will still speed up nearby block, entity and tile entity ticks.")
                .translation("config." + Wizardry.MODID + ".world_time_manipulation")
                .define("worldTimeManipulation", true);

        BONEMEAL_GROWS_CRYSTAL_FLOWERS = BUILDER
                .comment("Whether using bonemeal on grass blocks has a chance to grow crystal flowers.")
                .translation("config." + Wizardry.MODID + ".bonemeal_grows_crystal_flowers")
                .define("bonemealGrowsCrystalFlowers", true);

        WANDS_MUST_BE_HELD_TO_DECREMENT_COOLDOWN = BUILDER
                .comment("Whether wands only decrement their cooldowns if a player holds them.")
                .translation("config." + Wizardry.MODID + ".wands_must_be_held_to_decrement_cooldown")
                .define("wandsMustBeHeldToDecrementCooldown", false);

        SLOW_TIME_AFFECTS_PLAYERS = BUILDER
                .comment("Whether players are slowed when another nearby player uses the slow time spell. If this is false, mobs and projectiles will still be affected but players will move at normal speed.")
                .translation("config." + Wizardry.MODID + ".slow_time_affects_players")
                .worldRestart()
                .define("slowTimeAffectsPlayers", true);

        CREATIVE_BYPASSES_ARCANE_LOCK = BUILDER
                .comment("Whether any player in creative mode can bypass arcane-locked blocks. If this is false, players must also be op in order to do so.")
                .translation("config." + Wizardry.MODID + ".creative_bypasses_arcane_lock")
                .worldRestart()
                .define("creativeBypassesArcaneLock", true);
        BUILDER.pop();
        //@formatter:on
        //endregion

        //region difficulty 设置
        //@formatter:off
        BUILDER.push(ConfigCategory.DIFFICULTY_CATEGORY)
                .comment("Settings that affect the mod's difficulty. In multiplayer, the server/LAN host settings will apply.");
        LEGACY_WAND_LEVELLING = BUILDER
                .comment("Controls whether wands are required to gain progression before they can be upgraded to the next tier. Enable this option to revert to the pre-4.2 system, which only requires tomes of arcana. Wands will still gain progression even when this is enabled, so if you go back to the new system you won't lose any progress.")
                .translation("config." + Wizardry.MODID + ".legacy_wand_levelling")
                .define("legacyWandLevelling", false);

        FRIENDLY_FIRE = BUILDER
                .comment("Controls which creatures may be damaged by your magic when allied to you. Your spells will not target your allies or creatures summoned/owned by them regardless of this setting, but this setting prevents all magic damage to allies.")
                .translation("config." + Wizardry.MODID + ".friendly_fire")
                .defineEnum("friendlyFire", FriendlyFire.ALL);

        MINION_REVENGE_TARGETING = BUILDER
                .comment("Whether summoned creatures can revenge attack their owner if their owner attacks them.")
                .translation("config." + Wizardry.MODID + ".minion_revenge_targeting")
                .define("minionRevengeTargeting", true);

        PLAYER_DAMAGE_SCALE = BUILDER
                .comment("Global damage scaling factor for the damage dealt by players casting spells, relative to 1.")
                .translation("config." + Wizardry.MODID + ".player_damage_scaling")
                .defineInRange("playerDamageScaling", 1.0, 0.0, 255.0);

        NPC_DAMAGE_SCALE = BUILDER
                .comment("Global damage scaling factor for the damage dealt by NPCs casting spells, relative to 1.")
                .translation("config." + Wizardry.MODID + ".npc_damage_scaling")
                .defineInRange("npcDamageScaling", 1.0, 0.0, 255.0);

        EVIL_WIZARD_SPAWN_RATE = BUILDER
                .comment("Spawn rate for naturally-spawned evil wizards; higher numbers mean more evil wizards will spawn. 5 is equivalent to witches, 100 is equivalent to zombies, skeletons and creepers. Set to 0 to disable evil wizard spawning entirely.")
                .translation("config." + Wizardry.MODID + ".evil_wizard_spawn_rate")
                .defineInRange("evilWizardSpawnRate", 3, 0, 100);

        ICE_WRAITH_SPAWN_RATE = BUILDER
                .comment("Spawn rate for naturally-spawned ice wraiths; higher numbers mean more ice wraiths will spawn. 5 is equivalent to witches, 100 is equivalent to zombies, skeletons and creepers. Set to 0 to disable ice wraith spawning entirely.")
                .translation("config." + Wizardry.MODID + ".ice_wraith_spawn_rate")
                .defineInRange("iceWraithSpawnRate", 3, 0, 100);

        LIGHTNING_WRAITH_SPAWN_RATE = BUILDER
                .comment("Spawn rate for naturally-spawned lightning wraiths; higher numbers mean more lightning wraiths will spawn. 5 is equivalent to witches, 100 is equivalent to zombies, skeletons and creepers. Set to 0 to disable lightning wraith spawning entirely.")
                .translation("config." + Wizardry.MODID + ".lightning_wraith_spawn_rate")
                .defineInRange("lightningWraithSpawnRate", 1, 0, 100);

        DISCOVERY_MODE = BUILDER
                .comment("For those who like a sense of mystery! When set to true, spells you haven't cast yet will be unreadable until you cast them (on a per-world basis). Has no effect when in creative mode. Spells of identification will be unobtainable in survival mode if this is false.")
                .translation("config." + Wizardry.MODID + ".discovery_mode")
                .worldRestart()
                .define("discoveryMode", true);

        FORFEIT_CHANCE = BUILDER
                .comment("The chance to 'misread' an undiscovered spell and trigger a forfeit instead. Setting this to 0 effectively disables the forfeit mechanic. Has no effect if discovery mode is disabled.")
                .translation("config." + Wizardry.MODID + ".forfeit_chance")
                .worldRestart()
                .defineInRange("forfeitChance", 0.2, 0.0, 1.0);

        PROGRESSION_REQUIREMENTS = BUILDER
                .comment("The amount of progression required to upgrade a wand to each tier (apprentice, advanced and master respectively).")
                .translation("config." + Wizardry.MODID + ".progression_requirements")
                .worldRestart()
                .defineList("progressionRequirements", Arrays.asList(1500, 3500, 6000), obj -> obj instanceof Integer);

        NOVICE_MAX_CHARGE = BUILDER
                .comment("Maximum mana a novice wand can store.")
                .translation("config." + Wizardry.MODID + ".novice_max_charge")
                .worldRestart()
                .defineInRange("noviceMaxCharge", 700, 0, Integer.MAX_VALUE);

        APPRENTICE_MAX_CHARGE = BUILDER
                .comment("Maximum mana an apprentice wand can store.")
                .translation("config." + Wizardry.MODID + ".apprentice_max_charge")
                .worldRestart()
                .defineInRange("apprenticeMaxCharge", 1000, 0, Integer.MAX_VALUE);

        ADVANCED_MAX_CHARGE = BUILDER
                .comment("Maximum mana an advanced wand can store.")
                .translation("config." + Wizardry.MODID + ".advanced_max_charge")
                .worldRestart()
                .defineInRange("advancedMaxCharge", 1500, 0, Integer.MAX_VALUE);

        MASTER_MAX_CHARGE = BUILDER
                .comment("Maximum mana a master wand can store.")
                .translation("config." + Wizardry.MODID + ".master_max_charge")
                .worldRestart()
                .defineInRange("masterMaxCharge", 2500, 0, Integer.MAX_VALUE);

        NOVICE_UPGRADE_LIMIT = BUILDER
                .comment("Maximum number of upgrades a novice wand can have.")
                .translation("config." + Wizardry.MODID + ".novice_upgrade_limit")
                .worldRestart()
                .defineInRange("noviceUpgradeLimit", 3, 0, Integer.MAX_VALUE);

        APPRENTICE_UPGRADE_LIMIT = BUILDER
                .comment("Maximum number of upgrades an apprentice wand can have.")
                .translation("config." + Wizardry.MODID + ".apprentice_upgrade_limit")
                .worldRestart()
                .defineInRange("apprenticeUpgradeLimit", 5, 0, Integer.MAX_VALUE);

        ADVANCED_UPGRADE_LIMIT = BUILDER
                .comment("Maximum number of upgrades an advanced wand can have.")
                .translation("config." + Wizardry.MODID + ".advanced_upgrade_limit")
                .worldRestart()
                .defineInRange("advancedUpgradeLimit", 7, 0, Integer.MAX_VALUE);

        MASTER_UPGRADE_LIMIT = BUILDER
                .comment("Maximum number of upgrades a master wand can have.")
                .translation("config." + Wizardry.MODID + ".master_upgrade_limit")
                .worldRestart()
                .defineInRange("masterUpgradeLimit", 9, 0, Integer.MAX_VALUE);

        CONDENSER_AMOUNT_MULTIPLIER = BUILDER
                .comment("Multiplier for condenser upgrade mana regeneration amount. Higher values make condensers more effective.")
                .translation("config." + Wizardry.MODID + ".condenser_amount_multiplier")
                .worldRestart()
                .defineInRange("condenserAmountMultiplier", 1.0, 0.0, Double.MAX_VALUE);

        FLESH_SPELLS_CAUSE_SLOWNESS = BUILDER
                .comment("Whether flesh spells (DiamondFlesh, IronFlesh, OakFlesh) apply slowness. When disabled, these spells only provide their defensive benefits without movement penalty.")
                .translation("config." + Wizardry.MODID + ".flesh_spells_cause_slowness")
                .worldRestart()
                .define("fleshSpellsCauseSlowness", true);

        DIAMOND_FLESH_ARMOR_BONUS = BUILDER
                .comment("Armor bonus provided by the DiamondFlesh spell.")
                .translation("config." + Wizardry.MODID + ".diamond_flesh_armor_bonus")
                .worldRestart()
                .defineInRange("diamondFleshArmorBonus", 4.0, 0.0, Double.MAX_VALUE);

        DIAMOND_FLESH_ARMOR_TOUGHNESS_BONUS = BUILDER
                .comment("Armor toughness bonus provided by the DiamondFlesh spell.")
                .translation("config." + Wizardry.MODID + ".diamond_flesh_armor_toughness_bonus")
                .worldRestart()
                .defineInRange("diamondFleshArmorToughnessBonus", 3.0, 0.0, Double.MAX_VALUE);

        IRON_FLESH_ARMOR_BONUS = BUILDER
                .comment("Armor bonus provided by the IronFlesh spell.")
                .translation("config." + Wizardry.MODID + ".iron_flesh_armor_bonus")
                .worldRestart()
                .defineInRange("ironFleshArmorBonus", 4.0, 0.0, Double.MAX_VALUE);

        OAK_FLESH_ARMOR_BONUS = BUILDER
                .comment("Armor bonus provided by the OakFlesh spell.")
                .translation("config." + Wizardry.MODID + ".oak_flesh_armor_bonus")
                .worldRestart()
                .defineInRange("oakFleshArmorBonus", 3.0, 0.0, Double.MAX_VALUE);

        OAK_FLESH_HEALTH_BONUS = BUILDER
                .comment("Health bonus provided by the OakFlesh spell (as a multiplier, e.g., 0.2 = 20% increase).")
                .translation("config." + Wizardry.MODID + ".oak_flesh_health_bonus")
                .worldRestart()
                .defineInRange("oakFleshHealthBonus", 0.2, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        //@formatter:on
        //endregion

        //region tweaks 设置
        //@formatter:off
        BUILDER.push(ConfigCategory.TWEAKS_CATEGORY)
                .comment("Assorted settings for tweaking the mod's behaviour. In multiplayer, the server/LAN host settings will apply.");
        BLINDNESS_TWEAK = BUILDER
                .comment("Whether to tweak the blindness effect to reduce follow distance when used on non-players. This automatically disables itself in favour of Potion-Core's implementation if installed.")
                .translation("config." + Wizardry.MODID + ".blindness_tweak")
                .define("blindnessTweak", true);

        RECENT_SPELL_EXPIRY_TIME = BUILDER
                .comment("The time in ticks after which recent spell casts expire and no longer count toward progression penalties. Default is 1200 ticks (1 minute). Lower values make progression penalties shorter-lived, higher values make them last longer.")
                .translation("config." + Wizardry.MODID + ".recent_spell_expiry_time")
                .defineInRange("recentSpellExpiryTime", 1200, 60, 72000);

        INJECT_MOB_DROPS = BUILDER
                .comment("Whether to inject Wizardry loot to mobs, based on the loot table 'Mob Loot Table Whitelist' / 'Mob Loot Table Blacklist' settings. If disabled, the lists won't have any effect!")
                .translation("config." + Wizardry.MODID + ".inject_mob_drops")
                .define("injectMobDrops", true);

        MOB_LOOT_TABLE_WHITELIST = BUILDER
                .comment("Whitelist for loot tables to inject additional mob drops (as specified in loot_tables/entities/mob_additions.json) into. Wizardry makes a best guess as to which loot tables belong to hostile mobs, but this may not always be correct or appropriate; add loot table locations (not entity IDs) to this list to manually include them.")
                .translation("config." + Wizardry.MODID + ".mob_loot_table_whitelist")
                .defineList("mobLootTableWhitelist", Collections.emptyList(), obj -> obj instanceof String);

        MOB_LOOT_TABLE_BLACKLIST = BUILDER
                .comment("Blacklist for loot tables to inject additional mob drops (as specified in loot_tables/entities/mob_additions.json) into. Wizardry makes a best guess as to which loot tables belong to hostile mobs, but this may not always be correct or appropriate; add loot table locations (not entity IDs) to this list to manually exclude them.")
                .translation("config." + Wizardry.MODID + ".mob_loot_table_blacklist")
                .defineList("mobLootTableBlacklist", Arrays.asList("entities/vex", "entities/ender_dragon", "entities/wither", Wizardry.MODID + ":entities/evil_wizard"), obj -> obj instanceof String);

        MOB_SPAWN_DIMENSIONS = BUILDER
                .comment("List of dimension ids in which wizardry's hostile mobs can spawn.")
                .translation("config." + Wizardry.MODID + ".mob_spawn_dimensions")
                .defineList("mobSpawnDimensions", Arrays.asList(0), obj -> obj instanceof Integer);

        MOB_SPAWN_BIOME_BLACKLIST = BUILDER
                .comment("List of names of biomes in which wizardry's hostile mobs cannot spawn. Biome names are not case-sensitive. For mod biomes, prefix with the mod ID (e.g. biomesoplenty:mystic_grove).")
                .translation("config." + Wizardry.MODID + ".mob_spawn_biome_blacklist")
                .defineList("mobSpawnBiomeBlacklist", Arrays.asList("mushroom_island", "mushroom_island_shore"), obj -> obj instanceof String);

        SUMMONED_CREATURE_TARGETS_WHITELIST = BUILDER
                .comment("List of names of entities which summoned creatures and wizards are allowed to attack, in addition to the defaults. Add mod creatures to this list if you want summoned creatures to attack them and they aren't already doing so. Entity names are not case sensitive. For mod entities, prefix with the mod ID (e.g. " + Wizardry.MODID + ":wizard).")
                .translation("config." + Wizardry.MODID + ".summoned_creature_targets_whitelist")
                .defineList("summonedCreatureTargetsWhitelist", Collections.emptyList(), obj -> obj instanceof String);

        SUMMONED_CREATURE_TARGETS_BLACKLIST = BUILDER
                .comment("List of names of entities which summoned creatures and wizards are specifically not allowed to attack, overriding the defaults and the whitelist. Add creatures to this list if allowing them to be attacked causes problems or is too destructive (removing creepers from this list is done at your own risk!). Entity names are not case sensitive. For mod entities, prefix with the mod ID (e.g. " + Wizardry.MODID + ":wizard).")
                .translation("config." + Wizardry.MODID + ".summoned_creature_targets_blacklist")
                .defineList("summonedCreatureTargetsBlacklist", Arrays.asList("creeper"), obj -> obj instanceof String);

        MIND_CONTROL_TARGETS_BLACKLIST = BUILDER
                .comment("List of names of entities which cannot be mind controlled, in addition to the defaults. Add creatures to this list if allowing them to be mind-controlled causes problems or could be exploited. Entity names are not case sensitive. For mod entities, prefix with the mod ID (e.g. " + Wizardry.MODID + ":wizard).")
                .translation("config." + Wizardry.MODID + ".mind_control_targets_blacklist")
                .defineList("mindControlTargetsBlacklist", Collections.emptyList(), obj -> obj instanceof String);

        POCKET_FURNACE_ITEM_BLACKLIST = BUILDER
                .comment("List of registry names of blocks or items which cannot be smelted by the pocket furnace spell, in addition to armour, tools and weapons. Block/item names are not case sensitive. For mod items, prefix with the mod ID (e.g. " + Wizardry.MODID + ":crystal_ore).")
                .translation("config." + Wizardry.MODID + ".pocket_furnace_item_blacklist")
                .defineList("pocketFurnaceItemBlacklist", Arrays.asList("cobblestone", "netherrack"), obj -> obj instanceof String);

        DIVINATION_ORE_WHITELIST = BUILDER
                .comment("List of registry names of ore blocks which can be detected by the divination spell. Block names are not case sensitive. For mod blocks, prefix with the mod ID (e.g. " + Wizardry.MODID + ":crystal_ore).")
                .translation("config." + Wizardry.MODID + ".divination_ore_whitelist")
                .defineList("divinationOreWhitelist", Collections.emptyList(), obj -> obj instanceof String);

        SWORD_ITEM_WHITELIST = BUILDER
                .comment("List of registry names of items which should count as swords for imbuement spells. Most swords should work automatically, but those that don't can be added manually here. Item names are not case sensitive. For mod items, prefix with the mod ID (e.g. tconstruct:broadsword).")
                .translation("config." + Wizardry.MODID + ".sword_item_whitelist")
                .defineList("swordItemWhitelist", Collections.emptyList(), obj -> obj instanceof String);

        BOW_ITEM_WHITELIST = BUILDER
                .comment("List of registry names of items which should count as bows for imbuement spells. Most bows should work automatically, but those that don't can be added manually here. Item names are not case sensitive. For mod items, prefix with the mod ID (e.g. tconstruct:shortbow).")
                .translation("config." + Wizardry.MODID + ".bow_item_whitelist")
                .defineList("bowItemWhitelist", Collections.emptyList(), obj -> obj instanceof String);

        CURRENCY_ITEMS = BUILDER
                .comment("List of registry names of items which wizard trades can use as currency (in the first slot; the second slot is unaffected). Each entry in this list should consist of an item registry name, followed by a single space, then an integer which defines the 'value' of the item. Higher values mean fewer of that currency item are required for a given trade. To specify metadata, use the format 'modid:item:meta value'. For example, 'minecraft:wool:1 5'. If no metadata is given, any metadata will be accepted. NBT tags may optionally be specified in SNBT format immediately after the item/metadata, e.g. 'minecraft:potion{Potion:\"minecraft:strength\"} 3'.")
                .translation("config." + Wizardry.MODID + ".currency_items")
                .defineList("currencyItems", Arrays.asList("gold_ingot 3", "emerald 6"), obj -> obj instanceof String);

        COOLDOWN_REDUCTION_PER_LEVEL = BUILDER
                .comment("The fraction by which cooldowns are reduced for each level of cooldown upgrade.")
                .translation("config." + Wizardry.MODID + ".cooldown_reduction_per_level")
                .defineInRange("cooldownReductionPerLevel", 0.15, 0.05, Double.MAX_VALUE);

        STORAGE_INCREASE_PER_LEVEL = BUILDER
                .comment("The fraction by which maximum charge is increased for each level of storage upgrade.")
                .translation("config." + Wizardry.MODID + ".storage_increase_per_level")
                .defineInRange("storageIncreasePerLevel", 0.15, 0.05, Double.MAX_VALUE);

        POTENCY_INCREASE_PER_TIER = BUILDER
                .comment("The fraction by which potency is increased for each tier of matching wand. May cause extreme lag with high values!")
                .translation("config." + Wizardry.MODID + ".potency_increase_per_tier")
                .defineInRange("potencyIncreasePerTier", 0.15, 0.05, Double.MAX_VALUE);

        DURATION_INCREASE_PER_LEVEL = BUILDER
                .comment("The fraction by which spell duration is increased for each level of duration upgrade.")
                .translation("config." + Wizardry.MODID + ".duration_increase_per_level")
                .defineInRange("durationIncreasePerLevel", 0.25, 0.05, Double.MAX_VALUE);

        RANGE_INCREASE_PER_LEVEL = BUILDER
                .comment("The fraction by which spell range is increased for each level of range upgrade. May cause extreme lag with high values!")
                .translation("config." + Wizardry.MODID + ".range_increase_per_level")
                .defineInRange("rangeIncreasePerLevel", 0.25, 0.05, Double.MAX_VALUE);

        BLAST_INCREASE_PER_LEVEL = BUILDER
                .comment("The fraction by which spell blast is increased for each level of blast upgrade. May cause extreme lag with high values!")
                .translation("config." + Wizardry.MODID + ".blast_increase_per_level")
                .defineInRange("blastIncreasePerLevel", 0.25, 0.05, Double.MAX_VALUE);

        FROST_SLOWNESS_INCREASE_PER_LEVEL = BUILDER
                .comment("The fraction by which movement speed is reduced per level of frost effect.")
                .translation("config." + Wizardry.MODID + ".frost_slowness_increase_per_level")
                .defineInRange("frostSlownessIncreasePerLevel", 0.5, 0.05, Double.MAX_VALUE);

        REPLACE_VANILLA_FIREBALLS = BUILDER
                .comment("Whether to replace Minecraft's own fireballs with wizardry fireballs. If this is disabled, only wizardry spells will use the custom fireballs.")
                .translation("config." + Wizardry.MODID + ".replace_vanilla_fireballs")
                .worldRestart()
                .define("replaceVanillaFireballs", true);

        REPLACE_VANILLA_FALL_DAMAGE = BUILDER
                .comment("Whether to replace Minecraft's distance-based fall damage calculation with an equivalent, velocity-based one. This is done such that mobs in freefall will take exactly the same damage as normal, so it will not break falling-based mob farms. Disable this if you experience falling-related weirdness! If this is disabled, some spells will use a more simplistic method of resetting the player's fall damage in certain cases.")
                .translation("config." + Wizardry.MODID + ".replace_vanilla_fall_damage")
                .worldRestart()
                .define("replaceVanillaFallDamage", true);

        SPELL_BOOK_COLORS_REQUIRE_ARCHIVISTS_EYEGLASS = BUILDER
                .comment("If true, spell book colors are only shown when the player has the charm of spell discovery equipped.")
                .translation("config." + Wizardry.MODID + ".spell_book_colors_require_charm")
                .define("spellBookColorsRequireArchivistsEyeglass", false);

        BOOKSHELF_SEARCH_RADIUS = BUILDER
                .comment("The maximum number of blocks a bookshelf can be from an arcane workbench or lectern to be able to link to it.")
                .translation("config." + Wizardry.MODID + ".bookshelf_search_radius")
                .worldRestart()
                .defineInRange("bookshelfSearchRadius", 4, 1, 10);

        BOOKSHELF_BLOCKS = BUILDER
                .comment("List of registry names of blocks that count as bookshelves for the arcane workbench and lectern. Block names are not case sensitive. For mod blocks, prefix with the mod ID (e.g. " + Wizardry.MODID + ":oak_bookshelf).")
                .translation("config." + Wizardry.MODID + ".bookshelf_blocks")
                .worldRestart()
                .defineList("bookshelfBlocks", Arrays.asList(
                        Wizardry.MODID + ":oak_bookshelf",
                        Wizardry.MODID + ":spruce_bookshelf",
                        Wizardry.MODID + ":birch_bookshelf",
                        Wizardry.MODID + ":jungle_bookshelf",
                        Wizardry.MODID + ":acacia_bookshelf",
                        Wizardry.MODID + ":dark_oak_bookshelf"
                ), obj -> obj instanceof String);

        BOOK_ITEMS = BUILDER
                .comment("List of registry names of items which can be placed in a bookshelf, in addition to the defaults. Item names are not case sensitive. For mod items, prefix with the mod ID (e.g. thaumcraft:thaumonomicon).")
                .translation("config." + Wizardry.MODID + ".book_items")
                .worldRestart()
                .defineList("bookItems", Collections.emptyList(), obj -> obj instanceof String);
        BUILDER.pop();
        //@formatter:on
        //endregion

        //region resistances 设置
        //@formatter:off
        BUILDER.push(ConfigCategory.RESISTANCES_CATEGORY)
                .comment("Settings which allow entities to be made immune to certain types of magic. In multiplayer, the server/LAN host settings will apply.");

        MOBS_IMMUNE_TO_FIRE = BUILDER
                .comment("List of names of entities that are immune to fire, in addition to the defaults. Add mod creatures to this list if you want them to be immune to fire magic and they aren't already. Entity names are not case sensitive. For mod entities, prefix with the mod ID (e.g. " + Wizardry.MODID + ":wizard).")
                .translation("config." + Wizardry.MODID + ".mobs_immune_to_fire")
                .worldRestart()
                .defineList("mobsImmuneToFire", Collections.emptyList(), obj -> obj instanceof String);

        MOBS_IMMUNE_TO_ICE = BUILDER
                .comment("List of names of entities that are immune to ice, in addition to the defaults. Add mod creatures to this list if you want them to be immune to ice magic and they aren't already. Entity names are not case sensitive. For mod entities, prefix with the mod ID (e.g. " + Wizardry.MODID + ":wizard).")
                .translation("config." + Wizardry.MODID + ".mobs_immune_to_ice")
                .worldRestart()
                .defineList("mobsImmuneToIce", Collections.emptyList(), obj -> obj instanceof String);

        MOBS_IMMUNE_TO_LIGHTNING = BUILDER
                .comment("List of names of entities that are immune to lightning, in addition to the defaults. Add mod creatures to this list if you want them to be immune to lightning magic and they aren't already. Entity names are not case sensitive. For mod entities, prefix with the mod ID (e.g. " + Wizardry.MODID + ":wizard).")
                .translation("config." + Wizardry.MODID + ".mobs_immune_to_lightning")
                .worldRestart()
                .defineList("mobsImmuneToLightning", Collections.emptyList(), obj -> obj instanceof String);

        MOBS_IMMUNE_TO_MAGIC = BUILDER
                .comment("List of names of entities that are immune to magic, in addition to the defaults. Add mod creatures to this list if you want them to be immune to magic damage and they aren't already. Entity names are not case sensitive. For mod entities, prefix with the mod ID (e.g. " + Wizardry.MODID + ":wizard).")
                .translation("config." + Wizardry.MODID + ".mobs_immune_to_magic")
                .worldRestart()
                .defineList("mobsImmuneToMagic", Collections.emptyList(), obj -> obj instanceof String);

        MOBS_IMMUNE_TO_WITHER = BUILDER
                .comment("List of names of entities that are immune to wither effects, in addition to the defaults. Add mod creatures to this list if you want them to be immune to withering magic and they aren't already. Entity names are not case sensitive. For mod entities, prefix with the mod ID (e.g. " + Wizardry.MODID + ":wizard).")
                .translation("config." + Wizardry.MODID + ".mobs_immune_to_wither")
                .worldRestart()
                .defineList("mobsImmuneToWither", Collections.emptyList(), obj -> obj instanceof String);

        MOBS_IMMUNE_TO_POISON = BUILDER
                .comment("List of names of entities that are immune to poison, in addition to the defaults. Add mod creatures to this list if you want them to be immune to poison magic and they aren't already. Entity names are not case sensitive. For mod entities, prefix with the mod ID (e.g. " + Wizardry.MODID + ":wizard).")
                .translation("config." + Wizardry.MODID + ".mobs_immune_to_poison")
                .worldRestart()
                .defineList("mobsImmuneToPoison", Collections.emptyList(), obj -> obj instanceof String);

        BUILDER.pop();
        //@formatter:on
        //endregion

        //region commands 设置
        //@formatter:off
        BUILDER.push(ConfigCategory.COMMANDS_CATEGORY)
                .comment("Settings for the commands added by Wizardry. In multiplayer, the server/LAN host settings will apply.");

        MAX_SPELL_COMMAND_MULTIPLIER = BUILDER
                .comment("Upper limit for the multipliers passed into the /cast command. This is here to stop players from accidentally breaking a world/server. Large blast mutipliers can cause extreme lag - you have been warned!")
                .translation("config." + Wizardry.MODID + ".cast_command_multiplier_limit")
                .defineInRange("castCommandMultiplierLimit", 20.0, 1.0, Double.MAX_VALUE);

        CAST_COMMAND_NAME = BUILDER
                .comment("The name of the /cast command. This is what you type directly after the /; for example if this was set to 'magic' then instead of typing /cast you would type /magic instead.")
                .translation("config." + Wizardry.MODID + ".cast_command_name")
                .worldRestart()
                .define("castCommandName", "cast");

        DISCOVERSPELL_COMMAND_NAME = BUILDER
                .comment("The name of the /discoverspell command. This is what you type directly after the /; for example if this was set to 'magic' then instead of typing /discoverspell you would type /magic instead.")
                .translation("config." + Wizardry.MODID + ".discoverspell_command_name")
                .worldRestart()
                .define("discoverspellCommandName", "discoverspell");

        ALLY_COMMAND_NAME = BUILDER
                .comment("The name of the /ally command. This is what you type directly after the /; for example if this was set to 'magic' then instead of typing /ally you would type /magic instead.")
                .translation("config." + Wizardry.MODID + ".ally_command_name")
                .worldRestart()
                .define("allyCommandName", "ally");

        ALLIES_COMMAND_NAME = BUILDER
                .comment("The name of the /allies command. This is what you type directly after the /; for example if this was set to 'magic' then instead of typing /allies you would type /magic instead.")
                .translation("config." + Wizardry.MODID + ".allies_command_name")
                .worldRestart()
                .define("alliesCommandName", "allies");

        BUILDER.pop();
        //@formatter:on
        //endregion

        //region worldgen 设置
        //@formatter:off
        BUILDER.push(ConfigCategory.WORLDGEN_CATEGORY)
                .comment("Settings that affect world generation. In multiplayer, the server/LAN host settings will apply.");

        FAST_WORLDGEN = BUILDER
                .comment("Whether to use faster worldgen at the cost of 'seamlessness'. Enabling this option removes the checks for steep slopes and cleanup of floating trees that improve the look of worldgen. Performance improvement will vary depending on your setup. This option will affect randomisation; for any given seed, structures will not be the same as when it is turned off.")
                .translation("config." + Wizardry.MODID + ".fast_worldgen")
                .define("fastWorldgen", false);

        TOWER_DIMENSIONS = BUILDER
                .comment("List of dimension ids in which wizard towers will generate. Remove all dimensions to disable wizard towers completely.")
                .translation("config." + Wizardry.MODID + ".tower_dimensions")
                .worldRestart()
                .defineList("towerDimensions", Arrays.asList(0), obj -> obj instanceof Integer);

        TOWER_RARITY = BUILDER
                .comment("Rarity of wizard towers. 1 in this many chunks will contain a wizard tower, meaning higher numbers are rarer.")
                .translation("config." + Wizardry.MODID + ".tower_rarity")
                .worldRestart()
                .defineInRange("towerRarity", 700, 20, 5000);

        EVIL_WIZARD_CHANCE = BUILDER
                .comment("The chance for wizard towers to generate with an evil wizard and chest inside, instead of a friendly wizard.")
                .translation("config." + Wizardry.MODID + ".evil_wizard_chance")
                .worldRestart()
                .defineInRange("evilWizardChance", 0.2, 0.0, 1.0);

        TOWER_FILES = BUILDER
                .comment("List of structure file locations for wizard towers without loot chests. One of these files will be randomly selected each time a wizard tower is generated. File locations are of the format [mod id]:[filename], which refers to the file assets/[mod id]/structures/[filename].nbt. Duplicate entries are permitted, allowing for simple weighting without duplicating the structure files themselves. This list should not be empty; to disable wizard towers, use the tower dimensions setting.")
                .translation("config." + Wizardry.MODID + ".tower_files")
                .worldRestart()
                .defineList("towerFiles", Arrays.asList(
                        Wizardry.MODID + ":wizard_tower_0",
                        Wizardry.MODID + ":wizard_tower_1",
                        Wizardry.MODID + ":wizard_tower_2",
                        Wizardry.MODID + ":wizard_tower_3"
                ), obj -> obj instanceof String);

        TOWER_WITH_CHEST_FILES = BUILDER
                .comment("List of structure file locations for wizard towers with loot chests. One of these files will be randomly selected each time a wizard tower is generated. File locations are of the format [mod id]:[filename], which refers to the file assets/[mod id]/structures/[filename].nbt. Duplicate entries are permitted, allowing for simple weighting without duplicating the structure files themselves. This list should not be empty; to disable wizard towers, use the tower dimensions setting.")
                .translation("config." + Wizardry.MODID + ".tower_with_chest_files")
                .worldRestart()
                .defineList("towerWithChestFiles", Arrays.asList(
                        Wizardry.MODID + ":wizard_tower_chest_0",
                        Wizardry.MODID + ":wizard_tower_chest_1",
                        Wizardry.MODID + ":wizard_tower_chest_2",
                        Wizardry.MODID + ":wizard_tower_chest_3"
                ), obj -> obj instanceof String);

        OBELISK_DIMENSIONS = BUILDER
                .comment("List of dimension ids in which obelisks will generate. Remove all dimensions to disable obelisks completely.")
                .translation("config." + Wizardry.MODID + ".obelisk_dimensions")
                .worldRestart()
                .defineList("obeliskDimensions", Arrays.asList(0, -1), obj -> obj instanceof Integer);

        OBELISK_RARITY = BUILDER
                .comment("Rarity of obelisks. 1 in this many chunks will contain an obelisk, meaning higher numbers are rarer.")
                .translation("config." + Wizardry.MODID + ".obelisk_rarity")
                .worldRestart()
                .defineInRange("obeliskRarity", 600, 20, 5000);

        OBELISK_FILES = BUILDER
                .comment("List of structure file locations for obelisks. One of these files will be randomly selected each time an obelisk is generated. File locations are of the format [mod id]:[filename], which refers to the file assets/[mod id]/structures/[filename].nbt. Duplicate entries are permitted, allowing for simple weighting without duplicating the structure files themselves. This list should not be empty; to disable obelisks, use the obelisk dimensions setting.")
                .translation("config." + Wizardry.MODID + ".obelisk_files")
                .worldRestart()
                .defineList("obeliskFiles", Arrays.asList(
                        Wizardry.MODID + ":obelisk_0",
                        Wizardry.MODID + ":obelisk_1",
                        Wizardry.MODID + ":obelisk_2",
                        Wizardry.MODID + ":obelisk_3",
                        Wizardry.MODID + ":obelisk_4"
                ), obj -> obj instanceof String);

        SHRINE_DIMENSIONS = BUILDER
                .comment("List of dimension ids in which shrines will generate. Remove all dimensions to disable shrines completely.")
                .translation("config." + Wizardry.MODID + ".shrine_dimensions")
                .worldRestart()
                .defineList("shrineDimensions", Arrays.asList(0, -1), obj -> obj instanceof Integer);

        SHRINE_RARITY = BUILDER
                .comment("Rarity of shrines. 1 in this many chunks will contain a shrine, meaning higher numbers are rarer.")
                .translation("config." + Wizardry.MODID + ".shrine_rarity")
                .worldRestart()
                .defineInRange("shrineRarity", 1100, 20, 5000);

        SHRINE_FILES = BUILDER
                .comment("List of structure file locations for shrines. One of these files will be randomly selected each time a shrine is generated. File locations are of the format [mod id]:[filename], which refers to the file assets/[mod id]/structures/[filename].nbt. Duplicate entries are permitted, allowing for simple weighting without duplicating the structure files themselves. This list should not be empty; to disable shrines, use the shrine dimensions setting.")
                .translation("config." + Wizardry.MODID + ".shrine_files")
                .worldRestart()
                .defineList("shrineFiles", Arrays.asList(
                        Wizardry.MODID + ":shrine_0",
                        Wizardry.MODID + ":shrine_1",
                        Wizardry.MODID + ":shrine_2",
                        Wizardry.MODID + ":shrine_3",
                        Wizardry.MODID + ":shrine_4",
                        Wizardry.MODID + ":shrine_5",
                        Wizardry.MODID + ":shrine_6",
                        Wizardry.MODID + ":shrine_7"
                ), obj -> obj instanceof String);

        SHRINE_REGENERATION_ENABLED = BUILDER
                .comment("Whether conquered shrines should regenerate after a period of time. When disabled, shrines remain conquered permanently.")
                .translation("config." + Wizardry.MODID + ".shrine_regeneration_enabled")
                .define("shrineRegenerationEnabled", true);

        SHRINE_REGENERATION_TIME = BUILDER
                .comment("Time in minutes for a conquered shrine to regenerate. Minimum 1 minute, maximum 1440 minutes (24 hours).")
                .translation("config." + Wizardry.MODID + ".shrine_regeneration_time")
                .defineInRange("shrineRegenerationTime", 1, 1, 1440);

        SHRINE_ALLOW_MULTIPLE_LOOT = BUILDER
                .comment("Whether players can loot shrines multiple times. If false, each player can only loot each shrine once until it regenerates.")
                .translation("config." + Wizardry.MODID + ".shrine_allow_multiple_loot")
                .define("shrineAllowMultipleLoot", false);

        LIBRARY_DIMENSIONS = BUILDER
                .comment("List of dimension ids in which library ruins will generate. Remove all dimensions to disable library ruins completely.")
                .translation("config." + Wizardry.MODID + ".library_dimensions")
                .worldRestart()
                .defineList("libraryDimensions", Arrays.asList(0), obj -> obj instanceof Integer);

        LIBRARY_RARITY = BUILDER
                .comment("Rarity of library ruins. 1 in this many chunks will contain a library ruin, meaning higher numbers are rarer.")
                .translation("config." + Wizardry.MODID + ".library_rarity")
                .worldRestart()
                .defineInRange("libraryRarity", 900, 20, 5000);

        LIBRARY_FILES = BUILDER
                .comment("List of structure file locations for surface library ruins. One of these files will be randomly selected each time a surface library ruin is generated. File locations are of the format [mod id]:[filename], which refers to the file assets/[mod id]/structures/[filename].nbt. Duplicate entries are permitted, allowing for simple weighting without duplicating the structure files themselves. This list should not be empty; to disable library ruins, use the library dimensions setting.")
                .translation("config." + Wizardry.MODID + ".library_files")
                .worldRestart()
                .defineList("libraryFiles", Arrays.asList(
                        Wizardry.MODID + ":library_ruins_0",
                        Wizardry.MODID + ":library_ruins_1",
                        Wizardry.MODID + ":library_ruins_2",
                        Wizardry.MODID + ":library_ruins_3"
                ), obj -> obj instanceof String);

        UNDERGROUND_LIBRARY_FILES = BUILDER
                .comment("List of structure file locations for underground library ruins. One of these files will be randomly selected each time an underground library ruin is generated. File locations are of the format [mod id]:[filename], which refers to the file assets/[mod id]/structures/[filename].nbt. Duplicate entries are permitted, allowing for simple weighting without duplicating the structure files themselves. This list should not be empty; to disable library ruins, use the library dimensions setting.")
                .translation("config." + Wizardry.MODID + ".underground_library_files")
                .worldRestart()
                .defineList("undergroundLibraryFiles", Arrays.asList(
                        Wizardry.MODID + ":underground_library_ruins_0",
                        Wizardry.MODID + ":underground_library_ruins_1",
                        Wizardry.MODID + ":underground_library_ruins_2",
                        Wizardry.MODID + ":underground_library_ruins_3"
                ), obj -> obj instanceof String);

        TREE_BLOCKS = BUILDER
                .comment("List of registry names of blocks which can be overwritten by wizardry's structure generators, affecting both fast and fancy structure generation. Most tree blocks and other foliage should work automatically, but those that don't can be added manually here. Block names are not case sensitive. For mod blocks, prefix with the mod ID (e.g. dynamictrees:oakbranch).")
                .translation("config." + Wizardry.MODID + ".tree_blocks")
                .worldRestart()
                .defineList("treeBlocks", Arrays.asList(DEFAULT_TREE_BLOCKS), obj -> obj instanceof String);

        ORE_DIMENSIONS = BUILDER
                .comment("List of dimension ids in which crystal ore will generate. Note that removing the overworld (id 0) from this list will make the mod VERY difficult to play!")
                .translation("config." + Wizardry.MODID + ".ore_dimensions")
                .worldRestart()
                .defineList("oreDimensions", Arrays.asList(0), obj -> obj instanceof Integer);

        FLOWER_DIMENSIONS = BUILDER
                .comment("List of dimension ids in which crystal flowers will generate.")
                .translation("config." + Wizardry.MODID + ".flower_dimensions")
                .worldRestart()
                .defineList("flowerDimensions", Arrays.asList(0), obj -> obj instanceof Integer);

        LOOT_INJECTION_LOCATIONS = BUILDER
                .comment("List of loot tables to inject wizardry loot (as specified in loot_tables/chests/dungeon_additions.json) into.")
                .translation("config." + Wizardry.MODID + ".loot_injection_locations")
                .worldRestart()
                .defineList("lootInjectionLocations", Arrays.asList(DEFAULT_LOOT_INJECTION_LOCATIONS), obj -> obj instanceof String);

        BUILDER.pop();
        //@formatter:on
        //endregion

        //region compatibility 设置
        //@formatter:off
        BUILDER.push(ConfigCategory.COMPATIBILITY_CATEGORY)
                .comment("Settings that affect how wizardry interacts with other mods. In multiplayer, the server/LAN host settings will apply.");

        DAMAGE_SOURCE_BLACKLIST = BUILDER
                .comment("List of damage source string identifiers to be ignored when re-applying damage. Case-sensitive. A message will be logged if wizardry detects a damage source that should be added to this list. Otherwise, don't change unless instructed to do so.")
                .translation("config." + Wizardry.MODID + ".damage_source_blacklist")
                .worldRestart()
                .defineList("damageSourceBlacklist", Collections.emptyList(), obj -> obj instanceof String);

        COMPATIBILITY_WARNINGS = BUILDER
                .comment("Whether to print compatibility warnings to the console. Set to false if excessive messages are being printed.")
                .translation("config." + Wizardry.MODID + ".compatibility_warnings")
                .define("compatibilityWarnings", true);

        BAUBLES_INTEGRATION = BUILDER
                .comment("If Baubles is installed, controls whether Baubles integration features are enabled. If this is disabled, wizardry will always behave as if Baubles is not installed.")
                .translation("config." + Wizardry.MODID + ".baubles_integration")
                .worldRestart()
                .define("baublesIntegration", true);

        JEI_INTEGRATION = BUILDER
                .comment("If JEI (Just Enough Items) is installed, controls whether JEI integration features are enabled. If this is disabled, wizardry will always behave as if JEI is not installed.")
                .translation("config." + Wizardry.MODID + ".jei_integration")
                .worldRestart()
                .define("jeiIntegration", true);

        ANTIQUE_ATLAS_INTEGRATION = BUILDER
                .comment("If Antique Atlas is installed, controls whether Antique Atlas integration features are enabled. If this is disabled, wizardry will always behave as if Antique Atlas is not installed.")
                .translation("config." + Wizardry.MODID + ".antique_atlas_integration")
                .worldRestart()
                .define("antiqueAtlasIntegration", true);

        AUTO_TOWER_MARKERS = BUILDER
                .comment("Controls whether wizardry automatically places antique atlas markers at the locations of wizard towers.")
                .translation("config." + Wizardry.MODID + ".auto_place_tower_markers")
                .worldRestart()
                .define("autoPlaceTowerMarkers", true);

        AUTO_OBELISK_MARKERS = BUILDER
                .comment("Controls whether wizardry automatically places antique atlas markers at the locations of obelisks.")
                .translation("config." + Wizardry.MODID + ".auto_place_obelisk_markers")
                .worldRestart()
                .define("autoPlaceObeliskMarkers", true);

        AUTO_SHRINE_MARKERS = BUILDER
                .comment("Controls whether wizardry automatically places antique atlas markers at the locations of shrines.")
                .translation("config." + Wizardry.MODID + ".auto_place_shrine_markers")
                .worldRestart()
                .define("autoPlaceShrineMarkers", true);

        AUTO_LIBRARY_MARKERS = BUILDER
                .comment("Controls whether wizardry automatically places antique atlas markers at the locations of library ruins.")
                .translation("config." + Wizardry.MODID + ".auto_place_library_markers")
                .worldRestart()
                .define("autoPlaceLibraryMarkers", true);

        AUTO_UNDERGROUND_LIBRARY_MARKERS = BUILDER
                .comment("Controls whether wizardry automatically places antique atlas markers at the locations of underground library ruins.")
                .translation("config." + Wizardry.MODID + ".auto_place_underground_library_markers")
                .worldRestart()
                .define("autoPlaceUndergroundLibraryMarkers", false);

        BUILDER.pop();
        //@formatter:on
        //endregion

        //region spells 设置
        //@formatter:off
        //@formatter:on
        //endregion
        
        SPEC = BUILDER.build();
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

    private static void valueChange() {
        noviceMaxCharge = NOVICE_MAX_CHARGE.get();
        apprenticeMaxCharge = APPRENTICE_MAX_CHARGE.get();
        advancedMaxCharge = ADVANCED_MAX_CHARGE.get();
        masterMaxCharge = MASTER_MAX_CHARGE.get();
        noviceUpgradeLimit = NOVICE_UPGRADE_LIMIT.get();
        apprenticeUpgradeLimit = APPRENTICE_UPGRADE_LIMIT.get();
        advancedUpgradeLimit = ADVANCED_UPGRADE_LIMIT.get();
        masterUpgradeLimit = MASTER_UPGRADE_LIMIT.get();
        progressionRequirements = PROGRESSION_REQUIREMENTS.get().stream().mapToInt(Integer::intValue).toArray();
        discoveryMode = DISCOVERY_MODE.get();
        fleshSpellsCauseSlowness = FLESH_SPELLS_CAUSE_SLOWNESS.get();
        ironFleshArmorBonus = IRON_FLESH_ARMOR_BONUS.get();
        bookshelfSearchRadius = BOOKSHELF_SEARCH_RADIUS.get();
        diamondFleshArmorBonus = DIAMOND_FLESH_ARMOR_BONUS.get();
        diamondFleshArmorToughnessBonus = DIAMOND_FLESH_ARMOR_TOUGHNESS_BONUS.get();
        oakFleshArmorBonus = OAK_FLESH_ARMOR_BONUS.get();
        oakFleshHealthBonus = OAK_FLESH_HEALTH_BONUS.get();
        condenserAmountMultiplier = CONDENSER_AMOUNT_MULTIPLIER.get();
        spellBookColorsRequireArchivistsEyeglass = SPELL_BOOK_COLORS_REQUIRE_ARCHIVISTS_EYEGLASS.get();
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
        Constants.blastRadiusIncreasePerLevel = BLAST_INCREASE_PER_LEVEL.get().floatValue();
        Constants.frostSlownessPerLevel = FROST_SLOWNESS_INCREASE_PER_LEVEL.get();

        baseSpellSlots = BASE_SPELL_SLOTS.get();
        bonemealGrowsCrystalFlowers = BONEMEAL_GROWS_CRYSTAL_FLOWERS.get();
        recentSpellExpiryTime = RECENT_SPELL_EXPIRY_TIME.get();
        legacyWandLevelling = LEGACY_WAND_LEVELLING.get();
        preventBindingSameSpellTwiceToWands = PREVENT_BINDING_SAME_SPELL_TWICE_TO_WANDS.get();
        singleUseSpellBooks = SINGLE_USE_SPELL_BOOKS.get();
        wandsMustBeHeldToDecrementCooldown = WANDS_MUST_BE_HELD_TO_DECREMENT_COOLDOWN.get();
        dispenserBlockDamage = DISPENSER_BLOCK_DAMAGE.get();
        playerBlockDamage = PLAYER_BLOCK_DAMAGE.get();
        playersMoveEachOther = PLAYERS_MOVE_EACH_OTHER.get();
        minionRevengeTargeting = MINION_REVENGE_TARGETING.get();
        summonedCreatureTargetsWhitelist = SUMMONED_CREATURE_TARGETS_WHITELIST.get().stream().map(Identifier::tryParse).filter(Objects::nonNull).collect(Collectors.toList());
        summonedCreatureTargetsBlacklist = SUMMONED_CREATURE_TARGETS_BLACKLIST.get().stream().map(Identifier::tryParse).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.@NonNull Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            Wizardry.LOGGER.info("Loaded {} config file: {}", Wizardry.MODID, event.getConfig().getFileName());
            SyncedConfig.valueChange();
        }
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.@NonNull Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            Wizardry.LOGGER.info("Reloaded {} config file: {}", Wizardry.MODID, event.getConfig().getFileName());
            SyncedConfig.valueChange();
        }
    }
}
