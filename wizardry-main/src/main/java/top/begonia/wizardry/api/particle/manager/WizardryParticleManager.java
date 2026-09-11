package top.begonia.wizardry.api.particle.manager;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.particle.CompositeQuadParticle;
import top.begonia.wizardry.api.particle.extension.MutableDoubleSpriteSet;
import top.begonia.wizardry.api.particle.extension.ParticleInitAccessor;
import top.begonia.wizardry.api.particle.options.IParticleOptionsExtension;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;
import top.begonia.wizardry.api.particle.type.ParticleTypeExtension;
import top.begonia.wizardry.client.data.manager.WizardryClientDataManager;
import top.begonia.wizardry.core.registry.WizardryParticles;

import java.util.Map;
import java.util.Optional;

public class WizardryParticleManager implements ParticleResourceAccessor {
    private Map<ParticleTypeExtension<?>, WizardryParticleProvider<? extends IParticleOptionsExtension>> providerRegistry;

    public WizardryParticleManager() {
    }

    public void updateProvider(Map<ParticleTypeExtension<?>, WizardryParticleProvider<? extends IParticleOptionsExtension>> providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    @Nullable
    public <T extends IParticleOptionsExtension> CompositeQuadParticle<T> createParticle(
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

    @NotNull
    public <T extends IParticleOptionsExtension> Optional<ParticleInitAccessor> createParticleOpt(
            ClientLevel clientLevel,
            @NonNull T options,
            double x, double y, double z
    ) {
        if (options.getType() instanceof ParticleTypeExtension<?> typeExtension) {
            WizardryParticleProvider<?> provider = this.providerRegistry.get(typeExtension);
            if (provider != null) {
                return Optional.ofNullable(dispatchCreate(provider, clientLevel, options, x, y, z));
            } else {
                Wizardry.LOGGER.info("未对粒子类型: {}, 关联 Provider.", options.getType());
                return Optional.empty();
            }
        } else {
            Wizardry.LOGGER.warn("未注册的粒子类型: " + options.getType());
            return Optional.empty();
        }
    }

    @NotNull
    public <T extends IParticleOptionsExtension> Optional<ParticleInitAccessor> createParticleOpt(
            ClientLevel clientLevel,
            @NonNull RandomSource random,
            @NonNull T options,
            double x, double y, double z,
            double radius, boolean move
    ) {
        double px = x + (random.nextDouble() * 2 - 1) * radius;
        double py = y + (random.nextDouble() * 2 - 1) * radius;
        double pz = z + (random.nextDouble() * 2 - 1) * radius;
        if (options.getType() instanceof ParticleTypeExtension<?> typeExtension) {
            WizardryParticleProvider<?> provider = this.providerRegistry.get(typeExtension);
            if (provider != null) {
                CompositeQuadParticle<T> particle = dispatchCreate(provider, clientLevel, options, x, y, z);
                if (particle != null && move){
                    particle.speed(px - x, py - y, pz - z);
                }
                return Optional.ofNullable(particle);
            } else {
                Wizardry.LOGGER.info("未对粒子类型: {}, 关联 Provider.", options.getType());
                return Optional.empty();
            }
        } else {
            Wizardry.LOGGER.warn("未注册的粒子类型: " + options.getType());
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends IParticleOptionsExtension> CompositeQuadParticle<T> dispatchCreate(
            WizardryParticleProvider<?> rawProvider,
            ClientLevel clientLevel,
            T options,
            double x, double y, double z
    ) {
        WizardryParticleProvider<T> provider = (WizardryParticleProvider<T>) rawProvider;
        return provider.createParticle(this, clientLevel, options, x, y, z);
    }

    public void spawnShockParticles(ClientLevel level, double x, double y, double z) {
        double px, py, pz;
        for (int i = 0; i < 8; i++) {
            px = x + level.getRandom().nextDouble() - 0.5;
            py = y + level.getRandom().nextDouble() - 0.5;
            pz = z + level.getRandom().nextDouble() - 0.5;
            this.createParticleOpt(
                    level,
                    new QuadParticleOptions(WizardryParticles.SPARK.get()),
                    px, py, pz
            ).ifPresent(ParticleInitAccessor::spawn);
            px = x + level.getRandom().nextDouble() - 0.5;
            py = y + level.getRandom().nextDouble() - 0.5;
            pz = z + level.getRandom().nextDouble() - 0.5;
            level.addParticle(ParticleTypes.LARGE_SMOKE, px, py, pz, 0, 0, 0);
        }
    }

    public void spawnHealParticles(ClientLevel level, LivingEntity entity) {

        for (int i = 0; i < 10; i++) {
            double x = entity.getX() + level.getRandom().nextDouble() * 2 - 1;
            double y = entity.getY() + entity.getEyeHeight() - 0.5 + level.getRandom().nextDouble();
            double z = entity.getZ() + level.getRandom().nextDouble() * 2 - 1;
            this.createParticleOpt(
                    level,
                    new QuadParticleOptions(WizardryParticles.SPARKLE.get()),
                    x, y, z
            ).ifPresent(p -> p.speed(0.0f, 0.1f, 0.0f)
                    .color(1.0f, 1.0f, 0.3f)
                    .spawn()
            );
        }

        this.createParticleOpt(
                level,
                new QuadParticleOptions(WizardryParticles.BUFF.get()),
                entity.getX(), entity.getY(), entity.getZ()
        ).ifPresent(p -> p.targetEntity(entity)
                .color(1.0f, 1.0f, 0.3f)
                .spawn()
        );
    }

    @Override
    @Nullable
    public MutableDoubleSpriteSet getSpriteSet(@NonNull ParticleTypeExtension<?> type) {
        Map<Identifier, MutableDoubleSpriteSet> spriteSetMap = WizardryClientDataManager.getInstance().getAllDataByType(MutableDoubleSpriteSet.class);
        return spriteSetMap.get(type.identifier().withPrefix("particles/"));
    }
}
