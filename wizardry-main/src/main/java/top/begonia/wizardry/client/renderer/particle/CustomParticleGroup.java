package top.begonia.wizardry.client.renderer.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.api.particle.WizardryParticle;
import top.begonia.wizardry.client.renderer.particle.state.CustomParticleGroupRenderState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomParticleGroup extends ParticleGroup<WizardryParticle> {
    public CustomParticleGroup(ParticleEngine engine) {
        super(engine);
    }

    @Override
    public @NonNull ParticleGroupRenderState extractRenderState(
            @NonNull Frustum frustum,
            @NonNull Camera camera,
            float partialTick
    ) {
        if (this.particles.isEmpty()) {
            return (collector, cameraState) -> {};
        }
        Map<RenderType, List<WizardryParticle>> categorized = new HashMap<>();
        for (WizardryParticle particle : this.particles) {
            categorized.computeIfAbsent(particle.renderType(), _ -> new ArrayList<>()).add(particle);
        }
        return new CustomParticleGroupRenderState(categorized, camera, partialTick);
    }
}
