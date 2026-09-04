package top.begonia.wizardry.api.particle.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.WizardryParticle;
import top.begonia.wizardry.api.particle.extension.MutableDoubleSpriteSet;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;
import top.begonia.wizardry.core.entity.ICustomHitbox;

import javax.annotation.Nullable;
import java.util.List;

public class WizardryQuadParticle extends WizardryParticle<QuadParticleOptions> {
    private static final double SPREAD_FACTOR = 0.2;
    private static final double IMPACT_FRICTION = 0.2;
    private static final int DEFAULT_COLOR = ARGB.color(255, 255, 255, 255);
    /**
     * 二维纹理集
     */
    private final MutableDoubleSpriteSet spriteSet;
    /**
     * 当前使用的精灵图片
     */
    protected TextureAtlasSprite currentSprite;
    /**
     * 随机数种子
     */
    protected long seed;
    /**
     * 是否启用阴影
     */
    protected boolean shaded = false;
    /**
     * 二维纹理组中当前的行索引
     */
    protected int textureRowIndex = 0;
    /**
     * 粒子的开始颜色 ARGB 格式
     */
    protected int startColor = DEFAULT_COLOR;
    /**
     * 粒子的当前颜色 ARGB 格式
     */
    protected int currentColor = DEFAULT_COLOR;
    /**
     * 粒子的结束颜色 ARGB 格式
     */
    protected int endColor = DEFAULT_COLOR;
    /**
     * 粒子的角度
     */
    protected float angle;
    /**
     * 粒子的范围
     */
    protected double radius = 0;
    protected double speed = 0;
    @Nullable
    protected Entity entity = null;
    protected double relativeX, relativeY, relativeZ;
    protected double prevVelX, prevVelY, prevVelZ;
    protected double relativeMotionX, relativeMotionY, relativeMotionZ;
    /**
     * 粒子的当前俯仰和偏航角
     */
    protected float yaw = Float.NaN, pitch = Float.NaN;
    protected float quadSize;
    protected float roll;
    protected float oRoll;

    public WizardryQuadParticle(
            ClientLevel level,
            @NonNull QuadParticleOptions options,
            double x, double y, double z
    ) {
        super(level, options, x, y, z);
        this.spriteSet = options.getSpriteSet();
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
        this.setSpriteFromAge(this.spriteSet);
    }

    @Override
    public void extractRenderState(
            @NonNull VertexConsumer vertexBuilder,
            @NonNull Camera camera,
            @NotNull CameraRenderState cameraRenderState,
            @NotNull PoseStack.Pose pose,
            float partialTick
    ) {
        Vec3 pos = camera.position();
        float x = (float) (Mth.lerp(partialTick, this.xo, this.x) - pos.x());
        float y = (float) (Mth.lerp(partialTick, this.yo, this.y) - pos.y());
        float z = (float) (Mth.lerp(partialTick, this.zo, this.z) - pos.z());
        Quaternionf rotation = new Quaternionf();
        this.getFacingCameraMode().setRotation(rotation, camera, partialTick);
        if (this.roll != 0.0F) {
            rotation.rotateZ(Mth.lerp(partialTick, this.oRoll, this.roll));
        }
        this.extractRotatedQuad(
                vertexBuilder,
                x, y, z,
                rotation,
                this.getQuadSize(partialTick),
                this.getU0(), this.getU1(),
                this.getV0(), this.getV1(),
                this.currentColor, this.getLightCoords(partialTick)
        );
    }

    public SingleQuadParticle.FacingCameraMode getFacingCameraMode() {
        return SingleQuadParticle.FacingCameraMode.LOOKAT_XYZ;
    }

    public void extractRotatedQuad(
            VertexConsumer builder,
            float x, float y, float z,
            Quaternionf rotation,
            float scale,
            float u0, float u1,
            float v0, float v1,
            int color, int lightCoords
    ) {
        this.extractVertex(builder, rotation, x, y, z, 1.0F, -1.0F, scale, u1, v1, color, lightCoords);
        this.extractVertex(builder, rotation, x, y, z, 1.0F, 1.0F, scale, u1, v0, color, lightCoords);
        this.extractVertex(builder, rotation, x, y, z, -1.0F, 1.0F, scale, u0, v0, color, lightCoords);
        this.extractVertex(builder, rotation, x, y, z, -1.0F, -1.0F, scale, u0, v1, color, lightCoords);
    }

    private void extractVertex(
            @NonNull VertexConsumer builder,
            Quaternionf rotation,
            float x, float y, float z,
            float nx, float ny,
            float scale,
            float u, float v,
            int color,
            int lightCoords
    ) {
        Vector3f scratch = (new Vector3f(nx, ny, 0.0F)).rotate(rotation).mul(scale).add(x, y, z);
        builder.addVertex(scratch.x(), scratch.y(), scratch.z()).setUv(u, v).setColor(color).setLight(lightCoords);
    }

    public RenderType renderType(){
        return RenderTypes.lightning();
    }

    public float getQuadSize(float partialTick) {
        return this.quadSize;
    }

    protected float getU0() {
        return this.currentSprite.getU0();
    }

    protected float getU1() {
        return this.currentSprite.getU1();
    }

    protected float getV0() {
        return this.currentSprite.getV0();
    }

    protected float getV1() {
        return this.currentSprite.getV1();
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void setShaded(boolean shaded) {
        this.shaded = shaded;
    }

    public void setGravity(boolean gravity) {
        this.gravity = gravity ? 1.0F : 0.0F;
    }

    public void hasPhysics(boolean hasPhysics) {
        this.hasPhysics = hasPhysics;
    }

    public void setSpin(double radius, double speed) {
        this.radius = radius;
        this.speed = speed * 2 * Math.PI;
        this.angle = this.random.nextFloat() * (float) Math.PI * 2;

        this.x = relativeX - radius * Mth.cos(angle);
        this.z = relativeZ + radius * Mth.sin(angle);

        this.relativeMotionX = xd;
        this.relativeMotionY = yd;
        this.relativeMotionZ = zd;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        if (entity != null) {
            this.setPos(entity.getX() + relativeX, entity.getY() + relativeY, entity.getZ() + relativeZ);
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            this.relativeMotionX = xd;
            this.relativeMotionY = yd;
            this.relativeMotionZ = zd;
        }
    }

    public void setStartColor(float alpha, float red, float green, float blue) {
        this.startColor = ARGB.colorFromFloat(alpha, red, green, blue);
    }

    public void setCurrentColor(float alpha, float red, float green, float blue){
        this.currentColor = ARGB.colorFromFloat(alpha, red, green, blue);
    }

    public void setEndColor(float alpha, float red, float green, float blue) {
        this.endColor = ARGB.colorFromFloat(alpha, red, green, blue);
    }

    public void setFacing(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void setTargetPosition(double x, double y, double z) {
    }

    public void setTargetVelocity(double vx, double vy, double vz) {
    }

    public void setTargetEntity(Entity target) {
    }

    public void setLength(double length) {
    }

    protected void updateEntityLinking(float partialTicks) {
        if (this.entity != null) {
            if (this.entity.isRemoved()) {
                this.remove();
                return;
            }
            this.xo = this.x + this.entity.xOld - this.entity.getX() - this.relativeMotionX * (1.0F - partialTicks);
            this.yo = this.y + this.entity.yOld - this.entity.getY() - this.relativeMotionY * (1.0F - partialTicks);
            this.zo = this.z + this.entity.zOld - this.entity.getZ() - this.relativeMotionZ * (1.0F - partialTicks);
        }
    }

    protected Vec3 getRenderPos(float partialTicks) {
        double lerpX = Mth.lerp(partialTicks, this.xo, this.x);
        double lerpY = Mth.lerp(partialTicks, this.yo, this.y);
        double lerpZ = Mth.lerp(partialTicks, this.zo, this.z);
        if (this.entity != null) {
            if (this.entity.isRemoved()) {
                this.remove();
            } else {
                double entityMovementX = Mth.lerp(partialTicks, this.entity.xOld, this.entity.getX());
                double entityMovementY = Mth.lerp(partialTicks, this.entity.yOld, this.entity.getY());
                double entityMovementZ = Mth.lerp(partialTicks, this.entity.zOld, this.entity.getZ());
                return new Vec3(
                        lerpX + entityMovementX - this.relativeMotionX * (1.0F - partialTicks),
                        lerpY + entityMovementY - this.relativeMotionY * (1.0F - partialTicks),
                        lerpZ + entityMovementZ - this.relativeMotionZ * (1.0F - partialTicks)
                );
            }
        }
        return new Vec3(lerpX, lerpY, lerpZ);
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
        float ageFraction = Mth.clamp((float) this.age / (float) this.lifetime, 0.0F, 1.0F);
        this.currentColor = ARGB.linearLerp(ageFraction, startColor, endColor);
        this.setSpriteFromAge(this.spriteSet);
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

    public void setSpriteFromAge(MutableDoubleSpriteSet sprites) {
        if (this.isAlive() && !sprites.isEmpty()) {
            this.setCurrentSprite(sprites.getSprite(this.textureRowIndex, this.age, this.lifetime));
        }
    }

    protected void setCurrentSprite(TextureAtlasSprite sprite) {
        this.currentSprite = sprite;
    }

    @Override
    protected int getLightCoords(float partialTick) {
        if (this.shaded) {
            return super.getLightCoords(partialTick);
        } else {
            return LightCoordsUtil.FULL_BRIGHT;
        }
    }

    public interface FacingCameraMode {
        SingleQuadParticle.FacingCameraMode LOOKAT_XYZ = (target, camera, partialTickTime) -> target.set(camera.rotation());
        SingleQuadParticle.FacingCameraMode LOOKAT_Y = (target, camera, partialTickTime) -> target.set(0.0F, camera.rotation().y, 0.0F, camera.rotation().w);

        void setRotation(Quaternionf var1, Camera var2, float var3);
    }
}
