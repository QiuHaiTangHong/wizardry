package top.begonia.wizardry.client.model.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.renderer.entity.state.WizardRenderState;

public class WizardModel extends HumanoidModel<WizardRenderState> {
    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(
                    Wizardry.MODID,
                    "wizard"
            ),
            "main"
    );

    public WizardModel(ModelPart root) {
        super(root);
    }

    public static @NonNull LayerDefinition createLayer(CubeDeformation cubeDeformation) {
        MeshDefinition mesh = HumanoidModel.createMesh(cubeDeformation, 0.0f);
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.getChild("head");
        head.addOrReplaceChild(
                "hat_brim",
                CubeListBuilder.create()
                        .texOffs(0, 47)
                        .mirror()
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                12.0F, 1.0F, 12.0F,
                                new CubeDeformation(1.0F)
                        ),
                PartPose.offsetAndRotation(
                        -6.0F, -7.0F, -6.0F,
                        0.0F, 0.0F, 0.0F
                )
        );
        head.addOrReplaceChild(
                "hat_segment_1",
                CubeListBuilder.create()
                        .texOffs(0, 32)
                        .mirror()
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                6.0F, 1.0F, 6.0F,
                                new CubeDeformation(1.0F)
                        ),
                PartPose.offsetAndRotation(
                        -0.0349066F, 0.0F, 0.0F,
                        -3.0F, -9.0F, -3.0F
                )
        );
        head.addOrReplaceChild(
                "hat_segment_2",
                CubeListBuilder.create()
                        .texOffs(24, 32)
                        .mirror()
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                3.0F, 3.0F, 3.0F,
                                new CubeDeformation(1.0F)
                        ),
                PartPose.offsetAndRotation(
                        -1.5F, -13.0F, -0.5F,
                        -0.2511622F, 0.0F, 0.0F
                )
        );
        head.addOrReplaceChild(
                "hat_segment_3",
                CubeListBuilder.create()
                        .texOffs(0, 39)
                        .mirror()
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                5.0F, 1.0F, 5.0F,
                                new CubeDeformation(1.0F)
                        ),
                PartPose.offsetAndRotation(
                        -2.5F, -10.0F, -2.5F,
                        -0.0698132F, 0.0F, 0.0F
                )
        );
        head.addOrReplaceChild(
                "hat_segment_4",
                CubeListBuilder.create()
                        .texOffs(0, 45)
                        .mirror()
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                4.0F, 2.0F, 4.0F,
                                new CubeDeformation(1.0F)
                        ),
                PartPose.offsetAndRotation(
                        -2.0F, -11.0F, -1.5F,
                        -0.1396263F, 0.0F, 0.0F
                )
        );
        head.addOrReplaceChild(
                "hat_segment_5",
                CubeListBuilder.create()
                        .texOffs(20, 39)
                        .mirror()
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                2.0F, 3.0F, 2.0F,
                                new CubeDeformation(1.0F)
                        ),
                PartPose.offsetAndRotation(
                        -1.0F, -15.0F, 1.0F,
                        -0.4363323F, 0.0F, 0.0F
                )
        );
        head.addOrReplaceChild(
                "hat_segment_6",
                CubeListBuilder.create()
                        .texOffs(28, 39)
                        .mirror()
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                1.0F, 2.0F, 1.0F,
                                new CubeDeformation(1.0F)
                        ),
                PartPose.offsetAndRotation(
                        -0.5F, -16.0F, 2.5F,
                        -0.715585F, 0.0F, 0.0F
                )
        );
        head.addOrReplaceChild(
                "hat_segment_7",
                CubeListBuilder.create()
                        .texOffs(36, 16)
                        .mirror()
                        .addBox(
                                4.0F, 0.0F, 2.0F,
                                8.0F, 20.0F, 6.0F,
                                new CubeDeformation(1.0F)
                        ),
                PartPose.offsetAndRotation(
                        -4.0F, 0.0F, -3.0F,
                        0.0F, 0.0F, 0.0F
                )
        );

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
