package top.begonia.wizardry.api.config.screen;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.apache.logging.log4j.util.Strings;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.api.config.utils.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConfigSectionScreen extends OptionsSubScreen {
    protected final Context context;
    // Ideally this should not be static, but we need it in the construtor's super() call
    protected static final TranslationChecker translationChecker = new TranslationChecker();
    public ModConfigSpec.RestartType needsRestart = ModConfigSpec.RestartType.NONE;
    protected boolean changed = false;
    protected final Map<String, ConfigSectionScreen> sectionCache = new HashMap<>();
    @Nullable
    protected Button undoButton, resetButton;
    protected final Button doneButton = Button.builder(CommonComponents.GUI_DONE, button -> onClose()).width(Button.SMALL_WIDTH).build();
    protected final UndoManager undoManager = new UndoManager();
    protected static final long MAX_SLIDER_SIZE = 256L;

    public ConfigSectionScreen(
            @NonNull Context context,
            Component title
    ) {
        super(context.parent(), Minecraft.getInstance().options, title);
        this.context = context;
    }

    public ConfigSectionScreen(
            final Context parentContext,
            final Screen parent,
            final Map<String, Object> valueSpecs,
            final String key,
            final Set<? extends UnmodifiableConfig.Entry> entrySet,
            Component title
    ) {
        this(Context.section(parentContext, parent, entrySet, valueSpecs, key), title);
    }

    @Override
    protected void addOptions() {
        rebuild();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected ConfigSectionScreen rebuild() {
        if (list != null) { // this may be called early, skip and wait for init() then
            list.clearEntries();
            boolean hasUndoableElements = false;
            final List<@Nullable Element> elements = new ArrayList<>();
            for (final UnmodifiableConfig.Entry entry : context.entries()) {
                final String key = entry.getKey();
                final Object rawValue = entry.getRawValue();
                switch (entry.getRawValue()) {
                    case ModConfigSpec.ConfigValue cv -> {
                        var valueSpec = getValueSpec(key);
                        var element = switch (valueSpec) {
                            case ModConfigSpec.ListValueSpec listValueSpec -> {
                                yield createList(key, listValueSpec, cv);
                            }
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class
                                    && spec.getDefault() instanceof String -> {
                                yield createStringValue(key, valueSpec::test, () -> (String) cv.getRaw(), cv::set);
                            }
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class
                                    && spec.getDefault() instanceof Integer -> {
                                yield createIntegerValue(key, valueSpec, () -> (Integer) cv.getRaw(), cv::set);
                            }
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class
                                    && spec.getDefault() instanceof Long -> {
                                yield createLongValue(key, valueSpec, () -> (Long) cv.getRaw(), cv::set);
                            }
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class
                                    && spec.getDefault() instanceof Double -> {
                                yield createDoubleValue(key, valueSpec, () -> (Double) cv.getRaw(), cv::set);
                            }
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class
                                    && spec.getDefault() instanceof Enum<?> -> {
                                yield createEnumValue(key, valueSpec, (Supplier) cv::getRaw, (Consumer) cv::set);
                            }
                            case null -> null;
                            default -> switch (cv) {
                                case ModConfigSpec.BooleanValue value -> {
                                    yield createBooleanValue(key, valueSpec, value::getRaw, value::set);
                                }
                                case ModConfigSpec.IntValue value -> {
                                    yield createIntegerValue(key, valueSpec, value::getRaw, value::set);
                                }
                                case ModConfigSpec.LongValue value -> {
                                    yield createLongValue(key, valueSpec, value::getRaw, value::set);
                                }
                                case ModConfigSpec.DoubleValue value -> {
                                    yield createDoubleValue(key, valueSpec, value::getRaw, value::set);
                                }
                                case ModConfigSpec.EnumValue value -> {
                                    yield createEnumValue(key, valueSpec, (Supplier) value::getRaw, (Consumer) value::set);
                                }
                                default -> {
                                    yield createOtherValue(key, cv);
                                }
                            };
                        };
                        elements.add(context.filter().filterEntry(context, key, element));
                    }
                    case UnmodifiableConfig subsection when context.valueSpecs().get(key) instanceof UnmodifiableConfig subconfig -> {
                        elements.add(createSection(key, subconfig, subsection));
                    }
                    default -> {
                        elements.add(context.filter().filterEntry(context, key, createOtherSection(key, rawValue)));
                    }
                }
            }
            elements.addAll(createSyntheticValues());
            for (final Element element : elements) {
                if (element != null) {
                    if (element.name() == null) {
                        list.addSmall(
                                new StringWidget(
                                        Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
                                        Component.empty(), font
                                ),
                                element.getWidget(options)
                        );
                    } else {
                        final StringWidget label = new StringWidget(
                                Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
                                element.name(), font
                        );
                        if (element.tooltip() != null) {
                            label.setTooltip(Tooltip.create(element.tooltip()));
                        }
                        list.addSmall(label, element.getWidget(options));
                    }
                    hasUndoableElements |= element.undoable();
                }
            }

            if (hasUndoableElements && undoButton == null) {
                createUndoButton();
                createResetButton();
            }
        }
        return this;
    }

    protected void onChanged(final String key) {
        this.changed = true;
        final ModConfigSpec.ValueSpec valueSpec = getValueSpec(key);
        if (valueSpec != null) {
            this.needsRestart = this.needsRestart.with(valueSpec.restartType());
        }
    }

    protected ModConfigSpec.@Nullable ValueSpec getValueSpec(final String key) {
        final Object object = this.context.valueSpecs().get(key);
        if (object instanceof final ModConfigSpec.ValueSpec vs) {
            return vs;
        } else {
            return null;
        }
    }

    protected String getTranslationKey(final String key) {
        final ModConfigSpec.ValueSpec valueSpec = getValueSpec(key);
        final String result = valueSpec != null
                ? valueSpec.getTranslationKey()
                : context.modSpec().getLevelTranslationKey(this.context.makeKeyList(key));
        return result != null
                ? result
                : this.context.modId() + ".configuration." + key;
    }

    protected MutableComponent getTranslationComponent(final String key) {
        return Component.translatable(translationChecker.check(getTranslationKey(key)));
    }

    protected String getComment(final String key) {
        final ModConfigSpec.ValueSpec valueSpec = this.getValueSpec(key);
        return valueSpec != null
                ? this.getValueSpec(key).getComment()
                : this.context.modSpec().getLevelComment(this.context.makeKeyList(key));
    }

    protected <T> OptionInstance.TooltipSupplier<T> getTooltip(final String key, ModConfigSpec.@Nullable Range<?> range) {
        return OptionInstance.cachedConstantTooltip(getTooltipComponent(key, range));
    }

    protected Component getTooltipComponent(final String key, ModConfigSpec.@Nullable Range<?> range) {
        final String tooltipKey = getTranslationKey(key) + ".tooltip";
        final String comment = getComment(key);
        final boolean hasTranslatedTooltip = translationChecker.existsWithFallback(tooltipKey);
        MutableComponent component = Component.empty()
                .append(getTranslationComponent(key)
                        .withStyle(ChatFormatting.BOLD));
        if (hasTranslatedTooltip || !Strings.isBlank(comment)) {
            component.append(BuiltInConfig.EMPTY_LINE)
                    .append(Component.translatableWithFallback(tooltipKey, comment));
        }
        // The "tooltip.warning" key is to be considered an internal API. It will be removed once Neo has a
        // generic styling mechanism for translation texts. Use at your own risk.
        component.append(
                translationChecker.optional(BuiltInConfig.EMPTY_LINE,
                        tooltipKey + ".warning", ChatFormatting.RED, ChatFormatting.BOLD
                )
        );
        if (hasTranslatedTooltip && range != null) {
            component.append(BuiltInConfig.EMPTY_LINE)
                    .append(Component.translatable(BuiltInConfig.RANGE_TOOLTIP, range.toString())
                            .withStyle(BuiltInConfig.RANGE_TOOLTIP_STYLE));
        }
        return component;
    }

    protected boolean isNonDefault(ModConfigSpec.@NonNull ConfigValue<?> cv) {
        return !Objects.equals(cv.getRaw(), cv.getDefault());
    }

    protected boolean isAnyNondefault() {
        for (final UnmodifiableConfig.Entry entry : this.context.entries()) {
            if (entry.getRawValue() instanceof final ModConfigSpec.ConfigValue<?> cv) {
                if (!(getValueSpec(entry.getKey()) instanceof ModConfigSpec.ListValueSpec) && isNonDefault(cv)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void addFooter() {
        if (this.undoButton != null || this.resetButton != null) {
            LinearLayout linearlayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
            if (this.undoButton != null) {
                linearlayout.addChild(this.undoButton);
            }
            if (this.resetButton != null) {
                linearlayout.addChild(this.resetButton);
            }
            linearlayout.addChild(this.doneButton);
        } else {
            super.addFooter();
        }
    }

    protected void createUndoButton() {
        undoButton = Button.builder(BuiltInConfig.UNDO, button -> {
            undoManager.undo();
            rebuild();
        }).tooltip(Tooltip.create(BuiltInConfig.UNDO_TOOLTIP)).width(Button.SMALL_WIDTH).build();
        undoButton.active = false;
    }

    protected void setUndoButtonstate(boolean state) {
        if (this.undoButton != null) {
            this.undoButton.active = state;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void createResetButton() {
        this.resetButton = Button.builder(BuiltInConfig.RESET, _ -> {
            List<UndoManager.Step<?>> list = new ArrayList<>();
            for (final UnmodifiableConfig.Entry entry : context.entries()) {
                if (entry.getRawValue() instanceof final ModConfigSpec.ConfigValue cv
                        && !(getValueSpec(entry.getKey()) instanceof ModConfigSpec.ListValueSpec)
                        && isNonDefault(cv)
                ) {
                    final String key = entry.getKey();
                    list.add(this.undoManager.step(v -> {
                        cv.set(v);
                        onChanged(key);
                    }, getValueSpec(key).correct(null), v -> {
                        cv.set(v);
                        onChanged(key);
                    }, cv.getRaw()));
                }
            }
            this.undoManager.add(list);
            this.rebuild();
        }).tooltip(Tooltip.create(BuiltInConfig.RESET_TOOLTIP)).width(Button.SMALL_WIDTH).build();
    }

    protected void setResetButtonstate(boolean state) {
        if (resetButton != null) {
            resetButton.active = state;
        }
    }

    @Override
    public void onClose() {
        if (this.changed) {
            if (this.lastScreen instanceof final ConfigSectionScreen parent) {
                // "bubble up" the marker so the top-most section can change the ModConfig
                parent.changed = true;
            } else {
                // we are a top-level per-type config screen, i.e. one specific config file. Save the config and tell the mod to reload.
                this.context.modSpec().save();
            }
            // the restart flag only matters when there were actual changes
            if (this.lastScreen instanceof final ConfigSectionScreen parent) {
                parent.needsRestart = parent.needsRestart.with(this.needsRestart);
            } else if (this.lastScreen instanceof final ConfigurationScreen parent) {
                parent.needsRestart = parent.needsRestart.with(this.needsRestart);
            }
        }
        super.onClose();
    }

    protected Collection<? extends Element> createSyntheticValues() {
        return Collections.emptyList();
    }

    @Nullable
    protected Element createStringValue(
            final String key,
            final Predicate<String> tester,
            final @NonNull Supplier<String> source,
            final Consumer<String> target
    ) {
        if (source.get().length() > 192) {
            // That's just too much for the UI
            final StringWidget label = new StringWidget(
                    Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
                    Component.literal(source.get().substring(0, 128)),
                    this.font
            );
            label.setTooltip(Tooltip.create(BuiltInConfig.LONG_STRING));
            return new Element(getTranslationComponent(key), getTooltipComponent(key, null), label, false);
        }
        final EditBox box = new EditBox(
                this.font,
                Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
                this.getTranslationComponent(key)
        );
        box.setEditable(true);
        // no filter or the user wouldn't be able to type
        box.setTooltip(Tooltip.create(getTooltipComponent(key, null)));
        box.setMaxLength(Mth.clamp(source.get().length() + 5, 128, 192));
        box.setValue(source.get());
        box.setResponder(newValue -> {
            if (tester.test(newValue)) {
                if (!newValue.equals(source.get())) {
                    undoManager.add(v -> {
                        target.accept(v);
                        onChanged(key);
                    }, newValue, v -> {
                        target.accept(v);
                        onChanged(key);
                    }, source.get());
                }
                box.setTextColor(EditBox.DEFAULT_TEXT_COLOR);
                return;
            }
            box.setTextColor(0xFFFF0000);
        });
        return new Element(
                this.getTranslationComponent(key),
                this.getTooltipComponent(key, null),
                box
        );
    }

    @Nullable
    protected Element createOtherSection(
            final String key,
            final Object value
    ) {
        return null;
    }

    @Nullable
    protected Element createOtherValue(
            final String key,
            final ModConfigSpec.@NonNull ConfigValue<?> value
    ) {
        final StringWidget label = new StringWidget(
                Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
                Component.literal(Objects.toString(value.getRaw())),
                this.font
        );
        label.setTooltip(Tooltip.create(BuiltInConfig.UNSUPPORTED_ELEMENT));
        return new Element(getTranslationComponent(key), getTooltipComponent(key, null), label, false);
    }

    /**
     * A custom variant of OptionsInstance.Enum that doesn't show the key on the button, just the value
     */
    public record Custom<T>(List<T> values) implements OptionInstance.ValueSet<T> {
        @Override
        public Function<OptionInstance<T>, AbstractWidget> createButton(OptionInstance.TooltipSupplier<T> tooltip, Options options, int x, int y, int width, OptionInstance.ValueUpdateListener<? super T> target) {
            return optionsInstance -> CycleButton.builder(optionsInstance.toString, (Supplier<T>) optionsInstance::get)
                    .withValues(CycleButton.ValueListSupplier.create(this.values))
                    .withTooltip(tooltip)
                    .displayOnlyValue()
                    .create(x, y, width, 20, optionsInstance.caption, (_, newValue) -> {
                        optionsInstance.set(newValue);
                        options.save();
                        target.valueChanged(newValue);
                    });
        }

        @Override
        public @NonNull Optional<T> validateValue(@NonNull T value) {
            return values.contains(value) ? Optional.of(value) : Optional.empty();
        }

        @Override
        public Codec<T> codec() {
            return null;
        }

        public static final Custom<Boolean> BOOLEAN_VALUES_NO_PREFIX = new Custom<>(ImmutableList.of(Boolean.TRUE, Boolean.FALSE));
    }

    @Nullable
    protected Element createBooleanValue(final String key, final ModConfigSpec.ValueSpec spec, final Supplier<Boolean> source, final Consumer<Boolean> target) {
        return new Element(getTranslationComponent(key), getTooltipComponent(key, null),
                new OptionInstance<>(getTranslationKey(key), getTooltip(key, null), OptionInstance.BOOLEAN_TO_STRING, ConfigurationScreen.ConfigurationSectionScreen.Custom.BOOLEAN_VALUES_NO_PREFIX, source.get(), newValue -> {
                    // regarding change detection: new value always is different (cycle button)
                    undoManager.add(v -> {
                        target.accept(v);
                        onChanged(key);
                    }, newValue, v -> {
                        target.accept(v);
                        onChanged(key);
                    }, source.get());
                }));
    }

    @Nullable
    protected <T extends Enum<T>> Element createEnumValue(
            final String key,
            final ModConfigSpec.@NonNull ValueSpec spec,
            final @NonNull Supplier<T> source,
            final Consumer<T> target
    ) {
        @SuppressWarnings("unchecked") final Class<T> clazz = (Class<T>) spec.getClazz();
        assert clazz != null;
        final List<T> list = Arrays.stream(clazz.getEnumConstants()).filter(spec::test).toList();
        return new Element(
                getTranslationComponent(key),
                getTooltipComponent(key, null),
                new OptionInstance<>(
                        getTranslationKey(key),
                        getTooltip(key, null),
                        (caption, displayvalue) -> displayvalue instanceof TranslatableEnum tenum
                                ? tenum.getTranslatedName()
                                : Component.literal(displayvalue.name()),
                        new Custom<>(list),
                        source.get(),
                        newValue -> {
                            // regarding change detection: new value always is different (cycle button)
                            undoManager.add(
                                    v -> {
                                        target.accept(v);
                                        onChanged(key);
                                    },
                                    newValue,
                                    v -> {
                                        target.accept(v);
                                        onChanged(key);
                                    },
                                    source.get()
                            );
                        }
                )
        );
    }

    @Nullable
    protected Element createIntegerValue(
            final String key,
            final ModConfigSpec.@NonNull ValueSpec spec,
            final Supplier<Integer> source,
            final Consumer<Integer> target
    ) {
        final ModConfigSpec.Range<Integer> range = spec.getRange();
        final int min = range != null
                ? range.getMin()
                : 0;
        final int max = range != null
                ? range.getMax()
                : Integer.MAX_VALUE;

        if ((long) max - (long) min < MAX_SLIDER_SIZE) {
            return this.createSlider(key, source, target, range);
        } else {
            return this.createNumberBox(key, spec, source, target, null, Integer::decode, 0);
        }
    }

    @Nullable
    protected Element createSlider(
            final String key,
            final @NonNull Supplier<Integer> source,
            final Consumer<Integer> target,
            final ModConfigSpec.@Nullable Range<Integer> range
    ) {
        return new Element(
                this.getTranslationComponent(key),
                this.getTooltipComponent(key, null),
                new OptionInstance<>(
                        this.getTranslationKey(key),
                        this.getTooltip(key, range),
                        (caption, displayvalue) -> Component.literal("" + displayvalue),
                        new OptionInstance.IntRange(range != null ? range.getMin()
                                : 0, range != null
                                ? range.getMax() : Integer.MAX_VALUE),
                        null,
                        source.get(),
                        newValue -> {
                            if (!newValue.equals(source.get())) {
                                undoManager.add(v -> {
                                    target.accept(v);
                                    onChanged(key);
                                }, newValue, v -> {
                                    target.accept(v);
                                    onChanged(key);
                                }, source.get());
                            }
                        }
                )
        );
    }

    @Nullable
    protected Element createLongValue(
            final String key,
            final ModConfigSpec.ValueSpec spec,
            final Supplier<Long> source,
            final Consumer<Long> target
    ) {
        return createNumberBox(
                key, spec,
                source, target,
                null, Long::decode,
                0L
        );
    }

    // if someone knows how to get a proper zero inside...
    @Nullable
    protected <T extends Number & Comparable<? super T>> Element createNumberBox(
            final String key,
            final ModConfigSpec.@NonNull ValueSpec spec,
            final @NonNull Supplier<T> source,
            final Consumer<T> target,
            @Nullable final Predicate<T> tester,
            final Function<String, T> parser,
            final T zero
    ) {
        final ModConfigSpec.Range<T> range = spec.getRange();
        final EditBox box = new EditBox(font, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, getTranslationComponent(key));
        box.setEditable(true);
        box.setTooltip(Tooltip.create(getTooltipComponent(key, range)));
        box.setValue(source.get() + "");
        box.setResponder(newValueString -> {
            try {
                final T newValue = parser.apply(newValueString);
                if (tester != null ? tester.test(newValue) : (newValue != null && (range == null || range.test(newValue)) && spec.test(newValue))) {
                    if (!newValue.equals(source.get())) {
                        undoManager.add(v -> {
                            target.accept(v);
                            onChanged(key);
                        }, newValue, v -> {
                            target.accept(v);
                            onChanged(key);
                        }, source.get());
                    }
                    box.setTextColor(EditBox.DEFAULT_TEXT_COLOR);
                    return;
                }
            } catch (final NumberFormatException e) {
                // field probably is just empty/partial, ignore that
            }
            box.setTextColor(0xFFFF0000);
        });
        return new Element(getTranslationComponent(key), getTooltipComponent(key, null), box);
    }

    @Nullable
    protected Element createDoubleValue(final String key, final ModConfigSpec.ValueSpec spec, final Supplier<Double> source, final Consumer<Double> target) {
        return createNumberBox(key, spec, source, target, null, Double::parseDouble, 0.0);
    }

    @Nullable
    protected Element createSection(final String key, final @NonNull UnmodifiableConfig subconfig, final UnmodifiableConfig subsection) {
        if (subconfig.isEmpty()) {
            return null;
        }
        return new Element(
                Component.translatable(BuiltInConfig.SECTION, getTranslationComponent(key)),
                this.getTooltipComponent(key, null),
                Button.builder(
                                Component.translatable(
                                        BuiltInConfig.SECTION,
                                        Component.translatable(
                                                translationChecker.check(
                                                        this.getTranslationKey(key) + ".button",
                                                        BuiltInConfig.SECTION_TEXT
                                                )
                                        )
                                ),
                                button -> minecraft.gui.setScreen(sectionCache.computeIfAbsent(
                                        key,
                                        k -> new ConfigSectionScreen(
                                                this.context,
                                                this,
                                                subconfig.valueMap(),
                                                key,
                                                subsection.entrySet(),
                                                Component.translatable(getTranslationKey(key))
                                        ).rebuild())
                                )
                        )
                        .tooltip(Tooltip.create(getTooltipComponent(key, null)))
                        .width(Button.DEFAULT_WIDTH)
                        .build(),
                false);
    }

    @Nullable
    protected <T> Element createList(
            final String key,
            final ModConfigSpec.ListValueSpec spec,
            final ModConfigSpec.ConfigValue<List<T>> list
    ) {
        return new Element(
                Component.translatable(
                        BuiltInConfig.SECTION,
                        getTranslationComponent(key)
                ),
                this.getTooltipComponent(key, null),
                Button.builder(
                        Component.translatable(
                                BuiltInConfig.SECTION,
                                Component.translatable(
                                        translationChecker.check(
                                                getTranslationKey(key) + ".button",
                                                BuiltInConfig.SECTION_TEXT
                                        )
                                )
                        ),
                        _ -> minecraft.gui.setScreen(
                                this.sectionCache.computeIfAbsent(
                                        key,
                                        _ -> new ConfigurationListScreen<>(
                                                Context.list(context, this), key,
                                                Component.translatable(
                                                        BuiltInConfig.CRUMB,
                                                        this.getTitle(),
                                                        BuiltInConfig.CRUMB_SEPARATOR,
                                                        this.getTranslationComponent(key)
                                                ),
                                                spec, list
                                        )
                                ).rebuild()
                        )
                ).tooltip(Tooltip.create(this.getTooltipComponent(key, null))).build(),
                false
        );
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.setUndoButtonstate(this.undoManager.canUndo()); // in render()? Really? --- Yes! This is how vanilla does it.
        this.setResetButtonstate(this.isAnyNondefault());
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }
}
