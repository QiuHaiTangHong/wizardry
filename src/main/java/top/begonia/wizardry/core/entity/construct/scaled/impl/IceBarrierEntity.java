package top.begonia.wizardry.core.entity.construct.scaled.impl;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.entity.atom.ICustomHitbox;
import top.begonia.wizardry.core.entity.construct.MagicConstructEntity;
import top.begonia.wizardry.core.entity.construct.scaled.ScaledConstructEntity;
import top.begonia.wizardry.core.registry.WizardryEntities;
import top.begonia.wizardry.core.registry.WizardrySounds;

public class IceBarrierEntity extends ScaledConstructEntity implements ICustomHitbox {
    private static final double THICKNESS = 0.4;
    private static final EntityDataAccessor<Integer> DATA_DELAY = SynchedEntityData.defineId(
            IceBarrierEntity.class,
            EntityDataSerializers.INT
    );

    public IceBarrierEntity(EntityType<? extends IceBarrierEntity> type, Level level) {
        super(type, level);
    }

    public IceBarrierEntity(Level level) {
        this(WizardryEntities.ICE_BARRIER.get(), level);
    }

    @Override
    public void setYRot(float yRot) {
        super.setYRot(yRot);
        float angle = this.getYRot() * Mth.DEG_TO_RAD;
        float a = Mth.cos(angle);
        float b = Mth.sin(angle);
        double x = this.getBbWidth() / 2 * a + THICKNESS / 2 * b;
        double z = this.getBbWidth() / 2 * b + THICKNESS / 2 * a;
        this.setBoundingBox(new AABB(
                this.getX() - x,
                this.getY(),
                this.getZ() - z,
                this.getX() + x,
                this.getY() + this.getBbHeight(),
                this.getZ() + z
        ));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DELAY, 0);
    }

    public void setDelay(int delay) {
        this.entityData.set(DATA_DELAY, delay);
    }

    public int getDelay() {
        return this.entityData.get(DATA_DELAY);
    }

    @Override
    public void tick() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        int lifetime = this.getLifetime();
        int delay = this.getDelay();
        float sizeMultiplier = this.getSizeMultiplier();

        if (!this.level().isClientSide()) {

            double extensionSpeed = 0;
            if (lifetime - this.tickCount < 20) {
                extensionSpeed = -0.01 * (this.tickCount - (lifetime - 20)) * sizeMultiplier;
            } else if (this.tickCount > 3 + delay) {
                extensionSpeed = 0;
            } else if (this.tickCount > delay) {
                extensionSpeed = 0.5 * sizeMultiplier;
            }

            this.move(MoverType.SELF, new Vec3(0, extensionSpeed, 0));
        }

        if (this.tickCount == delay + 1) {
            this.playSound(WizardrySounds.ENTITY_ICE_BARRIER_EXTEND.get(), 1, 1.5f);
        }

        super.tick();

        Vec3 look = this.getLookAngle();

        if (!this.level().isClientSide()) {

            for (Entity entity : this.level().getEntities(this, this.getBoundingBox().deflate(2))) {

                if (entity instanceof MagicConstructEntity) {
                    continue;
                }

                if (!entity.getBoundingBox().intersects(this.getBoundingBox())) {
                    continue;
                }

                // For some reason the player position seems to be off by 1 block in x and z, no idea how so for now
                // I've just fudged it by adding 1 to x and z
                double perpendicularDist = getSignedPerpendicularDistance(entity.position().add(1, 0, 1));

                if (Math.abs(perpendicularDist) < entity.getBbWidth() / 2 + THICKNESS / 2) {

                    double velocity = 0.25 * Math.signum(perpendicularDist);
                    entity.addDeltaMovement(new Vec3(velocity * look.x, 0, velocity * look.z));
                }
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.setDelay(valueInput.getIntOr("delay", this.getDelay()));
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putInt("delay", this.getDelay());
    }

    @Override
    public Vec3 calculateIntercept(Vec3 origin, @NonNull Vec3 endpoint, float fuzziness) {
        // Calculate the point at which the line intersects the barrier plane
        Vec3 vec = endpoint.subtract(origin);

        double perpendicularDist = getPerpendicularDistance(origin);
        double perpendicularDist2 = getPerpendicularDistance(endpoint);

        Vec3 intercept = origin.add(vec.scale(perpendicularDist / (perpendicularDist + perpendicularDist2)));

        // This seems to be all over the palce, but the calculation MUST be right because it works for entity collisions!
//		world.spawnParticle(EnumParticleTypes.END_ROD, intercept.x, intercept.y, intercept.z, 0, 0, 0);

        // If the point is within the hitbox (expanded by the fuzziness), it was a hit
        return this.getBoundingBox().deflate(fuzziness).contains(intercept) ? intercept : null;
    }

    @Override
    public boolean contains(Vec3 point) {
        return this.getBoundingBox().contains(point) && getPerpendicularDistance(point) < THICKNESS / 2;
    }

    private double getPerpendicularDistance(Vec3 point) {
        return Math.abs(getSignedPerpendicularDistance(point));
    }

    private double getSignedPerpendicularDistance(@NonNull Vec3 point) {
        Vec3 look = this.getLookAngle();
        Vec3 delta = new Vec3(point.x - this.getX(), 0, point.z - this.getZ());
        return delta.dot(look);
    }
}
