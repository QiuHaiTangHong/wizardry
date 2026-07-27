package top.begonia.wizardry.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.jspecify.annotations.NonNull;

public class SageArmourModel<T extends HumanoidRenderState> extends AbstractWizardArmourModel<T> {
    public ModelPart collar;

    public SageArmourModel(ModelPart root) {
        super(root);
        this.collar = this.body.getChild("collar");
    }

    public static @NonNull ArmorModelSetExtension<MeshDefinition> createArmorMeshSetExtension(
            @NonNull CubeDeformation innerDeformation,
            @NonNull CubeDeformation outerDeformation
    ) {
        return AbstractWizardArmourModel.createArmorMeshSetExtension(
                SageArmourModel::createBaseArmorMesh,
                innerDeformation,
                outerDeformation
        );
    }

    protected static @NonNull MeshDefinition createBaseArmorMesh(CubeDeformation cubeDeformation) {
        MeshDefinition mesh = AbstractWizardArmourModel.createBaseArmorMesh(cubeDeformation);
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.getChild("body");
        body.addOrReplaceChild("collar",
                CubeListBuilder.create()
                        .texOffs(0, 32)
                        .mirror()
                        .addBox(-7.0F, 0.0F, -2.0F, 14.0F, 4.0F, 4.0F, new CubeDeformation(1.0F)),
                PartPose.ZERO);
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.6F)),
                PartPose.ZERO);
        head.addOrReplaceChild("hat_brim",
                CubeListBuilder.create()
                        .texOffs(10, 45)
                        .mirror()
                        .addBox(-9.0F, -5.7F, -9.0F, 18.0F, 1.0F, 18.0F, new CubeDeformation(0.6F)),
                PartPose.ZERO);
        head.addOrReplaceChild("hat_segment_1",
                CubeListBuilder.create().texOffs(0, 42).mirror().addBox(0.0F, 0.0F, 0.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(-3.0F, -10.6F, -3.0F, -0.1396263F, 0.0F, 0.0F));
        head.addOrReplaceChild("hat_segment_2",
                CubeListBuilder.create().texOffs(0, 50).mirror().addBox(0.0F, 0.0F, 0.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(-2.5F, -12.13333F, -2.0F, -0.2443461F, 0.0F, 0.0F));
        head.addOrReplaceChild("hat_segment_3",
                CubeListBuilder.create().texOffs(0, 57).mirror().addBox(0.0F, 0.0F, 0.0F, 4.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(-2.0F, -13.6F, -1.0F, -0.4014257F, 0.0F, 0.0F));
        head.addOrReplaceChild("hat_segment_4",
                CubeListBuilder.create().texOffs(16, 58).mirror().addBox(0.0F, 0.0F, 0.0F, 3.0F, 2.0F, 3.0F),
                PartPose.offsetAndRotation(-1.5F, -14.6F, 0.0F, -0.5759587F, 0.0F, 0.0F));
        head.addOrReplaceChild("hat_segment_5",
                CubeListBuilder.create().texOffs(20, 54).mirror().addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(-1.0F, -14.6F, 0.0F, 0.3316126F, 0.0F, 0.0F));
        head.addOrReplaceChild("hat_segment_6",
                CubeListBuilder.create().texOffs(20, 50).mirror().addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F),
                PartPose.offsetAndRotation(-0.5F, -15.1F, 2.0F, -0.5585054F, 0.0F, 0.0F));
        return mesh;
    }

    @Override
    public void setupAnim(@NonNull T state) {
        super.setupAnim(state);
    }
}
