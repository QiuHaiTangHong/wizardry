package top.begonia.wizardry.client.layout.hybrid;

import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.data.definition.handbook.part.ImageData;
import top.begonia.wizardry.client.layout.atom.ImageElement;
import top.begonia.wizardry.client.layout.atom.TextElement;
import top.begonia.wizardry.client.layout.util.Context;

import java.util.Optional;

public class TitleElement extends AbstractHybridElement {
    private final String rawData;

    public TitleElement(String title) {
        this.rawData = title;
    }

    @Override
    public void format(@NonNull Context context) {
        this.clearElements();
        this.font = context.getFont();
        TextElement textElement = new TextElement(Component.literal(this.rawData).getVisualOrderText());
        textElement.format(context);
        textElement.setXOffset(textElement.getXOffset() + 1);
        ImageData rulerData = new ImageData(Context.TEXTURE, "", 0, 181, context.getMaxWidth(), 7, Optional.of(Context.TEXTURE_WIDTH), Optional.of(Context.TEXTURE_HEIGHT), Optional.of(false));
        ImageElement imageElement = new ImageElement(rulerData);
        imageElement.format(context);
        imageElement.setYOffset(imageElement.getHeight() + 2);
        this.setHeight(imageElement.getHeight() + textElement.getHeight());
        this.setWidth(context.getMaxWidth());
        this.addElement(textElement);
        this.addElement(imageElement);
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
