package top.begonia.wizardry.api.particle.impl;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.CompositeQuadParticle;
import top.begonia.wizardry.api.particle.extension.Layer;
import top.begonia.wizardry.api.particle.extension.MutableDoubleSpriteSet;
import top.begonia.wizardry.api.particle.extension.TextureParticle;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;
import top.begonia.wizardry.api.particle.renderer.state.CompositeQuadParticleRenderState;

public class OneQuadParticle extends CompositeQuadParticle<QuadParticleOptions> implements TextureParticle {
    /**
     * 二维纹理集
     */
    protected MutableDoubleSpriteSet spriteSet;
    /**
     * 当前使用的精灵图片
     */
    protected TextureAtlasSprite currentSprite;
    /**
     * 二维纹理组中当前的行索引
     */
    protected int textureRowIndex = 0;

    public OneQuadParticle(
            ClientLevel level,
            @NonNull QuadParticleOptions options,
            double x, double y, double z
    ) {
        super(level, options, x, y, z);
        this.spriteSet = options.getSpriteSet();
        this.setSpriteFromAge();
    }

    @Override
    protected void extractSurface(
            @NonNull CompositeQuadParticleRenderState state,
            Layer layer,
            @NonNull Camera camera,
            float partialTick,
            float lerpX, float lerpY, float lerpZ,
            Quaternionf rotation,
            float scale,
            float u0, float u1,
            float v0, float v1,
            int color, int lightCoords
    ) {
        Vector3f scratch = new Vector3f();

        scratch.set(1.0F, -1.0F, 0.0F).rotate(rotation).mul(scale).add(lerpX, lerpY, lerpZ);
        float x0 = scratch.x();
        float y0 = scratch.y();
        float z0 = scratch.z();

        scratch.set(1.0F, 1.0F, 0.0F).rotate(rotation).mul(scale).add(lerpX, lerpY, lerpZ);
        float x1 = scratch.x();
        float y1 = scratch.y();
        float z1 = scratch.z();

        scratch.set(-1.0F, 1.0F, 0.0F).rotate(rotation).mul(scale).add(lerpX, lerpY, lerpZ);
        float x2 = scratch.x();
        float y2 = scratch.y();
        float z2 = scratch.z();

        scratch.set(-1.0F, -1.0F, 0.0F).rotate(rotation).mul(scale).add(lerpX, lerpY, lerpZ);
        float x3 = scratch.x();
        float y3 = scratch.y();
        float z3 = scratch.z();

        state.addQuad(layer,
                x0, y0, z0, u1, v1,
                x1, y1, z1, u1, v0,
                x2, y2, z2, u0, v0,
                x3, y3, z3, u0, v1,
                color, lightCoords
        );
    }

    @Override
    public void setCurrentSprite(@NotNull TextureAtlasSprite sprite) {
        this.currentSprite = sprite;
    }

    @Override
    public int getCurrentRawIndex() {
        return this.textureRowIndex;
    }

    @Override
    public @NotNull TextureAtlasSprite getCurrentAtlasSprite(int index) {
        return this.spriteSet.getSprite(index, this.age, this.lifetime);
    }

    @Override
    public void setSpriteFromAge() {
        this.currentSprite = this.getCurrentAtlasSprite(this.getCurrentRawIndex());
    }

    @Override
    public float getU0() {
        return this.currentSprite.getU0();
    }

    @Override
    public float getU1() {
        return this.currentSprite.getU1();
    }

    @Override
    public float getV0() {
        return this.currentSprite.getV0();
    }

    @Override
    public float getV1() {
        return this.currentSprite.getV1();
    }
}
