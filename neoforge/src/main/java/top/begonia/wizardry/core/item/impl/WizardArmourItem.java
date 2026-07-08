package top.begonia.wizardry.core.item.impl;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.item.IManaStoringItem;
import top.begonia.wizardry.core.item.IWorkbenchItem;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.util.ArmourHelper;
import top.begonia.wizardry.core.util.ItemStackHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
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
    public final void setDamage(@NonNull ItemStack stack, int damage) {
    }

    @Override
    public int getMana(@NonNull ItemStack stack) {
        return getManaCapacity(stack) - getDamage(stack);
    }

    @Override
    public void setMana(@NonNull ItemStack stack, int mana) {
        super.setDamage(stack, getManaCapacity(stack) - mana);
    }

    @Override
    public int getManaCapacity(@NonNull ItemStack stack) {
        return this.getMaxDamage(stack);
    }

    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return 0;
    }

    public static boolean doAllArmourPiecesHaveMana(LivingEntity entity) {
        return Arrays.stream(EquipmentSlot.values())
                .filter(slot -> slot.getType() == EquipmentSlot.Type.ANIMAL_ARMOR)
                .noneMatch(slot -> {
                    ItemStack stack = entity.getItemBySlot(slot);
                    if (stack.has(WizardryComponents.MANA)) {
                        int mana = stack.getOrDefault(WizardryComponents.MANA, 0);
                        return mana <= 0;
                    }
                    return false;
                });
    }

    @Override
    public void applyUpgrade(@Nullable Player player, @NonNull Slot center, @NonNull Slot upgradeSlot) {
        ItemStack stack = center.getItem();
        ItemStack upgrade = upgradeSlot.getItem();
        ArmourHelper.ArmourMaterialType originalType = ItemStackHelper.getArmourMaterialType(stack);
        ArmorType armorType = ItemStackHelper.getArmorType(stack);
        ElementEnum element = stack.getOrDefault(WizardryComponents.ELEMENT, ElementEnum.DEFAULT);
        if (originalType == ArmourHelper.ArmourMaterialType.WIZARD) {
            for (ArmourHelper.ArmourMaterialType currentType : ArmourHelper.ArmourMaterialType.values()) {
                if (upgrade.getItem() == currentType.getBuilder().getUpgradeItem()) {
                    ItemStack newStack = ItemStackHelper.generateArmour(WizardryItems.ARMOUR.get(), element, currentType, armorType);
                    ((WizardArmourItem) newStack.getItem()).setMana(newStack, this.getMana(stack));
                    upgrade.shrink(1);
                    upgradeSlot.set(upgrade);
                    center.set(newStack);
                }
            }
        }
    }

    @Override
    public boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, @NonNull Slot upgrade, Slot[] spellBooks) {
        boolean changed = false;
        if (upgrade.hasItem()) {
            ItemStack original = centre.getItem().copy();
            this.applyUpgrade(player, centre, upgrade);
            changed = !ItemStack.isSameItem(centre.getItem(), original);
        }
        if (crystals.getItem() != ItemStack.EMPTY && !this.isManaFull(centre.getItem())) {
            int chargeDepleted = this.getManaCapacity(centre.getItem()) - this.getMana(centre.getItem());
            int manaPerItem = crystals.getItem().getItem() instanceof IManaStoringItem ?
                    ((IManaStoringItem) crystals.getItem().getItem()).getMana(crystals.getItem()) :
                    crystals.getItem().getItem() instanceof MagicCrystalItem ? ServerConfig.Constants.manaPerCrystal : ServerConfig.Constants.manaPerShard;

            if (crystals.getItem().getItem() == WizardryItems.CRYSTAL_SHARD.get()) {
                manaPerItem = ServerConfig.Constants.manaPerShard;
            }
            if (crystals.getItem().getItem() == WizardryItems.GRAND_CRYSTAL.get()) {
                manaPerItem = ServerConfig.Constants.grandCrystalMana;
            }

            if (crystals.getItem().getCount() * manaPerItem < chargeDepleted) {
                this.rechargeMana(centre.getItem(), crystals.getItem().getCount() * ServerConfig.Constants.manaPerCrystal);
                crystals.getItem().shrink(crystals.getItem().getCount());

            } else {
                this.setMana(centre.getItem(), this.getManaCapacity(centre.getItem()));
                crystals.getItem().shrink((int) Math.ceil(((double) chargeDepleted) / ServerConfig.Constants.manaPerCrystal));
            }

            changed = true;
        }

        if (changed) {
            centre.setChanged();
        }

        return changed;
    }

    @Override
    public boolean showTooltip(ItemStack stack) {
        return true;
    }
}
