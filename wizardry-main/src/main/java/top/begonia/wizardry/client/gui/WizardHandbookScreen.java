package top.begonia.wizardry.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.gui.widget.InvisibleButton;
import top.begonia.wizardry.client.gui.widget.TurnPageButton;
import top.begonia.wizardry.client.layout.container.handbook.HandbookElement;
import top.begonia.wizardry.client.layout.util.Context;
import top.begonia.wizardry.core.registry.WizardrySounds;

public class WizardHandbookScreen extends Screen {
    private Button bookmark;
    private Button previous;
    private Button previousSection;
    private Button menu;

    private HandbookElement handbookElement;

    public WizardHandbookScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        super.init();
        final int left = this.width / 2 - Context.GUI_WIDTH / 2;
        final int top = this.height / 2 - Context.GUI_HEIGHT / 2;
        this.clearWidgets();
        Context context = new Context(Context.PAGE_WIDTH, Context.PAGE_HEIGHT, this.font);
        this.addRenderableWidget(handbookElement = new HandbookElement(context));
        this.addRenderableWidget(new TurnPageButton(
                left + Context.GUI_WIDTH - Context.BUTTON_INSET_X - TurnPageButton.WIDTH,
                top + Context.GUI_HEIGHT - Context.BUTTON_INSET_Y - TurnPageButton.HEIGHT,
                TurnPageButton.Type.NEXT_PAGE,
                Context.TEXTURE,
                Context.TEXTURE_WIDTH, Context.TEXTURE_HEIGHT,
                _ -> this.handbookElement.getPageTurner().next()
        ));
        this.addRenderableWidget(previous = new TurnPageButton(
                left + Context.BUTTON_INSET_X,
                top + Context.GUI_HEIGHT - Context.BUTTON_INSET_Y - TurnPageButton.HEIGHT,
                TurnPageButton.Type.PREVIOUS_PAGE,
                Context.TEXTURE,
                Context.TEXTURE_WIDTH, Context.TEXTURE_HEIGHT,
                _ -> this.handbookElement.getPageTurner().prev()
        ));
        this.addRenderableWidget(new TurnPageButton(
                left + Context.GUI_WIDTH - Context.BUTTON_INSET_X - TurnPageButton.WIDTH - Context.BUTTON_SPACING,
                top + Context.GUI_HEIGHT - Context.BUTTON_INSET_Y - TurnPageButton.HEIGHT,
                TurnPageButton.Type.NEXT_SECTION,
                Context.TEXTURE,
                Context.TEXTURE_WIDTH, Context.TEXTURE_HEIGHT,
                _ -> {
                }
        ));
        this.addRenderableWidget(previousSection = new TurnPageButton(
                left + Context.BUTTON_INSET_X + Context.BUTTON_SPACING,
                top + Context.GUI_HEIGHT - Context.BUTTON_INSET_Y - TurnPageButton.HEIGHT,
                TurnPageButton.Type.PREVIOUS_SECTION,
                Context.TEXTURE,
                Context.TEXTURE_WIDTH, Context.TEXTURE_HEIGHT,
                _ -> {

                }
        ));
        this.addRenderableWidget(menu = new TurnPageButton(
                left + Context.GUI_WIDTH / 2 - 28,
                top + Context.GUI_HEIGHT - Context.BUTTON_INSET_Y - TurnPageButton.HEIGHT,
                TurnPageButton.Type.CONTENTS,
                Context.TEXTURE,
                Context.TEXTURE_WIDTH, Context.TEXTURE_HEIGHT,
                _ -> {

                }
        ));
        this.addRenderableWidget(bookmark = new InvisibleButton(
                left + 130,
                top + 172,
                11, 19,
                _ -> {
                }
        ) {
            @Override
            public void playDownSound(@NonNull SoundManager soundManager) {
                soundManager.play(SimpleSoundInstance.forUI(WizardrySounds.MISC_PAGE_TURN.get(), 1.0F));
            }
        });
        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(WizardrySounds.MISC_BOOK_OPEN.get(), 1.0F));
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        int left = (this.width - Context.GUI_WIDTH) / 2;
        int top = (this.height - Context.GUI_HEIGHT) / 2;

        //绘制背景
        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, Context.TEXTURE, left, top, 0, 0, Context.GUI_WIDTH, Context.GUI_HEIGHT, Context.TEXTURE_WIDTH, Context.TEXTURE_HEIGHT);

        int currentPage = this.handbookElement.getPageTurner().getCurrentPage();

        //特殊的背景
        if (currentPage == 0) {
            guiGraphicsExtractor.blit(
                    RenderPipelines.GUI_TEXTURED,
                    Context.TEXTURE,
                    left, top,
                    368.0F, 0.0F,
                    Context.GUI_WIDTH / 2, Context.GUI_HEIGHT,
                    Context.TEXTURE_WIDTH, Context.TEXTURE_HEIGHT
            );
            previous.visible = false;
            previousSection.visible = false;
            menu.visible = false;
        } else {
            previous.visible = true;
            previousSection.visible = true;
            menu.visible = true;
        }

        int color = ARGB.color(255, 0, 0, 0);

        //绘制页码
        if (currentPage > 0) {
            String pageNumber = String.valueOf(doubleToSinglePage(currentPage, false));
            guiGraphicsExtractor.text(
                    this.font,
                    pageNumber,
                    left + Context.TEXT_INSET_X + Context.PAGE_WIDTH / 2 - this.font.width(pageNumber) / 2,
                    top + Context.GUI_HEIGHT - Context.PAGE_NUMBER_INSET,
                    color,
                    false
            );
        }
        String pageNumber = String.valueOf(doubleToSinglePage(currentPage, true));
        guiGraphicsExtractor.text(
                this.font,
                pageNumber,
                left + Context.GUI_WIDTH - Context.TEXT_INSET_X - Context.PAGE_WIDTH / 2 - this.font.width(pageNumber) / 2,
                top + Context.GUI_HEIGHT - Context.PAGE_NUMBER_INSET,
                color,
                false
        );

        //绘制书签
        bookmark.visible = false;
        guiGraphicsExtractor.blit(
                RenderPipelines.GUI_TEXTURED,
                Context.TEXTURE,
                left + 138,
                top, 299,
                0, 11,
                191,
                Context.TEXTURE_WIDTH, Context.TEXTURE_HEIGHT
        );

        this.handbookElement.setXOffset(left);
        this.handbookElement.setYOffset(top);

        //绘制按钮
        super.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTick);
    }

    public static int doubleToSinglePage(int doublePageIndex, boolean rightHandPage) {
        return rightHandPage ? doublePageIndex * 2 + 1 : doublePageIndex * 2;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (this.handbookElement.mouseClicked(event, doubleClick)) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }
}
