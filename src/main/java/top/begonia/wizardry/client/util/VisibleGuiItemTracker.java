package top.begonia.wizardry.client.util;

import net.minecraft.resources.Identifier;
import org.joml.Vector4i;

import java.util.ArrayList;
import java.util.List;

public final class VisibleGuiItemTracker {
    private static final List<VisibleGuiItem> ITEMS = new ArrayList<>();

    public record VisibleGuiItem(Identifier identifier, Vector4i bounds) {
    }

    public static List<VisibleGuiItem> getItems() {
        return ITEMS;
    }

    public static void clear() {
        ITEMS.clear();
    }

    public static void add(VisibleGuiItem item) {
        ITEMS.add(item);
    }

    private VisibleGuiItemTracker() {
    }
}
