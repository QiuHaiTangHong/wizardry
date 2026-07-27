package top.begonia.wizardry.core.item.impl;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.gui.WizardHandbookScreen;

import java.util.function.Consumer;

public class WizardHandbookItem extends Item {

    // Yep, I hardcoded my own name into the mod. Don't want people changing it now, do I?
    private static final String AUTHOR = "Electroblob";

    public WizardHandbookItem(Properties properties) {
        super(properties);
    }

    @Deprecated
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        builder.accept(Component.translatable(
                this.descriptionId + ".author",
                Component.literal(AUTHOR).withStyle(ChatFormatting.GRAY))
        );
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        if (level.isClientSide()) {
            Minecraft.getInstance().gui.setScreen(new WizardHandbookScreen());
        }
        return InteractionResult.SUCCESS;
    }
}
