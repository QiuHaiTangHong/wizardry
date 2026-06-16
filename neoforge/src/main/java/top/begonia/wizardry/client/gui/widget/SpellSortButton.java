package top.begonia.wizardry.client.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.util.ISpellSortable;

public class SpellSortButton extends Button {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/container/spell_sort_buttons.png");
    private static final int TEXTURE_WIDTH = 32;
    private static final int TEXTURE_HEIGHT = 32;
    public final ISpellSortable.SortType sortType;
    private final ISpellSortable sortable;
    private final Screen parent;

    public SpellSortButton(
            int x, int y,
            ISpellSortable.SortType sortType,
            ISpellSortable sortable,
            Screen parent,
            OnPress onPress
    ) {
        super(
                x, y,
                10, 10,
                Component.translatable("container." + Wizardry.MODID + ".arcane_workbench.sort_" + sortType.name),
                onPress,
                DEFAULT_NARRATION
        );
        this.sortType = sortType;
        this.sortable = sortable;
        this.parent = parent;
        this.setTooltip(Tooltip.create(this.getMessage()));
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            int k = 0;
            int l = this.sortType.ordinal() * this.height;

            if (sortType == sortable.getSortType()) {
                k += this.width;
                if (sortable.isSortDescending()) {
                    k += this.width;
                }
            }

            guiGraphicsExtractor.blit(
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    this.getX(), this.getY(),
                    k, l,
                    this.width, this.height,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT
            );
        }
    }
}
