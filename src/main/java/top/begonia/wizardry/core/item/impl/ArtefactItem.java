package top.begonia.wizardry.core.item.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.util.TooltipBuilder;

import java.util.function.Consumer;

public class ArtefactItem extends Item {
    private boolean enabled = true;

    public ArtefactItem(Properties properties) {
        super(properties);
    }

    public static boolean isArtefactActive(Player player, Item artefact) {
        if (artefact instanceof ArtefactItem artefactItem) {
            if (!artefactItem.enabled) {
                return false;
            }

        } else {
            throw new IllegalArgumentException("Not an artefact!");
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        TooltipBuilder.addMultiLineDescription(
                builder,
                this.getDescriptionId() + ".desc",
                Style.EMPTY
        );
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }

    @Override
    public boolean isFoil(@NonNull ItemStack itemStack) {
        return itemStack.getRarity() == Rarity.EPIC;
    }
}
