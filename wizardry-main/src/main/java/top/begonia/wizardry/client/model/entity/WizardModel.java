package top.begonia.wizardry.client.model.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.jspecify.annotations.NonNull;

public class WizardModel<T extends HumanoidRenderState> extends HumanoidModel<T> {
    public WizardModel(ModelPart root) {
        super(root);
    }

    public static @NonNull LayerDefinition createLayer(CubeDeformation cubeDeformation) {
        MeshDefinition mesh = HumanoidModel.createMesh(cubeDeformation, 0.0f);
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.getChild("head");

        // 胡子
        head.addOrReplaceChild(
                "beard",
                CubeListBuilder.create()
                        .texOffs(32, 0)
                        .mirror()
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                8.0F, 5.0F, 0.0F,
                                new CubeDeformation(1.0F)
                        ),
                PartPose.offsetAndRotation(
                        -4.0F, 0.0F, -4.0F,
                        0.0F, 0.0F, 0.0F
                )
        );
        return LayerDefinition.create(mesh, 64, 32);
    }
}
