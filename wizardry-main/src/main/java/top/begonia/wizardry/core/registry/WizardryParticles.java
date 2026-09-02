package top.begonia.wizardry.core.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.particle.type.BeamParticleType;
import top.begonia.wizardry.api.particle.type.QuadParticleType;

public final class WizardryParticles {
    private WizardryParticles() {
    }

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Wizardry.MODID);

    public static final DeferredHolder<ParticleType<?>, BeamParticleType> BEAM = PARTICLES.register(
            "beam",
            identifier -> new BeamParticleType(identifier, false)
    );

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> BLOCK_HIGHLIGHT = PARTICLES.register(
            "block_highlight",
            identifier -> new QuadParticleType(identifier, false)
    );

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> BUFF = PARTICLES.register(
            "buff",
            identifier -> new QuadParticleType(identifier, false)
    );

    /**
     * 云粒子类型
     */
    public static final DeferredHolder<ParticleType<?>, QuadParticleType> CLOUD = PARTICLES.register(
            "cloud",
            identifier -> new QuadParticleType(identifier, false)
    );

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> DARK_MAGIC = PARTICLES.register(
            "dark_magic",
            identifier -> new QuadParticleType(identifier, false)
    );

    /**
     * 尘埃粒子类型
     */
    public static final DeferredHolder<ParticleType<?>, QuadParticleType> DUST = PARTICLES.register(
            "dust",
            identifier -> new QuadParticleType(identifier, false)
    );

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> FLASH = PARTICLES.register(
            "flash",
            identifier -> new QuadParticleType(identifier, false)
    );

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> GUARDIAN_BEAM = PARTICLES.register(
            "guardian_beam",
            identifier -> new QuadParticleType(identifier, false)
    );

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> ICE = PARTICLES.register(
            "ice",
            identifier -> new QuadParticleType(identifier, false)
    );

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> LEAF = PARTICLES.register(
            "leaf",
            identifier -> new QuadParticleType(identifier, false)
    );

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> LIGHTNING = PARTICLES.register(
            "lightning",
            identifier -> new QuadParticleType(identifier, false)
    );

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> LIGHTNING_PULSE = PARTICLES.register(
            "lightning_pulse",
            identifier -> new QuadParticleType(identifier, false)
    );

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> MAGIC_BUBBLE = PARTICLES.register(
            "magic_bubble",
            identifier -> new QuadParticleType(identifier, false)
    );

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> MAGIC_FIRE = register("magic_fire");

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> PATH = register("path");

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> SCORCH = register("scorch");

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> SNOW = register("snow");

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> SPARK = register("spark");

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> SPARKLE = register("sparkle");

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> SPHERE = register("sphere");

    public static final DeferredHolder<ParticleType<?>, QuadParticleType> VINE = register("vine");

    public static @NonNull DeferredHolder<ParticleType<?>, QuadParticleType> register(String name) {
        return PARTICLES.register("particles/" + name, (identifier) -> new QuadParticleType(identifier, false));
    }

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}
