package top.begonia.wizardry.api.particle.manager;

import org.jetbrains.annotations.Nullable;
import top.begonia.wizardry.api.particle.extension.MutableDoubleSpriteSet;
import top.begonia.wizardry.api.particle.type.ParticleTypeExtension;

public interface ParticleResourceAccessor {
    @Nullable
    MutableDoubleSpriteSet getSpriteSet(ParticleTypeExtension<?> type);
}
