package top.begonia.wizardry.core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class TooltipBuilder {
    private static final int TOOLTIP_WRAP_WIDTH = 140;

    public static void addMultiLineDescription(
            @NonNull Consumer<Component> tooltipConsumer,
            String translationKey,
            Style baseStyle,
            Object... args
    ) {
        Font font = Minecraft.getInstance().font;
        Component fullText = Component.translatable(translationKey, args).withStyle(baseStyle);
        List<FormattedText> splitLines = font.getSplitter().splitLines(fullText, TOOLTIP_WRAP_WIDTH, baseStyle);
        for (FormattedText line : splitLines) {
            tooltipConsumer.accept(convertToComponent(line));
        }
    }

    private static @NonNull Component convertToComponent(@NonNull FormattedText formattedText) {
        MutableComponent root = Component.empty();
        formattedText.visit((style, textSegment) -> {
            root.append(Component.literal(textSegment).withStyle(style));
            return Optional.empty();
        }, Style.EMPTY);
        return root;
    }

    public static @NonNull List<Component> splitComponentToList(
            @NonNull Component component,
            int maxWrapWidth,
            Style fallbackBaseStyle
    ) {
        Font font = Minecraft.getInstance().font;
        FormattedCharSequence sequence = component.getVisualOrderText();
        List<Component> result = new ArrayList<>();
        List<FormattedText> splitLines = font.getSplitter().splitLines(component, maxWrapWidth, fallbackBaseStyle);
        for (FormattedText line : splitLines) {
            result.add(convertToComponent(line));
        }

        return result;
    }

    public static @NonNull List<Component> splitComponentToList(@NonNull Component component, Style fallbackBaseStyle) {
        return splitComponentToList(component, TOOLTIP_WRAP_WIDTH, fallbackBaseStyle);
    }
}
