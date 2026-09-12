package top.begonia.wizardry.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.model.entity.WizardModel;
import top.begonia.wizardry.client.renderer.entity.state.WizardRenderState;
import top.begonia.wizardry.core.entity.living.WizardEntity;

public class WizardRenderer extends MobRenderer<WizardEntity, WizardRenderState, WizardModel> {
    private static final Identifier[] TEXTURES = new Identifier[6];
    public WizardRenderer(EntityRendererProvider.Context context) {
        super(context, new WizardModel(context.bakeLayer(WizardModel.MODEL_LAYER_LOCATION)), 0.5F);
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/wizard/wizard_" + i + ".png");
        }
    }

    @Override
    public @NonNull Identifier getTextureLocation(@NonNull WizardRenderState state) {
        return WizardRenderer.TEXTURES[state.textureIndex];
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
