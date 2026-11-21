package top.begonia.wizardry.api.client.config.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.data.definition.hub.SkinsData;

import javax.annotation.Nullable;
import java.util.*;

public class SelectStringEntriesWidget extends AbstractSelectionList<SelectStringEntriesWidget.Entry> {
    private int selectedIndex = -1;
    private int maxEntryWidth = 0;

    public SelectStringEntriesWidget(
            @NonNull Minecraft minecraft,
            int width,
            int height,
            int y,
            int itemHeight,
            @NonNull Map<String, SkinsData.Skin> selectableValues,
            @Nullable Object currentValue
    ) {
        super(minecraft, width, height, y, itemHeight);
        List<Map.Entry<String, SkinsData.Skin>> sortedList = new ArrayList<>(selectableValues.entrySet());
        sortedList.sort(EntryComparator.INSTANCE);

        for (int i = 0; i < sortedList.size(); i++) {
            Map.Entry<String, SkinsData.Skin> entry = sortedList.get(i);
            Entry listEntry = new Entry(this, sortedList.get(i));
            this.addEntry(listEntry);
            int textWidth = minecraft.font.width(entry.getKey());
            if (textWidth > this.maxEntryWidth) {
                this.maxEntryWidth = textWidth;
            }
            if (Objects.equals(currentValue, entry.getValue())) {
                this.selectedIndex = i;
            }
        }
    }

    @Override
    public int getRowWidth() {
        return this.maxEntryWidth;
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {
    }

    private enum EntryComparator implements Comparator<Map.Entry<String, SkinsData.Skin>> {
        INSTANCE;

        @Override
        public int compare(Map.@NonNull Entry<String, SkinsData.Skin> o1, Map.@NonNull Entry<String, SkinsData.Skin> o2) {
            int compare = o1.getKey().toLowerCase(Locale.ROOT).compareTo(o2.getKey().toLowerCase(Locale.ROOT));
            if (compare == 0) {
                compare = o1.getValue().toString().toLowerCase(Locale.ROOT)
                        .compareTo(o2.getValue().toString().toLowerCase(Locale.ROOT));
            }
            return compare;
        }
    }

    public class Entry extends AbstractSelectionList.Entry<Entry> {
        private final SelectStringEntriesWidget owningList;
        private final Map.Entry<String, SkinsData.Skin> data;
        private final Component displayText;

        public Entry(SelectStringEntriesWidget owningList, Map.@NonNull Entry<String, SkinsData.Skin> data) {
            this.owningList = owningList;
            this.data = data;
            this.displayText = Component.literal(data.getKey());
        }

        public Component getDisplayText() {
            return this.displayText;
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            graphics.text(
                    minecraft.font,
                    this.getDisplayText(),
                    this.getContentX(), this.getContentY(),
                    ARGB.color(255, 255, 255, 255)
            );
        }
    }
}
