package top.begonia.wizardry.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;

public interface IWizardryArmour {
    default void updateVisible(EquipmentSlot equipmentSlot) {
        if (this instanceof HumanoidModel<?> humanoidModel) {
            humanoidModel.hat.visible = false;
            humanoidModel.head.visible = false;
            humanoidModel.body.visible = false;
            humanoidModel.leftArm.visible = false;
            humanoidModel.rightArm.visible = false;
            humanoidModel.leftLeg.visible = false;
            humanoidModel.rightLeg.visible = false;
            switch (equipmentSlot) {
                case HEAD -> {
                    humanoidModel.hat.visible = true;
                    humanoidModel.head.visible = true;
                }
                case CHEST -> {
                    humanoidModel.body.visible = true;
                    humanoidModel.leftArm.visible = true;
                    humanoidModel.rightArm.visible = true;
                }
                case FEET, LEGS -> {
                    humanoidModel.leftLeg.visible = true;
                    humanoidModel.rightLeg.visible = true;
                }
            }
        }
    }
}
