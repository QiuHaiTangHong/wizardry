package top.begonia.wizardry.core.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.constants.ConfigCategory;
import top.begonia.wizardry.core.constants.GuiPosition;

@EventBusSubscriber(modid = Wizardry.MODID)
public final class ClientConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    public static final String DEFAULT_HUD_SKIN_KEY = "default";
    private static final ModConfigSpec.BooleanValue BOOKS_PAUSE_GAME;
    private static final ModConfigSpec.BooleanValue UNFOCUSED_SEARCH_BARS;
    private static final ModConfigSpec.BooleanValue SHOW_SPELL_HUD;
    private static final ModConfigSpec.BooleanValue SHOW_CHARGE_METER;
    private static final ModConfigSpec.EnumValue<GuiPosition> SPELL_HUD_POSITION;
    private static final ModConfigSpec.ConfigValue<String> SPELL_HUD_SKIN;
    private static final ModConfigSpec.BooleanValue SHIFT_SCROLLING;
    private static final ModConfigSpec.BooleanValue REVERSE_SCROLL_DIRECTION;
    private static final ModConfigSpec.BooleanValue SUMMONED_CREATURE_NAMES;
    public static final ModConfigSpec.BooleanValue SPELL_BOOK_COLORS;
    public static boolean spellBookColors;
    public static boolean booksPauseGame;
    public static boolean unfocusedSearchBars;
    public static boolean showSpellHUD;
    public static boolean showChargeMeter;
    public static GuiPosition spellHUDPosition;
    public static String spellHUDSkin = DEFAULT_HUD_SKIN_KEY;
    public static boolean summonedCreatureNames;
    public static boolean shiftScrolling;
    public static boolean reverseScrollDirection;

    static {
        BUILDER
                .translation("config." + Wizardry.MODID + ".category." + ConfigCategory.CLIENT_CATEGORY)
                .comment("Client-side settings that only affect the local minecraft game. If this file is on a dedicated server, these settings will have no effect; in multiplayer, each player obeys their own settings.")
                .push(ConfigCategory.CLIENT_CATEGORY);
        BOOKS_PAUSE_GAME = BUILDER
                .comment("Whether opening any of wizardry's books pauses the game in singleplayer. Has no effect on servers or LAN worlds.")
                .translation("config." + Wizardry.MODID + ".books_pause_game")
                .define("booksPauseGame", true);
        UNFOCUSED_SEARCH_BARS = BUILDER
                .comment("Whether to allow the Arcane Workbench and lectern search field to lose focus and start unfocused. If true, the search field won't automatically capture keyboard input.")
                .translation("config." + Wizardry.MODID + ".unfocused_search_bars")
                .define("unfocusedSearchBars", false);
        SHOW_SPELL_HUD = BUILDER
                .comment("Whether to show the spell HUD in the corner of the screen when holding a wand.")
                .translation("config." + Wizardry.MODID + ".show_spell_hud")
                .define("showSpellHUD", true);
        SHOW_CHARGE_METER = BUILDER
                .comment("Whether to show the spell charge-up meter around the crosshairs when charging up a spell.")
                .translation("config." + Wizardry.MODID + ".show_charge_meter")
                .define("showChargeMeter", false);
        SPELL_HUD_POSITION = BUILDER
                .comment("The position of the spell HUD.")
                .translation("config." + Wizardry.MODID + ".spell_hud_position")
                .defineEnum("spellHUDPosition", GuiPosition.BOTTOM_LEFT);
        SPELL_HUD_SKIN = BUILDER
                .comment("The skin used for the spell HUD.")
                .translation("config." + Wizardry.MODID + ".spell_hud_skin")
                .define("spellHUDSkin", DEFAULT_HUD_SKIN_KEY);
        SHIFT_SCROLLING = BUILDER
                .comment("Whether you can switch between spells on a wand by scrolling with the mouse wheel while sneaking. Note that this will only affect you; other players connected to the same server obey their own settings.")
                .translation("config." + Wizardry.MODID + ".shift_scrolling")
                .define("shiftScrolling", true);
        REVERSE_SCROLL_DIRECTION = BUILDER
                .comment("The scroll direction used to switch between spells on a wand while sneaking.")
                .translation("config." + Wizardry.MODID + ".reverse_scroll_direction")
                .define("reverseScrollDirection", false);
        SUMMONED_CREATURE_NAMES = BUILDER
                .comment("Whether to show summoned creatures' names and owners above their heads.")
                .translation("config." + Wizardry.MODID + ".summoned_creature_names")
                .define("summonedCreatureNames", true);
        SPELL_BOOK_COLORS = BUILDER
                .comment("Whether to show elemental colors on spell books for discovered spells.")
                .translation("config." + Wizardry.MODID + ".spell_book_colors")
                .define("spellBookColors", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private static void valueChange() {
        booksPauseGame = BOOKS_PAUSE_GAME.get();
        unfocusedSearchBars = UNFOCUSED_SEARCH_BARS.get();
        showSpellHUD = SHOW_SPELL_HUD.get();
        showChargeMeter = SHOW_CHARGE_METER.get();
        spellHUDPosition = SPELL_HUD_POSITION.get();
        spellHUDSkin = SPELL_HUD_SKIN.get();
        summonedCreatureNames = SUMMONED_CREATURE_NAMES.get();
        spellBookColors = SPELL_BOOK_COLORS.get();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.@NonNull Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            Wizardry.LOGGER.info("Loaded {} config file: {}", Wizardry.MODID, event.getConfig().getFileName());
            ClientConfig.valueChange();
        }
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.@NonNull Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            Wizardry.LOGGER.info("Reloaded {} config file: {}", Wizardry.MODID, event.getConfig().getFileName());
            ClientConfig.valueChange();
        }
    }
}
