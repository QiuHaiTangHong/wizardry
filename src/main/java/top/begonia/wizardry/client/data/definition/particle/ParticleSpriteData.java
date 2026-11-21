package top.begonia.wizardry.client.data.definition.particle;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.core.data.IResultData;
import top.begonia.wizardry.api.client.particle.extension.MutableDoubleSpriteSet;

public record ParticleSpriteData(
        MutableDoubleSpriteSet spriteSet
) implements IResultData {
    @Contract(pure = true)
    @Override
    public @NonNull Class<? extends IResultData> getDataClass() {
        return ParticleSpriteData.class;
    }
}
