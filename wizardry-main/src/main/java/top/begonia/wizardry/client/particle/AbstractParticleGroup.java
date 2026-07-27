package top.begonia.wizardry.client.particle;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class AbstractParticleGroup extends ParticleGroup<AbstractParticle> {
    private final QuadParticleRenderState particleTypeRenderState = new QuadParticleRenderState();

    public AbstractParticleGroup(ParticleEngine engine) {
        super(engine);
    }

    @Override
    public @NonNull ParticleGroupRenderState extractRenderState(@NonNull Frustum frustum, @NonNull Camera camera, float partialTickTime) {
        for (AbstractParticle particle : this.particles) {
            if (frustum.pointInFrustum(particle.getPos().x, particle.getPos().y, particle.getPos().z)) {
                try {
                    particle.extract(this.particleTypeRenderState, camera, partialTickTime);
                } catch (Throwable var9) {
                    CrashReport report = CrashReport.forThrowable(var9, "Rendering Particle");
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
