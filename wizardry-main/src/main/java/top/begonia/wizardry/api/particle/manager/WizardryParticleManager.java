package top.begonia.wizardry.api.particle.manager;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.particle.WizardryParticle;
import top.begonia.wizardry.api.particle.extension.MutableDoubleSpriteSet;
import top.begonia.wizardry.api.particle.options.IParticleOptionsExtension;
import top.begonia.wizardry.api.particle.type.ParticleTypeExtension;
import top.begonia.wizardry.client.data.manager.WizardryClientDataManager;

import java.util.Map;

public class WizardryParticleManager implements ParticleResourceAccessor {
    private final Map<ParticleTypeExtension<?>, WizardryParticleProvider<? extends IParticleOptionsExtension>> providerRegistry;

    public WizardryParticleManager(Map<ParticleTypeExtension<?>, WizardryParticleProvider<? extends IParticleOptionsExtension>> providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    @Nullable
    public <T extends IParticleOptionsExtension> WizardryParticle<T> createParticle(
            ClientLevel clientLevel,
            @NonNull T options,
            double x, double y, double z
    ) {
        if (options.getType() instanceof ParticleTypeExtension<?> typeExtension) {
            WizardryParticleProvider<?> provider = this.providerRegistry.get(typeExtension);
            if (provider != null) {
                return dispatchCreate(provider, clientLevel, options, x, y, z);
            } else {
                Wizardry.LOGGER.info("未对粒子类型: {}, 关联 Provider.", options.getType());
                return null;
            }
        } else {
            throw new IllegalArgumentException("未注册的粒子类型: " + options.getType());
        }
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
        Map<Identifier, MutableDoubleSpriteSet> spriteSetMap = WizardryClientDataManager.getInstance().getAllDataByType(MutableDoubleSpriteSet.class);
        return spriteSetMap.get(type.identifier().withPrefix("particles/"));
    }
}
