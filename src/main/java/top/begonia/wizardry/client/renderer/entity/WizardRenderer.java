package top.begonia.wizardry.client.renderer.entity;

import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.model.armour.WizardArmourModel;
import top.begonia.wizardry.client.model.entity.WizardModel;
import top.begonia.wizardry.client.renderer.entity.state.WizardRenderState;
import top.begonia.wizardry.client.renderer.layers.LayerTiledOverlay;
import top.begonia.wizardry.client.util.EntityLayerLocations;
import top.begonia.wizardry.core.entity.living.wizard.AbstractWizardEntity;
import top.begonia.wizardry.core.util.ArmourHelper;

public class WizardRenderer<T extends AbstractWizardEntity> extends HumanoidMobRenderer<T, WizardRenderState, WizardModel<WizardRenderState>> {
    public WizardRenderer(EntityRendererProvider.Context context) {
        super(context, new WizardModel<>(context.bakeLayer(EntityLayerLocations.WIZARD_ENTITY)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(
                this,
                ArmorModelSet.bake(
                        ArmourHelper.ModelLayers.WIZARD,
                        context.getModelSet(),
                        WizardArmourModel::new
                ),
                context.getEquipmentRenderer()
        ));
        this.addLayer(
                new LayerTiledOverlay<>(this)
        );
    }

    @Override
    public @NonNull Identifier getTextureLocation(@NonNull WizardRenderState state) {
        return state.textures[state.textureIndex];
    }

    @Override
    public void extractRenderState(@NonNull T entity, @NonNull WizardRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.textureIndex = entity.getTextureIndex();
        state.textures = entity.getWizardTextures();
    }

    @Override
    public @NonNull WizardRenderState createRenderState() {
        return new WizardRenderState();
    }
}
