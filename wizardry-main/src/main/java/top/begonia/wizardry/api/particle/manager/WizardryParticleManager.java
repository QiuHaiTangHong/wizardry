package top.begonia.wizardry.api.particle.manager;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.data.definition.particle.ParticleDescriptionData;
import top.begonia.wizardry.client.data.manager.WizardryClientDataManager;
import top.begonia.wizardry.api.event.data.RegisterParticleEvent;
import top.begonia.wizardry.api.particle.WizardryParticle;
import top.begonia.wizardry.api.particle.extension.MutableDoubleSpriteSet;
import top.begonia.wizardry.api.particle.options.IParticleOptionsExtension;
import top.begonia.wizardry.api.particle.type.ParticleTypeExtension;

import java.util.HashMap;
import java.util.Map;

public class WizardryParticleManager implements ParticleResourceAccessor {
    private Map<Identifier, ParticleDescriptionData> particleDescriptionDataMap;
    private final Map<Identifier, MutableDoubleSpriteSet> spriteSetCache = new HashMap<>();
    private final Map<ParticleTypeExtension<?>, WizardryParticleProvider<? extends IParticleOptionsExtension>> providerRegistry = new HashMap<>();

    public WizardryParticleManager(ClientLevel clientLevel) {
        this.particleDescriptionDataMap = WizardryClientDataManager.getInstance().getAllDataByType(ParticleDescriptionData.class);
        NeoForge.EVENT_BUS.post(new RegisterParticleEvent(this.providerRegistry));
    }

    public <T extends IParticleOptionsExtension> WizardryParticle<T> createParticle(
            ClientLevel clientLevel,
            @NonNull T options,
            double x, double y, double z
    ) {
        if (options.getType() instanceof ParticleTypeExtension<?> typeExtension) {
            WizardryParticleProvider<?> provider = this.providerRegistry.get(typeExtension);
            if (provider != null) {
                return dispatchCreate(provider, clientLevel, options, x, y, z);
            }
        }
        throw new IllegalArgumentException("未注册的粒子类型: " + options.getType());
    }

    @SuppressWarnings("unchecked")
    private <T extends IParticleOptionsExtension> WizardryParticle<T> dispatchCreate(
            WizardryParticleProvider<?> rawProvider,
            ClientLevel clientLevel,
            T options,
            double x, double y, double z
    ) {
        WizardryParticleProvider<T> provider = (WizardryParticleProvider<T>) rawProvider;
        return provider.createParticle(this, clientLevel, options, x, y, z);
    }

    @Override
    @Nullable
    public MutableDoubleSpriteSet getSpriteSet(@NonNull ParticleTypeExtension<?> type) {
        return this.spriteSetCache.get(type.identifier());
    }
}
