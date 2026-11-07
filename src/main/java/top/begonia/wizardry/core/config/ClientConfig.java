package top.begonia.wizardry.core.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.constants.ConfigCategory;
import top.begonia.wizardry.api.constants.ElementEnum;
import top.begonia.wizardry.api.constants.GuiPosition;

@EventBusSubscriber(modid = Wizardry.MODID)
public final class ClientConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    public static final String DEFAULT_HUD_SKIN_KEY = "default";

    //region client 设置
    //@formatter:off
    public static boolean shiftScrolling = true;
    public static final ModConfigSpec.BooleanValue SHIFT_SCROLLING;

    public static boolean reverseScrollDirection = false;
    public static final ModConfigSpec.BooleanValue REVERSE_SCROLL_DIRECTION;

    public static boolean showSpellHUD = true;
    public static final ModConfigSpec.BooleanValue SHOW_SPELL_HUD;

    public static boolean showChargeMeter = true;
    public static final ModConfigSpec.BooleanValue SHOW_CHARGE_METER;

    public static boolean spellBookColors = true;
    public static final ModConfigSpec.BooleanValue SPELL_BOOK_COLORS;

    public static boolean loadHandbook = true;
    public static final ModConfigSpec.BooleanValue LOAD_HANDBOOK;

    public static boolean unfocusedSearchBars = false;
    public static final ModConfigSpec.BooleanValue UNFOCUSED_SEARCH_BARS;

    public static GuiPosition spellHUDPosition = GuiPosition.BOTTOM_LEFT;
    public static final ModConfigSpec.EnumValue<GuiPosition> SPELL_HUD_POSITION;

    public static String spellHUDSkin = DEFAULT_HUD_SKIN_KEY;
    public static final ModConfigSpec.ConfigValue<String> SPELL_HUD_SKIN;

    public static boolean handbookProgression = true;
    public static final ModConfigSpec.BooleanValue HANDBOOK_PROGRESSION;

    public static boolean booksPauseGame = true;
    public static final ModConfigSpec.BooleanValue BOOKS_PAUSE_GAME;

    public static boolean summonedCreatureNames = true;
    public static final ModConfigSpec.BooleanValue SUMMONED_CREATURE_NAMES;

    public static boolean useShaders = true;
    public static final ModConfigSpec.BooleanValue USE_SHADERS;

    public static boolean screenShake = true;
    public static final ModConfigSpec.BooleanValue SCREEN_SHAKE;

    public static boolean blinkEffect = true;
    public static final ModConfigSpec.BooleanValue BLINK_EFFECT;

    public static boolean spellcastingAnimations = true;
    public static final ModConfigSpec.BooleanValue SPELLCASTING_ANIMATIONS;

    public static ElementEnum donationPerkElement = ElementEnum.MAGIC;
    public static final ModConfigSpec.EnumValue<ElementEnum> DONATION_PERK_ELEMENT;
    //@formatter:on
    //endregion

    static {
        //region client 设置
        //@formatter:off
        BUILDER.push(ConfigCategory.CLIENT_CATEGORY)
                .comment("Client-side settings that only affect the local minecraft game. If this file is on a dedicated server, these settings will have no effect; in multiplayer, each player obeys their own settings.");
        SHIFT_SCROLLING = BUILDER
                .comment("Whether you can switch between spells on a wand by scrolling with the mouse wheel while sneaking. Note that this will only affect you; other players connected to the same server obey their own settings.")
                .translation("config." + Wizardry.MODID + ".shift_scrolling")
                .define("shiftScrolling", true);

        REVERSE_SCROLL_DIRECTION = BUILDER
                .comment("The scroll direction used to switch between spells on a wand while sneaking.")
                .translation("config." + Wizardry.MODID + ".reverse_scroll_direction")
                .define("reverseScrollDirection", false);

        SHOW_SPELL_HUD = BUILDER
                .comment("Whether to show the spell HUD in the corner of the screen when holding a wand.")
                .translation("config." + Wizardry.MODID + ".show_spell_hud")
                .define("showSpellHUD", true);

        SHOW_CHARGE_METER = BUILDER
                .comment("Whether to show the spell charge-up meter around the crosshairs when charging up a spell.")
                .translation("config." + Wizardry.MODID + ".show_charge_meter")
                .define("showChargeMeter", true);

        SPELL_BOOK_COLORS = BUILDER
                .comment("Whether to show elemental colors on spell books for discovered spells.")
                .translation("config." + Wizardry.MODID + ".spell_book_colors")
                .define("spellBookColors", true);

        LOAD_HANDBOOK = BUILDER
                .comment("Whether to initialise the in-game handbook. Setting this to false will brick the in-game handbook, but it might help if you have startup crashes.")
                .translation("config." + Wizardry.MODID + ".load_handbook")
                .define("loadHandbook", true);

        UNFOCUSED_SEARCH_BARS = BUILDER
                .comment("Whether to allow the Arcane Workbench and lectern search field to lose focus and start unfocused. If true, the search field won't automatically capture keyboard input.")
                .translation("config." + Wizardry.MODID + ".unfocused_search_bars")
                .define("unfocusedSearchBars", false);

        SPELL_HUD_POSITION = BUILDER
                .comment("The position of the spell HUD.")
                .translation("config." + Wizardry.MODID + ".spell_hud_position")
                .defineEnum("spellHUDPosition", GuiPosition.BOTTOM_LEFT);

        SPELL_HUD_SKIN = BUILDER
                .comment("The skin used for the spell HUD.")
                .translation("config." + Wizardry.MODID + ".spell_hud_skin")
                .define("spellHUDSkin", DEFAULT_HUD_SKIN_KEY);

        HANDBOOK_PROGRESSION = BUILDER
                .comment("When set to true, sections of The Wizard's Handbook are unlocked when a player gains the advancement that triggers them, and are hidden otherwise. When set to false, the entire handbook is readable regardless of advancement progress.")
                .translation("config." + Wizardry.MODID + ".handbook_progression")
                .define("handbookProgression", true);

        BOOKS_PAUSE_GAME = BUILDER
                .comment("Whether opening any of wizardry's books pauses the game in singleplayer. Has no effect on servers or LAN worlds.")
                .translation("config." + Wizardry.MODID + ".books_pause_game")
                .define("booksPauseGame", true);

        SUMMONED_CREATURE_NAMES = BUILDER
                .comment("Whether to show summoned creatures' names and owners above their heads.")
                .translation("config." + Wizardry.MODID + ".summoned_creature_names")
                .define("summonedCreatureNames", true);

        USE_SHADERS = BUILDER
                .comment("Whether to use custom shaders for certain spells. These use the vanilla shader system (like mob spectating shaders) and shouldn't have much of an effect on performance in most cases, but they may conflict with other shaders.")
                .translation("config." + Wizardry.MODID + ".use_shaders")
                .define("useShaders", true);

        SCREEN_SHAKE = BUILDER
                .comment("Whether to use the screen shake effect for certain spells.")
                .translation("config." + Wizardry.MODID + ".screen_shake")
                .define("screenShake", true);

        BLINK_EFFECT = BUILDER
                .comment("Whether to use the screen blink effect for teleportation spells.")
                .translation("config." + Wizardry.MODID + ".blink_effect")
                .define("blinkEffect", true);

        SPELLCASTING_ANIMATIONS = BUILDER
                .comment("Whether to use custom animations for players casting spells. Disable this option if it conflicts with other mods that add player animations.")
                .translation("config." + Wizardry.MODID + ".spellcasting_animations")
                .define("spellcastingAnimations", true);

        DONATION_PERK_ELEMENT = BUILDER
                .comment("The element of the flying companion orb rendered on donor and contributor players, leave empty to disable the effect. If you're one of them, this setting will change how all players see you (if not, it won't do anything).")
                .translation("config." + Wizardry.MODID + ".donation_perk_element")
                .defineEnum("donationPerkElement", ElementEnum.MAGIC);
        BUILDER.pop();
        //@formatter:on
        //endregion
        SPEC = BUILDER.build();
    }

    private static void valueChange() {
        //region client 设置
        //@formatter:off
        shiftScrolling = SHIFT_SCROLLING.get();
        reverseScrollDirection = REVERSE_SCROLL_DIRECTION.get();
        showSpellHUD = SHOW_SPELL_HUD.get();
        showChargeMeter = SHOW_CHARGE_METER.get();
        spellBookColors = SPELL_BOOK_COLORS.get();
        loadHandbook = LOAD_HANDBOOK.get();
        unfocusedSearchBars = UNFOCUSED_SEARCH_BARS.get();
        spellHUDPosition = SPELL_HUD_POSITION.get();
        spellHUDSkin = SPELL_HUD_SKIN.get();
        handbookProgression = HANDBOOK_PROGRESSION.get();
        booksPauseGame = BOOKS_PAUSE_GAME.get();
        summonedCreatureNames = SUMMONED_CREATURE_NAMES.get();
        useShaders = USE_SHADERS.get();
        screenShake = SCREEN_SHAKE.get();
        blinkEffect = BLINK_EFFECT.get();
        spellcastingAnimations = SPELLCASTING_ANIMATIONS.get();
        donationPerkElement = DONATION_PERK_ELEMENT.get();
        //@formatter:on
        //endregion
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
