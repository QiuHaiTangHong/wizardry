package top.begonia.wizardry.client.gui.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WizardryConfigurationSectionScreen extends ConfigurationScreen.ConfigurationSectionScreen {
    public WizardryConfigurationSectionScreen(Screen parent, ModConfig.Type type, ModConfig modConfig, Component title) {
        super(parent, type, modConfig, title);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected ConfigurationScreen.@NonNull ConfigurationSectionScreen rebuild() {
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
                        if (element != null) {
                            elements.add(context.filter().filterEntry(context, key, element));
                        }
                    }
                    case UnmodifiableConfig subsection when context.valueSpecs().get(key) instanceof UnmodifiableConfig subconfig -> {
                        elements.add(createSection(key, subconfig, subsection));
                    }
                    default -> {
                        Element otherElement = createOtherSection(key, rawValue);
                        if (otherElement != null) {
                            elements.add(context.filter().filterEntry(context, key, otherElement));
                        }
                    }

                }
            }
            elements.addAll(createSyntheticValues());

            for (final Element element : elements) {
                if (element != null) {
                    if (element.name() == null) {
                        list.addSmall(
                                new StringWidget(
                                        Button.DEFAULT_WIDTH,
                                        Button.DEFAULT_HEIGHT,
                                        Component.empty(),
                                        font
                                ),
                                element.getWidget(options)
                        );
                    } else {
                        final StringWidget label = new StringWidget(
                                Button.DEFAULT_WIDTH,
                                Button.DEFAULT_HEIGHT,
                                element.name(),
                                font
                        );
                        label.setTooltip(Tooltip.create(
                                element.tooltip() != null
                                        ? element.tooltip()
                                        : Component.empty()
                        ));
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
}
