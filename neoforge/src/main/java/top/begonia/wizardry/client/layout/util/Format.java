package top.begonia.wizardry.client.layout.util;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.*;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.handbook.part.ImageData;
import top.begonia.wizardry.client.data.definition.handbook.part.RecipeTagData;
import top.begonia.wizardry.client.layout.atom.CraftingRecipeElement;
import top.begonia.wizardry.client.layout.atom.IAtomElement;
import top.begonia.wizardry.client.layout.atom.LinkElement;
import top.begonia.wizardry.client.layout.atom.TextElement;
import top.begonia.wizardry.client.layout.hybrid.CaptionImageElement;
import top.begonia.wizardry.client.layout.hybrid.LineElement;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.config.ServerConfig;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class Format {
    public static final Map<String, String> FORMAT_TAGS = new HashMap<>();
    public static final Format INSTANCE = new Format();

    private Format() {
        initFormatTags();
    }

    public enum Tags {
        FORMAT_MARKER("#"),
        HYPERLINK_MARKER("@"),
        IMAGE_TAG("image"),
        RECIPE_TAG("recipe"),
        RULER_TAG("ruler");
        private final String tag;

        Tags(String tag) {
            this.tag = tag;
        }

        @Override
        public String toString() {
            return this.tag;
        }
    }

    public static void addFormatTag(String tag, String value) {
        FORMAT_TAGS.put(tag, value);
    }

    private static void initFormatTags() {
        addFormatTag("example_charging_loss", "" + (ServerConfig.Constants.manaPerCrystal - 30));
        addFormatTag("mana_per_crystal", "" + ServerConfig.Constants.manaPerCrystal);
        addFormatTag("novice_max_charge", "" + TierEnum.NOVICE.getMaxCharge());
        addFormatTag("apprentice_max_charge", "" + TierEnum.APPRENTICE.getMaxCharge());
        addFormatTag("advanced_max_charge", "" + TierEnum.ADVANCED.getMaxCharge());
        addFormatTag("master_max_charge", "" + TierEnum.MASTER.getMaxCharge());
        addFormatTag("version", Wizardry.VERSION);
        addFormatTag("mcversion", Wizardry.MC_VERSION);
    }

    private void associateLinks(LinkElement prev, IAtomElement current) {
        if (prev != null && current instanceof LinkElement currentLink) {
            prev.setAssociatedElement(currentLink);
            currentLink.setAssociatedElement(prev);
        }
    }

    private void formatText(@NonNull String text, MutableComponent rootComponent) {
        StringBuilder textBuffer = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            int cp = text.codePointAt(i);
            int charCount = Character.charCount(cp);
            if (cp == '#') {
                int nextIndex = tryConsumeHashTag(text, i, rootComponent, textBuffer);
                if (nextIndex != -1) {
                    i = nextIndex;
                    continue;
                }
                textBuffer.append('#');
            } else if (cp == '@') {
                flushBuffer(rootComponent, textBuffer);
                int nextIndex = formatLinkTag(i, text, rootComponent);
                if (nextIndex != -1) {
                    i = nextIndex;
                    continue;
                }
                textBuffer.append('@');
            } else {
                textBuffer.appendCodePoint(cp);
            }
            i += charCount;
        }
        flushBuffer(rootComponent, textBuffer);
    }

    private int tryConsumeHashTag(@NonNull String text, int startIndex, MutableComponent parent, StringBuilder buffer) {
        if (text.startsWith("#colour_", startIndex)) {
            flushBuffer(parent, buffer);
            return formatColorTag(startIndex, text, parent);
        }
        String bestMatchValue = null;
        int bestMatchLen = 0;
        for (String tagName : FORMAT_TAGS.keySet()) {
            String fullTag = "#" + tagName;
            if (text.startsWith(fullTag, startIndex)) {
                if (fullTag.length() > bestMatchLen) {
                    bestMatchValue = FORMAT_TAGS.get(tagName);
                    bestMatchLen = fullTag.length();
                }
            }
        }
        if (bestMatchValue != null) {
            flushBuffer(parent, buffer);
            parent.append(Component.literal(bestMatchValue));
            return startIndex + bestMatchLen;
        }
        return -1;
    }

    private void flushBuffer(MutableComponent root, @NonNull StringBuilder buffer) {
        if (!buffer.isEmpty()) {
            root.append(Component.literal(buffer.toString()));
            buffer.setLength(0);
        }
    }

    private int formatColorTag(int index, @NonNull String text, MutableComponent parent) {
        int tagEnd = text.indexOf("#colour_reset", index);
        if (tagEnd != -1) {
            int startIndex = index + "#colour_".length();
            Style foundStyle = null;
            int nameLength = 0;
            for (ElementEnum e : ElementEnum.values()) {
                if (text.startsWith(e.getSerializedName(), startIndex)) {
                    foundStyle = e.getStyle();
                    nameLength = e.getSerializedName().length();
                    break;
                }
            }
            if (foundStyle == null) {
                for (TierEnum t : TierEnum.values()) {
                    if (text.startsWith(t.getSerializedName(), startIndex)) {
                        foundStyle = t.getStyle();
                        nameLength = t.getSerializedName().length();
                        break;
                    }
                }
            }
            if (foundStyle != null) {
                String content = text.substring(startIndex + nameLength, tagEnd);
                parent.append(Component.literal(content).withStyle(foundStyle));
                return tagEnd + "#colour_reset".length();
            }
        }
        return -1;
    }

    private int formatLinkTag(final int index, @NonNull String text, MutableComponent parent) {
        int tagEnd = text.indexOf("@", index + 1);
        if (tagEnd != -1) {
            String raw = text.substring(index + 1, tagEnd);
            String[] keyValue = raw.split("\\s", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String display = keyValue[1];
                Style linkStyle = Style.EMPTY
                        .withColor(TextColor.fromRgb(0x5555FF))
                        .withClickEvent(new ClickEvent.OpenUrl(URI.create(key)));
                parent.append(Component.literal(display).withStyle(linkStyle));
                return tagEnd + 1;
            }
        }
        parent.append(Component.literal(String.valueOf(text.charAt(index))));
        return index + 1;
    }

    public void createImage(String text, @NonNull Context context, List<IAtomElement> elementQueue) {
        Map<String, ImageData> imageDataMap = context.getImages();
        ImageData imageData = imageDataMap.get(text);
        if (imageData != null) {
            elementQueue.add(new CaptionImageElement(imageData));
        }
    }

    public void createRecipe(String text, @NonNull Context context, List<IAtomElement> elementQueue) {
        Map<String, RecipeTagData> recipeDataMap = context.getRecipes();
        RecipeTagData recipeData = recipeDataMap.get(text);
        if (recipeData != null) {
            recipeData.locations().forEach(identifier -> elementQueue.add(new CraftingRecipeElement(identifier)));
        }
    }

    @NonNull
    public MutableComponent createTextElement(@NonNull Context context, @NonNull Font font, List<IAtomElement> elementQueue, MutableComponent rootComponent, String paragraph) {
        formatText(paragraph, rootComponent);
        List<FormattedCharSequence> lines = font.split(rootComponent, context.getMaxWidth() - 1);
        AtomicInteger heightSum = new AtomicInteger(0);
        AtomicInteger maxWidth = new AtomicInteger(0);
        AtomicReference<LinkElement> prevLineEndElement = new AtomicReference<>(null);
        AtomicReference<ClickEvent> prevLineEndEvent = new AtomicReference<>(null);
        AtomicReference<ClickEvent> activeClickEvent = new AtomicReference<>(null);
        for (FormattedCharSequence line : lines) {
            List<IAtomElement> currentLineElements = new ArrayList<>();
            List<FormattedCharSequence> charBuffer = new ArrayList<>();
            AtomicInteger currentLineWidth = new AtomicInteger(0);
            line.accept((_, style, codepoint) -> {
                ClickEvent currentEvent = style.getClickEvent();
                ClickEvent lastEvent = activeClickEvent.get();
                if (currentEvent != lastEvent && !charBuffer.isEmpty()) {
                    IAtomElement atom = createAndFormatAtom(charBuffer, lastEvent, context, currentLineWidth, heightSum);
                    currentLineElements.add(atom);
                    if (lastEvent != null && lastEvent.equals(prevLineEndEvent.get())) {
                        associateLinks(prevLineEndElement.get(), atom);
                    }
                    charBuffer.clear();
                    prevLineEndEvent.set(null);
                    prevLineEndElement.set(null);
                }
                String charStr = new String(Character.toChars(codepoint));
                charBuffer.add(FormattedCharSequence.backward(charStr, style));
                activeClickEvent.set(currentEvent);
                return true;
            });
            if (!charBuffer.isEmpty()) {
                ClickEvent lastEvent = activeClickEvent.get();
                IAtomElement lastAtom = createAndFormatAtom(charBuffer, lastEvent, context, currentLineWidth, heightSum);
                currentLineElements.add(lastAtom);
                if (lastEvent != null && lastEvent.equals(prevLineEndEvent.get())) {
                    associateLinks(prevLineEndElement.get(), lastAtom);
                }
                if (lastAtom instanceof LinkElement link) {
                    prevLineEndElement.set(link);
                    prevLineEndEvent.set(lastEvent);
                } else {
                    prevLineEndElement.set(null);
                    prevLineEndEvent.set(null);
                }
            }
            if (!currentLineElements.isEmpty()) {
                int lineHeight = currentLineElements.getFirst().getHeight();
                heightSum.addAndGet(lineHeight);
                maxWidth.set(Math.max(currentLineWidth.get(), maxWidth.get()));
                elementQueue.add(new LineElement(new ArrayList<>(currentLineElements)));
            }
        }

        elementQueue.add(IAtomElement.EMPTY_ELEMENT);
        return Component.empty();
    }

    private @NonNull IAtomElement createAndFormatAtom(List<FormattedCharSequence> buffer, ClickEvent event, Context context, @NonNull AtomicInteger xOffset, @NonNull AtomicInteger yOffset) {
        FormattedCharSequence content = FormattedCharSequence.composite(new ArrayList<>(buffer));
        IAtomElement element = (event == null) ? new TextElement(content) : new LinkElement(content, event.toString());
        element.format(context);
        element.setXOffset(xOffset.get());
        element.setYOffset(yOffset.get());
        xOffset.addAndGet(element.getWidth());
        return element;
    }
}
