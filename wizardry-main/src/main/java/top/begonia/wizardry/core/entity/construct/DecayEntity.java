package top.begonia.wizardry.core.entity.construct;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import top.begonia.wizardry.client.util.ParticleBuilder;
import top.begonia.wizardry.core.registry.*;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.List;

/**
 * 衰败实体类
 * <p> 实现一个衰败的实体逻辑, 该实体继承自 MagicConstructEntity 并实现了基础生命周期管理.
 * 提供了纹理索引属性和多种构造方法来创建实例, 并在每个 tick 中执行特定行为:
 * - 每 700 次 tick 有 1/700 的概率播放特定环境音效
 * - 在服务器侧, 找到目标实体并附加衰败效果 (除非是自己的主人)
 * - 在客户端以一定概率生成视觉粒子效果表示衰败魔法施放
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @date 2026.07.09
 */
public class DecayEntity extends MagicConstructEntity {
    public int textureIndex;

    public DecayEntity(EntityType<?> type, Level level) {
        super(type, level);
        textureIndex = this.getRandom().nextInt(10);
    }

    public DecayEntity(Level level) {
        this(WizardryEntities.DECAY.get(), level);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.random.nextInt(700) == 0 && this.tickCount + 100 < this.getLifetime()) {
            this.playSound(WizardrySounds.ENTITY_DECAY_AMBIENT.get(), 0.2F + this.random.nextFloat() * 0.2F,
                    0.6F + this.random.nextFloat() * 0.15F);
        }
        if (!this.level().isClientSide()) {
            List<LivingEntity> targets = this.level().getEntitiesOfClass(
                    LivingEntity.class,
                    this.getBoundingBox().inflate(this.getBbWidth() / 2f, this.getBbHeight(), this.getBbWidth() / 2f)
            );
            for (LivingEntity target : targets) {
                if (target != this.getOwner()) {
                    if (!target.hasEffect(WizardryMobEffects.DECAY)) {
                        int duration = (int) WizardrySpells.DECAY.get().getBaseProperty(AbstractSpell.EFFECT_DURATION);
                        target.addEffect(new MobEffectInstance(WizardryMobEffects.DECAY, duration, 0));
                    }
                }
            }
        } else if (this.random.nextInt(15) == 0) {

            double radius = this.random.nextDouble() * 0.8;
            float angle = this.random.nextFloat() * (float) Math.PI * 2;
            float brightness = this.random.nextFloat() * 0.4f;

            ParticleBuilder.create(WizardryParticles.DARK_MAGIC.get())
                    .pos(this.getX() + radius * Mth.cos(angle), this.getY(), this.getZ() + radius * Mth.sin(angle))
                    .clr(brightness, 0, brightness + 0.1f)
                    .spawn(this.level());
        }
    }
}
