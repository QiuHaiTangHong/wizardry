package top.begonia.wizardry.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.config.ClientConfig;
import top.begonia.wizardry.client.gui.widget.SpellSortButton;
import top.begonia.wizardry.common.inventory.menu.ArcaneWorkbenchMenu;
import top.begonia.wizardry.client.util.ISpellSortable;
import top.begonia.wizardry.common.inventory.settings.ArcaneWorkbenchSettings;

public class ArcaneWorkbenchScreen extends AbstractContainerScreen<ArcaneWorkbenchMenu> {
    private Button applyBtn;
    private Button clearBtn;
    private final Button[] sortButtons = new Button[3];
    private EditBox searchField;
    private final ArcaneWorkbenchSettings settings;

    public ArcaneWorkbenchScreen(ArcaneWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.settings = ArcaneWorkbenchSettings.DEFAULT;
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - settings.mainGui().guiWidth()) / 2;
        this.topPos = (this.height - settings.mainGui().guiHeight()) / 2;

        this.addRenderableWidget(this.applyBtn = new SimpleButton(
                this.width / 2 + 64, this.height / 2 + 3,
                Component.translatable("container." + Wizardry.MODID + ".arcane_workbench.apply"),
                button -> {
                },
                null,
                this.settings.applyButton()
        ));
        this.applyBtn.active = false;
        this.addRenderableWidget(this.clearBtn = new SimpleButton(
                this.width / 2 + 64, this.height / 2 - 16,
                Component.translatable("container." + Wizardry.MODID + ".arcane_workbench.clear"),
                button -> {
                },
                null,
                this.settings.cleanButton()
        ));
        this.clearBtn.active = false;
        this.addRenderableWidget(sortButtons[0] = new SpellSortButton(
                this.leftPos - 44, this.topPos + 8,
                ISpellSortable.SortType.TIER,
                this.menu,
                this,
                button -> {
                },
                null
        ));
        this.addRenderableWidget(sortButtons[1] = new SpellSortButton(
                this.leftPos - 31, this.topPos + 8,
                ISpellSortable.SortType.ELEMENT,
                this.menu,
                this,
                button -> {
                },
                null
        ));
        this.addRenderableWidget(sortButtons[2] = new SpellSortButton(
                this.leftPos - 18, this.topPos + 8,
                ISpellSortable.SortType.ALPHABETICAL,
                this.menu,
                this,
                button -> {
                },
                null
        ));

        this.searchField = new EditBox(
                this.font,
                this.leftPos - 113, this.topPos + 22,
                104, this.font.lineHeight,
                Component.empty()
        );
        this.searchField.setMaxLength(50);
        this.searchField.setBordered(false);
        this.searchField.setVisible(true);
        this.searchField.setTextColor(16777215);
        this.searchField.setCanLoseFocus(ClientConfig.unfocusedSearchBars);
        this.searchField.setFocused(!ClientConfig.unfocusedSearchBars);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                this.settings.mainGui().identifier(),
                this.getGuiLeft(), this.getGuiTop(),
                0.0F, 0.0F,
                this.settings.mainGui().guiWidth(), this.settings.mainGui().guiHeight(),
                this.settings.mainGui().textureWidth(), this.settings.mainGui().textureHeight()
        );
        for (int i = 0; i < ArcaneWorkbenchMenu.CRYSTAL_SLOT; i++) {
            Slot slot = this.menu.slots.get(i);
            if (slot.x >= 0 && slot.y >= 0 && slot.isActive()) {
                graphics.blit(
                        RenderPipelines.GUI_TEXTURED,
                        this.settings.mainGui().identifier(),
                        this.leftPos + slot.x - 10, this.topPos + slot.y - 10,
                        0, 220,
                        36, 36,
                        this.settings.mainGui().textureWidth(), this.settings.mainGui().textureHeight()
                );
            }
        }
    }

    private static class SimpleButton extends Button {
        private final ArcaneWorkbenchSettings.ButtonSettings buttonSettings;

        public SimpleButton(int x, int y, Component message, OnPress onPress, CreateNarration createNarration, ArcaneWorkbenchSettings.ButtonSettings buttonSettings) {
            super(
                    x, y,
                    buttonSettings.buttonWidthSize(), buttonSettings.buttonHeightSize(),
                    message,
                    onPress,
                    createNarration
            );
            this.buttonSettings = buttonSettings;
        }

        @Override
        protected void extractContents(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
            int u = buttonSettings.buttonU();
            int v = buttonSettings.buttonV();
            if (this.isActive()) {
                if (this.isHovered) {
                    u += this.width * 2;
                }
            } else {
                u += this.width;
            }

            guiGraphicsExtractor.blit(
                    RenderPipelines.GUI_TEXTURED,
                    buttonSettings.identifier(),
                    this.getX(), this.getY(),
                    u, v,
                    this.width, this.height,
                    buttonSettings.textureWidth(), buttonSettings.textureHeight()
            );
        }
    }
}
