package top.begonia.wizardry.api.particle.extension;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.NotNull;

public interface TextureParticle {
    void setCurrentSprite(@NotNull TextureAtlasSprite sprite);

    int getCurrentRawIndex();

    @NotNull
    TextureAtlasSprite getCurrentAtlasSprite(int index);

    void setSpriteFromAge();

    float getU0();

    float getU1();

    float getV0();

    float getV1();
}
