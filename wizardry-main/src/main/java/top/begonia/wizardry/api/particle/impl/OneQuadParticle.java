package top.begonia.wizardry.api.particle.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.CompositeQuadParticle;
import top.begonia.wizardry.api.particle.extension.extract.ExtractFlow;
import top.begonia.wizardry.api.particle.extension.Layer;
import top.begonia.wizardry.api.particle.extension.MutableDoubleSpriteSet;
import top.begonia.wizardry.api.particle.extension.TextureParticle;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;

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
    protected void extractSurface(@NonNull ExtractFlow extractFlow) {
        Quaternionf rotation = extractFlow.getRotate();
        Layer layer = extractFlow.getLayer();
        Vector3f scratch = new Vector3f();
        float scale = extractFlow.getScale();
        float lerpX = extractFlow.getX();
        float lerpY = extractFlow.getY();
        float lerpZ = extractFlow.getZ();
        float u0 = extractFlow.getU0();
        float u1 = extractFlow.getU1();
        float v0 = extractFlow.getV0();
        float v1 = extractFlow.getV1();

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

        extractFlow.getState().addQuad(layer,
                x0, y0, z0, u1, v1,
                x1, y1, z1, u1, v0,
                x2, y2, z2, u0, v0,
                x3, y3, z3, u0, v1,
                extractFlow.getHEXColor(), extractFlow.getLightCoords()
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
