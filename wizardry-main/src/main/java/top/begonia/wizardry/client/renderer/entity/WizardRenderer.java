package top.begonia.wizardry.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.renderer.entity.state.WizardRenderState;
import top.begonia.wizardry.core.entity.living.WizardEntity;

public class WizardRenderer extends EntityRenderer<WizardEntity, WizardRenderState> {
    private static final Identifier[] TEXTURES = new Identifier[6];
    private final EntityRenderDispatcher entityRenderDispatcher;
    public WizardRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.entityRenderDispatcher = context.getEntityRenderDispatcher();
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/wizard/wizard_" + i + ".png");
        }
        this.shadowRadius = 0.5F;
    }

    @Override
    public void submit(
            @NonNull WizardRenderState state,
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector,
            @NonNull CameraRenderState camera
    ) {
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public void extractRenderState(@NonNull WizardEntity entity, @NonNull WizardRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.textureIndex = entity.getTextureIndex();
    }

    @Override
    public @NonNull WizardRenderState createRenderState() {
        return new WizardRenderState();
    }
}
