package top.begonia.wizardry.core.entity.construct.scaled.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.entity.construct.scaled.ScaledConstructEntity;
import top.begonia.wizardry.core.item.ArtefactItem;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.EntityUtils;

import java.util.Comparator;
import java.util.List;

public class StormcloudEntity extends ScaledConstructEntity {
    public StormcloudEntity(EntityType<?> type, Level level) {
        super(type, level);
        float r = WizardrySpells.STORMCLOUD.get().getBaseProperty(AbstractSpell.EFFECT_RADIUS);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {

        super.tick();

        Vec3 velocity = this.getDeltaMovement();
        this.move(MoverType.SELF, velocity.multiply(1.0F, 0.0F, 1.0F));

        if (this.level().isClientSide()) {

            float areaFactor = (this.getBbWidth() * this.getBbWidth()) / 36; // Ensures cloud/raindrop density stays the same for different sizes

            for (int i = 0; i < 2 * areaFactor; i++) {
                ClientLevel clientLevel = (ClientLevel) this.level();
                WizardryClient.particleManager.getParticle(
                        clientLevel,
                        WizardryParticles.CLOUD.get(),
                        0, 0, 0
                ).ifPresent(p -> p.color(0.3f, 0.3f, 0.3f).shaded(true).spawn());
            }
        }

        boolean stormcloudRingActive = this.getOwner() instanceof Player player && ArtefactItem.isArtefactActive(player, WizardryItems.RING_STORMCLOUD.get());

        List<LivingEntity> targets = this.level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().deflate(0, -10, 0)
        );

        targets.removeIf(t -> !this.isValidTarget(t));

        float damage = WizardrySpells.STORMCLOUD.get().getBaseProperty(AbstractSpell.DAMAGE) * this.getDamageMultiplier();

        for (LivingEntity target : targets) {

            if (target.tickCount % 150 == 0) { // Use target's lifetime so they don't all get hit at once, looks better

                if (!this.level().isClientSide()) {
                    EntityUtils.attackEntityWithoutKnockback(
                            (ServerLevel) this.level(),
                            target,
                            WizardryDamageSource.causeIndirectMagicDamage(
                                    WizardryDamageTypes.SHOCK.apply(this.registryAccess()),
                                    this,
                                    this.getOwner(),
                                    false
                            ),
                            damage
                    );
                } else {
                    ClientLevel clientLevel = (ClientLevel) this.level();
                    WizardryClient.particleManager.getParticle(
                            clientLevel,
                            WizardryParticles.LIGHTNING.get(),
                            target.getX(), getY() + this.getBbHeight() / 2, target.getZ()
                    ).ifPresent(p -> p.targetEntity(target).scaleValue(2.0F).spawn());
                    WizardryClient.particleManager.spawnShockParticles(
                            clientLevel,
                            target.getX(),
                            target.getY() + target.getBbHeight(),
                            target.getZ()
                    );
                }

                target.playSound(WizardrySounds.ENTITY_STORMCLOUD_THUNDER.get(), 1, 1.6f);
                target.playSound(WizardrySounds.ENTITY_STORMCLOUD_ATTACK.get(), 1, 1);

                if (stormcloudRingActive) {
                    this.setLifetime(this.getLifetime() - 40); // Each strike prolongs the lifetime by 2 seconds with the ring
                }
            }
        }

        if (stormcloudRingActive) {
            EntityUtils.getLivingWithinRadius(this.getBbWidth() * 3, this.getX(), this.getY(), this.getZ(), this.level()).stream()
                    .filter(this::isValidTarget).min(Comparator.comparingDouble(this::distanceToSqr)).ifPresent(e -> {
                        Vec3 vel = e.position().subtract(this.position()).normalize().scale(0.2);
                        this.setDeltaMovement(vel.multiply(1.0F, 0.0F, 1.0F));
                    });
        }

    }
}
