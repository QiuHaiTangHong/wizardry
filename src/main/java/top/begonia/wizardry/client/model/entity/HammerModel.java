package top.begonia.wizardry.client.model.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

@SuppressWarnings("unused")
public class HammerModel<T extends EntityRenderState> extends EntityModel<T> {
    public HammerModel(ModelPart root) {
        this(root, RenderTypes::entityCutout);
    }

    public HammerModel(ModelPart root, Function<Identifier, RenderType> renderType) {
        super(root, renderType);
    }

    public static @NonNull LayerDefinition createLayer(CubeDeformation cubeDeformation) {
        return LayerDefinition.create(
                HammerModel.createMesh(cubeDeformation, 0),
                64, 64
        );
    }

    public static @NonNull MeshDefinition createMesh(CubeDeformation cubeDeformation, float yOffset) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror()
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                20.0F, 12.0F, 12.0F
                        ),
                PartPose.offsetAndRotation(
                        -10.0F, 12.0F, -6.0F,
                        0.0F, 0.0F, 0.0F
                )
        );
        root.addOrReplaceChild(
                "handle",
                CubeListBuilder.create()
                        .texOffs(0, 24)
                        .mirror()
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                4.0F, 14.0F, 4.0F
                        ),
                PartPose.offsetAndRotation(
                        -2F, -2F, -2F,
                        0.0F, 0.0F, 0.0F
                )
        );
        root.addOrReplaceChild(
                "handle_end",
                CubeListBuilder.create()
                        .texOffs(0, 49)
                        .mirror()
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                5.0F, 5.0F, 5.0F
                        ),
                PartPose.offsetAndRotation(
                        -2.5F, -7.0F, -2.5F,
                        0.0F, 0.0F, 0.0F
                )
        );
        root.addOrReplaceChild(
                "handle_base",
                CubeListBuilder.create()
                        .texOffs(0, 42)
                        .mirror()
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                5.0F, 2.0F, 5.0F
                        ),
                PartPose.offsetAndRotation(
                        -2.5F, -10.0F, -2.5F,
                        0.0F, 0.0F, 0.0F
                )
        );
        root.addOrReplaceChild(
                "ring1",
                CubeListBuilder.create()
                        .texOffs(20, 24)
                        .mirror()
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                2.0F, 14.0F, 14.0F
                        ),
                PartPose.offsetAndRotation(
                        -8.0F, -11.0F, -7.0F,
                        0.0F, 0.0F, 0.0F
                )
        );
        root.addOrReplaceChild(
                "ring2",
                CubeListBuilder.create()
                        .texOffs(20, 24)
                        .mirror()
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                2.0F, 14.0F, 14.0F
                        ),
                PartPose.offsetAndRotation(
                        6.0F, 11.0F, -7.0F,
                        0.0F, 0.0F, 0.0F
                )
        );
        return mesh;
    }
}
