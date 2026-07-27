package top.begonia.wizardry.client.data.definition.particle;

import net.minecraft.core.particles.ParticleType;
import top.begonia.wizardry.core.api.data.IParserContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParticleParserContextData implements IParserContext {
    private final Map<ParticleType<?>, ParticleCombinedHolder> particleHolders = new ConcurrentHashMap<>();

    public Map<ParticleType<?>, ParticleCombinedHolder> getParticleHolders() {
        return this.particleHolders;
    }

    public ParticleCombinedHolder getParticleHolder(ParticleType<?> type) {
        return this.particleHolders.get(type);
    }

    @Override
    public Class<? extends IParserContext> getParserContextClass() {
        return ParticleParserContextData.class;
    }
}
