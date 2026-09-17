package top.begonia.wizardry.api.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.entity.atom.ICustomHitbox;
import top.begonia.wizardry.api.particle.extension.FacingCameraMode;
import top.begonia.wizardry.api.particle.extension.Layer;
import top.begonia.wizardry.api.particle.extension.TextureParticle;
import top.begonia.wizardry.api.particle.extension.builder.ParticleBuilder;
import top.begonia.wizardry.api.particle.extension.extract.*;
import top.begonia.wizardry.api.particle.options.IParticleOptionsExtension;
import top.begonia.wizardry.api.particle.renderer.CompositeQuadParticleRenderState;

import javax.annotation.Nullable;
import java.util.List;

public abstract class CompositeQuadParticle<T extends IParticleOptionsExtension> extends Particle implements ParticleBuilder {
    public static final ParticleRenderType RENDER_TYPE = new ParticleRenderType("wizardry_composite_quad", "WCQ");
    private static final double SPREAD_FACTOR = 0.2;
    private static final double IMPACT_FRICTION = 0.2;
    protected ParticleOptions options;
    protected ExtractFlow extractFlow = new ExtractFlow();
    /**
     * 是否启用阴影
     */
    protected boolean shaded = false;
    protected float roll;
    protected float oRoll;
    /**
     * 组成粒子的面片大小
     */
    protected float quadSize;
    /**
     * 粒子的速度
     */
    protected double speed;
    /**
     * 粒子的角度
     */
    protected float angle;
    /**
     * 粒子的范围
     */
    protected double radius = 0;
    @Nullable
    protected EntityReference<Entity> linkEntityReference = null;
    protected double relativeX, relativeY, relativeZ;
    protected double prevVelX, prevVelY, prevVelZ;
    protected double relativeMotionX, relativeMotionY, relativeMotionZ;
    /**
     * 粒子的当前俯仰和偏航角
     */
    protected float yaw, pitch;

    public CompositeQuadParticle(
            ClientLevel level,
            @NonNull T options,
            double x, double y, double z
    ) {
        super(level, x, y, z, options.xa(), options.ya(), options.za());
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.friction = 0.9800000190734863F;
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double z() {
        return this.z;
    }

    @Override
    public @NonNull ParticleBuilder scaleValue(float scale) {
        this.quadSize *= scale;
        this.setSize(0.2F * scale, 0.2F * scale);
        return this;
    }

    @Override
    public ParticleBuilder time(int time) {
        this.lifetime = time;
        return this;
    }

    @Override
    public ParticleBuilder speed(double xd, double yd, double zd) {
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        return this;
    }

    @Override
    public ParticleBuilder startColor(float red, float green, float blue) {
        this.extractFlow.setStartColor(red, green, blue);
        return this;
    }

    @Override
    public ParticleBuilder currentColor(float red, float green, float blue) {
        this.extractFlow.setColor(red, green, blue);
        return this;
    }

    @Override
    public ParticleBuilder endColor(float red, float green, float blue) {
        this.extractFlow.setEndColor(red, green, blue);
        return this;
    }

    @Override
    public ParticleBuilder alpha(float alpha) {
        this.extractFlow.setAlpha(alpha);
        return this;
    }

    @Override
    public ParticleBuilder shaded(boolean shaded) {
        this.shaded = shaded;
        return this;
    }

    @Override
    public ParticleBuilder gravity(boolean gravity) {
        this.gravity = gravity ? 1.0F : 0.0F;
        return this;
    }

    @Override
    public ParticleBuilder spin(double radius, double speed) {
        this.radius = radius;
        this.speed = speed * 2 * Math.PI;
        this.angle = this.random.nextFloat() * (float) Math.PI * 2;

        this.x = relativeX - radius * Mth.cos(angle);
        this.z = relativeZ + radius * Mth.sin(angle);

        this.relativeMotionX = xd;
        this.relativeMotionY = yd;
        this.relativeMotionZ = zd;

        return this;
    }

    @Override
    public ParticleBuilder facing(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        return this;
    }

    @Override
    public ParticleBuilder linkEntity(Entity linkEntity) {
        this.linkEntityReference = EntityReference.of(linkEntity);
        return this;
    }

    @Override
    public ParticleBuilder targetPosition(double x, double y, double z) {
        return this;
    }

    @Override
    public ParticleBuilder targetVelocity(double vx, double vy, double vz) {
        return this;
    }

    @Override
    public ParticleBuilder targetEntity(Entity target) {
        return this;
    }

    @Override
    public ParticleBuilder length(double length) {
        return this;
    }

    @Override
    public ParticleBuilder physics(boolean hasPhysics) {
        this.hasPhysics = hasPhysics;
        return this;
    }

    @Override
    public void spawn() {
        Minecraft.getInstance().particleEngine.add(this);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.yd -= 0.04D * (double) this.gravity;
        this.move(this.xd, this.yd, this.zd);
        if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }
        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;

        if (this.onGround && !this.hasPhysics) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
        }

        Entity linkEntity = this.getLinkEntity();
        if (linkEntity != null || this.radius > 0) {
            double tx = this.relativeX;
            double ty = this.relativeY;
            double tz = this.relativeZ;
            if (linkEntity != null) {
                if (linkEntity.isRemoved()) {
                    this.remove();
                } else {
                    Vec3 entityCenter = linkEntity.getBoundingBox().getCenter();
                    tx += entityCenter.x();
                    ty += entityCenter.y();
                    tz += entityCenter.z();
                }
            }
            if (this.radius > 0) {
                this.angle += (float) this.speed;
                tx += this.radius * -Mth.cos(this.angle);
                tz += this.radius * Mth.sin(this.angle);
            }
            this.setPos(tx, ty, tz);

            this.relativeX += this.relativeMotionX;
            this.relativeY += this.relativeMotionY;
            this.relativeZ += this.relativeMotionZ;
        }

        if (this instanceof TextureParticle textureParticle) {
            textureParticle.setSpriteFromAge();
        }

        if (this.hasPhysics) {
            if (this.xd == 0 && this.prevVelX != 0) {
                this.yd *= IMPACT_FRICTION;
                this.zd *= IMPACT_FRICTION;
                this.yd += (this.random.nextDouble() * 2 - 1) * this.prevVelX * SPREAD_FACTOR;
                this.zd += (this.random.nextDouble() * 2 - 1) * this.prevVelX * SPREAD_FACTOR;
            }

            if (this.yd == 0 && this.prevVelY != 0) {
                this.xd *= IMPACT_FRICTION;
                this.zd *= IMPACT_FRICTION;
                this.xd += (this.random.nextDouble() * 2 - 1) * this.prevVelY * SPREAD_FACTOR;
                this.zd += (this.random.nextDouble() * 2 - 1) * this.prevVelY * SPREAD_FACTOR;
            }

            if (this.zd == 0 && this.prevVelZ != 0) {
                this.xd *= IMPACT_FRICTION;
                this.yd *= IMPACT_FRICTION;
                this.xd += (this.random.nextDouble() * 2 - 1) * this.prevVelZ * SPREAD_FACTOR;
                this.yd += (this.random.nextDouble() * 2 - 1) * this.prevVelZ * SPREAD_FACTOR;
            }
            double searchRadius = 20.0D;
            AABB searchBox = new AABB(this.x, this.y, this.z, this.x, this.y, this.z).inflate(searchRadius);
            List<Entity> nearbyEntities = this.level.getEntities(null, searchBox);
            Vec3 currentPos = new Vec3(this.x, this.y, this.z);
            Vec3 previousPos = new Vec3(this.xo, this.yo, this.zo);
            for (Entity e : nearbyEntities) {
                if (e instanceof ICustomHitbox customHitbox) {
                    if (customHitbox.calculateIntercept(currentPos, previousPos, 0) != null) {
                        this.remove();
                        break;
                    }
                }
            }
        }
        this.prevVelX = this.xd;
        this.prevVelY = this.yd;
        this.prevVelZ = this.zd;
    }

    @Override
    public @NonNull ParticleRenderType getGroup() {
        return CompositeQuadParticle.RENDER_TYPE;
    }

    @Override
    protected int getLightCoords(float partialTick) {
        if (this.shaded) {
            return super.getLightCoords(partialTick);
        } else {
            return LightCoordsUtil.FULL_BRIGHT;
        }
    }

    protected Entity getLinkEntity() {
        if (this.linkEntityReference != null) {
            return this.linkEntityReference.getEntity(this.level, Entity.class);
        }
        return null;
    }

    @SuppressWarnings("unused")
    public float getQuadSize(float partialTick) {
        return this.quadSize;
    }

    public FacingCameraMode getFacingCameraMode() {
        return FacingCameraMode.LOOK_AT_XYZ;
    }

    /**
     * 渲染提交的入口，实际上现在的实现并不是性能最好的，但是为了可读性，采用现在的设计。
     * 如果你不想使用 extractFlow 可以直接在这里完成所有的数据抽取和计算，
     * 但是，除非你在有把握实现正确的效果，不然请继续使用现有设计。
     *
     * @param state       渲染状态
     * @param camera      摄像机
     * @param partialTick 帧间时差
     */
    public void extract(CompositeQuadParticleRenderState state, @NonNull Camera camera, float partialTick) {
        this.extractFlow.beginExtraction(
                state,
                this.getLayer(),
                camera,
                this.getLinkEntity(),
                this.xo, this.yo, this.zo,
                this.x, this.y, this.z,
                this.getQuadSize(partialTick),
                partialTick,
                this.age,
                this.lifetime,
                this.getLightCoords(partialTick)
        );
        this.extractGlobalAdditionalData(extractFlow);
        this.extractPosition(extractFlow);
        this.extractRotation(extractFlow);
        this.extractSize(extractFlow);
        this.extractColor(extractFlow);
        this.extractUv(extractFlow);
        this.extractSurface(extractFlow);
    }

    /**
     * 在每一帧开始抽取时，向 extractFlow 注入不在标准流数据中的数据。
     * 这个方法必须在其它extract*之前。
     *
     * @param extractFlow 拥有全部当前渲染数据的流
     */
    protected void extractGlobalAdditionalData(ExtractFlow extractFlow) {
    }

    protected void extractPosition(@NonNull IPositionFlowOperation positionOperation) {
        Vector3f camePos = positionOperation.getCamera().position().toVector3f();
        Vector3d oldPos = positionOperation.getOldPos();
        Vector3d pos = positionOperation.getPosition();
        positionOperation.setPosition(pos.sub(oldPos)
                .mul(positionOperation.getPartialTick())
                .add(oldPos)
                .sub(camePos)
        );
    }

    protected void extractRotation(@NonNull IRotateFlowOperation rotateOperation) {
        Quaternionf rotation = rotateOperation.getRotate();
        this.getFacingCameraMode().setRotation(rotation, rotateOperation.getCamera(), rotateOperation.getPartialTick());
        if (this.roll != 0.0F) {
            rotation.rotateZ(Mth.lerp(rotateOperation.getPartialTick(), this.oRoll, this.roll));
        }
        rotateOperation.setRotate(rotation);
    }

    protected void extractSize(ISizeFlowOperation sizeFlowOperation) {
    }

    protected void extractColor(@NonNull IColorFlowOperation colorFlowOperation) {
        // 计算当前的颜色
        float ageFraction = colorFlowOperation.getAge() / colorFlowOperation.getLifetime();
        Vector3f startColor = colorFlowOperation.getStartColor();
        Vector3f endColor = colorFlowOperation.getEndColor();
        colorFlowOperation.setVecColor(endColor.sub(startColor).mul(ageFraction).add(startColor));
    }

    public void extractUv(IUvFlowOperation uvOperation) {
        if (this instanceof TextureParticle textureParticle) {
            uvOperation.setU0(textureParticle.getU0())
                    .setU1(textureParticle.getU1())
                    .setV0(textureParticle.getV0())
                    .setV1(textureParticle.getV1());
        }
    }

    protected abstract void extractSurface(ExtractFlow extractFlow);

    protected Layer getLayer() {
        if (this instanceof TextureParticle textureParticle) {
            return Layer.bySprite(textureParticle.getCurrentAtlasSprite(textureParticle.getCurrentRawIndex()));
        }
        return Layer.DEFAULT_QUAD;
    }
}
