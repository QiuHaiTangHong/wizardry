package top.begonia.wizardry.core.effect.impl;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.effect.MagicMobEffect;
import top.begonia.wizardry.core.entity.construct.DecayEntity;
import top.begonia.wizardry.core.registry.WizardryMobEffects;

import java.util.List;

public class DecayMobEffect extends MagicMobEffect {
    public DecayMobEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "decay_slowness"),
                -0.1D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
        int interval = 25 >> amplifier;
        return interval == 0 || tickCount % interval == 0;
    }

    @Override
    public boolean applyEffectTick(@NotNull ServerLevel level, @NotNull LivingEntity entity, int amplifier) {
        entity.hurtServer(level, level.damageSources().wither(), 1.0F);
        return true;
    }

    public static void onEntityTickEventPre(EntityTickEvent.@NonNull Pre event) {
        if (!(event.getEntity() instanceof LivingEntity target) || target.level().isClientSide()) {
            return;
        }
        ServerLevel level = (ServerLevel) target.level();
        if (target.tickCount % ServerConfig.Constants.DECAY_SPREAD_INTERVAL == 0 && !target.level().isClientSide()
                && target.hasEffect(WizardryMobEffects.DECAY) && target.onGround()) {
            List<DecayEntity> entities = level.getEntitiesOfClass(DecayEntity.class, target.getBoundingBox());
            if (!entities.isEmpty()) {
                return;
            }
            DecayEntity decay = new DecayEntity(target.level());
            decay.setOwner(target);
            decay.setPos(target.getX(), target.getY(), target.getZ());
            target.level().addFreshEntity(decay);
        }
    }
}
