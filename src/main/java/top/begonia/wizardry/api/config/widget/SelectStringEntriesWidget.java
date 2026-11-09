package top.begonia.wizardry.api.config.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.*;

public class SelectStringEntriesWidget extends AbstractSelectionList<SelectStringEntriesWidget.Entry> {
    private final List<Entry> entries = new ArrayList<>();
    private int selectedIndex = -1;
    private int maxEntryWidth = 0;

    public SelectStringEntriesWidget(
            @NonNull Minecraft minecraft,
            int width,
            int height,
            int y,
            int itemHeight,
            @NonNull Map<Object, String> selectableValues,
            @Nullable Object currentValue
    ) {
        super(minecraft, width, height, y, itemHeight);
        List<Map.Entry<Object, String>> sortedList = new ArrayList<>(selectableValues.entrySet());
        sortedList.sort(EntryComparator.INSTANCE);

        int index = 0;
        for (Map.Entry<Object, String> entry : sortedList) {
            Entry listEntry = new Entry(this, entry);
            this.entries.add(listEntry);
            this.addEntry(listEntry);
            int textWidth = minecraft.font.width(entry.getValue());
            if (textWidth > this.maxEntryWidth) {
                this.maxEntryWidth = textWidth;
            }
            if (Objects.equals(currentValue, entry.getKey())) {
                this.selectedIndex = index;
            }
            index++;
        }
    }

    @Override
    public int getRowWidth() {
        return this.maxEntryWidth + 20;
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {
        if (selectedIndex >= 0 && selectedIndex < entries.size()) {
            narrationElementOutput.add(NarratedElementType.TITLE, entries.get(selectedIndex).getDisplayText());
        }
    }

    private enum EntryComparator implements Comparator<Map.Entry<Object, String>> {
        INSTANCE;

        @Override
        public int compare(Map.@NonNull Entry<Object, String> o1, Map.@NonNull Entry<Object, String> o2) {
            int compare = o1.getValue().toLowerCase(Locale.ROOT).compareTo(o2.getValue().toLowerCase(Locale.ROOT));
            if (compare == 0) {
                compare = o1.getKey().toString().toLowerCase(Locale.ROOT)
                        .compareTo(o2.getKey().toString().toLowerCase(Locale.ROOT));
            }
            return compare;
        }
    }

    public class Entry extends AbstractSelectionList.Entry<Entry> {
        private final SelectStringEntriesWidget owningList;
        private final Map.Entry<Object, String> data;
        private final Component displayText;

        public Entry(SelectStringEntriesWidget owningList, Map.@NonNull Entry<Object, String> data) {
            this.owningList = owningList;
            this.data = data;
            this.displayText = Component.literal(data.getValue());
        }

        public Component getDisplayText() {
            return this.displayText;
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            graphics.text(minecraft.font, this.getDisplayText(), 20, 20, 0);
        }
    }
}
