package top.begonia.wizardry.core.entity.living;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.core.block.ReceptacleBlock;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.registry.WizardrySounds;

import javax.annotation.Nullable;

// TODO 缺失AI逻辑
public class RemnantEntity extends Mob {
    /**
     * Data parameter for the remnant's element.
     */
    private static final EntityDataAccessor<Integer> ELEMENT = SynchedEntityData.defineId(
            RemnantEntity.class,
            EntityDataSerializers.INT
    );
    /**
     * Data parameter that tracks whether the remnant is currently attacking (charging).
     */
    private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(
            RemnantEntity.class,
            EntityDataSerializers.BOOLEAN
    );

    @Nullable
    private BlockPos boundOrigin;

    public RemnantEntity(EntityType<? extends RemnantEntity> type, Level level) {
        super(type, level);
    }

    protected void defineSynchedData(SynchedEntityData.@NonNull Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(ELEMENT, 1)
                .define(ATTACKING, false);
    }

    public static AttributeSupplier.@NonNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @SuppressWarnings("deprecation")
    public @org.jspecify.annotations.Nullable SpawnGroupData finalizeSpawn(
            @NonNull ServerLevelAccessor level,
            @NonNull DifficultyInstance difficulty,
            @NonNull EntitySpawnReason spawnReason,
            @org.jspecify.annotations.Nullable SpawnGroupData groupData
    ) {
        this.setElement(ElementEnum.values()[1 + this.random.nextInt(ElementEnum.values().length - 1)]); // Exclude MAGIC
        this.setBoundOrigin(BlockPos.containing(this.position()));
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
    }

    public ElementEnum getElement() {
        return ElementEnum.values()[this.entityData.get(ELEMENT)];
    }

    public void setElement(@NonNull ElementEnum element) {
        this.entityData.set(ELEMENT, element.ordinal());
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    @Nullable
    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos boundOriginIn) {
        this.boundOrigin = boundOriginIn;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return WizardrySounds.ENTITY_REMNANT_AMBIENT.get();
    }

    @Override
    public @NonNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return WizardrySounds.ENTITY_REMNANT_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NonNull DamageSource source) {
        return WizardrySounds.ENTITY_REMNANT_HURT.get();
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putInt("Element", this.getElement().ordinal());
        valueOutput.storeNullable("BoundOrigin", BlockPos.CODEC, this.boundOrigin);
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.setElement(ElementEnum.values()[valueInput.getIntOr("Element", 0)]);
        valueInput.read("BoundOrigin", BlockPos.CODEC).ifPresent(pos -> this.boundOrigin = pos);
    }

    @Override
    public void tick() {

        // 使用和 EntityVex 一样的技巧穿过物体飞行
        this.noPhysics = true;
        super.tick();
        this.noPhysics = true;

        this.setNoGravity(true);

        if (this.level() instanceof ClientLevel clientLevel) {

            Vec3 centre = this.position().add(0, this.getBbHeight() / 2, 0);

            int[] colours = ReceptacleBlock.PARTICLE_COLOURS.get(this.getElement());

            if (this.random.nextInt(10) == 0) {
                WizardryClient.particleManager.getParticle(
                        clientLevel,
                        new QuadParticleOptions(WizardryParticles.FLASH.get()),
                        0, this.getBbHeight() / 2, 0
                ).ifPresent(p -> p.scaleValue(this.getBbWidth())
                        .targetEntity(this)
                        .time(48)
                        .color(colours[0])
                        .spawn()
                );
            }

            double r = this.getBbWidth() / 3;

            double x = r * (this.random.nextDouble() * 2 - 1);
            double y = r * (this.random.nextDouble() * 2 - 1);
            double z = r * (this.random.nextDouble() * 2 - 1);

            if (this.deathTime > 0) {
                // Spew out particles on death
                for (int i = 0; i < 8; i++) {
                    WizardryClient.particleManager.getParticle(
                            clientLevel,
                            this.random,
                            new QuadParticleOptions(WizardryParticles.DUST.get()),
                            centre.x + x, centre.y + y, centre.z + z,
                            0.1, true
                    ).ifPresent(p -> p.time(12)
                            .color(colours[1])
                            .endColor(colours[2])
                            .spawn()
                    );
                }
            } else {
                WizardryClient.particleManager.getParticle(
                        clientLevel,
                        new QuadParticleOptions(WizardryParticles.DUST.get()),
                        centre.x + x, centre.y + y, centre.z + z
                ).ifPresent(p -> p.speed(x * -0.03, 0.02, z * -0.03)
                        .time(24 + this.random.nextInt(8))
                        .color(colours[1])
                        .endColor(colours[2])
                        .spawn()
                );
            }
        }

    }
}
