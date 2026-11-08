package top.begonia.wizardry.api.config.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class BuiltInConfig {
    /**
     * Prefix for static keys the configuration screens use internally.
     */
    public static final String LANG_PREFIX = "neoforge.configuration.uitext.";
    /**
     * A wrapper for the labels of buttons that open a new screen. Default: "%s..."
     */
    public static final String SECTION = LANG_PREFIX + "section";
    /**
     * A default for the labels of buttons that open a new screen. Default: "Edit"
     */
    public static final String SECTION_TEXT = LANG_PREFIX + "sectiontext";
    /**
     * The breadcrumb separator. Default: "%s > %s"
     */
    public static final Component CRUMB_SEPARATOR = Component.translatable(LANG_PREFIX + "breadcrumb.separator").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
    public static final String CRUMB = LANG_PREFIX + "breadcrumb.order";
    /**
     * The label of list elements. Will be supplied the index into the list. Default: "%s:"
     */
    public static final String LIST_ELEMENT = LANG_PREFIX + "listelement";
    /**
     * How the range will be added to the tooltip when using translated tooltips. Mimics what the comment does in ModConfigSpec.
     */
    public static final String RANGE_TOOLTIP = LANG_PREFIX + "rangetooltip";
    public static final ChatFormatting RANGE_TOOLTIP_STYLE = ChatFormatting.GRAY;
    /**
     * How the filename will be added to the tooltip.
     */
    public static final String FILENAME_TOOLTIP = LANG_PREFIX + "filenametooltip";
    public static final ChatFormatting FILENAME_TOOLTIP_STYLE = ChatFormatting.GRAY;
    /**
     * A literal to create an empty line in a tooltip.
     */
    public static final MutableComponent EMPTY_LINE = Component.literal("\n\n");

    public static final Component TOOLTIP_CANNOT_EDIT_THIS_WHILE_ONLINE = Component.translatable(LANG_PREFIX + "notonline").withStyle(ChatFormatting.RED);
    public static final Component TOOLTIP_CANNOT_EDIT_THIS_WHILE_OPEN_TO_LAN = Component.translatable(LANG_PREFIX + "notlan").withStyle(ChatFormatting.RED);
    public static final Component TOOLTIP_CANNOT_EDIT_NOT_LOADED = Component.translatable(LANG_PREFIX + "notloaded").withStyle(ChatFormatting.RED);
    public static final Component NEW_LIST_ELEMENT = Component.translatable(LANG_PREFIX + "newlistelement");
    public static final Component MOVE_LIST_ELEMENT_UP = Component.translatable(LANG_PREFIX + "listelementup");
    public static final Component MOVE_LIST_ELEMENT_DOWN = Component.translatable(LANG_PREFIX + "listelementdown");
    public static final Component REMOVE_LIST_ELEMENT = Component.translatable(LANG_PREFIX + "listelementremove");
    public static final Component UNSUPPORTED_ELEMENT = Component.translatable(LANG_PREFIX + "unsupportedelement").withStyle(ChatFormatting.RED);
    public static final Component LONG_STRING = Component.translatable(LANG_PREFIX + "longstring").withStyle(ChatFormatting.RED);
    public static final Component GAME_RESTART_TITLE = Component.translatable(LANG_PREFIX + "restart.game.title");
    public static final Component GAME_RESTART_MESSAGE = Component.translatable(LANG_PREFIX + "restart.game.text");
    public static final Component GAME_RESTART_YES = Component.translatable("menu.quit"); // TitleScreen.init() et.al.
    public static final Component SERVER_RESTART_TITLE = Component.translatable(LANG_PREFIX + "restart.server.title");
    public static final Component SERVER_RESTART_MESSAGE = Component.translatable(LANG_PREFIX + "restart.server.text");
    public static final Component RETURN_TO_MENU = Component.translatable("menu.returnToMenu"); // PauseScreen.RETURN_TO_MENU
    public static final Component SAVING_LEVEL = Component.translatable("menu.savingLevel"); // PauseScreen.SAVING_LEVEL
    public static final Component RESTART_NO = Component.translatable(LANG_PREFIX + "restart.return");
    public static final Component RESTART_NO_TOOLTIP = Component.translatable(LANG_PREFIX + "restart.return.tooltip").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
    public static final Component UNDO = Component.translatable(LANG_PREFIX + "undo");
    public static final Component UNDO_TOOLTIP = Component.translatable(LANG_PREFIX + "undo.tooltip");
    public static final Component RESET = Component.translatable(LANG_PREFIX + "reset");
    public static final Component RESET_TOOLTIP = Component.translatable(LANG_PREFIX + "reset.tooltip");
    public static final int BIG_BUTTON_WIDTH = 310;
}
