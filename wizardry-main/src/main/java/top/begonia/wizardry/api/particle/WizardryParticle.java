package top.begonia.wizardry.api.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.particles.ParticleOptions;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.options.IParticleOptionsExtension;

public abstract class WizardryParticle<T extends IParticleOptionsExtension> extends Particle {
    public static final ParticleRenderType CUSTOM = new ParticleRenderType("wizardry:custom", "WC");
    public ParticleOptions options;
    public WizardryParticle(
            ClientLevel level,
            @NonNull T options,
            double x, double y, double z
    ) {
        super(level, x, y, z, options.xa(), options.ya(), options.za());
    }

    public abstract void extractRenderState(
            @NonNull VertexConsumer vertexConsumer,
            @NonNull final Camera camera,
            @NotNull final CameraRenderState cameraRenderState,
            @NotNull final PoseStack.Pose pose,
            float partialTick
    );

    public RenderType renderType(){
        return RenderTypes.glint();
    }

    @Override
    public @NonNull ParticleRenderType getGroup() {
        return WizardryParticle.CUSTOM;
    }
}
