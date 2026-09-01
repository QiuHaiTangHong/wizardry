package top.begonia.wizardry.core.api.particle.manager;

import org.jetbrains.annotations.Nullable;
import top.begonia.wizardry.core.api.particle.extension.MutableDoubleSpriteSet;
import top.begonia.wizardry.core.api.particle.type.ParticleTypeExtension;

public interface ParticleResourceAccessor {
    @Nullable
    MutableDoubleSpriteSet getSpriteSet(ParticleTypeExtension<?> type);
}
