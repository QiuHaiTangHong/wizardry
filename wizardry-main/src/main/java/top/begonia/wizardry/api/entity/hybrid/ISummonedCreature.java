package top.begonia.wizardry.api.entity.hybrid;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.item.ISpellCastingItem;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.data.player.WizardPlayerDataOperator;
import top.begonia.wizardry.api.entity.atom.ILifeTicksEntity;
import top.begonia.wizardry.api.entity.atom.IOwnableEntity;
import top.begonia.wizardry.api.entity.atom.ISummonEntity;
import top.begonia.wizardry.core.entity.living.wizard.impl.WizardEntity;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.util.AllyDesignationSystem;

public interface ISummonedCreature extends IOwnableEntity, ILifeTicksEntity, ISummonEntity {
    String NAMEPLATE_TRANSLATION_KEY = "entity." + Wizardry.MODID + ".summonedcreature.nameplate";

    static void onEntityTickEventPre(@NonNull LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof ISummonedCreature summoner) {
            event.setCanceled(true);
            DamageSource source = event.getSource();
            Entity directEntity = source.getDirectEntity();
            RegistryAccess registryAccess = event.getEntity().level().registryAccess();
            Holder<DamageType> type = source instanceof WizardryDamageSource
                    ? source.typeHolder()
                    : WizardryDamageTypes.MAGIC.apply(registryAccess);
            boolean isRetaliatory = source instanceof WizardryDamageSource wizardryDamageSource
                    && wizardryDamageSource.isRetaliatory();
            if (source.getDirectEntity() == source.getEntity()) {
                source = WizardryDamageSource.causeDirectMinionDamage(type, summoner.selfThisLivingEntity(), summoner.getOwner(), isRetaliatory);
            } else if (source.getEntity() != source.getDirectEntity()) {
                source = WizardryDamageSource.causeIndirectMinionDamage(type, directEntity, summoner.selfThisLivingEntity(), summoner.getOwner(), isRetaliatory);
            }
        }
    }

    default Entity selfThisLivingEntity() {
        if (this instanceof LivingEntity livingEntity) {
            return livingEntity;
        } else {
            throw new ClassCastException("ISummonedCreature 必须继承自 Entity，但当前是: " + this.getClass().getName());
        }
    }

    default boolean isValidTarget(Entity target) {
        if (AllyDesignationSystem.isValidTarget(this.getOwner(), target)) {
            if (target instanceof Player player) {
                if (this.getOwner() instanceof WizardEntity wizardEntity) {
                    return wizardEntity.getLastHurtByMob() != player;
                }
                return true;
            }
            return (target instanceof Mob
                    || target instanceof ISummonedCreature
                    || (target instanceof WizardEntity && !(this.getOwner() instanceof WizardEntity))
                    || (target instanceof LivingEntity livingEntity && livingEntity.getLastHurtByMob() == this.getOwner())
                    || ServerConfig.summonedCreatureTargetsWhitelist.contains(BuiltInRegistries.ENTITY_TYPE.getKey(target.getType())))
                    && !ServerConfig.summonedCreatureTargetsBlacklist.contains(BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()));
        }
        return false;
    }

    default TargetingConditions.Selector getTargetSelector() {
        return (entity, _) -> !entity.isInvisible()
                && (this.getOwner() == null
                ? entity instanceof Player player
                && !player.isCreative() : isValidTarget(entity));
    }

    default boolean hasAnimation() {
        return true;
    }

    default int getAnimationColour(float animationProgress) {
        return ARGB.color(255, 0, 0, 0);
    }

    default void onSuccessfulAttack(LivingEntity target) {
    }

    default boolean shouldLastHurtByMob(LivingEntity entity) {
        return ServerConfig.minionRevengeTargeting || isValidTarget(entity);
    }

    @Override
    default void updateDelegate() {
        if (!(this instanceof Entity thisEntity)) {
            throw new ClassCastException("Implementations of ISummonedCreature must extend SoundLoopSpellEntity!");
        }
        if (thisEntity.tickCount == 1) {
            this.onSpawn();
        }
        if (thisEntity.tickCount > this.getLifetime() && this.getLifetime() > 0) {
            this.onDespawn();
            thisEntity.discard();
        }
        if (this.hasParticleEffect()
                && thisEntity.level() instanceof ClientLevel clientLevel
                && thisEntity.getRandom().nextInt(8) == 0
        ) {
            WizardryClient.particleManager.getParticle(
                    clientLevel,
                    new QuadParticleOptions(WizardryParticles.DARK_MAGIC.get()),
                    thisEntity.getX(), thisEntity.getY() + thisEntity.getRandom().nextDouble() * 1.5, thisEntity.getZ()
            ).ifPresent(p -> p.color(0.1f, 0.0f, 0.0f)
                    .spawn()
            );
        }
    }

    default boolean interactDelegate(@NonNull Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        WizardPlayerDataOperator dataOperator = WizardPlayerDataOperator.get(player);
        if (player.isShiftKeyDown() && stack.getItem() instanceof ISpellCastingItem) {
            if (!player.level().isClientSide() && this.getOwner() == player) {
                dataOperator.getSelectedMinion().ifPresentOrElse(
                        (iSummonedCreature) -> dataOperator.setSelectedMinion(null),
                        () -> dataOperator.setSelectedMinion(this)
                );
            }
            return true;
        }
        return false;
    }
}
