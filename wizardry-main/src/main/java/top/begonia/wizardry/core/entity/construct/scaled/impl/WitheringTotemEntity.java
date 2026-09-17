package top.begonia.wizardry.core.entity.construct.scaled.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.client.util.ClientHelper;
import top.begonia.wizardry.client.util.GeometryUtils;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.entity.construct.scaled.ScaledConstructEntity;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.BlockUtils;
import top.begonia.wizardry.core.util.EntityUtils;

import java.util.Comparator;
import java.util.List;

public class WitheringTotemEntity extends ScaledConstructEntity {
    private static final int PERIMETER_PARTICLE_DENSITY = 6;
    private static final EntityDataAccessor<Float> DATA_HEALTH_DRAINED = SynchedEntityData.defineId(
            WitheringTotemEntity.class,
            EntityDataSerializers.FLOAT
    );

    public WitheringTotemEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static final String MAX_EXPLOSION_DAMAGE = "max_explosion_damage";
    public static final String MAX_TARGETS = "max_targets";

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HEALTH_DRAINED, 0.0F);
    }

    public float getHealthDrained() {
        return this.entityData.get(DATA_HEALTH_DRAINED);
    }

    public void addHealthDrained(float health) {
        this.entityData.set(DATA_HEALTH_DRAINED, this.getHealthDrained() + health);
    }

    @Override
    protected boolean shouldScaleWidth() {
        return false;
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {

        if (this.level().isClientSide() && this.tickCount == 1) {
            ClientHelper.playMovingSound(
                    this,
                    WizardrySounds.ENTITY_WITHERING_TOTEM_AMBIENT.get(),
                    WizardrySounds.SPELLS,
                    1,
                    1,
                    true
            );
        }

        super.tick();

        double radius = WizardrySpells.WITHERING_TOTEM.get().getBaseProperty(AbstractSpell.EFFECT_RADIUS) * this.getSizeMultiplier();

        if (this.level().isClientSide()) {
            WizardryClient.particleManager.getParticle(
                    (ClientLevel) this.level(),
                    this.random,
                    WizardryParticles.DUST.get(),
                    this.getX(), this.getY(), this.getZ(),
                    0.3F,
                    false
            ).ifPresent(p -> p.speed(0, -0.02 - this.random.nextFloat() * 0.01, 0)
                    .color(0xf575f5)
                    .endColor(0x382366)
                    .spawn()
            );

            for (int i = 0; i < PERIMETER_PARTICLE_DENSITY; i++) {

                float angle = ((float) Math.PI * 2) / PERIMETER_PARTICLE_DENSITY * (i + this.random.nextFloat());

                double x = this.getX() + radius * Mth.sin(angle);
                double z = this.getZ() + radius * Mth.cos(angle);

                Integer y = BlockUtils.getNearestSurface(
                        this.level(),
                        new BlockPos((int) x, (int) this.getY(), (int) z),
                        Direction.UP,
                        5,
                        true,
                        BlockUtils.SurfaceCriteria.COLLIDABLE
                );

                if (y != null) {
                    WizardryClient.particleManager.getParticle(
                            (ClientLevel) this.level(),
                            WizardryParticles.DUST.get(),
                            x, y, z
                    ).ifPresent(p -> p.speed(0, 0.01, 0)
                            .color(0xf575f5)
                            .endColor(0x382366)
                            .spawn()
                    );
                }
            }
        }

        List<LivingEntity> nearby = EntityUtils.getLivingWithinRadius(
                radius,
                this.getX(), this.getY(), this.getZ(),
                this.level()
        );
        nearby.removeIf(e -> !isValidTarget(e));
        nearby.sort(Comparator.comparingDouble(e -> e.distanceToSqr(this)));

        int targetsRemaining = (int) (WizardrySpells.WITHERING_TOTEM.get().getBaseProperty(MAX_TARGETS)
                + (int) ((this.getDamageMultiplier() - 1) / ServerConfig.Constants.potencyIncreasePerTier));

        while (!nearby.isEmpty() && targetsRemaining > 0) {

            LivingEntity target = nearby.removeFirst();

            if (EntityUtils.isLiving(target)) {

                if (target.tickCount % target.hurtDuration == 1) {

                    float damage = WizardrySpells.WITHERING_TOTEM.get().getBaseProperty(AbstractSpell.DAMAGE);

                    if (EntityUtils.attackEntityWithoutKnockback(
                            (ServerLevel) this.level(),
                            target,
                            WizardryDamageSource.causeIndirectMagicDamage(
                                    WizardryDamageTypes.WITHER.apply(this.registryAccess()),
                                    this,
                                    this.getOwner(),
                                    false
                            ),
                            damage
                    )) {
                        addHealthDrained(damage);
                    }
                }

                targetsRemaining--;

                if (this.level().isClientSide()) {
                    ClientLevel clientLevel = (ClientLevel) this.level();
                    Vec3 centre = GeometryUtils.getCentre(this);
                    Vec3 pos = GeometryUtils.getCentre(target);

//                    WizardryClient.particleManager.getParticle(
//                            (ClientLevel) this.level(),
//                            WizardryParticles.BEAM.get(),
//                            centre.x, centre.y, centre.z
//                    ).ifPresent(p -> p.targetEntity(target)
//                            .color(0.1f + 0.2f * this.random.nextFloat(), 0, 0.3f)
//                            .spawn()
//                    );

                    for (int i = 0; i < 3; i++) {
                        WizardryClient.particleManager.getParticle(
                                (ClientLevel) this.level(),
                                this.random,
                                WizardryParticles.DUST.get(),
                                pos.x, pos.y, pos.z,
                                0.3,
                                false
                        ).ifPresent(p -> p.speed(pos.subtract(centre).normalize().scale(-0.1))
                                .color(0x0c0024)
                                .endColor(0x610017)
                                .spawn()
                        );
                    }
                }
            }
        }
    }

    @Override
    public void despawn() {

        double radius = WizardrySpells.WITHERING_TOTEM.get().getBaseProperty(AbstractSpell.EFFECT_RADIUS) * this.getSizeMultiplier();

        List<LivingEntity> nearby = EntityUtils.getLivingWithinRadius(radius, this.getX(), this.getY(), this.getZ(), this.level());
        nearby.removeIf(e -> !isValidTarget(e));

        float damage = Math.min(getHealthDrained() * 0.2f, WizardrySpells.WITHERING_TOTEM.get().getBaseProperty(MAX_EXPLOSION_DAMAGE));

        for (LivingEntity target : nearby) {

            if (!this.level().isClientSide()
                    && EntityUtils.attackEntityWithoutKnockback(
                    (ServerLevel) this.level(),
                    target,
                    WizardryDamageSource.causeIndirectMagicDamage(
                            WizardryDamageTypes.MAGIC.apply(this.registryAccess()),
                            this,
                            this.getOwner(),
                            false
                    ),
                    damage
            )) {
                target.addEffect(new MobEffectInstance(
                        MobEffects.WITHER,
                        (int) WizardrySpells.WITHERING_TOTEM.get().getBaseProperty(AbstractSpell.EFFECT_DURATION),
                        (int) WizardrySpells.WITHERING_TOTEM.get().getBaseProperty(AbstractSpell.EFFECT_STRENGTH)
                ));
            }
        }

        if (this.level().isClientSide()) {
            Vec3 pos = GeometryUtils.getCentre(this);
            WizardryClient.particleManager.getParticle(
                    (ClientLevel) this.level(),
                    WizardryParticles.SPHERE.get(),
                    pos.x, pos.y, pos.z
            ).ifPresent(p -> p.scaleValue((float) radius)
                    .color(0xbe1a53)
                    .endColor(0x210f4a)
                    .spawn()
            );
        }

        this.playSound(WizardrySounds.ENTITY_WITHERING_TOTEM_EXPLODE.get(), 1, 1);
        super.despawn();
    }
}
