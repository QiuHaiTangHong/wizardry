package top.begonia.wizardry.client.model;

import com.google.common.collect.Maps;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractWizardArmourModel<T extends HumanoidRenderState> extends HumanoidModel<T> {
    private final ModelPart robe;
    protected static final Map<EquipmentSlot, Set<String>> ADULT_ARMOR_PARTS_PER_SLOT;

    public AbstractWizardArmourModel(ModelPart root) {
        super(root);
        this.robe = root.getChild("robe");
    }

    static {
        ADULT_ARMOR_PARTS_PER_SLOT = Maps.newEnumMap(
                Map.of(
                        EquipmentSlot.HEAD, Set.of("head"),
                        EquipmentSlot.CHEST, Set.of("body", "left_arm", "right_arm", "robe"),
                        EquipmentSlot.LEGS, Set.of("left_leg", "right_leg", "body"),
                        EquipmentSlot.FEET, Set.of("left_leg", "right_leg")
                )
        );
    }

    @Contract("_, _, _, _ -> new")
    protected static @NonNull ArmorModelSetExtension<MeshDefinition> createArmorMeshSetExtension(
            @NonNull Function<CubeDeformation, MeshDefinition> baseFactory,
            @NonNull Map<EquipmentSlot, Set<String>> partsPerSlot,
            @NonNull CubeDeformation innerDeformation,
            @NonNull CubeDeformation outerDeformation
    ) {
        MeshDefinition head = baseFactory.apply(outerDeformation);
        head.getRoot().retainPartsAndChildren(partsPerSlot.get(EquipmentSlot.HEAD));
        MeshDefinition chest = baseFactory.apply(outerDeformation);
        chest.getRoot().retainExactParts(partsPerSlot.get(EquipmentSlot.CHEST));
        MeshDefinition legs = baseFactory.apply(innerDeformation);
        legs.getRoot().retainExactParts(partsPerSlot.get(EquipmentSlot.LEGS));
        MeshDefinition feet = baseFactory.apply(outerDeformation);
        feet.getRoot().retainExactParts(partsPerSlot.get(EquipmentSlot.FEET));
        return new ArmorModelSetExtension<>(head, chest, legs, feet);
    }

    public static @NonNull ArmorModelSetExtension<MeshDefinition> createArmorMeshSetExtension(
            @NonNull Function<CubeDeformation, MeshDefinition> baseFactory,
            @NonNull CubeDeformation innerDeformation,
            @NonNull CubeDeformation outerDeformation
    ) {
        return createArmorMeshSetExtension(baseFactory, ADULT_ARMOR_PARTS_PER_SLOT, innerDeformation, outerDeformation);
    }

    public static @NonNull ArmorModelSetExtension<MeshDefinition> createArmorMeshSetExtension(
            @NonNull CubeDeformation innerDeformation,
            @NonNull CubeDeformation outerDeformation
    ) {
        return createArmorMeshSetExtension(AbstractWizardArmourModel::createBaseArmorMesh, innerDeformation, outerDeformation);
    }

    protected static @NonNull MeshDefinition createBaseArmorMesh(CubeDeformation cubeDeformation) {
        MeshDefinition mesh = createMesh(cubeDeformation, 0.0F);
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(-0.1F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .mirror()
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(-0.1F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("robe",
                CubeListBuilder.create()
                        .texOffs(40, 32)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 9.0F, 4.0F, cubeDeformation),
                PartPose.offset(0.0F, 12.0F, 0.0F)
        );
        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(16, 16)
                        .mirror()
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 11.0F, 4.0F, cubeDeformation),
                PartPose.ZERO);
        return mesh;
    }

    @Override
    public void setupAnim(@NonNull T state) {
        super.setupAnim(state);
        if (state instanceof AvatarRenderState avatarRenderState) {
            boolean showBody = !avatarRenderState.isSpectator;
            this.body.visible = showBody;
            this.rightArm.visible = showBody;
            this.leftArm.visible = showBody;
            this.rightLeg.visible = showBody;
            this.leftLeg.visible = showBody;
            this.hat.visible = avatarRenderState.showHat;
        }
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
