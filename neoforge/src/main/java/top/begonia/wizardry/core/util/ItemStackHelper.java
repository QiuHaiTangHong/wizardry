package top.begonia.wizardry.core.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.data.WandUpgrades;
import top.begonia.wizardry.core.item.impl.WizardArmourItem;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.*;

public final class ItemStackHelper {
    private static final HashMap<Item, String> upgradeMap = new HashMap<>();

    public static AbstractSpell @NonNull [] getSpells(@NonNull ItemStack wand) {
        List<Holder<AbstractSpell>> spellHolders = wand.getOrDefault(
                WizardryComponents.SPELLS.get(),
                List.of()
        );
        return spellHolders.stream()
                .map(Holder::value)
                .toArray(AbstractSpell[]::new);
    }

    public static void setSpells(ItemStack wand, AbstractSpell[] spells) {
        if (spells == null || spells.length == 0) {
            wand.set(WizardryComponents.SPELLS.get(), List.of());
            return;
        }
        Registry<AbstractSpell> registry = WizardrySpells.SPELLS.getRegistry().get();
        List<Holder<AbstractSpell>> spellHolders = Arrays.stream(spells)
                .map(spell -> {
                    AbstractSpell target = (spell != null) ? spell : WizardrySpells.NONE.get();
                    return registry.wrapAsHolder(target);
                })
                .toList();
        wand.set(WizardryComponents.SPELLS.get(), spellHolders);
    }

    public static AbstractSpell getCurrentSpell(ItemStack wand) {
        AbstractSpell[] spells = getSpells(wand);
        int selectedSpell = wand.getOrDefault(WizardryComponents.CURRENT_SPELL.get(), 0);
        if (selectedSpell >= 0 && selectedSpell < spells.length) {
            return spells[selectedSpell];
        }
        return WizardrySpells.NONE.get();
    }

    public static AbstractSpell getNextSpell(ItemStack wand) {

        AbstractSpell[] spells = getSpells(wand);
        int index = getNextSpellIndex(wand);

        if (index >= 0 && index < spells.length) {
            return spells[index];
        }

        return WizardrySpells.NONE.get();
    }

    public static AbstractSpell getPreviousSpell(ItemStack wand) {

        AbstractSpell[] spells = getSpells(wand);
        int index = getPreviousSpellIndex(wand);

        if (index >= 0 && index < spells.length) {
            return spells[index];
        }

        return WizardrySpells.NONE.get();
    }

    public static @NonNull MutableComponent getScrollDisplayName(@NonNull ItemStack scroll) {
        AbstractSpell spell = scroll.getOrDefault(WizardryComponents.SPELL.get(), WizardrySpells.NONE).value();
        return Component.translatable("item." + Wizardry.MODID + ".scroll", spell.getDisplayName());
    }

    public static void selectNextSpell(@NonNull ItemStack wand) {
        if (!wand.has(WizardryComponents.SPELLS.get())) {
            wand.set(WizardryComponents.SPELLS.get(), new ArrayList<>());
        }
        int nextIndex = getNextSpellIndex(wand);
        wand.set(WizardryComponents.CURRENT_SPELL.get(), nextIndex);
    }

    public static void selectPreviousSpell(@NonNull ItemStack wand) {
        if (!wand.has(WizardryComponents.SPELLS.get())) {
        }
        int prevIndex = getPreviousSpellIndex(wand);
        wand.set(WizardryComponents.CURRENT_SPELL.get(), prevIndex);
    }

    public static boolean selectSpell(ItemStack wand, int index) {
        AbstractSpell[] spells = getSpells(wand);
        if (!wand.has(WizardryComponents.SPELLS.get())) {

        }
        if (index < 0 || index >= spells.length) {
            return false;
        }
        wand.set(WizardryComponents.CURRENT_SPELL.get(), index);
        return true;
    }

    private static int getNextSpellIndex(ItemStack wand) {
        int numberOfSpells = getSpells(wand).length;
        if (numberOfSpells <= 1) {
            return 0;
        }
        int currentIndex = wand.getOrDefault(WizardryComponents.CURRENT_SPELL.get(), 0);
        return (currentIndex + 1) % numberOfSpells;
    }

    private static int getPreviousSpellIndex(ItemStack wand) {
        int numberOfSpells = getSpells(wand).length;
        if (numberOfSpells <= 1) return 0;
        int currentIndex = wand.getOrDefault(WizardryComponents.CURRENT_SPELL.get(), 0);
        return (currentIndex - 1 + numberOfSpells) % numberOfSpells;
    }

    public static int @NonNull [] getCooldowns(@NonNull ItemStack wand) {
        List<Integer> cooldownList = wand.getOrDefault(WizardryComponents.COOLDOWN_ARRAY_KEY.get(), List.of());
        int[] cooldowns = new int[cooldownList.size()];
        for (int i = 0; i < cooldownList.size(); i++) {
            cooldowns[i] = cooldownList.get(i);
        }
        return cooldowns;
    }

    public static void setCooldowns(ItemStack wand, int[] cooldowns) {
        if (cooldowns == null) {
            wand.remove(WizardryComponents.COOLDOWN_ARRAY_KEY.get());
            return;
        }
        List<Integer> list = new ArrayList<>(cooldowns.length);
        for (int cooldown : cooldowns) {
            list.add(cooldown);
        }
        wand.set(WizardryComponents.COOLDOWN_ARRAY_KEY.get(), list);
    }

    public static void decrementCooldowns(ItemStack wand) {
        int[] cooldowns = getCooldowns(wand);
        if (cooldowns.length == 0) return;
        for (int i = 0; i < cooldowns.length; i++) {
            if (cooldowns[i] > 0) cooldowns[i]--;
            if (cooldowns[i] < 0) cooldowns[i] = 0;
        }
        setCooldowns(wand, cooldowns);
    }

    public static int getCurrentCooldown(@NonNull ItemStack wand) {
        int selectedSpell = wand.getOrDefault(WizardryComponents.CURRENT_SPELL.get(), 0);
        int[] cooldowns = getCooldowns(wand);
        if (selectedSpell < 0 || selectedSpell >= cooldowns.length) {
            return 0;
        }
        return cooldowns[selectedSpell];
    }

    public static int getNextCooldown(ItemStack wand) {
        int[] cooldowns = getCooldowns(wand);
        int nextSpell = getNextSpellIndex(wand);
        if (nextSpell < 0 || cooldowns.length <= nextSpell) {
            return 0;
        }
        return cooldowns[nextSpell];
    }

    public static int getPreviousCooldown(ItemStack wand) {
        int[] cooldowns = getCooldowns(wand);
        int previousSpell = getPreviousSpellIndex(wand);
        if (previousSpell < 0 || cooldowns.length <= previousSpell) {
            return 0;
        }
        return cooldowns[previousSpell];
    }

    public static void setCurrentCooldown(@NonNull ItemStack wand, int cooldown) {
        int selectedSpell = wand.getOrDefault(WizardryComponents.CURRENT_SPELL.get(), 0);
        int spellCount = getSpells(wand).length;
        if (selectedSpell < 0 || selectedSpell >= spellCount) return;
        int[] cooldowns = getCooldowns(wand);
        if (cooldowns.length < spellCount) {
            cooldowns = java.util.Arrays.copyOf(cooldowns, spellCount);
        }
        int[] maxCooldowns = getMaxCooldowns(wand);
        if (maxCooldowns.length < spellCount) {
            maxCooldowns = java.util.Arrays.copyOf(maxCooldowns, spellCount);
        }
        int finalCooldown = Math.max(1, cooldown);
        cooldowns[selectedSpell] = finalCooldown;
        maxCooldowns[selectedSpell] = finalCooldown;
        setCooldowns(wand, cooldowns);
        setMaxCooldowns(wand, maxCooldowns);
    }

    public static int[] getMaxCooldowns(@NonNull ItemStack wand) {
        List<Integer> list = wand.getOrDefault(WizardryComponents.MAX_COOLDOWN_ARRAY_KEY.get(), List.of());
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    public static void setMaxCooldowns(ItemStack wand, int[] cooldowns) {
        if (cooldowns == null) {
            wand.remove(WizardryComponents.MAX_COOLDOWN_ARRAY_KEY.get());
            return;
        }
        List<Integer> list = java.util.stream.IntStream.of(cooldowns).boxed().toList();
        wand.set(WizardryComponents.MAX_COOLDOWN_ARRAY_KEY.get(), list);
    }

    public static int getCurrentMaxCooldown(@NonNull ItemStack wand) {
        int selectedSpell = wand.getOrDefault(WizardryComponents.CURRENT_SPELL.get(), 0);
        int[] maxCooldowns = getMaxCooldowns(wand);

        if (selectedSpell < 0 || selectedSpell >= maxCooldowns.length) return 0;
        return maxCooldowns[selectedSpell];
    }

    public static int getUpgradeLevel(ItemStack wand, Item upgrade) {

        String key = upgradeMap.get(upgrade);
        if (key == null) return 0;
        WandUpgrades upgrades = wand.getOrDefault(
                WizardryComponents.UPGRADES.get(),
                WandUpgrades.EMPTY
        );
        return upgrades.counts().getOrDefault(key, 0);
    }

    public static int getTotalUpgrades(ItemStack wand) {

        int totalUpgrades = 0;

        for (Item item : upgradeMap.keySet()) {
            totalUpgrades += getUpgradeLevel(wand, item);
        }

        return totalUpgrades;
    }

    public static void applyUpgrade(ItemStack wand, Item upgrade) {
        String key = upgradeMap.get(upgrade);
        if (key == null) {
            return;
        }
        wand.update(
                WizardryComponents.UPGRADES.get(),
                WandUpgrades.EMPTY,
                current -> current.withUpgrade(key)
        );
    }

    public static boolean isWandUpgrade(Item upgrade) {
        return upgradeMap.containsKey(upgrade);
    }

    public static @NonNull @UnmodifiableView Set<Item> getSpecialUpgrades() {
        return Collections.unmodifiableSet(ItemStackHelper.upgradeMap.keySet());
    }

    static String getIdentifier(Item upgrade) {
        if (!isWandUpgrade(upgrade)) throw new IllegalArgumentException(
                "Tried to get a wand upgrade key for an item" + "that is not a registered special wand upgrade.");
        return upgradeMap.get(upgrade);
    }

    public static void registerSpecialUpgrade(Item upgrade, String identifier) {
        if (upgradeMap.containsValue(identifier))
            throw new IllegalArgumentException("Duplicate wand upgrade identifier: " + identifier);
        upgradeMap.put(upgrade, identifier);
    }

    public static void populateUpgradeMap() {
        upgradeMap.put(WizardryItems.CONDENSER_UPGRADE.get(), "condenser");
        upgradeMap.put(WizardryItems.STORAGE_UPGRADE.get(), "storage");
        upgradeMap.put(WizardryItems.SIPHON_UPGRADE.get(), "siphon");
        upgradeMap.put(WizardryItems.RANGE_UPGRADE.get(), "range");
        upgradeMap.put(WizardryItems.DURATION_UPGRADE.get(), "duration");
        upgradeMap.put(WizardryItems.COOLDOWN_UPGRADE.get(), "cooldown");
        upgradeMap.put(WizardryItems.BLAST_UPGRADE.get(), "blast");
        upgradeMap.put(WizardryItems.ATTUNEMENT_UPGRADE.get(), "attunement");
        upgradeMap.put(WizardryItems.MELEE_UPGRADE.get(), "melee");
    }

    public static void setProgression(@NonNull ItemStack wand, int progression) {
        wand.set(WizardryComponents.PROGRESSION.get(), progression);
    }

    public static int getProgression(@NonNull ItemStack wand) {
        return wand.getOrDefault(WizardryComponents.PROGRESSION.get(), 0);
    }

    public static void addProgression(ItemStack wand, int progression) {
        setProgression(wand, getProgression(wand) + progression);
    }

    public static boolean rechargeManaOnApplyButtonPressed(Slot centre, Slot crystals) {
        return false;
    }

    public static @NonNull TierEnum getTier(@NonNull ItemStack stack) {
        return stack.getOrDefault(WizardryComponents.TIER, TierEnum.NOVICE);
    }

    public static void setTier(@NonNull ItemStack stack, TierEnum tier) {
        stack.set(WizardryComponents.TIER, tier);
    }

    public static @NonNull ElementEnum getElement(@NonNull ItemStack stack) {
        return stack.getOrDefault(WizardryComponents.ELEMENT, ElementEnum.MAGIC);
    }

    public static void setElement(@NonNull ItemStack stack, ElementEnum element) {
        stack.set(WizardryComponents.ELEMENT, element);
    }

    public static @NonNull ItemStack getWand(TierEnum tier, ElementEnum element) {
        ItemStack itemStack = new ItemStack(WizardryItems.WAND);
        ItemStackHelper.setTier(itemStack, tier);
        ItemStackHelper.setElement(itemStack, element);
        itemStack.set(DataComponents.MAX_DAMAGE, tier.getMaxCharge());
        return itemStack;
    }

    /**
     * 装备生成工厂。
     * <p>
     * 遵循 26.1.1 的现代化全面数据组件化标准。不再依赖硬编码，而是将材质基类、子元素、最大耐久度、
     * 可附魔状态、属性修饰符以及最重要的 {@link Equippable} 可穿戴组件(包含渲染资产 ID 映射)
     * 统一作为独立 Component 编译写入生成的 {@link ItemStack} 中.
     * </p>
     *
     * @param armourItem         模组盔甲物品基类实例
     * @param element            装备绑定的元素核心属性
     * @param armourMaterialType 装备所属的法袍骨骼材质分类
     * @param armorType          装备的具体槽位物理形态(HELMET, CHESTPLATE 等)
     * @return 包含完整数据驱动上下文组件、可直接给予玩家的 {@link ItemStack} 实例
     */
    public static @NonNull ItemStack generateArmour(
            WizardArmourItem armourItem,
            ElementEnum element,
            ArmourHelper.@NonNull ArmourMaterialType armourMaterialType,
            @NonNull ArmorType armorType
    ) {
        ItemStack itemStack = new ItemStack(armourItem);
        ArmorMaterial armorMaterial = armourMaterialType.getBuilder().build(element);
        itemStack.set(WizardryComponents.ARMOR_MATERIAL_TYPE, armourMaterialType);
        itemStack.set(WizardryComponents.ARMOR_TYPE, armorType);
        itemStack.set(WizardryComponents.ELEMENT, element);
        itemStack.set(DataComponents.MAX_DAMAGE, armorType.getDurability(armorMaterial.durability()));
        itemStack.set(DataComponents.MAX_STACK_SIZE, 1);
        itemStack.set(DataComponents.DAMAGE, 0);
        itemStack.set(DataComponents.ENCHANTABLE, new Enchantable(armorMaterial.enchantmentValue()));
        itemStack.set(DataComponents.ATTRIBUTE_MODIFIERS, armorMaterial.createAttributes(armorType));
        itemStack.set(DataComponents.EQUIPPABLE, Equippable.builder(armorType.getSlot()).setEquipSound(armorMaterial.equipSound()).setAsset(armorMaterial.assetId()).setAllowedEntities(EntityTypes.PLAYER).build());
        return itemStack;
    }

    public static ArmourHelper.ArmourMaterialType getArmourMaterialType(@NonNull ItemStack stack) {
        return stack.get(WizardryComponents.ARMOR_MATERIAL_TYPE);
    }

    public static ArmorType getArmorType(@NonNull ItemStack stack) {
        return stack.get(WizardryComponents.ARMOR_TYPE);
    }
}
