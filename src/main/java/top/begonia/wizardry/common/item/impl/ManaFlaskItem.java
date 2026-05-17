package top.begonia.wizardry.common.item.impl;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

import java.util.function.Consumer;

public class ManaFlaskItem extends Item {

    public enum Size {

        SMALL(75, 25, Rarity.COMMON),
        MEDIUM(350, 40, Rarity.COMMON),
        LARGE(1400, 60, Rarity.RARE);

        public final int capacity;
        public final int useDuration;
        public final Rarity rarity;

        Size(int capacity, int useDuration, Rarity rarity) {
            this.capacity = capacity;
            this.useDuration = useDuration;
            this.rarity = rarity;
        }
    }

    public final Size size;

    public ManaFlaskItem(Size size, Properties properties) {
        super(properties);
        this.size = size;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(
            @NonNull ItemStack itemStack,
            @NonNull TooltipContext context,
            @NonNull TooltipDisplay display,
            @NonNull Consumer<Component> builder,
            @NonNull TooltipFlag tooltipFlag
    ) {
        builder.accept(Component.translatable(
                "item." + Wizardry.MODID + ".mana_flask.desc",
                size.capacity
        ).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NonNull ItemUseAnimation getUseAnimation(@NonNull ItemStack stack) {
        return ItemUseAnimation.BLOCK;
    }

    @Override
    public int getUseDuration(@NonNull ItemStack stack, @NonNull LivingEntity entity) {
        return size.useDuration;
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player player, @NonNull InteractionHand hand) {
        ItemStack flask = player.getItemInHand(hand);
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public void onUseTick(@NonNull Level world, @NonNull LivingEntity entity, @NonNull ItemStack stack, int count) {

    }

    @Override
    public @NonNull ItemStack finishUsingItem(@NonNull ItemStack stack, @NonNull Level world, @NonNull LivingEntity entity) {
        if (entity instanceof Player player) {
            findAndChargeItem(stack, player);
        }
        return stack;
    }

    private void findAndChargeItem(ItemStack stack, Player player) {
    }
}
