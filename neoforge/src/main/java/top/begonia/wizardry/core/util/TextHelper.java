package top.begonia.wizardry.core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本格式化工具类
 * <p>提供一系列用于 Minecraft 聊天组件 ({@code Component}) 和格式化文本 ({@code FormattedText}) 处理的静态辅助方法
 * <p>主要功能包括:
 * <ul>
 *     <li>将多行描述文本分割并转换为组件列表, 用于工具提示或聊天消息显示</li>
 *     <li>将带有占位符的翻译字符串与已有组件拼接, 实现动态文本组合</li>
 *     <li>根据翻译键和样式数组生成带样式切换的多行组件列表, 支持类似 BBCode 的样式区域标记</li>
 * </ul>
 * <p>所有方法均依赖 {@code Minecraft.getInstance().font} 提供的文本拆分器进行宽度适配, 默认工具提示换行宽度为 140 像素
 * <p>该类为 {@code final} 工具类, 仅包含静态方法, 不应被实例化
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @date 2026.07.03
 * @since 1.0.0
 */
public final class TextHelper {
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

    public static @NonNull Component componentWithComponent(String translationKey, Component... components) {
        String rawString = Language.getInstance().getOrDefault(translationKey);
        MutableComponent rootComponent = Component.empty();
        Pattern pattern = Pattern.compile("%(\\d+)\\$s");
        Matcher matcher = pattern.matcher(rawString);
        int currentPos = 0;
        while (matcher.find()) {
            int start = matcher.start();
            if (start > currentPos) {
                String textSegment = rawString.substring(currentPos, start);
                rootComponent.append(Component.literal(textSegment));
            }
            int componentIndex = Integer.parseInt(matcher.group(1)) - 1;
            if (componentIndex >= 0 && componentIndex < components.length) {
                Component targetComponent = components[componentIndex];
                if (targetComponent != null) {
                    rootComponent.append(targetComponent);
                }
            }
            currentPos = matcher.end();
        }
        if (currentPos < rawString.length()) {
            String tailText = rawString.substring(currentPos);
            rootComponent.append(Component.literal(tailText));
        }
        return rootComponent;
    }

    public static @NonNull List<Component> componentWithStyles(String translationKey, Style... styles) {
        List<Component> finalLines = new ArrayList<>();
        String rawString = Language.getInstance().getOrDefault(translationKey);
        String[] rawLines = rawString.split("\n");
        Deque<Style> styleStack = new ArrayDeque<>();
        styleStack.push(Style.EMPTY);
        Pattern pattern = Pattern.compile("%(\\d+)\\$s");
        for (String rawLine : rawLines) {
            MutableComponent lineRoot = Component.empty();
            int currentPos = 0;
            Matcher matcher = pattern.matcher(rawLine);
            while (matcher.find()) {
                int start = matcher.start();
                if (start > currentPos) {
                    String textSegment = rawLine.substring(currentPos, start);
                    if (!styleStack.isEmpty()) {
                        lineRoot.append(Component.literal(textSegment).withStyle(styleStack.peek()));
                    } else {
                        lineRoot.append(Component.literal(textSegment).withStyle(Style.EMPTY));
                    }
                }
                int styleIndex = Integer.parseInt(matcher.group(1)) - 1;
                if (styleIndex >= 0 && styleIndex < styles.length) {
                    Style targetStyle = styles[styleIndex];
                    if (styleStack.size() > 1 && styleStack.peek().equals(targetStyle)) {
                        styleStack.pop();
                    } else {
                        styleStack.push(targetStyle);
                    }
                }
                currentPos = matcher.end();
            }
            if (currentPos < rawLine.length()) {
                String tailText = rawLine.substring(currentPos);
                if (!styleStack.isEmpty()) {
                    lineRoot.append(Component.literal(tailText).withStyle(styleStack.peek()));
                } else {
                    lineRoot.append(Component.literal(tailText).withStyle(Style.EMPTY));
                }
            }
            finalLines.add(lineRoot);
        }

        return finalLines;
    }
}
