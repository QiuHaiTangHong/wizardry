package top.begonia.wizardry.core.entity.living;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.core.block.ReceptacleBlock;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.api.entity.atom.IAttachedEntity;
import top.begonia.wizardry.api.entity.ai.control.VexMoveControl;
import top.begonia.wizardry.core.entity.living.vex.AbstractVexFlavorEntity;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.registry.WizardrySounds;

import javax.annotation.Nullable;

public class RemnantEntity extends AbstractVexFlavorEntity implements IAttachedEntity {
    private static final EntityDataAccessor<Integer> ELEMENT = SynchedEntityData.defineId(
            RemnantEntity.class,
            EntityDataSerializers.INT
    );
    @Nullable
    private BlockPos boundOrigin;

    public RemnantEntity(EntityType<? extends RemnantEntity> type, Level level) {
        super(type, level);
        this.moveControl = new VexMoveControl<>(this);
    }

    public static AttributeSupplier.@NonNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, LivingEntity.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, RemnantEntity.class));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    protected void defineSynchedData(SynchedEntityData.@NonNull Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(ELEMENT, 1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ClientLevel clientLevel) {

            Vec3 centre = this.position().add(0, this.getBbHeight() / 2, 0);

            int[] colours = ReceptacleBlock.PARTICLE_COLOURS.get(this.getElement());

            if (this.random.nextInt(10) == 0) {
                WizardryClient.particleManager.getParticle(
                        clientLevel,
                        WizardryParticles.FLASH.get(),
                        0, this.getBbHeight() / 2, 0
                ).ifPresent(p -> p.scaleValue(this.getBbWidth())
                        .linkEntity(this)
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
                            WizardryParticles.DUST.get(),
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
                        WizardryParticles.DUST.get(),
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

    @Override
    protected SoundEvent getAmbientSound() {
        return WizardrySounds.ENTITY_REMNANT_AMBIENT.get();
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

    @SuppressWarnings("deprecation")
    public @Nullable SpawnGroupData finalizeSpawn(
            @NonNull ServerLevelAccessor level,
            @NonNull DifficultyInstance difficulty,
            @NonNull EntitySpawnReason spawnReason,
            @Nullable SpawnGroupData groupData
    ) {
        this.setElement(ElementEnum.values()[1 + this.random.nextInt(ElementEnum.values().length - 1)]); // Exclude MAGIC
        this.setBoundOrigin(BlockPos.containing(this.position()));
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
    }

    @Override
    public @NonNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public ElementEnum getElement() {
        return ElementEnum.values()[this.entityData.get(ELEMENT)];
    }

    public void setElement(@NonNull ElementEnum element) {
        this.entityData.set(ELEMENT, element.ordinal());
    }

    @Override
    public boolean checkSpawnRules(
            @NonNull LevelAccessor level,
            @NonNull EntitySpawnReason spawnReason
    ) {
        return true;
    }

    @Override
    public void updateDelegate() {

    }
}
