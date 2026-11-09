package top.begonia.wizardry.api.config.screen;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.realmsclient.RealmsMainScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.config.utils.BuiltInConfig;
import top.begonia.wizardry.api.config.utils.Context;
import top.begonia.wizardry.api.config.utils.TranslationChecker;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ConfigScreen extends OptionsSubScreen {
    protected final ModContainer mod;
    private final SectionFactory sectionScreen;
    protected static final TranslationChecker translationChecker = new TranslationChecker();
    public ModConfigSpec.RestartType needsRestart = ModConfigSpec.RestartType.NONE;
    private boolean autoClose = false;

    public ConfigScreen(
            final @NonNull ModContainer mod,
            final Screen parent
    ) {
        super(
                parent,
                Minecraft.getInstance().options,
                Component.translatable(
                        translationChecker.check(
                                mod.getModId() + ".configuration.title",
                                BuiltInConfig.LANG_PREFIX + "title"
                        ),
                        mod.getModInfo().getDisplayName()
                )
        );
        this.mod = mod;
        this.sectionScreen = ConfigSectionScreen::new;
    }

    private interface SectionFactory {
        Screen apply(
                @NonNull Context context,
                final ModConfig.Type type,
                Component title
        );
    }

    @Override
    protected void addOptions() {
        for (final ModConfig.Type type : ModConfig.Type.values()) {
            for (final ModConfig modConfig : ModConfigs.getConfigSet(type)) {
                if (modConfig.getModId().equals(mod.getModId())
                        && modConfig.getSpec() instanceof ModConfigSpec modConfigSpec
                        && modConfigSpec.isLoaded()
                ) {
                    Set<? extends UnmodifiableConfig.Entry> entries = modConfigSpec.getValues().entrySet();
                    Map<String, Object> valueSpecs = modConfigSpec.getSpec().valueMap();
                    for (UnmodifiableConfig.Entry entry : entries) {
                        String key = entry.getKey();
                        UnmodifiableConfig value = entry.getRawValue();
                        if (valueSpecs.get(key) instanceof UnmodifiableConfig subConfigMap) {
                            Set<? extends UnmodifiableConfig.Entry> subSection = value.entrySet();
                            Map<String, Object> subConfig = subConfigMap.valueMap();
                            Context context = new Context(
                                    mod.getModId(),
                                    this,
                                    modConfig,
                                    modConfigSpec,
                                    subSection,
                                    subConfig,
                                    List.of(key),
                                    (_, _, element) -> element
                            );
                            MutableComponent message = Component.translatableWithFallback(
                                    "config." + Wizardry.MODID + ".category." + key,
                                    key
                            );
                            MutableComponent subScreenTitle = message.copy();
                            Button btn = Button.builder(
                                    message,
                                    _ -> this.minecraft.gui.setScreen(this.sectionScreen.apply(context, type, subScreenTitle))
                            ).width(BuiltInConfig.BIG_BUTTON_WIDTH).build();
                            if (list != null) {
                                this.list.addSmall(btn, null);
                            }
                        }
                    }
                }
            }
        }
    }

    public Component translatableConfig(@NonNull ModConfig modConfig, String suffix, String fallback) {
        return Component.translatable(
                translationChecker.check(
                        mod.getModId()
                                + ".configuration.section."
                                + modConfig.getFileName()
                                .replaceAll("[^a-zA-Z0-9]+", ".")
                                .replaceFirst("^\\.", "")
                                .replaceFirst("\\.$", "")
                                .toLowerCase(Locale.ENGLISH) + suffix, fallback
                ),
                mod.getModInfo().getDisplayName()
        );
    }

    @Override
    public void added() {
        super.added();
        if (autoClose) {
            autoClose = false;
            onClose();
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void onClose() {
        translationChecker.finish();
        switch (needsRestart) {
            case GAME -> {
                minecraft.gui.setScreen(new TooltipConfirmScreen(b -> {
                    if (b) {
                        minecraft.stop();
                    } else {
                        super.onClose();
                    }
                }, BuiltInConfig.GAME_RESTART_TITLE, BuiltInConfig.GAME_RESTART_MESSAGE, BuiltInConfig.GAME_RESTART_YES, BuiltInConfig.RESTART_NO));
                return;
            }
            case WORLD -> {
                if (minecraft.level != null) {
                    minecraft.gui.setScreen(
                            new TooltipConfirmScreen(
                                    b -> {
                                        if (b) {
                                            onDisconnect();
                                        } else {
                                            super.onClose();
                                        }
                                    },
                                    BuiltInConfig.SERVER_RESTART_TITLE,
                                    BuiltInConfig.SERVER_RESTART_MESSAGE,
                                    minecraft.isLocalServer()
                                            ? BuiltInConfig.RETURN_TO_MENU
                                            : CommonComponents.GUI_DISCONNECT,
                                    BuiltInConfig.RESTART_NO
                            )
                    );
                    return;
                }
            }
        }
        super.onClose();
    }

    private void onDisconnect() {
        boolean flag = this.minecraft.isLocalServer();
        ServerData serverdata = this.minecraft.getCurrentServer();
        if (this.minecraft.level != null) {
            this.minecraft.level.disconnect(ClientLevel.DEFAULT_QUIT_MESSAGE);
        }
        if (flag) {
            this.minecraft.disconnectWithSavingScreen();
        } else {
            this.minecraft.disconnectWithProgressScreen();
        }

        TitleScreen titlescreen = new TitleScreen();
        if (flag) {
            this.minecraft.gui.setScreen(titlescreen);
        } else if (serverdata != null && serverdata.isRealm()) {
            this.minecraft.gui.setScreen(new RealmsMainScreen(titlescreen));
        } else {
            this.minecraft.gui.setScreen(new JoinMultiplayerScreen(titlescreen));
        }
    }
}
