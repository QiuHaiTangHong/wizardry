package top.begonia.wizardry.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.jspecify.annotations.NonNull;

public class RobeArmourModel<T extends HumanoidRenderState> extends HumanoidModel<T> implements IWizardryArmour {

    ModelPart robe;

    public RobeArmourModel(ModelPart root) {
        super(root);
        this.robe = root.getChild("robe");
    }

    public static @NonNull LayerDefinition createLayerDefinition(CubeDeformation delta) {
        MeshDefinition mesh = HumanoidModel.createMesh(delta, 0.0F);
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(16, 16)
                        .mirror()
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 11.0F, 4.0F, delta),
                PartPose.ZERO
        );
        root.addOrReplaceChild("robe",
                CubeListBuilder.create()
                        .texOffs(40, 32)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 9.0F, 4.0F, delta),
                PartPose.offset(0.0F, 12.0F, 0.0F)
        );
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(@NonNull T state) {
        super.setupAnim(state);
        this.robe.visible = this.body.visible;
        if (state.isCrouching) {
            this.robe.z = 4.0F;
        } else {
            this.robe.z = 0.0F;
        }
        this.robe.xRot = (this.leftLeg.xRot + this.rightLeg.xRot) / 2.0F;
        this.robe.yRot = this.body.yRot;
        this.robe.zRot = (this.leftLeg.zRot + this.rightLeg.zRot) / 2.0F;
    }
}
