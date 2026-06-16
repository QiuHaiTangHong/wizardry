package top.begonia.wizardry.client.layout.util;

import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.handbook.part.ImageData;
import top.begonia.wizardry.client.data.definition.handbook.part.RecipeTagData;
import top.begonia.wizardry.client.layout.atom.IAtomElement;
import top.begonia.wizardry.client.layout.hybrid.PageElement;

import java.util.*;

public final class Context {
    private final int maxWidth;
    private final int maxHeight;
    private final Font font;
    private int currentPageIndex = 0;
    private int currentY = 0;
    private Map<String, Integer> colours;
    private Map<String, ImageData> images;
    private Map<String, RecipeTagData> recipes;
    private Map<String, Map<String, String>> catalogueEntry = new LinkedHashMap<>();

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/handbook/handbook.png");
    public static final int GUI_WIDTH = 288, GUI_HEIGHT = 180;
    public static final int TEXTURE_WIDTH = 512, TEXTURE_HEIGHT = 256;
    public static final int PAGE_WIDTH = 123, PAGE_HEIGHT = 137;
    public static final int TEXT_INSET_X = 17;
    public static final int BUTTON_INSET_X = 22, BUTTON_INSET_Y = 13;
    public static final int BUTTON_SPACING = 20;
    public static final int PAGE_NUMBER_INSET = 22;

    Map<Integer, List<IAtomElement>> pages = new HashMap<>();

    public Context(int maxWidth, int maxHeight, Font font) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.font = font;
    }

    public void reserveSpace(int height, @NonNull IAtomElement element, int leftPadding, int topPadding, boolean xCenter) {
        element.format(this);
        if (currentY + height > maxHeight && currentY > 0) {
            if (element == IAtomElement.EMPTY_ELEMENT) {
                return;
            }
            currentPageIndex++;
            currentY = 0;
        }
        setElement(element, leftPadding, topPadding, xCenter);
        currentY += height;
    }

    public void reserveSpace(@NonNull IAtomElement element, int leftPadding, int topPadding, boolean xCenter) {
        element.format(this);
        if (currentY + element.getHeight() > maxHeight && currentY > 0) {
            if (element == IAtomElement.EMPTY_ELEMENT) {
                return;
            }
            currentPageIndex++;
            currentY = 0;
        }
        setElement(element, leftPadding, topPadding, xCenter);
        currentY += element.getHeight();
    }

    private void setElement(@NonNull IAtomElement element, int leftPadding, int topPadding, boolean xCenter) {
        int xOnPage = 0;
        if (xCenter) {
            xOnPage = (this.maxWidth - element.getWidth()) / 2;
        }
        element.setXOffset(xOnPage + leftPadding);
        element.setYOffset(currentY + topPadding);
        pages.computeIfAbsent(currentPageIndex, _ -> new ArrayList<>()).add(element);
    }

    public Font getFont() {
        return this.font;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public @NonNull List<PageElement> getPages() {
        List<PageElement> pageElements = new ArrayList<>();
        List<Integer> sortedKeys = new ArrayList<>(this.pages.keySet());
        Collections.sort(sortedKeys);
        for (int index : sortedKeys) {
            List<IAtomElement> elements = this.pages.get(index);
            List<IAtomElement> trimmed = new ArrayList<>(elements);
            while (!trimmed.isEmpty() && trimmed.getFirst() == IAtomElement.EMPTY_ELEMENT) {
                trimmed.removeFirst();
            }
            while (!trimmed.isEmpty() && trimmed.getLast() == IAtomElement.EMPTY_ELEMENT) {
                trimmed.removeLast();
            }
            if (!trimmed.isEmpty()) {
                pageElements.add(new PageElement(trimmed));
            }
        }
        return pageElements;
    }

    public void clear() {
        this.currentPageIndex = 0;
        this.currentY = 0;
        this.pages.clear();
    }

    public Integer getColor(String name) {
        return this.colours.getOrDefault(name, 0xFF000000);
    }

    public void setColours(Map<String, Integer> colours) {
        this.colours = colours;
    }

    public Map<String, ImageData> getImages() {
        return images;
    }

    public void setImages(Map<String, ImageData> images) {
        this.images = images;
    }

    public Map<String, RecipeTagData> getRecipes() {
        return recipes;
    }

    public Map<String, Map<String, String>> getCatalogueEntry() {
        return catalogueEntry;
    }

    public void setCatalogueEntry(Map<String, Map<String, String>> catalogueEntry) {
        this.catalogueEntry = catalogueEntry;
    }

    public void setRecipes(Map<String, RecipeTagData> recipes) {
        this.recipes = recipes;
    }
}