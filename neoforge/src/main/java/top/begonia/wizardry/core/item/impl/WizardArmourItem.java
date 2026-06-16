package top.begonia.wizardry.core.item.impl;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.item.IManaStoringItem;
import top.begonia.wizardry.core.item.IWorkbenchItem;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.util.ArmourHelper;

import java.util.function.Consumer;

public class WizardArmourItem extends Item implements IWorkbenchItem, IManaStoringItem {
    private static final float SAGE_OTHER_COST_REDUCTION = 0.2f;
    private static final float WARLOCK_SPEED_BOOST = 0.2f;

    public WizardArmourItem(Item.Properties properties) {
        super(properties);
    }

    public ArmorType getArmorType(@NonNull ItemStack itemStack) {
        return itemStack.get(WizardryComponents.ARMOR_TYPE);
    }

    public ArmourHelper.ArmourMaterialType getArmourMaterial(@NonNull ItemStack itemStack) {
        return itemStack.get(WizardryComponents.ARMOR_MATERIAL_TYPE);
    }

    public ElementEnum getElement(@NonNull ItemStack itemStack) {
        return itemStack.get(WizardryComponents.ELEMENT);
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack itemStack) {
        ArmourHelper.ArmourMaterialType armourMaterial = itemStack.get(WizardryComponents.ARMOR_MATERIAL_TYPE);
        ArmorType armorType = itemStack.get(WizardryComponents.ARMOR_TYPE);
        ElementEnum element = itemStack.get(WizardryComponents.ELEMENT);
        if (element != null && armorType != null && armourMaterial != null) {
            return Component.translatable("item." + Wizardry.MODID + "." + armourMaterial.getSerializedName() + "_" + armorType.getSerializedName() + "_" + element.getSerializedName()).withStyle(element.getStyle());
        }
        return Component.empty();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(
            @NonNull ItemStack itemStack,
            @NonNull TooltipContext context,
            @NonNull TooltipDisplay display,
            @NonNull Consumer<Component> builder,
            @NonNull TooltipFlag tooltipFlag
    ) {
        ArmourHelper.ArmourMaterialType armourMaterialType = this.getArmourMaterial(itemStack);
        float cooldownReduction = armourMaterialType.getBuilder().getCooldownReduction();
        float elementalCostReduction = armourMaterialType.getBuilder().getElementalCostReduction();
        ElementEnum element = this.getElement(itemStack);
        if (element != null) {
            builder.accept(Component.translatable(
                    "item." + Wizardry.MODID + ".wizard_armour.element_cost_reduction",
                    (int) (elementalCostReduction * 100),
                    element.getDisplayName()).withStyle(ChatFormatting.DARK_GRAY)
            );
        }

        if (armourMaterialType == ArmourHelper.ArmourMaterialType.SAGE) {
            builder.accept(
                    Component.translatable(
                            "item." + Wizardry.MODID + ".wizard_armour.enchantability"
                    ).withStyle(ChatFormatting.BLUE)
            );
        }

        if (cooldownReduction > 0) {
            builder.accept(
                    Component.translatable(
                            "item." + Wizardry.MODID + ".wizard_armour.cooldown_reduction", (int) (cooldownReduction * 100)
                    ).withStyle(ChatFormatting.DARK_GRAY)
            );
        }

        if (armourMaterialType != ArmourHelper.ArmourMaterialType.WIZARD) {

            builder.accept(
                    Component.translatable(
                            "item." + Wizardry.MODID + ".wizard_armour.full_set"
                    ).withStyle(ChatFormatting.AQUA)
            );

            int fullSetBonus = 0;

            if (armourMaterialType == ArmourHelper.ArmourMaterialType.SAGE) {
                fullSetBonus = (int) (SAGE_OTHER_COST_REDUCTION * 100);
            }
            if (armourMaterialType == ArmourHelper.ArmourMaterialType.WARLOCK) {
                fullSetBonus = (int) (WARLOCK_SPEED_BOOST * 100);
            }

            builder.accept(
                    Component.translatable(
                            "item." + Wizardry.MODID + "." + armourMaterialType.getSerializedName() + "_armour.full_set_bonus",
                            fullSetBonus
                    ).withStyle(ChatFormatting.AQUA)
            );

        }
    }

    @Override
    public int getBarColor(@NonNull ItemStack stack) {
        float ratio = (float) stack.getDamageValue() / stack.getMaxDamage();
        return ARGB.srgbLerp(ratio, 0xff8bfe, 0x8e2ee4);
    }

    @Override
    public int getMana(@NonNull ItemStack itemStack) {
        return itemStack.getOrDefault(WizardryComponents.MANA, 0);
    }

    @Override
    public void setMana(@NonNull ItemStack itemStack, int mana) {
        itemStack.set(WizardryComponents.MANA, mana);
    }

    @Override
    public int getManaCapacity(@NonNull ItemStack stack) {
        return 0;
    }

    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        return false;
    }

    @Override
    public boolean showTooltip(ItemStack stack) {
        return false;
    }
}
