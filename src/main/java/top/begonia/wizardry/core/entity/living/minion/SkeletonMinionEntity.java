package top.begonia.wizardry.core.entity.living.minion;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.api.entity.hybrid.ISummonedCreature;
import top.begonia.wizardry.core.item.ArtefactItem;
import top.begonia.wizardry.core.registry.WizardryEntities;
import top.begonia.wizardry.core.registry.WizardryItems;

public class SkeletonMinionEntity extends Skeleton implements ISummonedCreature {
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(SkeletonMinionEntity.class, EntityDataSerializers.INT);
    private @Nullable EntityReference<LivingEntity> owner;

    public SkeletonMinionEntity(EntityType<? extends Skeleton> type, Level level) {
        super(type, level);
    }

    public SkeletonMinionEntity(Level level) {
        this(WizardryEntities.SKELETON_MINION.get(), level);
    }

    public static AttributeSupplier.@NonNull Builder createAttributes() {
        return Skeleton
                .createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0);
    }

    @Override
    public void setOwner(LivingEntity entity) {
        this.owner = EntityReference.of(entity);
    }

    @Override
    public int getLifetime() {
        return this.getEntityData().get(DATA_LIFETIME);
    }

    @Override
    public void setLifetime(int lifetime) {
        this.getEntityData().set(DATA_LIFETIME, lifetime);
    }

    @Override
    public @Nullable EntityReference<LivingEntity> getOwnerReference() {
        return this.owner;
    }

    @Override
    public @Nullable LivingEntity getOwner() {
        return EntityReference.getLivingEntity(this.owner, this.level());
    }

    @Override
    public void onSpawn() {
        this.spawnParticleEffect();
        if (this.getOwner() instanceof Player player && ArtefactItem.isArtefactActive(player, WizardryItems.CHARM_UNDEAD_HELMETS.get())) {
            setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
        }
    }

    @Override
    public void onDespawn() {
        this.spawnParticleEffect();
    }

    @Override
    public boolean hasParticleEffect() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_LIFETIME, -1);
    }

    @Override
    public void tick() {
        super.tick();
        this.updateDelegate();
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        EntityReference.store(this.owner, valueOutput, "owner");
        valueOutput.putInt("lifetime", this.getEntityData().get(DATA_LIFETIME));
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        this.owner = EntityReference.read(valueInput, "owner");
        this.getEntityData().set(DATA_LIFETIME, valueInput.getIntOr("lifetime", -1));
    }

    private void spawnParticleEffect() {
        if (this.level().isClientSide()) {
            for (int i = 0; i < 15; i++) {
                this.level().addParticle(
                        ParticleTypes.LARGE_SMOKE,
                        this.getX() + this.getRandom().nextFloat() - 0.5f,
                        this.getY() + this.getRandom().nextFloat() * this.getBbHeight(),
                        this.getZ() + this.getRandom().nextFloat() - 0.5f,
                        0, 0, 0
                );
            }
        }
    }
}
