package top.begonia.wizardry.client.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class InvisibleButton extends Button {

    public InvisibleButton(int x, int y, int width, int height, Button.OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
    }

}
