package top.begonia.wizardry.core.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.particle.WizardryParticleType;

public final class WizardryParticles {
    private WizardryParticles() {
    }

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Wizardry.MODID);

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> BEAM = register("beam");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> BLOCK_HIGHLIGHT = register("block_highlight");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> BUFF = register("buff");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> CLOUD = register("cloud");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> DARK_MAGIC = register("dark_magic");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> DUST = register("dust");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> FLASH = register("flash");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> GUARDIAN_BEAM = register("guardian_beam");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> ICE = register("ice");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> LEAF = register("leaf");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> LIGHTNING = register("lightning");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> LIGHTNING_PULSE = register("lightning_pulse");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> MAGIC_BUBBLE = register("magic_bubble");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> MAGIC_FIRE = register("magic_fire");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> PATH = register("path");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> SCORCH = register("scorch");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> SNOW = register("snow");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> SPARK = register("spark");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> SPARKLE = register("sparkle");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> SPHERE = register("sphere");

    public static final DeferredHolder<ParticleType<?>, WizardryParticleType> VINE = register("vine");

    public static @NonNull DeferredHolder<ParticleType<?>, WizardryParticleType> register(String name) {
        return PARTICLES.register("particles/" + name, () -> new WizardryParticleType(false));
    }

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}
