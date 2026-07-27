package top.begonia.wizardry.client.data.definition.particle;

import top.begonia.wizardry.core.api.data.IResultData;

public record ParticleResultData(
        ParticleCombinedHolder particleHolder
) implements IResultData {
    @Override
    public Class<? extends IResultData> getDataClass() {
        return ParticleResultData.class;
    }
}
