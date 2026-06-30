package top.begonia.wizardry.core.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

import java.util.List;

@EventBusSubscriber(modid = Wizardry.MODID)
public final class CommonConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.IntValue NOVICE_MAX_CHARGE;
    private static final ModConfigSpec.IntValue APPRENTICE_MAX_CHARGE;
    private static final ModConfigSpec.IntValue ADVANCED_MAX_CHARGE;
    private static final ModConfigSpec.IntValue MASTER_MAX_CHARGE;

    private static final ModConfigSpec.IntValue NOVICE_UPGRADE_LIMIT;
    private static final ModConfigSpec.IntValue APPRENTICE_UPGRADE_LIMIT;
    private static final ModConfigSpec.IntValue ADVANCED_UPGRADE_LIMIT;
    private static final ModConfigSpec.IntValue MASTER_UPGRADE_LIMIT;

    private static final ModConfigSpec.BooleanValue DISCOVERY_MODE;

    private static final ModConfigSpec.ConfigValue<List<? extends Integer>> PROGRESSION_REQUIREMENTS;

    private static final ModConfigSpec.BooleanValue FLESH_SPELLS_CAUSE_SLOWNESS;
    private static final ModConfigSpec.DoubleValue IRON_FLESH_ARMOR_BONUS;
    private static final ModConfigSpec.IntValue BOOKSHELF_SEARCH_RADIUS;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> BOOKSHELF_BLOCKS;

    static {
        BUILDER.push("General Settings");
        NOVICE_MAX_CHARGE = BUILDER
                .translation("config." + Wizardry.MODID + ".novice_max_charge")
                .comment("Maximum mana a novice wand can store.")
                .worldRestart()
                .defineInRange("noviceMaxCharge", 700, 1, Integer.MAX_VALUE);
        APPRENTICE_MAX_CHARGE = BUILDER
                .translation("config." + Wizardry.MODID + ".apprentice_max_charge")
                .comment("Maximum mana an apprentice wand can store.")
                .worldRestart()
                .defineInRange("apprenticeMaxCharge", 1000, 1, Integer.MAX_VALUE);
        ADVANCED_MAX_CHARGE = BUILDER
                .translation("config." + Wizardry.MODID + ".advanced_max_charge")
                .comment("Maximum mana an advanced wand can store.")
                .worldRestart()
                .defineInRange("advancedMaxCharge", 1500, 1, Integer.MAX_VALUE);
        MASTER_MAX_CHARGE = BUILDER
                .translation("config." + Wizardry.MODID + ".master_max_charge")
                .comment("Maximum mana a master wand can store.")
                .worldRestart()
                .defineInRange("masterMaxCharge", 2500, 1, Integer.MAX_VALUE);

        NOVICE_UPGRADE_LIMIT = BUILDER
                .translation("config." + Wizardry.MODID + ".novice_upgrade_limit")
                .comment("Maximum number of upgrades a novice wand can have.")
                .worldRestart()
                .defineInRange("noviceUpgradeLimit", 3, 0, 100);
        APPRENTICE_UPGRADE_LIMIT = BUILDER
                .translation("config." + Wizardry.MODID + ".apprentice_upgrade_limit")
                .comment("Maximum number of upgrades an apprentice wand can have.")
                .worldRestart()
                .defineInRange("apprenticeUpgradeLimit", 5, 0, 100);
        ADVANCED_UPGRADE_LIMIT = BUILDER
                .translation("config." + Wizardry.MODID + ".advanced_upgrade_limit")
                .comment("Maximum number of upgrades an advanced wand can have.")
                .worldRestart()
                .defineInRange("advancedUpgradeLimit", 7, 0, 100);
        MASTER_UPGRADE_LIMIT = BUILDER
                .translation("config." + Wizardry.MODID + ".master_upgrade_limit")
                .comment("Maximum number of upgrades a master wand can have.")
                .worldRestart()
                .defineInRange("masterUpgradeLimit", 9, 0, 100);

        PROGRESSION_REQUIREMENTS = BUILDER
                .comment("The amount of progression required to upgrade a wand to each tier (apprentice, advanced and master respectively).")
                .translation("config." + Wizardry.MODID + ".progression_requirements")
                .worldRestart()
                .defineList("progressionRequirements", List.of(1500, 3500, 6000), o -> o instanceof Integer);

        DISCOVERY_MODE = BUILDER
                .comment("For those who like a sense of mystery! When set to true, spells you haven't cast yet will be unreadable until you cast them (on a per-world basis). Has no effect when in creative mode. Spells of identification will be unobtainable in survival mode if this is false.")
                .translation("config." + Wizardry.MODID + ".discovery_mode")
                .worldRestart()
                .define("discoveryMode", true);

        FLESH_SPELLS_CAUSE_SLOWNESS = BUILDER
                .comment("Whether flesh spells (DiamondFlesh, IronFlesh, OakFlesh) apply slowness. When disabled, these spells only provide their defensive benefits without movement penalty.")
                .translation("config." + Wizardry.MODID + ".flesh_spells_cause_slowness")
                .worldRestart()
                .define("fleshSpellsCauseSlowness", true);

        IRON_FLESH_ARMOR_BONUS = BUILDER
                .comment("Armor bonus provided by the IronFlesh spell.")
                .translation("config." + Wizardry.MODID + ".iron_flesh_armor_bonus")
                .worldRestart()
                .defineInRange("ironFleshArmorBonus", 4.0, 0.0, Double.MAX_VALUE);
        BOOKSHELF_SEARCH_RADIUS = BUILDER
                .translation("config." + Wizardry.MODID + ".bookshelf_search_radius")
                .comment("The maximum number of blocks a bookshelf can be from an arcane workbench or lectern to be able to link to it.")
                .worldRestart()
                .defineInRange("bookshelfSearchRadius", 4, 1, 10);
        BOOKSHELF_BLOCKS = BUILDER
                .translation("config." + Wizardry.MODID + ".bookshelf_blocks")
                .comment("List of registry names of blocks that count as bookshelves for the arcane workbench and lectern. Block names are not case sensitive. For mod blocks, prefix with the mod ID (e.g. " + Wizardry.MODID + ":oak_bookshelf).")
                .worldRestart()
                .defineList("bookshelfBlocks", List.of("oak", "spruce", "birch", "jungle", "acacia", "dark_oak"), o -> o instanceof String);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static int noviceMaxCharge;
    public static int apprenticeMaxCharge;
    public static int advancedMaxCharge;
    public static int masterMaxCharge;

    public static int noviceUpgradeLimit;
    public static int apprenticeUpgradeLimit;
    public static int advancedUpgradeLimit;
    public static int masterUpgradeLimit;

    public static int[] progressionRequirements;
    public static boolean discoveryMode;

    public static boolean fleshSpellsCauseSlowness;

    public static double ironFleshArmorBonus;

    public static int bookshelfSearchRadius;

    public static List<? extends String> bookshelfBlocks;

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
        bookshelfBlocks = BOOKSHELF_BLOCKS.get();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.@NonNull Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            Wizardry.LOGGER.info("Loaded {} config file: {}", Wizardry.MODID, event.getConfig().getFileName());
            CommonConfig.valueChange();
        }
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.@NonNull Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            Wizardry.LOGGER.info("Reloaded {} config file: {}", Wizardry.MODID, event.getConfig().getFileName());
            CommonConfig.valueChange();
        }
    }
}
