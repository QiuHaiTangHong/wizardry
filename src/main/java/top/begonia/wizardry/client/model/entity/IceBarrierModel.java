package top.begonia.wizardry.client.model.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jspecify.annotations.NonNull;

public class IceBarrierModel<T extends EntityRenderState> extends EntityModel<T> {
    public IceBarrierModel(ModelPart root) {
        super(root);
    }

    public static @NonNull LayerDefinition createLayer(CubeDeformation cubeDeformation) {
        return LayerDefinition.create(
                IceBarrierModel.createMesh(cubeDeformation, 0),
                64, 64
        );
    }

    public static @NonNull MeshDefinition createMesh(CubeDeformation cubeDeformation, float yOffset) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "shield_surface",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(
                                -14.5F, -14.5F, -1.5F,
                                29, 29, 3
                        ),
                PartPose.offsetAndRotation(
                        0.0F, 24.0F, 0.0F,
                        0.0F, 0.0F, 0.7854F
                )
        );
        root.addOrReplaceChild(
                "shield_tip",
                CubeListBuilder.create()
                        .texOffs(0, 32)
                        .addBox(
                                -9.0F, -9.0F, -4.0F,
                                18, 18, 8
                        ),
                PartPose.offsetAndRotation(
                        0.0F, 24.0F, 0.0F,
                        0.0F, 0.0F, 0.7854F
                )
        );
        return mesh;
    }
}
