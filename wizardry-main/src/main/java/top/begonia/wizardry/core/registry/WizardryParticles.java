package top.begonia.wizardry.core.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.particle.options.impl.QuadParticleOptions;
import top.begonia.wizardry.api.particle.options.impl.TargetParticleOptions;

public final class WizardryParticles {
    private WizardryParticles() {
    }

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Wizardry.MODID);

    public static final DeferredHolder<ParticleType<?>, TargetParticleOptions> BEAM = PARTICLES.register(
            "beam",
            identifier -> new TargetParticleOptions(identifier, false)
    );

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> BLOCK_HIGHLIGHT = register("block_highlight");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> BUFF = register("buff");

    /**
     * 云粒子类型
     */
    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> CLOUD = register("cloud");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> DARK_MAGIC = register("dark_magic");

    /**
     * 尘埃粒子类型
     */
    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> DUST = register("dust");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> FLASH = register("flash");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> GUARDIAN_BEAM = register("guardian_beam");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> ICE = register("ice");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> LEAF = register("leaf");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> LIGHTNING = register("lightning");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> LIGHTNING_PULSE = register("lightning_pulse");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> MAGIC_BUBBLE = register("magic_bubble");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> MAGIC_FIRE = register("magic_fire");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> PATH = register("path");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> SCORCH = register("scorch");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> SNOW = register("snow");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> SPARK = register("spark");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> SPARKLE = register("sparkle");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> SPHERE = register("sphere");

    public static final DeferredHolder<ParticleType<?>, QuadParticleOptions> VINE = register("vine");

    public static @NonNull DeferredHolder<ParticleType<?>, QuadParticleOptions> register(String name) {
        return PARTICLES.register("particles/" + name, (identifier) -> new QuadParticleOptions(identifier, false));
    }

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}
