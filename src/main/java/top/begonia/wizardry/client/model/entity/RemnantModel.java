package top.begonia.wizardry.client.model.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

@SuppressWarnings("unused")
public class RemnantModel<T extends LivingEntityRenderState> extends EntityModel<T> {
    public RemnantModel(ModelPart root) {
        this(root, RenderTypes::entityCutout);
    }

    public RemnantModel(ModelPart root, Function<Identifier, RenderType> renderType) {
        super(root, renderType);
    }

    public static @NonNull LayerDefinition createLayer(CubeDeformation cubeDeformation) {
        return LayerDefinition.create(
                RemnantModel.createMesh(cubeDeformation, 0),
                64, 32
        );
    }

    public static @NonNull MeshDefinition createMesh(CubeDeformation cubeDeformation, float yOffset) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "cube",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror()
                        .addBox(
                                -4.0F, -4.0F, -4.0F,
                                8.0F, 8.0F, 8.0F
                        ),
                PartPose.ZERO
        );
        return mesh;
    }
}
