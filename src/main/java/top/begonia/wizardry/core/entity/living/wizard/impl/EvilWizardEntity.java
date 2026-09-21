package top.begonia.wizardry.core.entity.living.wizard.impl;

import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.api.entity.hybrid.ISummonedCreature;
import top.begonia.wizardry.core.entity.living.wizard.AbstractWizardEntity;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.util.AllyDesignationSystem;
import top.begonia.wizardry.core.util.EntityUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EvilWizardEntity extends AbstractWizardEntity {
    public static final Identifier[] TEXTURES = new Identifier[6];
    public final Set<UUID> groupUUIDs = new HashSet<>();
    public boolean hasStructure = false;
    TargetingConditions.@Nullable Selector targetSelector;

    public EvilWizardEntity(EntityType<? extends EvilWizardEntity> type, Level level) {
        super(type, level);
        this.getNavigation().setCanOpenDoors(true);
        this.clearHome();
        if (level.isClientSide()) {
            for (int i = 0; i < TEXTURES.length; i++) {
                TEXTURES[i] = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/evil_wizard/evil_wizard_" + i + ".png");
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector = (entity, _) -> {
            if (!entity.isInvisible()
                    && AllyDesignationSystem.isValidTarget(EvilWizardEntity.this, entity)
            ) {
                return entity instanceof Player
                        || (entity instanceof ISummonedCreature
                        || entity instanceof WizardEntity
                        || ServerConfig.summonedCreatureTargetsWhitelist
                        .contains(EntityUtils.getIdentifier(entity))
                )
                        && !ServerConfig.summonedCreatureTargetsBlacklist
                        .contains(EntityUtils.getIdentifier(entity));
            }
            return false;
        };

        this.goalSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                0,
                false,
                true,
                this.targetSelector
        ));
    }

    @Override
    public Identifier[] getWizardTextures() {
        return TEXTURES;
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("hasStructure", this.hasStructure);
        var uuidsOutput = output.list("groupUUIDs", UUIDUtil.CODEC);
        this.groupUUIDs.forEach(uuidsOutput::add);
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.groupUUIDs.clear();
        valueInput.getBooleanOr("hasStructure", false);
        valueInput.listOrEmpty("groupUUIDs", UUIDUtil.CODEC).forEach(this.groupUUIDs::add);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return WizardrySounds.ENTITY_EVIL_WIZARD_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NonNull DamageSource source) {
        return WizardrySounds.ENTITY_EVIL_WIZARD_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return WizardrySounds.ENTITY_EVIL_WIZARD_DEATH.get();
    }
}
