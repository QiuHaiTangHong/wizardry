package top.begonia.wizardry.api.client.config.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.client.config.utils.ConfigShared;
import top.begonia.wizardry.api.client.config.utils.UndoManager;

public class SelectHUDSkinScreen extends Screen {
    protected final HeaderAndFooterLayout layout;
    private final Screen parentScreen;
    protected final SkinSelectionList skinSelectionList;
    protected final Button undoButton, resetButton, done;
    protected final UndoManager undoManager;
    protected final ModConfigSpec.ConfigValue<String> skinValue;
    protected final ModConfigSpec.ValueSpec skinSpec;

    public SelectHUDSkinScreen(
            @NonNull Screen parentScreen,
            Component title,
            ModConfigSpec.ValueSpec skinSpec,
            ModConfigSpec.ConfigValue<String> skinValue,
            UndoManager undoManager
    ) {
        super(title);
        this.parentScreen = parentScreen;
        this.undoManager = undoManager;
        this.skinSpec = skinSpec;
        this.skinValue = skinValue;
        this.layout = new HeaderAndFooterLayout(this);
        this.skinSelectionList = new SkinSelectionList(
                this.minecraft,
                entry -> this.undoManager.add(
                        this.skinValue::set,
                        entry.getKey(),
                        this.skinValue::set,
                        this.skinValue.getRaw()
                ),
                this.skinValue.get(),
                this.layout.getWidth(),
                this.layout.getContentHeight(),
                18
        );
        this.undoButton = Button.builder(
                        ConfigShared.UNDO,
                        _ -> {
                            this.undoManager.undo();
                            this.skinSelectionList.setFocused(this.skinSelectionList.toEntry(this.skinValue.get()));
                        }
                ).tooltip(Tooltip.create(ConfigShared.UNDO_TOOLTIP))
                .width(Button.SMALL_WIDTH)
                .build();
        this.undoButton.active = false;
        this.resetButton = Button.builder(
                        ConfigShared.RESET,
                        _ -> {
                            if (
                                    ConfigShared.isNonDefault(this.skinValue)
                            ) {
                                this.undoManager.add(
                                        this.skinValue::set,
                                        this.skinValue.getDefault(),
                                        this.skinValue::set,
                                        this.skinValue.getRaw()
                                );
                                this.skinSelectionList.setFocused(this.skinSelectionList.toEntry(this.skinValue.get()));
                            }
                        }
                ).tooltip(Tooltip.create(ConfigShared.RESET_TOOLTIP))
                .width(Button.SMALL_WIDTH)
                .build();
        this.resetButton.active = false;
        this.done = Button.builder(
                        CommonComponents.GUI_DONE,
                        (_) -> this.onClose()
                )
                .width(Button.SMALL_WIDTH)
                .build();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.setUndoButtonState(this.undoManager.canUndo());
        this.setResetButtonState(ConfigShared.isNonDefault(this.skinValue));
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    protected void setUndoButtonState(boolean state) {
        if (this.undoButton != null) {
            this.undoButton.active = state;
        }
    }

    protected void setResetButtonState(boolean state) {
        if (this.resetButton != null) {
            this.resetButton.active = state;
        }
    }

    @Override
    public void onClose() {
        if (this.parentScreen instanceof ConfigSectionScreen screen) {
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

    @Override
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
