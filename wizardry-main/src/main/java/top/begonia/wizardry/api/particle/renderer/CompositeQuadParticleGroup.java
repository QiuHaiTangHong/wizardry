package top.begonia.wizardry.api.particle.renderer;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.CompositeQuadParticle;
import top.begonia.wizardry.api.particle.renderer.state.CompositeQuadParticleRenderState;

import java.util.Objects;

public class CompositeQuadParticleGroup extends ParticleGroup<CompositeQuadParticle<?>> {
    private final CompositeQuadParticleRenderState particleTypeRenderState = new CompositeQuadParticleRenderState();

    public CompositeQuadParticleGroup(ParticleEngine engine) {
        super(engine);
    }

    @Override
    public @NonNull ParticleGroupRenderState extractRenderState(@NonNull Frustum frustum, @NonNull Camera camera, float partialTickTime) {
        for (CompositeQuadParticle<?> particle : this.particles) {
            if (frustum.pointInFrustum(particle.x(), particle.y(), particle.z())) {
                try {
                    particle.extract(this.particleTypeRenderState, camera, partialTickTime);
                } catch (Throwable throwable) {
                    CrashReport report = CrashReport.forThrowable(throwable, "Rendering Particle");
                    CrashReportCategory category = report.addCategory("Particle being rendered");
                    Objects.requireNonNull(particle);
                    category.setDetail("Particle", particle::toString);
                    throw new ReportedException(report);
                }
            }
        }

        return this.particleTypeRenderState;
    }
}
