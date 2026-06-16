package top.begonia.wizardry.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.jspecify.annotations.NonNull;

public class RobeArmourModel<T extends HumanoidRenderState> extends AbstractWizardArmourModel<T> {

    public RobeArmourModel(ModelPart root) {
        super(root);
    }

    public static @NonNull ArmorModelSetExtension<MeshDefinition> createArmorMeshSetExtension(
            @NonNull CubeDeformation innerDeformation,
            @NonNull CubeDeformation outerDeformation
    ) {
        return AbstractWizardArmourModel.createArmorMeshSetExtension(
                RobeArmourModel::createBaseArmorMesh,
                innerDeformation,
                outerDeformation
        );
    }

    protected static @NonNull MeshDefinition createBaseArmorMesh(CubeDeformation cubeDeformation) {
        return AbstractWizardArmourModel.createBaseArmorMesh(cubeDeformation);
    }

    @Override
    public void setupAnim(@NonNull T state) {
        super.setupAnim(state);
    }
}
