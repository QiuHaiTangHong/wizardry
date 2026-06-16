package top.begonia.wizardry.core.api.data.event;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import top.begonia.wizardry.client.data.definition.particle.ParticleCombinedHolder;
import top.begonia.wizardry.client.particle.MutableDoubleSpriteSet;
import top.begonia.wizardry.client.particle.WizardryParticleOptions;

import java.util.Map;

public class RegisterParticleEvent extends Event implements IModBusEvent {
    private final Map<ParticleType<?>, ParticleCombinedHolder> particleHolders;

    public RegisterParticleEvent(Map<ParticleType<?>, ParticleCombinedHolder> particleHolders) {
        this.particleHolders = particleHolders;
    }

    public <T extends WizardryParticleOptions> void register(ParticleType<T> type, ParticleConstructor constructor) {
        var holder = this.particleHolders.computeIfAbsent(type, _ -> new ParticleCombinedHolder());
        holder.setConstructor(constructor);
    }

    @FunctionalInterface
    public interface ParticleConstructor {
        <T extends WizardryParticleOptions> Particle create(
                T options,
                ClientLevel level,
                double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed,
                MutableDoubleSpriteSet spriteSet
        );
    }
}
