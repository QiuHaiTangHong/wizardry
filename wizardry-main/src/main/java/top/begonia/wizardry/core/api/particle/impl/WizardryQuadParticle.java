package top.begonia.wizardry.core.api.particle.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.api.particle.WizardryParticle;
import top.begonia.wizardry.core.api.particle.extension.MutableDoubleSpriteSet;
import top.begonia.wizardry.core.api.particle.options.QuadParticleOptions;

public class WizardryQuadParticle extends WizardryParticle<QuadParticleOptions> {
    private MutableDoubleSpriteSet spriteSet;
    public WizardryQuadParticle(
            ClientLevel level,
            @NonNull QuadParticleOptions options,
            double x, double y, double z
    ) {
        super(level, options, x, y, z);
        this.spriteSet = options.getSpriteSet();
    }

    @Override
    public void extractRenderState(
            @NonNull VertexConsumer vertexConsumer,
            @NonNull Camera camera,
            @NotNull CameraRenderState cameraRenderState,
            @NotNull PoseStack.Pose pose,
            float partialTick
    ) {

    }
}
