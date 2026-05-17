package top.begonia.wizardry.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.model.IWizardryArmour;
import top.begonia.wizardry.client.model.RobeArmourModel;
import top.begonia.wizardry.client.model.SageArmourModel;
import top.begonia.wizardry.client.model.WizardArmourModel;
import top.begonia.wizardry.common.util.ArmourMaterialHelper;

import java.util.HashMap;
import java.util.Map;

public class WizardryArmorRenderer implements IClientItemExtensions {
    private final Map<ModelLayerLocation, HumanoidModel<?>> modelCache = new HashMap<>();

    @Override
    public @NonNull Model<?> getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.@NonNull LayerType layerType, @NonNull Model original) {
        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) return original;

        ModelLayerLocation layerLocation = ArmourMaterialHelper.getModelLayer(equippable.assetId());
        if (layerLocation == null) return original;

        return modelCache.computeIfAbsent(layerLocation, loc -> {
            EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
            ModelPart root = modelSet.bakeLayer(loc);
            if (loc.equals(ArmourMaterialHelper.ModelLayer.WIZARD)) {
                return new WizardArmourModel<>(root);
            } else if (loc.equals(ArmourMaterialHelper.ModelLayer.SAGE)) {
                return new SageArmourModel<>(root);
            } else {
                return new RobeArmourModel<>(root);
            }
        });
    }

    @Override
    public @NonNull Model<?> getGenericArmorModel(@NonNull ItemStack itemStack, EquipmentClientInfo.@NonNull LayerType layerType, @NonNull Model original) {
        Model<?> model = IClientItemExtensions.super.getGenericArmorModel(itemStack, layerType, original);
        if (model instanceof HumanoidModel<?> humanoid) {
            Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
            if (equippable != null) {
                if (humanoid instanceof IWizardryArmour wizardryArmour) {
                    wizardryArmour.updateVisible(equippable.slot());
                }
            }
        }
        return model;
    }
}
