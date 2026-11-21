package top.begonia.wizardry.api.client.config.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.client.config.utils.BuiltInConfig;
import top.begonia.wizardry.api.client.config.utils.Context;
import top.begonia.wizardry.api.client.config.utils.UndoManager;

public class SelectHUDSkinScreen extends Screen {
    private final Context context;
    protected final HeaderAndFooterLayout layout;
    private final SkinSelectionList skinSelectionList;
    protected final Button undoButton, resetButton, done;
    protected final String key;
    protected final UndoManager undoManager;
    protected final ModConfigSpec.ConfigValue<String> skinValue;
    protected final ModConfigSpec.ValueSpec skinSpec;

    public SelectHUDSkinScreen(
            @NonNull Context context,
            Component title,
            String key,
            ModConfigSpec.ValueSpec skinSpec,
            ModConfigSpec.ConfigValue<String> skinValue
    ) {
        super(title);
        this.context = context;
        this.key = key;
        this.undoManager = new UndoManager();
        this.skinSpec = skinSpec;
        this.skinValue = skinValue;
        this.layout = new HeaderAndFooterLayout(this);
        this.undoButton = Button.builder(
                        BuiltInConfig.UNDO,
                        _ -> this.undoManager.undo()
                ).tooltip(Tooltip.create(BuiltInConfig.UNDO_TOOLTIP))
                .width(Button.SMALL_WIDTH)
                .build();
        this.undoButton.active = false;
        this.resetButton = Button.builder(
                        BuiltInConfig.RESET,
                        _ -> this.undoManager.redo()
                ).tooltip(Tooltip.create(BuiltInConfig.RESET_TOOLTIP))
                .width(Button.SMALL_WIDTH)
                .build();
        this.resetButton.active = false;
        this.done = Button.builder(
                        CommonComponents.GUI_DONE,
                        (_) -> this.onClose()
                )
                .width(Button.SMALL_WIDTH)
                .build();
        this.skinSelectionList = new SkinSelectionList(
                this.minecraft,
                this,
                this.layout.getWidth(),
                this.layout.getContentHeight(),
                18
        );
    }

    @Override
    public void onClose() {
        if (this.context.parent() instanceof ConfigSectionScreen screen) {
            this.skinValue.save();
            this.minecraft.gui.setScreen(screen.rebuild());
        } else {
            this.minecraft.gui.setScreen(null);
        }
    }

    @Override
    protected void init() {
        this.addTitle();
        this.addContents();
        this.addFooter();
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
        this.skinSelectionList.updateSize(this.width, this.layout);
    }

    protected void addContents() {
        this.layout.addToContents(this.skinSelectionList);
    }

    protected void addTitle() {
        this.layout.addTitleHeader(this.title, this.font);
    }

    protected void addFooter() {
        LinearLayout linearlayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        if (this.undoButton != null) {
            linearlayout.addChild(this.undoButton);
        }
        if (this.resetButton != null) {
            linearlayout.addChild(this.resetButton);
        }
        if (this.done != null) {
            linearlayout.addChild(this.done);
        }
    }
}
