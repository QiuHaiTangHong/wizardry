package top.begonia.wizardry.core.item.impl;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.entity.projectile.bomb.SmokeBombEntity;
import top.begonia.wizardry.core.registry.WizardryEntities;
import top.begonia.wizardry.core.registry.WizardrySounds;

public class SmokeBombItem extends Item {
    public SmokeBombItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        level.playSound(
                null, player.getX(), player.getY(), player.getZ(),
                WizardrySounds.ENTITY_FIREBOMB_THROW, SoundSource.PLAYERS,
                0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        player.getCooldowns().addCooldown(itemStack, 20);
        if (!level.isClientSide()) {
            SmokeBombEntity smokeBomb = WizardryEntities.SMOKE_BOMB.get().create(level, EntitySpawnReason.SPAWN_ITEM_USE);
            if (smokeBomb != null) {
                smokeBomb.aim(player, 1.0F);
                level.addFreshEntity(smokeBomb);
            }
        }
        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}