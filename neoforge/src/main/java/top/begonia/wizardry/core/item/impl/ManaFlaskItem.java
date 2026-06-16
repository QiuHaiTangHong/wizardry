package top.begonia.wizardry.core.item.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.constants.ManaFlaskTypeEnum;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.util.TextHelper;

import java.util.function.Consumer;

public class ManaFlaskItem extends Item {
    public ManaFlaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack stack) {
        ManaFlaskTypeEnum type = stack.getOrDefault(WizardryComponents.MANA_FLASK_TYPE.get(), ManaFlaskTypeEnum.SMALL);
        return Component.translatable("item." + Wizardry.MODID + "." + type.getSerializedName() + "_mana_flask").withStyle(type.rarity.getStyleModifier());
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        TextHelper.addMultiLineDescription(
                builder,
                this.getDescriptionId() + ".desc",
                Style.EMPTY
        );
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}
