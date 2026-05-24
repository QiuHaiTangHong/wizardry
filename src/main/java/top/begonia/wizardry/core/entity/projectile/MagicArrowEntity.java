package top.begonia.wizardry.core.entity.projectile;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.*;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.damage.WizardryDamageType;
import top.begonia.wizardry.core.item.impl.ArtefactItem;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.util.AllyDesignationSystem;
import top.begonia.wizardry.core.util.RayTracer;

public abstract class MagicArrowEntity extends Arrow {

    public static final double LAUNCH_Y_OFFSET = 0.1;
    public static final int SEEKING_TIME = 15;

    private int knockbackStrength;
    public float damageMultiplier = 1.0f;

    public MagicArrowEntity(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
    }

    public void aim(LivingEntity caster, float speed) {

        this.setOwner(caster);

        this.setPos(caster.getX(), caster.getY() + (double) caster.getEyeHeight() - LAUNCH_Y_OFFSET, caster.getZ());
        this.setRot(caster.getYRot(), caster.getXRot());

        Vec3 lookVec = calculateViewVector(0, this.getYRot());

        this.setPos(this.getX() - lookVec.x * 0.16F, this.getY() - 0.10000000149011612D, this.getZ() - lookVec.z * 0.16F);

        this.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, speed * 1.5F, 1.0F);
    }

    public void aim(LivingEntity owner, @NonNull Entity target, float speed, float aimingError) {
        this.setOwner(owner);
        double spawnY = owner.getY() + (double) owner.getEyeHeight() - LAUNCH_Y_OFFSET;
        double dx = target.getX() - owner.getX();
        double dz = target.getZ() - owner.getZ();
        double dy = target.getY() + (double) (target.getBbHeight() / (this.doGravity() ? 3.0F : 2.0F)) - spawnY;
        double horizontalDistance = Mth.sqrt((float) (dx * dx + dz * dz));
        if (horizontalDistance >= 1.0E-7D) {
            this.setPos(owner.getX() + (dx / horizontalDistance), spawnY, owner.getZ() + (dz / horizontalDistance));
            float bulletDropCompensation = this.doGravity() ? (float) horizontalDistance * 0.2F : 0.0F;
            this.shoot(dx, dy + (double) bulletDropCompensation, dz, speed, aimingError);
        }
    }

    public abstract double getDamage();

    public abstract int getLifetime();

    public ResourceKey<DamageType> getDamageType() {
        return WizardryDamageType.MAGIC;
    }

    public boolean doGravity() {
        return true;
    }

    public boolean doDeceleration() {
        return true;
    }

    public boolean doOverpenetration() {
        return false;
    }

    public float getSeekingStrength() {
        return this.getOwner() instanceof Player player && ArtefactItem.isArtefactActive(player,
                WizardryItems.RING_SEEKING.get()) ? 2 : 0;
    }

    public void setKnockbackStrength(int knockback) {
        this.knockbackStrength = knockback;
    }

    protected void tickInAir() {
    }

    protected void tickInGround() {
        this.discard();
    }

    protected void onHitEntityHurtAfter(LivingEntity entity) {

    }

    protected void onHitBlockAfter(BlockHitResult hitResult) {

    }

    @Override
    protected void onHitEntity(@NonNull EntityHitResult hitResult) {
        if (this.level().isClientSide()) {
            return;
        }
        Entity target = hitResult.getEntity();
        ServerLevel serverLevel = (ServerLevel) this.level();
        DamageSource damageSource;
        if (this.getOwner() == null) {
            damageSource = this.damageSources().thrown(this, null);
        } else {
            damageSource = this.damageSources().indirectMagic(this, this.getOwner());
        }
        if (target.hurtServer(serverLevel, damageSource, (float) (this.getDamage() * this.damageMultiplier))) {
            if (target instanceof LivingEntity livingEntity) {
                this.onHitEntityHurtAfter(livingEntity);
                if (this.knockbackStrength > 0) {
                    Vec3 motion = this.getDeltaMovement();
                    double horizontalDist = motion.horizontalDistance();
                    if (horizontalDist > 0.0D) {
                        double strengthFactor = (double) this.knockbackStrength * 0.6D;
                        target.addDeltaMovement(new Vec3(
                                motion.x * strengthFactor / horizontalDist,
                                0.1D,
                                motion.z * strengthFactor / horizontalDist
                        ));
                    }
                }
                if (this.getOwner() instanceof LivingEntity livingOwner) {
                    EnchantmentHelper.doPostAttackEffects(serverLevel, livingOwner, damageSource);
                }
                if (this.getOwner() instanceof ServerPlayer serverPlayer
                        && target instanceof Player
                        && target != this.getOwner()) {
                    serverPlayer.connection.send(new ClientboundGameEventPacket(
                            ClientboundGameEventPacket.PLAY_ARROW_HIT_SOUND, 0.0F
                    ));
                }
            }
            if (!(target instanceof EnderMan) && !this.doOverpenetration()) {
                this.discard();
            }
        } else {
            if (!this.doOverpenetration()) {
                this.discard();
            }
        }
    }

    @Override
    protected void onHitBlock(@NonNull BlockHitResult hitResult) {
        this.onHitBlockAfter(hitResult);
        super.onHitBlock(hitResult);
    }

    @Override
    public void tick() {
        if (this.isRemoved()) {
            return;
        }
        if (!this.isInGround() && this.getSeekingStrength() > 0) {
            Vec3 velocity = this.getDeltaMovement();
            Vec3 scanStart = this.position();
            Vec3 scanEnd = scanStart.add(velocity.scale(SEEKING_TIME));
            var hit = RayTracer.rayTrace(
                    this.level(),
                    this.getOwner(),
                    scanStart,
                    scanEnd,
                    this.getSeekingStrength(),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    LivingEntity.class,
                    RayTracer.ignoreEntityFilter(null)
            );
            if (hit != null && AllyDesignationSystem.isValidTarget(this.getOwner(), hit.getEntity())) {
                Entity targetEntity = hit.getEntity();
                Vec3 direction = new Vec3(
                        targetEntity.getX(),
                        targetEntity.getY() + (targetEntity.getBbHeight() / 2.0F),
                        targetEntity.getZ()
                ).subtract(this.position())
                        .normalize()
                        .scale(velocity.length());

                Vec3 moveDelta = this.getDeltaMovement();
                this.setDeltaMovement(moveDelta.add(direction.subtract(moveDelta).scale(2.0D / SEEKING_TIME)));
            }
        }
        super.tick();
        if (this.getLifetime() >= 0 && this.tickCount > this.getLifetime()) {
            this.discard();
            return;
        }
        if (this.isInGround()) {
            this.tickInGround();
        } else {
            this.tickInAir();
        }
    }

    @Override
    protected boolean canHitEntity(@NonNull Entity entity) {
        if (entity.isPickable() && (entity != this.getOwner() || this.tickCount >= 5)) {
            return true;
        }
        return super.canHitEntity(entity);
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.damageMultiplier = valueInput.getFloatOr("damageMultiplier", 0.0F);
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putFloat("damageMultiplier", this.damageMultiplier);
    }
}