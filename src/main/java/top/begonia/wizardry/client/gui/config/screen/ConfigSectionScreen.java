package top.begonia.wizardry.client.gui.config.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.client.gui.config.utils.Context;
import top.begonia.wizardry.client.gui.config.utils.Filter;
import top.begonia.wizardry.client.gui.config.utils.UndoManager;

import java.util.HashMap;
import java.util.Map;

public class ConfigSectionScreen extends OptionsSubScreen {
    protected static final long MAX_SLIDER_SIZE = 256L;
    protected final Context context;
    protected boolean changed = false;
    protected ModConfigSpec.RestartType needsRestart = ModConfigSpec.RestartType.NONE;
    protected final Map<String, ConfigSectionScreen> sectionCache = new HashMap<>();
    @Nullable
    protected Button undoButton, resetButton; // must not be changed after creation unless the reference inside the layout also is replaced
    protected final Button doneButton = Button.builder(CommonComponents.GUI_DONE, _ -> onClose()).width(Button.SMALL_WIDTH).build();
    protected final UndoManager undoManager = new UndoManager();

    /**
     * 实际的入口
     *
     * @param context 上下文
     * @param title   标题
     */
    public ConfigSectionScreen(
            final @NonNull Context context,
            final Component title
    ) {
        super(context.parent(), Minecraft.getInstance().options, title);
        this.context = context;
    }

    public ConfigSectionScreen(
            final Screen parent,
            final ModConfig.Type type,
            final ModConfig modConfig,
            Component title,
            Filter filter
    ) {
        this(Context.top(modConfig.getModId(), parent, modConfig, filter), title);
        this.needsRestart = type == ModConfig.Type.STARTUP ? ModConfigSpec.RestartType.GAME : ModConfigSpec.RestartType.NONE;
    }

    public ConfigSectionScreen(
            final Screen parent,
            final ModConfig.Type type,
            final ModConfig modConfig,
            Component title
    ) {
        this(parent, type, modConfig, title, (_, _, original) -> original);
    }

    @Override
    protected void addOptions() {
        rebuild();
    }

    protected ConfigSectionScreen rebuild() {
        return this;
    }
}
