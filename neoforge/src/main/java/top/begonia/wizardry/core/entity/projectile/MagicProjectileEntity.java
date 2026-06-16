package top.begonia.wizardry.core.entity.projectile;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.*;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.util.AllyDesignationSystem;

public abstract class MagicProjectileEntity extends ThrowableItemProjectile {

    public static final double LAUNCH_Y_OFFSET = 0.1;
    public static final int SEEKING_TIME = 15;
    public float damageMultiplier = 1.0f;
    protected static final EntityDataAccessor<Float> DAMAGE_MULTIPLIER_ACCESSOR =
            SynchedEntityData.defineId(MagicProjectileEntity.class, EntityDataSerializers.FLOAT);

    // 注册表使用
    public MagicProjectileEntity(EntityType<? extends MagicProjectileEntity> type, Level level) {
        super(type, level);
    }

    //玩家使用
    public MagicProjectileEntity(EntityType<? extends MagicProjectileEntity> type, LivingEntity owner, Level level, ItemStack itemStack) {
        super(type, owner, level, itemStack);
    }

    //发射器使用
    public MagicProjectileEntity(EntityType<? extends MagicProjectileEntity> type, double x, double y, double z, Level level, ItemStack itemStack) {
        super(type, x, y, z, level, itemStack);
    }

    public void aim(@NonNull LivingEntity caster, float speed) {
        this.setPos(caster.getX(), caster.getEyeY() - LAUNCH_Y_OFFSET, caster.getZ());
        this.setOwner(caster);
        this.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, speed, 1.0F);
    }

    public void aim(LivingEntity caster, @NonNull Entity target, float speed, float aimingError) {
        this.setOwner(caster);
        double startY = caster.getEyeY() - LAUNCH_Y_OFFSET;
        double dx = target.getX() - caster.getX();
        double dy = this.isNoGravity()
                ? target.getY() + (double) (target.getBbHeight() / 2.0F) - startY
                : target.getY() + (double) (target.getBbHeight() / 3.0F) - startY;
        double dz = target.getZ() - caster.getZ();
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        if (horizontalDistance >= 1.0E-7D) {
            double dxNormalised = dx / horizontalDistance;
            double dzNormalised = dz / horizontalDistance;
            this.setPos(caster.getX() + dxNormalised, startY, caster.getZ() + dzNormalised);
            float bulletDropCompensation = !this.isNoGravity() ? (float) horizontalDistance * 0.2F : 0.0F;
            this.shoot(dx, dy + (double) bulletDropCompensation, dz, speed, aimingError);
        }
    }

    public void setCaster(LivingEntity caster) {
        this.setOwner(caster);
    }

    public float getSeekingStrength() {
        if (this.getOwner() instanceof Player player) {
            // TODO: 在这里通过高版本的组件或装备栏检测玩家是否佩戴了索敌之戒 ring_seeking
            // 示例：if (ArtifactUtil.hasArtifact(player, WizardryItems.RING_SEEKING)) return 2.0F;
            return 0.0F;
        }
        return 0.0F;
    }

    protected void createParticles(Level level) {
    }

    @Override
    public void onRemoval(@NonNull RemovalReason reason) {
        super.onRemoval(reason);
        if (reason == RemovalReason.DISCARDED && this.level().isClientSide()) {
            this.createParticles(this.level());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getLifetime() >= 0 && this.tickCount > this.getLifetime()) {
            this.discard();
            return;
        }
        if (this.getSeekingStrength() > 0.0F) {
            Vec3 velocity = this.getDeltaMovement();
            Vec3 startPos = this.position();
            Vec3 endPos = startPos.add(velocity.scale(SEEKING_TIME));
            AABB boundingBox = this.getBoundingBox().expandTowards(velocity.scale(SEEKING_TIME)).inflate(this.getSeekingStrength());
            EntityHitResult hit = ProjectileUtil.getEntityHitResult(
                    this.level(),
                    this,
                    startPos,
                    endPos,
                    boundingBox,
                    entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.isAlive() && entity != this.getOwner()
            );

            if (hit != null) {
                Entity target = hit.getEntity();
                if (AllyDesignationSystem.isValidTarget(this.getOwner(), target)) {
                    Vec3 targetDirection = new Vec3(target.getX(), target.getY() + (double) (target.getBbHeight() / 2.0F), target.getZ())
                            .subtract(startPos)
                            .normalize()
                            .scale(velocity.length());
                    double motionX = velocity.x + 2.0D * (targetDirection.x - velocity.x) / SEEKING_TIME;
                    double motionY = velocity.y + 2.0D * (targetDirection.y - velocity.y) / SEEKING_TIME;
                    double motionZ = velocity.z + 2.0D * (targetDirection.z - velocity.z) / SEEKING_TIME;
                    this.setDeltaMovement(motionX, motionY, motionZ);
                }
            }
        }

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
            this.discard();
        }
        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.damageMultiplier = valueInput.getFloatOr("damageMultiplier", 0.0f);
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putFloat("damageMultiplier", 1.0f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DAMAGE_MULTIPLIER_ACCESSOR, 1.0f);
    }

    @Override
    public @NonNull Packet<ClientGamePacketListener> getAddEntityPacket(@NonNull ServerEntity serverEntity) {
        Entity owner = this.getOwner();
        return new ClientboundAddEntityPacket(this, serverEntity, owner == null ? 0 : owner.getId());
    }

    @Override
    public void recreateFromPacket(@NonNull ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        int ownerId = packet.getData();
        if (ownerId != 0) {
            Entity owner = this.level().getEntity(ownerId);
            if (owner != null) {
                this.setOwner(owner);
            }
        }
    }

    @Override
    public @NonNull SoundSource getSoundSource() {
        return SoundSource.PLAYERS;
    }

    public abstract int getLifetime();
}
