package top.begonia.wizardry.client.renderer.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.particle.QuadMeshParticle;

public class QuadMeshParticleGroup extends ParticleGroup<QuadMeshParticle> {
    public QuadMeshParticleGroup(ParticleEngine engine) {
        super(engine);
    }

    @Override
    public @NonNull ParticleGroupRenderState extractRenderState(
            @NonNull Frustum frustum,
            @NonNull Camera camera,
            float partialTick
    ) {
        return null;
    }
}
