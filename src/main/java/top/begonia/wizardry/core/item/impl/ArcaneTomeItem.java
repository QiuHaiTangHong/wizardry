package top.begonia.wizardry.core.item.impl;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.util.TooltipBuilder;

import java.util.function.Consumer;

public class ArcaneTomeItem extends Item {

    public ArcaneTomeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(@NonNull ItemStack stack) {
        return true;
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack itemStack) {
        Component name = itemStack.getComponents().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
        TierEnum tierEnum = itemStack.getOrDefault(WizardryComponents.TIER, TierEnum.NOVICE);
        if (tierEnum == TierEnum.APPRENTICE) {
            return name.copy().withStyle(Rarity.UNCOMMON.getStyleModifier());
        } else if (tierEnum == TierEnum.ADVANCED) {
            return name.copy().withStyle(Rarity.RARE.getStyleModifier());
        } else if (tierEnum == TierEnum.MASTER) {
            return name.copy().withStyle(Rarity.EPIC.getStyleModifier());
        }
        return name;
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        TierEnum tier = itemStack.getOrDefault(WizardryComponents.TIER, TierEnum.NOVICE);
        builder.accept(tier.getDisplayNameWithFormatting());
        TooltipBuilder.addMultiLineDescription(
                builder,
                this.getDescriptionId() + ".desc",
                Style.EMPTY,
                tier.previous().getDisplayNameWithFormatting(),
                tier.getDisplayNameWithFormatting()
        );
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}
