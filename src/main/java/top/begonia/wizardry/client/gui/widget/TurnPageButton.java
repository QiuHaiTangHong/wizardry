package top.begonia.wizardry.client.gui.widget;


import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.registry.WizardrySounds;

public class TurnPageButton extends Button {

    public static final int WIDTH = 20;
    public static final int HEIGHT = 12;

    public enum Type {
        NEXT_PAGE(0, 196),
        PREVIOUS_PAGE(0, 208),
        NEXT_SECTION(0, 220),
        PREVIOUS_SECTION(0, 232),
        CONTENTS(0, 244);
        private final int u, v;

        Type(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }

    public final Type type;
    private final Identifier texture;
    private final int textureWidth, textureHeight;

    public TurnPageButton(int x, int y, Type type, Identifier texture, int textureWidth, int textureHeight, Button.OnPress onPress) {
        super(x, y, WIDTH, HEIGHT, Component.empty(), onPress, DEFAULT_NARRATION);
        this.type = type;
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(WizardrySounds.MISC_PAGE_TURN.get(), 1.0F));
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        guiGraphicsExtractor.blit(
                RenderPipelines.GUI_TEXTURED,
                this.texture,
                this.getX(),
                this.getY(),
                (float) (this.isHovered() ? type.u + this.width : type.u),
                (float) type.v,
                this.width,
                this.height,
                this.textureWidth,
                this.textureHeight
        );
    }

}
