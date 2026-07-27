package top.begonia.wizardry.client.layout.hybrid;

import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.layout.atom.LinkElement;
import top.begonia.wizardry.client.layout.util.Context;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CatalogueElement extends AbstractHybridElement {
    private final Map<String, String> rawData = new LinkedHashMap<>();
    private final int interval = 1;

    public CatalogueElement(Map<String, String> catalogueEntry) {
        this.rawData.putAll(catalogueEntry);
    }

    @Override
    public void format(@NonNull Context context) {
        AtomicInteger heightSum = new AtomicInteger(0);
        this.rawData.forEach((key, value) -> {
            FormattedCharSequence displayFormattedCharSequence = FormattedCharSequence.forward(value, Style.EMPTY);
            LinkElement linkElement = new LinkElement(displayFormattedCharSequence, key);
            linkElement.format(context);
            linkElement.setYOffset(heightSum.get() + interval);
            linkElement.setXOffset(1);
            this.addElement(linkElement);
            heightSum.getAndAdd(linkElement.getHeight() + interval);
        });
        this.setHeight(heightSum.get());
        this.setWidth(context.getMaxWidth());
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
