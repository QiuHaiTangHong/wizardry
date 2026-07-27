package top.begonia.wizardry.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.inventory.menu.BookshelfMenu;

public class BookshelfScreen extends AbstractContainerScreen<BookshelfMenu> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/container/bookshelf.png");

    public BookshelfScreen(BookshelfMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 148);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.leftPos, this.topPos,
                0.0F, 0.0F,
                this.imageWidth, this.imageHeight,
                256, 256
        );
    }

}