package top.begonia.wizardry.core.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.constants.GuiPosition;

@EventBusSubscriber(modid = Wizardry.MODID)
public final class ClientConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue BOOKS_PAUSE_GAME;
    public static final ModConfigSpec.BooleanValue UNFOCUSED_SEARCH_BARS;
    public static final ModConfigSpec.BooleanValue SHOW_SPELL_HUD;
    public static final ModConfigSpec.BooleanValue SHOW_CHARGE_METER;
    public static final ModConfigSpec.EnumValue<GuiPosition> SPELL_HUD_POSITION;
    public static final String DEFAULT_HUD_SKIN_KEY = "default";
    public static final ModConfigSpec.ConfigValue<String> SPELL_HUD_SKIN;

    static {
        BUILDER.push("Client Settings");
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
                .translation("config." + Wizardry.MODID + ".unfocused_search_bars")
                .defineEnum("spellHUDPosition", GuiPosition.BOTTOM_LEFT);
        SPELL_HUD_SKIN = BUILDER
                .comment("The skin used for the spell HUD.")
                .translation("config." + Wizardry.MODID + ".spell_hud_skin")
                .define("spellHUDSkin", DEFAULT_HUD_SKIN_KEY);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static boolean booksPauseGame;
    public static boolean unfocusedSearchBars;
    public static boolean showSpellHUD;
    public static boolean showChargeMeter;
    public static GuiPosition spellHUDPosition;
    public static String spellHUDSkin = DEFAULT_HUD_SKIN_KEY;

    private static void valueChange() {
        booksPauseGame = BOOKS_PAUSE_GAME.get();
        unfocusedSearchBars = UNFOCUSED_SEARCH_BARS.get();
        showSpellHUD = SHOW_SPELL_HUD.get();
        showChargeMeter = SHOW_CHARGE_METER.get();
        spellHUDPosition = SPELL_HUD_POSITION.get();
        spellHUDSkin = SPELL_HUD_SKIN.get();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            Wizardry.LOGGER.info("Loaded {} config file: {}", Wizardry.MODID, event.getConfig().getFileName());
            ClientConfig.valueChange();
        }
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            Wizardry.LOGGER.info("Reloaded {} config file: {}", Wizardry.MODID, event.getConfig().getFileName());
            ClientConfig.valueChange();
        }
    }
}
