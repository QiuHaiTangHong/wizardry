package top.begonia.wizardry.core.entity.construct.scaled.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.entity.construct.scaled.ScaledConstructEntity;
import top.begonia.wizardry.core.registry.WizardryMobEffects;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.EntityUtils;

import java.util.List;

public class FrostSigilEntity extends ScaledConstructEntity {
    public FrostSigilEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.setSizeMultiplier(WizardrySpells.FROST_SIGIL.get().getBaseProperty(AbstractSpell.EFFECT_RADIUS));
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {

        super.tick();

        if (!this.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            List<LivingEntity> targets = EntityUtils.getLivingWithinCylinder(
                    this.getBbWidth() / 2,
                    this.position(),
                    this.getBbHeight(),
                    this.level()
            );

            for (LivingEntity target : targets) {

                if (this.isValidTarget(target)) {

                    EntityUtils.attackEntityWithoutKnockback(
                            serverLevel,
                            target,
                            this.getOwner() != null
                            ? WizardryDamageSource.causeIndirectMagicDamage(
                                    WizardryDamageTypes.FROST.apply(this.registryAccess()),
                                    this,
                                    this.getOwner(),
                                    false
                            )
                            : new DamageSources(this.registryAccess()).magic(),
                            WizardrySpells.FROST_SIGIL.get().getBaseProperty(AbstractSpell.DAMAGE) * this.getDamageMultiplier()
                    );

                    if(!EntityUtils.isEntityImmune(WizardryDamageTypes.FROST.apply(this.registryAccess()), target)){
                        target.addEffect(new MobEffectInstance(
                                WizardryMobEffects.FROST,
                                (int) WizardrySpells.FROST_SIGIL.get().getBaseProperty(AbstractSpell.EFFECT_DURATION),
                                (int) WizardrySpells.FROST_SIGIL.get().getBaseProperty(AbstractSpell.EFFECT_STRENGTH)
                        ));
                    }

                    this.playSound(WizardrySounds.ENTITY_FROST_SIGIL_TRIGGER.get(), 1.0f, 1.0f);

                    // The trap is destroyed once triggered.
                    this.discard();
                }
            }
        } else if (this.random.nextInt(15) == 0) {
            double radius = (0.5 + this.random.nextDouble() * 0.3) * this.getBbWidth() / 2;
            float angle = this.random.nextFloat() * (float) Math.PI * 2;
            ClientLevel clientLevel = (ClientLevel) this.level();
            WizardryClient.particleManager.getParticle(
                    clientLevel,
                    WizardryParticles.SNOW.get(),
                    this.getX() + radius * Mth.cos(angle),
                    this.getY() + 0.1,
                    this.getZ() + radius * Mth.sin(angle)
            ).ifPresent(p -> p.speed(0, 0, 0).spawn());
        }
    }
}
