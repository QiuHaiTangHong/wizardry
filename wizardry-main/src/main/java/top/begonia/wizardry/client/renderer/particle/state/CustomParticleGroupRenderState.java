package top.begonia.wizardry.client.renderer.particle.state;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.particle.CustomParticle;

import java.util.List;
import java.util.Map;

public record CustomParticleGroupRenderState(
        Map<RenderType, List<CustomParticle>> categorizedParticles,
        net.minecraft.client.Camera camera,
        float partialTick
) implements ParticleGroupRenderState {

    @Override
    public void submit(@NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState cameraRenderState) {
        if (this.categorizedParticles.isEmpty()) {
            return;
        }
        PoseStack poseStack = new PoseStack();
        this.categorizedParticles.forEach((renderType, particleList) -> {
            submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) -> {
                for (CustomParticle particle : particleList) {
                    particle.extractRenderState(
                            vertexConsumer,
                            this.camera,
                            cameraRenderState,
                            pose,
                            this.partialTick
                    );
                }
            });
        });
    }
}
