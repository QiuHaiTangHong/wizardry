package top.begonia.wizardry.api.config.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.api.config.utils.BuiltInConfig;
import top.begonia.wizardry.api.config.utils.Context;
import top.begonia.wizardry.api.config.utils.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ConfigListScreen<T> extends ConfigSectionScreen {
    protected final String key;
    protected final ModConfigSpec.ListValueSpec spec;

    // the original data
    protected final ModConfigSpec.ConfigValue<List<T>> valueList;
    // the copy of the data we are working on
    protected List<T> cfgList;

    public ConfigListScreen(
            final Context context,
            final String key,
            final Component title,
            final ModConfigSpec.ListValueSpec spec,
            final ModConfigSpec.@NonNull ConfigValue<List<T>> valueList
    ) {
        super(context, title);
        this.key = key;
        this.spec = spec;
        this.valueList = valueList; // === (ListValueSpec)getValueSpec(key)
        this.cfgList = new ArrayList<>(valueList.getRaw());
    }

    @Override
    protected ConfigSectionScreen rebuild() {
        if (list != null) { // this may be called early, skip and wait for init() then
            list.clearEntries();

            for (int idx = 0; idx < cfgList.size(); idx++) {
                var entry = cfgList.get(idx);
                var element = switch (entry) {
                    case null -> null;
                    case final Boolean value -> createBooleanListValue(idx, value);
                    case final Integer value -> createIntegerListValue(idx, value);
                    case final Long value -> createLongListValue(idx, value);
                    case final Double value -> createDoubleListValue(idx, value);
                    case final String value -> createStringListValue(idx, value);
                    default -> createOtherValue(idx, entry);
                };

                if (element != null) {
                    final AbstractWidget widget = element.getWidget(options);
                    if (widget instanceof EditBox box) {
                        // Force our responder to check content and set text colour.
                        // This is only needed on lists, as section cannot have new UI elements added with bad data.
                        // Here, this can happen when a new element is added to the list.
                        // As the new value is the old value, no undo record will be created.
                        box.setValue(box.getValue());
                    }
                    list.addSmall(createListLabel(idx), widget);
                }
            }

            createAddElementButton();
            if (undoButton == null) {
                createUndoButton();
                createResetButton();
            }
        }
        return this;
    }

    @Override
    protected boolean isAnyNondefault() {
        return !cfgList.equals(valueList.getDefault());
    }

    @SuppressWarnings("unchecked")
    protected void createAddElementButton() {
        final Supplier<?> newElement = spec.getNewElementSupplier();
        final ModConfigSpec.Range<Integer> sizeRange = spec.getSizeRange();

        if (newElement != null && sizeRange.test(cfgList.size() + 1)) {
            if (this.list != null) {
                list.addSmall(
                        new StringWidget(
                                Button.DEFAULT_WIDTH,
                                Button.DEFAULT_HEIGHT,
                                Component.empty(),
                                font
                        ),
                        Button.builder(
                                BuiltInConfig.NEW_LIST_ELEMENT,
                                _ -> {
                                    List<T> newValue = new ArrayList<>(cfgList);
                                    newValue.add((T) newElement.get());
                                    undoManager.add(v -> {
                                        cfgList = v;
                                        onChanged(key);
                                    }, newValue, v -> {
                                        cfgList = v;
                                        onChanged(key);
                                    }, cfgList);
                                    rebuild();
                                }
                        ).build()
                );
            }
        }
    }


    protected AbstractWidget createListLabel(int idx) {
        return new ListLabelWidget(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.translatable(BuiltInConfig.LIST_ELEMENT, idx), idx);
    }

    @Nullable
    protected Element createOtherValue(final int idx, final T entry) {
        final StringWidget label = new StringWidget(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.literal(Objects.toString(entry)), font);
        label.setTooltip(Tooltip.create(BuiltInConfig.UNSUPPORTED_ELEMENT));
        return new Element(getTranslationComponent(key), getTooltipComponent(key, null), label, false);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected Element createStringListValue(final int idx, final String value) {
        return createStringValue(key, v -> spec.test(List.of(v)), () -> value, newValue -> cfgList.set(idx, (T) newValue));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected Element createDoubleListValue(final int idx, final Double value) {
        return createNumberBox(key, spec, () -> value, newValue -> cfgList.set(idx, (T) newValue), v -> spec.test(List.of(v)), Double::parseDouble, 0.0);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected Element createLongListValue(final int idx, final Long value) {
        return createNumberBox(key, spec, () -> value, newValue -> cfgList.set(idx, (T) newValue), v -> spec.test(List.of(v)), Long::decode, 0L);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected Element createIntegerListValue(final int idx, final Integer value) {
        return createNumberBox(key, spec, () -> value, newValue -> cfgList.set(idx, (T) newValue), v -> spec.test(List.of(v)), Integer::decode, 0);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected Element createBooleanListValue(final int idx, final Boolean value) {
        return createBooleanValue(key, spec, () -> value, newValue -> cfgList.set(idx, (T) newValue));
    }

    protected boolean swap(final int idx, final boolean simulate) {
        final List<T> values = new ArrayList<>(cfgList);
        values.add(idx, values.remove(idx + 1));
        return addUndoListener(simulate, values);
    }

    protected boolean del(final int idx, final boolean simulate) {
        final List<T> values = new ArrayList<>(cfgList);
        values.remove(idx);
        return addUndoListener(simulate, values);
    }

    private boolean addUndoListener(boolean simulate, List<T> values) {
        final boolean valid = spec.test(values);
        if (!simulate && valid) {
            undoManager.add(v -> {
                cfgList = v;
                onChanged(key);
            }, values, v -> {
                cfgList = v;
                onChanged(key);
            }, cfgList);
            rebuild();
        }
        return valid;
    }

    @Override
    public void onClose() {
        if (changed && spec.test(cfgList)) {
            valueList.set(cfgList);
            if (this.context.parent() instanceof ConfigSectionScreen parent) {
                parent.onChanged(key);
            }
        }
        super.onClose();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        doneButton.active = spec.test(cfgList);
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    protected void onChanged(final String key) {
        changed = true;
        // parent's onChanged() will be fired when we actually assign the changed list. For now,
        // we've only changed our working copy.
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void createResetButton() {
        resetButton = Button.builder(BuiltInConfig.RESET, button -> {
            undoManager.add(
                    v -> {
                        cfgList = v;
                        onChanged(key);
                    }, new ArrayList<>((List<T>) getValueSpec(key).correct(null)),
                    v -> {
                        cfgList = v;
                        onChanged(key);
                    }, cfgList);
            rebuild();
        }).tooltip(Tooltip.create(BuiltInConfig.RESET_TOOLTIP)).width(Button.SMALL_WIDTH).build();
    }

    public class ListLabelWidget extends AbstractContainerWidget {
        protected final Button upButton = Button.builder(BuiltInConfig.MOVE_LIST_ELEMENT_UP, this::up).build();
        protected final Button downButton = Button.builder(BuiltInConfig.MOVE_LIST_ELEMENT_DOWN, this::down).build();
        protected final Button delButton = Button.builder(BuiltInConfig.REMOVE_LIST_ELEMENT, this::rem).build();
        protected final StringWidget label = new StringWidget(0, 0, 0, 0, Component.empty(), font);
        protected final int idx;
        protected final boolean isFirst;
        protected final boolean isLast;

        public ListLabelWidget(final int x, final int y, final int width, final int height, final Component labelText, final int idx) {
            super(x, y, width, height, labelText, AbstractScrollArea.defaultSettings(font.lineHeight));
            this.idx = idx;
            this.isFirst = idx == 0;
            this.isLast = idx + 1 == cfgList.size();
            this.label.setMessage(labelText);
            this.checkButtons();
            this.updateLayout();
        }

        @Override
        public void setX(final int pX) {
            super.setX(pX);
            this.updateLayout();
        }

        @Override
        public void setY(final int pY) {
            super.setY(pY);
            this.updateLayout();
        }

        @Override
        public void setHeight(final int pHeight) {
            super.setHeight(pHeight);
            this.updateLayout();
        }

        @Override
        public void setWidth(int pWidth) {
            super.setWidth(pWidth);
            this.updateLayout();
        }

        @Override
        public void setSize(int pWidth, int pHeight) {
            super.setSize(pWidth, pHeight);
            this.updateLayout();
        }

        protected void updateLayout() {
            this.upButton.setX(getX());
            this.downButton.setX(getX() + getHeight() + 2);
            this.delButton.setX(getX() + getWidth() - getHeight());
            this.label.setX(getX() + 2 * 22);

            this.upButton.setY(getY());
            this.downButton.setY(getY());
            this.delButton.setY(getY());
            this.label.setY(getY());

            this.upButton.setHeight(getHeight());
            this.downButton.setHeight(getHeight());
            this.delButton.setHeight(getHeight());
            this.label.setHeight(getHeight());

            this.upButton.setWidth(getHeight());
            this.downButton.setWidth(getHeight());
            this.delButton.setWidth(getHeight());
            this.label.setWidth(getWidth() - 3 * (getHeight() + 2));
        }

        void up(final Button button) {
            swap(idx - 1, false);
        }

        void down(final Button button) {
            swap(idx, false);
        }

        void rem(final Button button) {
            del(idx, false);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(upButton, label, downButton, delButton);
        }

        @Override
        protected void extractWidgetRenderState(final @NonNull GuiGraphicsExtractor pGuiGraphics, final int pMouseX, final int pMouseY, final float pPartialTick) {
            this.checkButtons();
            this.label.extractRenderState(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            if (!this.isFirst) {
                this.upButton.extractRenderState(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            }
            if (!this.isLast) {
                this.downButton.extractRenderState(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            }
            this.delButton.extractRenderState(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        protected void checkButtons() {
            this.upButton.visible = !isFirst;
            this.upButton.active = !isFirst && swap(idx - 1, true);
            this.downButton.visible = !isLast;
            this.downButton.active = !isLast && swap(idx, true);
            ModConfigSpec.Range<Integer> sizeRange = spec.getSizeRange();
            this.delButton.active = !cfgList.isEmpty() && sizeRange.test(cfgList.size() - 1) && del(idx, true);
        }

        @Override
        protected void updateWidgetNarration(final @NonNull NarrationElementOutput pNarrationElementOutput) {
            // TODO I have no idea. Help?
        }

        @Override
        protected int contentHeight() {
            return 0; // TODO 1.21.4 no idea
        }

        @Override
        protected double scrollRate() {
            return 4.0; // TODO 1.21.4 no idea
        }
    }
}
