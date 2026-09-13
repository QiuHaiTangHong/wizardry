package top.begonia.wizardry.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.model.entity.RemnantModel;
import top.begonia.wizardry.client.renderer.entity.state.RemnantRenderState;
import top.begonia.wizardry.client.util.EntityLayerLocations;
import top.begonia.wizardry.core.entity.living.RemnantEntity;

public class RemnantRenderer extends MobRenderer<RemnantEntity, RemnantRenderState, RemnantModel<RemnantRenderState>> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/remnant.png");

    public RemnantRenderer(EntityRendererProvider.Context context, RemnantModel<RemnantRenderState> model, float shadow) {
        super(context, model, shadow);
    }

    public RemnantRenderer(EntityRendererProvider.Context context) {
        this(context, new RemnantModel<>(context.bakeLayer(EntityLayerLocations.REMNANT_ENTITY)), 0.0F);
    }

    @Override
    public void extractRenderState(
            @NonNull RemnantEntity entity,
            @NonNull RemnantRenderState state,
            float partialTicks
    ) {
        super.extractRenderState(entity, state, partialTicks);
        state.hurtTime = entity.hurtTime;
        state.maxHurtTime = entity.hurtDuration;
        state.isAttacking = entity.getAttackAnim(partialTicks) > 0.0F;
    }

    @Override
    public void submit(
            @NonNull RemnantRenderState state,
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector,
            @NonNull CameraRenderState camera
    ) {
        float age = state.ageInTicks + state.partialTick;
        float rotationSpeed = state.isAttacking ? 10 : 3;
        float scale = RemnantRenderer.getExpansion(state) + Mth.sin(age * 0.1f) * 0.06f;
        Quaternionf rotateMul = new Quaternionf();
        rotateMul.setAngleAxis(60.0F, 0.7071F, 0.0F, 0.7071F);
        RenderType renderType = this.model.renderType(TEXTURE);

        poseStack.pushPose();
        poseStack.translate(0.0F, state.boundingBoxHeight / 2, 0.0F);
        poseStack.scale(scale, scale, scale);
        poseStack.rotateAround(
                Axis.YP.rotationDegrees(age * rotationSpeed / 2),
                0.0F, 0.0F, 0.0F
        );
        poseStack.rotateAround(
                rotateMul,
                0.0F, 0.0F, 0.0F
        );
        poseStack.rotateAround(
                Axis.YP.rotationDegrees(age * rotationSpeed),
                0.0F, 0.0F, 0.0F
        );
        submitNodeCollector.submitModel(
                this.model,
                state,
                poseStack,
                renderType,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor,
                null
        );

        poseStack.scale(0.875F, 0.875F, 0.875F);
        poseStack.rotateAround(
                rotateMul,
                0.0F, 0.0F, 0.0F
        );
        poseStack.rotateAround(
                new Quaternionf(Axis.YP.rotationDegrees(age * rotationSpeed)),
                0.0F, 0.0F, 0.0F
        );
        submitNodeCollector.submitModel(
                this.model,
                state,
                poseStack,
                renderType,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor,
                null
        );
        poseStack.popPose();
    }

    private static float getExpansion(@NonNull RemnantRenderState state) {
        float expansion = 0.9f;
        if (state.deathTime > 0) {
            float f = (state.deathTime + state.partialTick) * 0.25f;
            f = 1 - (1 / (f + 1));
            expansion -= f * 0.75f;
        } else if (state.hurtTime > 0) {
            float f = (state.hurtTime - state.partialTick) / state.maxHurtTime;
            // 借用受伤时相机倾斜效果的一点巧妙数学
            f = Mth.sin(f * f * f * f * (float) Math.PI);
            expansion += f * 0.2f;
        }
        return expansion;
    }

    @Override
    public @NonNull Identifier getTextureLocation(
            @NonNull RemnantRenderState livingEntityRenderState
    ) {
        return TEXTURE;
    }

    @Override
    public @NonNull RemnantRenderState createRenderState() {
        return new RemnantRenderState();
    }
}
