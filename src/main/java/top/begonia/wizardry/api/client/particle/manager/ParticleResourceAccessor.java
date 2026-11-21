package top.begonia.wizardry.api.client.particle.manager;

import org.jetbrains.annotations.Nullable;
import top.begonia.wizardry.api.client.particle.extension.MutableDoubleSpriteSet;
import top.begonia.wizardry.api.client.particle.type.ParticleTypeExtension;

public interface ParticleResourceAccessor {
    @Nullable
    MutableDoubleSpriteSet getSpriteSet(ParticleTypeExtension<?> type);
}
