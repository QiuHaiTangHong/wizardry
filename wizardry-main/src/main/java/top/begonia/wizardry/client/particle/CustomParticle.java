package top.begonia.wizardry.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public abstract class CustomParticle extends Particle {
    public static final ParticleRenderType CUSTOM = new ParticleRenderType("wizardry:custom", "WC");
    public CustomParticle(
            ClientLevel level,
            double x, double y, double z
    ) {
        super(level, x, y, z);
    }

    public CustomParticle(
            ClientLevel level,
            double x, double y, double z,
            double xa, double ya, double za
    ) {
        super(level, x, y, z, xa, ya, za);
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
        return CustomParticle.CUSTOM;
    }
}
