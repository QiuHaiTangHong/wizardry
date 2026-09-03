package top.begonia.wizardry.core.entity.living.minion;

import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import top.begonia.wizardry.core.entity.living.ISummonedCreature;
import top.begonia.wizardry.core.item.ArtefactItem;
import top.begonia.wizardry.core.registry.WizardryEntities;
import top.begonia.wizardry.core.registry.WizardryEntityDataSerializers;
import top.begonia.wizardry.core.registry.WizardryItems;

import java.util.Optional;
import java.util.UUID;

public class SkeletonMinionEntity extends Skeleton implements ISummonedCreature {
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(SkeletonMinionEntity.class, WizardryEntityDataSerializers.CASTER_UUID.get());
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(SkeletonMinionEntity.class, EntityDataSerializers.INT);

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
    public int getLifetime() {
        return this.getEntityData().get(DATA_LIFETIME);
    }

    @Override
    public void setLifetime(int lifetime) {
        this.getEntityData().set(DATA_LIFETIME, lifetime);
    }

    @Override
    public @Nullable Entity getOwner() {
        return this.getEntityData().get(DATA_OWNER_UUID).map(uuid -> this.level().getEntity(uuid)).orElse(null);
    }

    @Override
    public void setOwner(Entity entity) {
        if (entity != null) {
            this.getEntityData().set(DATA_OWNER_UUID, Optional.of(entity.getUUID()));
        }
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
        builder.define(DATA_OWNER_UUID, Optional.empty());
        builder.define(DATA_LIFETIME, -1);
    }

    @Override
    public void tick() {
        super.tick();
        this.updateDelegate();
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        valueOutput.storeNullable("ownerUUID", UUIDUtil.CODEC, this.getEntityData().get(DATA_OWNER_UUID).orElse(null));
        valueOutput.putInt("lifetime", this.getEntityData().get(DATA_LIFETIME));
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        this.getEntityData().set(DATA_OWNER_UUID, valueInput.read("ownerUUID", UUIDUtil.CODEC));
        this.getEntityData().set(DATA_LIFETIME, valueInput.getIntOr("lifetime", -1));
    }

    private void spawnParticleEffect() {
        if (this.level().isClientSide()) {
            for (int i = 0; i < 15; i++) {
                this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getX() + this.getRandom().nextFloat() - 0.5f,
                        this.getY() + this.getRandom().nextFloat() * this.getBbHeight(), this.getZ() + this.getRandom().nextFloat() - 0.5f, 0, 0, 0);
            }
        }
    }
}
