package top.begonia.wizardry.api.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.extension.FacingCameraMode;
import top.begonia.wizardry.api.particle.extension.Layer;
import top.begonia.wizardry.api.particle.extension.ParticleBuilder;
import top.begonia.wizardry.api.particle.extension.TextureParticle;
import top.begonia.wizardry.api.particle.options.IParticleOptionsExtension;
import top.begonia.wizardry.api.particle.renderer.state.CompositeQuadParticleRenderState;
import top.begonia.wizardry.core.entity.ICustomHitbox;

import javax.annotation.Nullable;
import java.util.List;

public abstract class CompositeQuadParticle<T extends IParticleOptionsExtension> extends Particle  implements ParticleBuilder {
    public static final ParticleRenderType RENDER_TYPE = new ParticleRenderType("wizardry_composite_quad", "WCQ");
    private static final double SPREAD_FACTOR = 0.2;
    private static final double IMPACT_FRICTION = 0.2;
    public ParticleOptions options;
    /**
     * 粒子的开始颜色
     */
    protected float startRed = 1.0f, startGreen = 1.0f, startBlue = 1.0f;
    /**
     * 粒子的当前颜色
     */
    protected float currentRed = 1.0f, currentGreen = 1.0f, currentBlue = 1.0f;
    /**
     * 粒子的结束颜色 ARGB 格式
     */
    protected float endRed = 1.0f, endGreen = 1.0f, endBlue = 1.0f;
    /**
     * 粒子的透明度
     */
    protected float alpha = 1.0f;
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
    protected Entity entity = null;
    protected double relativeX, relativeY, relativeZ;
    protected double prevVelX, prevVelY, prevVelZ;
    protected double relativeMotionX, relativeMotionY, relativeMotionZ;
    /**
     * 粒子的当前俯仰和偏航角
     */
    protected float yaw = Float.NaN, pitch = Float.NaN;

    public CompositeQuadParticle(
            ClientLevel level,
            @NonNull T options,
            double x, double y, double z
    ) {
        super(level, x, y, z, options.xa(), options.ya(), options.za());
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
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
    public ParticleBuilder time(int time){
        this.lifetime = time;
        return this;
    }

    @Override
    public ParticleBuilder speed(double xd, double yd, double zd){
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        return this;
    }

    @Override
    public ParticleBuilder startColor(float red, float green, float blue) {
        this.startRed = red;
        this.startGreen = green;
        this.startBlue = blue;
        return this;
    }

    @Override
    public ParticleBuilder currentColor(float red, float green, float blue) {
        this.currentRed = red;
        this.currentGreen = green;
        this.currentBlue = blue;
        return this;
    }

    @Override
    public ParticleBuilder endColor(float red, float green, float blue) {
        this.endRed = red;
        this.endGreen = green;
        this.endBlue = blue;
        return this;
    }

    @Override
    public ParticleBuilder alpha(float alpha) {
        this.alpha = alpha;
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
    public ParticleBuilder targetPosition(double x, double y, double z) {
        return this;
    }

    @Override
    public ParticleBuilder targetVelocity(double vx, double vy, double vz) {
        return this;
    }

    @Override
    public ParticleBuilder targetEntity(Entity target) {
        this.entity = target;
        if (entity != null) {
            this.setPos(entity.getX() + relativeX, entity.getY() + relativeY, entity.getZ() + relativeZ);
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            this.relativeMotionX = xd;
            this.relativeMotionY = yd;
            this.relativeMotionZ = zd;
        }
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
    public void spawn(){
        Minecraft.getInstance().particleEngine.add(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.hasPhysics && this.onGround) {
            this.xd /= 0.699999988079071D;
            this.zd /= 0.699999988079071D;
        }
        if (this.entity != null || this.radius > 0) {
            double tx = this.relativeX;
            double ty = this.relativeY;
            double tz = this.relativeZ;
            if (this.entity != null) {
                if (this.entity.isRemoved()) {
                    this.remove();
                    return;
                } else {
                    tx += this.entity.getX();
                    ty += this.entity.getY();
                    tz += this.entity.getZ();
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

        // 计算当前的颜色
        float ageFraction = (float) this.age / (float) this.lifetime;
        this.currentRed = this.startRed + (this.endRed - this.startRed) * ageFraction;
        this.currentGreen = this.startGreen + (this.endGreen - this.startGreen) * ageFraction;
        this.currentBlue = this.startBlue + (this.endBlue - this.startBlue) * ageFraction;

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

    @SuppressWarnings("unused")
    public float getQuadSize(float partialTick) {
        return this.quadSize;
    }

    protected @NonNull Quaternionf calculateRotation(Camera camera, float partialTickTime) {
        Quaternionf rotation = new Quaternionf();
        this.getFacingCameraMode().setRotation(rotation, camera, partialTickTime);
        if (this.roll != 0.0F) {
            rotation.rotateZ(Mth.lerp(partialTickTime, this.oRoll, this.roll));
        }
        return rotation;
    }

    public FacingCameraMode getFacingCameraMode() {
        return FacingCameraMode.LOOK_AT_XYZ;
    }

    protected abstract void extractSurface(
            CompositeQuadParticleRenderState state,
            Layer layer,
            @NonNull Camera camera,
            float partialTick,
            float lerpX, float lerpY, float lerpZ,
            Quaternionf rotation,
            float scale,
            float u0, float u1,
            float v0, float v1,
            int color, int lightCoords
    );

    public void extract(CompositeQuadParticleRenderState state, @NonNull Camera camera, float partialTick) {
        Layer layer = this.getLayer();
        int color = ARGB.colorFromFloat(this.alpha, this.currentRed, this.currentGreen, this.currentBlue);
        int lightCoords = this.getLightCoords(partialTick);

        float u0 = 0.0f;
        float u1 = 1.0f;
        float v0 = 0.0f;
        float v1 = 1.0f;

        if (this instanceof TextureParticle textureParticle) {
            u0 = textureParticle.getU0();
            u1 = textureParticle.getU1();
            v0 = textureParticle.getV0();
            v1 = textureParticle.getV1();
        }

        Vec3 pos = camera.position();
        float lerpX = (float) (Mth.lerp(partialTick, this.xo, this.x) - pos.x());
        float lerpY = (float) (Mth.lerp(partialTick, this.yo, this.y) - pos.y());
        float lerpZ = (float) (Mth.lerp(partialTick, this.zo, this.z) - pos.z());

        float scale = this.getQuadSize(partialTick);

        Quaternionf rotation = this.calculateRotation(camera, partialTick);

        this.extractSurface(
                state,
                layer,
                camera,
                partialTick,
                lerpX, lerpY, lerpZ,
                rotation,
                scale,
                u0, u1, v0, v1,
                color, lightCoords
        );
    }

    protected Layer getLayer() {
        if (this instanceof TextureParticle textureParticle) {
            return Layer.bySprite(textureParticle.getCurrentAtlasSprite(textureParticle.getCurrentRawIndex()));
        }
        return Layer.DEFAULT_QUAD;
    }
}
