package top.begonia.wizardry.core.entity.construct;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.registry.WizardryEntities;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.impl.ray.impl.EntrapmentRaySpell;

import java.util.Optional;

public class BubbleEntity extends MagicConstructEntity {
    private static final EntityDataAccessor<Boolean> DATA_IS_DARK_ORB = SynchedEntityData.defineId(BubbleEntity.class, EntityDataSerializers.BOOLEAN);

    public BubbleEntity(Level level) {
        super(WizardryEntities.BUBBLE.get(), level);
    }

    public BubbleEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static void onLivingIncomingDamageEvent(@NonNull LivingIncomingDamageEvent event) {
        if (event.getEntity().getVehicle() instanceof BubbleEntity bubbleEntity && !bubbleEntity.isDarkOrb()) {
            bubbleEntity.playSound(WizardrySounds.ENTITY_BUBBLE_POP.get(), 1.5f, 1.0f);
            bubbleEntity.discard();
        }
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_IS_DARK_ORB, false);
    }

    @Override
    public void tick() {
        super.tick();
        this.move(MoverType.SELF, new Vec3(0, 0.03, 0));
        Optional<Entity> passenger = Optional.ofNullable(this.getFirstPassenger());
        if (this.isDarkOrb()) {
            passenger.ifPresent((passengerEntity) -> {
                if (this.level() instanceof ServerLevel serverLevel
                        && passengerEntity.tickCount % (int) (WizardrySpells.ENTRAPMENT.get().getBaseProperty(EntrapmentRaySpell.DAMAGE_INTERVAL)) == 0
                ) {
                    if (this.getOwner() != null) {
                        passengerEntity.hurtServer(
                                serverLevel,
                                WizardryDamageSource.causeIndirectMagicDamage(
                                        WizardryDamageTypes.MAGIC.apply(registryAccess()),
                                        this,
                                        this.getOwner(),
                                        false
                                ),
                                1 * this.getDamageMultiplier());
                    } else {
                        passengerEntity.hurtServer(
                                serverLevel,
                                this.level().damageSources().magic(),
                                1 * this.getDamageMultiplier()
                        );
                    }
                }
            });
            for (int i = 0; i < 5; i++) {
                this.level().addParticle(ParticleTypes.PORTAL,
                        this.getX() + (this.getRandom().nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        this.getY() + this.getRandom().nextDouble() * (double) this.getBbHeight() + 0.5d,
                        this.getZ() + (this.getRandom().nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        (this.getRandom().nextDouble() - 0.5D) * 2.0D, -this.getRandom().nextDouble(),
                        (this.getRandom().nextDouble() - 0.5D) * 2.0D);
            }
            if (this.getLifetime() - this.tickCount == 75) {
                this.playSound(WizardrySounds.ENTITY_ENTRAPMENT_VANISH.get(), 1.5f, 1.0f);
            } else if (this.tickCount % 100 == 1 && this.tickCount < 150) {
                this.playSound(WizardrySounds.ENTITY_ENTRAPMENT_AMBIENT.get(), 1.5f, 1.0f);
            }
        }

        if (passenger.isEmpty() && this.tickCount > 1) {
            if (!this.isDarkOrb()) {
                this.playSound(WizardrySounds.ENTITY_BUBBLE_POP.get(), 1.5f, 1.0f);
            }
            this.discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.getEntityData().set(DATA_IS_DARK_ORB, valueInput.getBooleanOr("isDarkOrb", false));
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putBoolean("isDarkOrb", this.getEntityData().get(DATA_IS_DARK_ORB));
    }

    public boolean isDarkOrb() {
        return this.entityData.get(DATA_IS_DARK_ORB);
    }

    public void setDarkOrb(boolean darkOrb) {
        this.entityData.set(DATA_IS_DARK_ORB, darkOrb);
    }

    @Override
    public void remove(@NonNull RemovalReason reason) {
        if (reason == RemovalReason.DISCARDED) {
            if (this.getFirstPassenger() != null) {
                this.getFirstPassenger().stopRiding();
            }
            if (!this.isDarkOrb()) {
                this.level().playSound(null, this, WizardrySounds.ENTITY_BUBBLE_POP.get(), SoundSource.NEUTRAL, 1.5f, 1.0f);
            }
        }
        super.remove(reason);
    }

    @Override
    public @NonNull Vec3 getPassengerRidingPosition(@NonNull Entity passenger) {
        return this.position().add(0, 0.1, 0);
    }
}
