package top.begonia.wizardry.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;

public class WizardArmourModel<T extends HumanoidRenderState> extends HumanoidModel<T> implements IWizardryArmour {

    private final ModelPart robe;
    private final ModelPart hatBobble;
    private static final boolean IS_CHRISTMAS = LocalDate.now().getMonthValue() == 12;

    public WizardArmourModel(ModelPart root) {
        super(root);
        this.robe = root.getChild("robe");
        this.hatBobble = this.head.getChild("hat_bobble");
    }

    public static LayerDefinition createLayerDefinition(CubeDeformation delta) {
        MeshDefinition mesh = HumanoidModel.createMesh(delta, 0.0F);
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(16, 16)
                        .mirror()
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 11.0F, 4.0F, delta),
                PartPose.ZERO);
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.6F)),
                PartPose.ZERO);
        head.addOrReplaceChild("hat_brim",
                CubeListBuilder.create()
                        .texOffs(0, 47)
                        .mirror()
                        .addBox(-8.0F, -6.85F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.6F)),
                PartPose.ZERO);
        head.addOrReplaceChild("hat_segment_1",
                CubeListBuilder.create().texOffs(0, 32).mirror().addBox(0.0F, 0.0F, 0.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.2F)),
                PartPose.offsetAndRotation(-3.0F, -10.6F, -3.0F, -0.1396263F, 0.0F, 0.0F));
        head.addOrReplaceChild("hat_segment_2",
                CubeListBuilder.create().texOffs(0, 40).mirror().addBox(0.0F, 0.0F, 0.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(-2.5F, -12.13333F, -2.0F, -0.2443461F, 0.0F, 0.0F));
        head.addOrReplaceChild("hat_segment_3",
                CubeListBuilder.create().texOffs(24, 32).mirror().addBox(0.0F, 0.0F, 0.0F, 4.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(-2.0F, -13.6F, -1.0F, -0.4014257F, 0.0F, 0.0F));
        head.addOrReplaceChild("hat_segment_4",
                CubeListBuilder.create().texOffs(24, 38).mirror().addBox(0.0F, 0.0F, 0.0F, 3.0F, 2.0F, 3.0F),
                PartPose.offsetAndRotation(-1.5F, -14.6F, 0.0F, -0.5759587F, 0.0F, 0.0F));
        head.addOrReplaceChild("hat_segment_5",
                CubeListBuilder.create().texOffs(20, 43).mirror().addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(-1.0F, -14.6F, 0.0F, 0.3316126F, 0.0F, 0.0F));
        head.addOrReplaceChild("hat_segment_6",
                CubeListBuilder.create().texOffs(28, 43).mirror().addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F),
                PartPose.offsetAndRotation(-0.5F, -15.1F, 2.0F, -0.5585054F, 0.0F, 0.0F));
        head.addOrReplaceChild("hat_bobble",
                CubeListBuilder.create()
                        .texOffs(52, 47)
                        .mirror()
                        .addBox(0.0F, 0.0F, 0.0F, 3.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(-1.5F, -15.1F, 4.0F, -0.65F, 0.0F, 0.0F));
        root.addOrReplaceChild("robe",
                CubeListBuilder.create()
                        .texOffs(40, 32)
                        .mirror()
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 7.0F, 4.0F, delta),
                PartPose.offset(0.0F, 12.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(@NonNull T state) {
        super.setupAnim(state);
        this.robe.visible = this.body.visible;
        this.hatBobble.visible = IS_CHRISTMAS;
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