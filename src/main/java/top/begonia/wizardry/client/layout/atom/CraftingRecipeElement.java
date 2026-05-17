package top.begonia.wizardry.client.layout.atom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.display.*;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.layout.container.handbook.HandbookElement;
import top.begonia.wizardry.client.layout.util.Context;
import top.begonia.wizardry.client.network.ClientPayloadHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p> crafting 配方元素类, 用于处理和渲染物品合成配方的显示界面.
 * 该类继承自 AbstractAtomElement, 主要负责在 GUI 中绘制配方的物品栏, 合成结果以及悬停时的提示信息.
 * 包含动画效果 (如物品轮播) 和鼠标交互逻辑, 实现一个动态的配方展示组件喵.
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @email mailto:2981263417@qq.com
 * @date 2026.05.03
 * @since 1.0.0
 */
public class CraftingRecipeElement extends AbstractAtomElement {
    private static final int TEXTURE_INSET_X = 45;
    private static final int TEXTURE_INSET_Y = 195;
    private static final int WIDTH = 111;
    private static final int HEIGHT = 56;
    private static final int SLOT_SIZE = 18;
    private static final int RESULT_X = 88;
    private static final int RESULT_Y = 20;

    private List<RecipeDisplay> recipeItemData;
    private final Identifier rawData;

    public CraftingRecipeElement(Identifier recipeIdentifier) {
        this.rawData = recipeIdentifier;
    }

    @Override
    public void format(@NonNull Context context) {
        this.font = context.getFont();
        this.recipeItemData = ClientPayloadHandler.getDisplays(this.rawData);
        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        guiGraphicsExtractor.blit(
                RenderPipelines.GUI_TEXTURED,
                Context.TEXTURE,
                this.getXOffset(), this.getYOffset(),
                TEXTURE_INSET_X, TEXTURE_INSET_Y,
                WIDTH, HEIGHT,
                Context.TEXTURE_WIDTH, Context.TEXTURE_HEIGHT
        );
        if (recipeItemData == null || recipeItemData.isEmpty() || Minecraft.getInstance().level == null) return;
        long gameMillis = Util.getMillis();
        int displayIndex = (int) ((gameMillis / 2000) % recipeItemData.size());
        RecipeDisplay display = recipeItemData.get(displayIndex);
        ContextMap contextMap = SlotDisplayContext.fromLevel(Minecraft.getInstance().level);

        if (display instanceof ShapedCraftingRecipeDisplay shaped) {
            renderIngredientList(guiGraphicsExtractor, shaped.ingredients(), shaped.width(), contextMap, gameMillis, mouseX, mouseY);
        } else if (display instanceof ShapelessCraftingRecipeDisplay shapeless) {
            renderIngredientList(guiGraphicsExtractor, shapeless.ingredients(), 3, contextMap, gameMillis, mouseX, mouseY);
        }

        ItemStack resultStack = display.result().resolveForFirstStack(contextMap);
        if (!resultStack.isEmpty()) {
            int rx = this.getXOffset() + RESULT_X;
            int ry = this.getYOffset() + RESULT_Y;
            guiGraphicsExtractor.item(resultStack, rx, ry);
            guiGraphicsExtractor.itemDecorations(this.font, resultStack, rx, ry);
            if (isMouseOverItem(mouseX, mouseY, rx, ry)) {
                HandbookElement.pushTooltip(() -> this.renderItemStackTooltip(guiGraphicsExtractor, resultStack, mouseX, mouseY));
            }
        }
    }

    private void renderItemStackTooltip(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, @NonNull ItemStack stack, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        List<Component> lines = stack.getTooltipLines(
                Item.TooltipContext.of(mc.level),
                mc.player,
                mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL
        );

        List<ClientTooltipComponent> visualLines = lines.stream()
                .map(Component::getVisualOrderText)
                .map(ClientTooltipComponent::create)
                .collect(Collectors.toList());

        stack.getTooltipImage().ifPresent(data -> visualLines.add(1, ClientTooltipComponent.create(data)));
        guiGraphicsExtractor.tooltip(
                this.font,
                visualLines,
                mouseX,
                mouseY,
                DefaultTooltipPositioner.INSTANCE,
                null,
                stack
        );
    }

    private boolean isMouseOverItem(int mouseX, int mouseY, int itemX, int itemY) {
        return mouseX >= itemX && mouseX < itemX + 16 &&
                mouseY >= itemY && mouseY < itemY + 16;
    }

    private void renderIngredientList(GuiGraphicsExtractor guiGraphicsExtractor, List<SlotDisplay> ingredients, int gridWidth, ContextMap context, long gameMillis, int mouseX, int mouseY) {
        int left = this.getXOffset();
        int top = this.getYOffset();
        for (int i = 0; i < ingredients.size(); i++) {
            int row = i / gridWidth;
            int col = i % gridWidth;
            SlotDisplay slot = ingredients.get(i);
            List<ItemStack> stacks = slot.resolve(context, SlotDisplay.ItemStackContentsFactory.INSTANCE).toList();
            if (!stacks.isEmpty()) {
                int itemIndex = (int) ((gameMillis / 1000) % stacks.size());
                ItemStack currentStack = stacks.get(itemIndex);
                if (!currentStack.isEmpty()) {
                    int ix = left + 2 + col * SLOT_SIZE;
                    int iy = top + 2 + row * SLOT_SIZE;
                    guiGraphicsExtractor.item(currentStack, ix, iy);
                    guiGraphicsExtractor.itemDecorations(this.font, currentStack, ix, iy);
                    if (mouseX >= ix && mouseX < ix + 16 && mouseY >= iy && mouseY < iy + 16) {
                        ItemStack finalStack = currentStack.copy();
                        HandbookElement.pushTooltip(() -> this.renderItemStackTooltip(guiGraphicsExtractor, finalStack, mouseX, mouseY));
                    }
                }
            }
        }
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}