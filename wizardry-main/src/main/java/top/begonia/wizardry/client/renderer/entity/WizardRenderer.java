package top.begonia.wizardry.client.renderer.entity;

import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.model.armour.WizardArmourModel;
import top.begonia.wizardry.client.model.entity.WizardModel;
import top.begonia.wizardry.client.renderer.entity.state.WizardRenderState;
import top.begonia.wizardry.client.util.EntityLayerLocations;
import top.begonia.wizardry.core.entity.living.WizardEntity;
import top.begonia.wizardry.core.util.ArmourHelper;

public class WizardRenderer extends HumanoidMobRenderer<WizardEntity, WizardRenderState, WizardModel<WizardRenderState>> {
    private static final Identifier[] TEXTURES = new Identifier[6];
    public WizardRenderer(EntityRendererProvider.Context context) {
        super(context, new WizardModel<>(context.bakeLayer(EntityLayerLocations.WIZARD_ENTITY)), 0.5F);
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/wizard/wizard_" + i + ".png");
        }
        this.addLayer(new HumanoidArmorLayer<>(
                this,
                ArmorModelSet.bake(
                        ArmourHelper.ModelLayers.WIZARD,
                        context.getModelSet(),
                        WizardArmourModel::new
                ),
                context.getEquipmentRenderer()
        ));
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
