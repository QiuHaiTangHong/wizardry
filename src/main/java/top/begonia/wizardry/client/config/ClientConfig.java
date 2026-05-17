package top.begonia.wizardry.client.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import top.begonia.wizardry.Wizardry;

@EventBusSubscriber(modid = Wizardry.MODID)
public class ClientConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue BOOKS_PAUSE_GAME;
    public static final ModConfigSpec.BooleanValue UNFOCUSED_SEARCH_BARS;

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
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static boolean booksPauseGame;
    public static boolean unfocusedSearchBars;

    private static void valueChange() {
        booksPauseGame = BOOKS_PAUSE_GAME.get();
        unfocusedSearchBars = UNFOCUSED_SEARCH_BARS.get();
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
