package top.begonia.wizardry.common.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.common.block.*;
import top.begonia.wizardry.common.data.spell.constants.ElementEnum;
import top.begonia.wizardry.common.data.spell.constants.TierEnum;
import top.begonia.wizardry.common.item.impl.*;
import top.begonia.wizardry.common.spell.AbstractSpell;
import top.begonia.wizardry.common.util.ArmourMaterialHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class WizardryItems {
    private WizardryItems() {
    }

    public static final List<DeferredItem<? extends Item>> WIZARD_ARMOUR_ITEMS = new ArrayList<>();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Wizardry.MODID);

    private static @NonNull DeferredHolder<Item, WandItem> registerItemWand(String name, TierEnum tier, ElementEnum element) {
        DeferredHolder<Item, WandItem> wand = ITEMS.register(name, (identifier) -> {
            Item.Properties properties = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, identifier)).durability(tier.maxCharge).stacksTo(1);
            return new WandItem(tier, element, properties);
        });
        WizardryCreativeTabs.addToTabs(WizardryCreativeTabs.TabsEnum.GEAR, wand);
        return wand;
    }

    public static <T extends Item> DeferredItem<T> registerItem(String name, Function<Item.Properties, T> itemFactory, WizardryCreativeTabs.TabsEnum tab) {
        DeferredItem<T> item = ITEMS.register(name, (identifier) -> {
            Item.Properties properties = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, identifier));
            return itemFactory.apply(properties);
        });
        WizardryCreativeTabs.addToTabs(tab, item);
        return item;
    }

    private static DeferredItem<WizardArmourItem> registerItem(String name, ArmourMaterialHelper.MaterialBuilder builder, ArmorType type, @Nullable ElementEnum element) {
        ArmorMaterial material = builder.build(element);
        Item.Properties properties = new Item.Properties().durability(type.getDurability(material.durability())).enchantable(material.enchantmentValue()).attributes(material.createAttributes(type)).component(DataComponents.EQUIPPABLE, Equippable.builder(type.getSlot()).setEquipSound(material.equipSound()).setAsset(material.assetId()).setAllowedEntities(EntityType.PLAYER).build());
        DeferredItem<WizardArmourItem> item = ITEMS.register(name, identifier -> {
            properties.setId(ResourceKey.create(Registries.ITEM, identifier));
            return new WizardArmourItem(element, properties);
        });
        WIZARD_ARMOUR_ITEMS.add(item);
        WizardryCreativeTabs.addToTabs(WizardryCreativeTabs.TabsEnum.GEAR, item);
        return item;
    }

    public static <T extends Item> @NonNull DeferredItem<T> registerItemWithSubItems(String name, Function<Item.Properties, T> itemFactory, WizardryCreativeTabs.TabsEnum tab, BiConsumer<BuildCreativeModeTabContentsEvent, T> tabPopulator) {
        DeferredItem<T> item = ITEMS.register(name, (identifier) -> {
            Item.Properties properties = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, identifier));
            return itemFactory.apply(properties);
        });
        WizardryCreativeTabs.addSpecialToTabs(tab, (event) -> tabPopulator.accept(event, item.get()));

        return item;
    }

    // 无元素法杖
    public static final DeferredHolder<Item, WandItem> MAGIC_WAND = registerItemWand("magic_wand", TierEnum.NOVICE, null);
    public static final DeferredHolder<Item, WandItem> APPRENTICE_WAND = registerItemWand("apprentice_wand", TierEnum.APPRENTICE, null);
    public static final DeferredHolder<Item, WandItem> ADVANCED_WAND = registerItemWand("advanced_wand", TierEnum.ADVANCED, null);
    public static final DeferredHolder<Item, WandItem> MASTER_WAND = registerItemWand("master_wand", TierEnum.MASTER, null);

    // 火系法杖
    public static final DeferredHolder<Item, WandItem> NOVICE_FIRE_WAND = registerItemWand("novice_fire_wand", TierEnum.NOVICE, ElementEnum.FIRE);
    public static final DeferredHolder<Item, WandItem> APPRENTICE_FIRE_WAND = registerItemWand("apprentice_fire_wand", TierEnum.APPRENTICE, ElementEnum.FIRE);
    public static final DeferredHolder<Item, WandItem> ADVANCED_FIRE_WAND = registerItemWand("advanced_fire_wand", TierEnum.ADVANCED, ElementEnum.FIRE);
    public static final DeferredHolder<Item, WandItem> MASTER_FIRE_WAND = registerItemWand("master_fire_wand", TierEnum.MASTER, ElementEnum.FIRE);

    // 冰系法杖
    public static final DeferredHolder<Item, WandItem> NOVICE_ICE_WAND = registerItemWand("novice_ice_wand", TierEnum.NOVICE, ElementEnum.ICE);
    public static final DeferredHolder<Item, WandItem> APPRENTICE_ICE_WAND = registerItemWand("apprentice_ice_wand", TierEnum.APPRENTICE, ElementEnum.ICE);
    public static final DeferredHolder<Item, WandItem> ADVANCED_ICE_WAND = registerItemWand("advanced_ice_wand", TierEnum.ADVANCED, ElementEnum.ICE);
    public static final DeferredHolder<Item, WandItem> MASTER_ICE_WAND = registerItemWand("master_ice_wand", TierEnum.MASTER, ElementEnum.ICE);

    // 雷系法杖
    public static final DeferredHolder<Item, WandItem> NOVICE_LIGHTNING_WAND = registerItemWand("novice_lightning_wand", TierEnum.NOVICE, ElementEnum.LIGHTNING);
    public static final DeferredHolder<Item, WandItem> APPRENTICE_LIGHTNING_WAND = registerItemWand("apprentice_lightning_wand", TierEnum.APPRENTICE, ElementEnum.LIGHTNING);
    public static final DeferredHolder<Item, WandItem> ADVANCED_LIGHTNING_WAND = registerItemWand("advanced_lightning_wand", TierEnum.ADVANCED, ElementEnum.LIGHTNING);
    public static final DeferredHolder<Item, WandItem> MASTER_LIGHTNING_WAND = registerItemWand("master_lightning_wand", TierEnum.MASTER, ElementEnum.LIGHTNING);

    // 亡灵系法杖
    public static final DeferredHolder<Item, WandItem> NOVICE_NECROMANCY_WAND = registerItemWand("novice_necromancy_wand", TierEnum.NOVICE, ElementEnum.NECROMANCY);
    public static final DeferredHolder<Item, WandItem> APPRENTICE_NECROMANCY_WAND = registerItemWand("apprentice_necromancy_wand", TierEnum.APPRENTICE, ElementEnum.NECROMANCY);
    public static final DeferredHolder<Item, WandItem> ADVANCED_NECROMANCY_WAND = registerItemWand("advanced_necromancy_wand", TierEnum.ADVANCED, ElementEnum.NECROMANCY);
    public static final DeferredHolder<Item, WandItem> MASTER_NECROMANCY_WAND = registerItemWand("master_necromancy_wand", TierEnum.MASTER, ElementEnum.NECROMANCY);

    // 土系法杖
    public static final DeferredHolder<Item, WandItem> NOVICE_EARTH_WAND = registerItemWand("novice_earth_wand", TierEnum.NOVICE, ElementEnum.EARTH);
    public static final DeferredHolder<Item, WandItem> APPRENTICE_EARTH_WAND = registerItemWand("apprentice_earth_wand", TierEnum.APPRENTICE, ElementEnum.EARTH);
    public static final DeferredHolder<Item, WandItem> ADVANCED_EARTH_WAND = registerItemWand("advanced_earth_wand", TierEnum.ADVANCED, ElementEnum.EARTH);
    public static final DeferredHolder<Item, WandItem> MASTER_EARTH_WAND = registerItemWand("master_earth_wand", TierEnum.MASTER, ElementEnum.EARTH);

    // 术法系法杖
    public static final DeferredHolder<Item, WandItem> NOVICE_SORCERY_WAND = registerItemWand("novice_sorcery_wand", TierEnum.NOVICE, ElementEnum.SORCERY);
    public static final DeferredHolder<Item, WandItem> APPRENTICE_SORCERY_WAND = registerItemWand("apprentice_sorcery_wand", TierEnum.APPRENTICE, ElementEnum.SORCERY);
    public static final DeferredHolder<Item, WandItem> ADVANCED_SORCERY_WAND = registerItemWand("advanced_sorcery_wand", TierEnum.ADVANCED, ElementEnum.SORCERY);
    public static final DeferredHolder<Item, WandItem> MASTER_SORCERY_WAND = registerItemWand("master_sorcery_wand", TierEnum.MASTER, ElementEnum.SORCERY);

    // 治疗系法杖
    public static final DeferredHolder<Item, WandItem> NOVICE_HEALING_WAND = registerItemWand("novice_healing_wand", TierEnum.NOVICE, ElementEnum.HEALING);
    public static final DeferredHolder<Item, WandItem> APPRENTICE_HEALING_WAND = registerItemWand("apprentice_healing_wand", TierEnum.APPRENTICE, ElementEnum.HEALING);
    public static final DeferredHolder<Item, WandItem> ADVANCED_HEALING_WAND = registerItemWand("advanced_healing_wand", TierEnum.ADVANCED, ElementEnum.HEALING);
    public static final DeferredHolder<Item, WandItem> MASTER_HEALING_WAND = registerItemWand("master_healing_wand", TierEnum.MASTER, ElementEnum.HEALING);

    //法术书
    public static final DeferredHolder<Item, SpellBookItem> SPELL_BOOK = registerItemWithSubItems(
            "spell_book",
            properties -> new SpellBookItem(properties.stacksTo(16)),
            WizardryCreativeTabs.TabsEnum.SPELLS,
            (buildCreativeModeTabContentsEvent, item) -> {
                buildCreativeModeTabContentsEvent.getParameters().holders().lookup(WizardrySpells.SPELLS_KEY).ifPresent(registry -> {
                    List<Holder.Reference<AbstractSpell>> sortedSpells = new ArrayList<>(registry.listElements().toList());
                    sortedSpells.sort(Comparator.comparing(Holder.Reference::value));
                    for (Holder.Reference<AbstractSpell> spellHolder : sortedSpells) {
                        if (spellHolder.is(WizardrySpells.NONE)) {
                            continue;
                        }
                        AbstractSpell spell = spellHolder.value();
                        if (spell.applicableForItem(item)) {
                            ItemStack spellBook = new ItemStack(item);
                            spellBook.set(WizardryComponents.SPELL_BOOK_KEY.get(), spellHolder);
                            buildCreativeModeTabContentsEvent.accept(spellBook);
                        }
                    }
                });
            }
    );

    //法术卷轴
    public static final DeferredHolder<Item, ScrollItem> SCROLL = registerItemWithSubItems(
            "scroll",
            properties -> new ScrollItem(properties.stacksTo(16)),
            WizardryCreativeTabs.TabsEnum.SPELLS,
            (buildCreativeModeTabContentsEvent, item) -> {
                buildCreativeModeTabContentsEvent.getParameters().holders().lookup(WizardrySpells.SPELLS_KEY).ifPresent(registry -> {
                    List<Holder.Reference<AbstractSpell>> sortedSpells = new ArrayList<>(registry.listElements().toList());
                    sortedSpells.sort(Comparator.comparing(Holder.Reference::value));
                    for (Holder.Reference<AbstractSpell> spellHolder : sortedSpells) {
                        if (spellHolder.is(WizardrySpells.NONE)) {
                            continue;
                        }
                        AbstractSpell spell = spellHolder.value();
                        if (spell.applicableForItem(item)) {
                            ItemStack scroll = new ItemStack(item);
                            scroll.set(WizardryComponents.SPELL_BOOK_KEY.get(), spellHolder);
                            buildCreativeModeTabContentsEvent.accept(scroll);
                        }
                    }
                });
            }
    );

    public static final DeferredHolder<Item, ArmourUpgradeItem> RESPLENDENT_THREAD = registerItem("resplendent_thread", properties -> new ArmourUpgradeItem(properties.stacksTo(1).rarity(Rarity.EPIC)), WizardryCreativeTabs.TabsEnum.WIZARDRY);
    public static final DeferredHolder<Item, ArmourUpgradeItem> CRYSTAL_SILVER_PLATING = registerItem("crystal_silver_plating", properties -> new ArmourUpgradeItem(properties.stacksTo(1).rarity(Rarity.EPIC)), WizardryCreativeTabs.TabsEnum.WIZARDRY);
    public static final DeferredHolder<Item, ArmourUpgradeItem> ETHEREAL_CRYSTALWEAVE = registerItem("ethereal_crystalweave", properties -> new ArmourUpgradeItem(properties.stacksTo(1).rarity(Rarity.EPIC)), WizardryCreativeTabs.TabsEnum.WIZARDRY);

    // --- 基础法师套装 (WIZARD) ---
    public static final DeferredItem<WizardArmourItem> WIZARD_HAT = registerItem("wizard_hat", ArmourMaterialHelper.WIZARD, ArmorType.HELMET, null);
    public static final DeferredItem<WizardArmourItem> WIZARD_ROBE = registerItem("wizard_robe", ArmourMaterialHelper.WIZARD, ArmorType.CHESTPLATE, null);
    public static final DeferredItem<WizardArmourItem> WIZARD_LEGGINGS = registerItem("wizard_leggings", ArmourMaterialHelper.WIZARD, ArmorType.LEGGINGS, null);
    public static final DeferredItem<WizardArmourItem> WIZARD_BOOTS = registerItem("wizard_boots", ArmourMaterialHelper.WIZARD, ArmorType.BOOTS, null);

    // --- 火元素 (FIRE) ---
    public static final DeferredItem<WizardArmourItem> WIZARD_HAT_FIRE = registerItem("wizard_hat_fire", ArmourMaterialHelper.WIZARD, ArmorType.HELMET, ElementEnum.FIRE);
    public static final DeferredItem<WizardArmourItem> WIZARD_ROBE_FIRE = registerItem("wizard_robe_fire", ArmourMaterialHelper.WIZARD, ArmorType.CHESTPLATE, ElementEnum.FIRE);
    public static final DeferredItem<WizardArmourItem> WIZARD_LEGGINGS_FIRE = registerItem("wizard_leggings_fire", ArmourMaterialHelper.WIZARD, ArmorType.LEGGINGS, ElementEnum.FIRE);
    public static final DeferredItem<WizardArmourItem> WIZARD_BOOTS_FIRE = registerItem("wizard_boots_fire", ArmourMaterialHelper.WIZARD, ArmorType.BOOTS, ElementEnum.FIRE);

    // --- 冰元素 (ICE) ---
    public static final DeferredItem<WizardArmourItem> WIZARD_HAT_ICE = registerItem("wizard_hat_ice", ArmourMaterialHelper.WIZARD, ArmorType.HELMET, ElementEnum.ICE);
    public static final DeferredItem<WizardArmourItem> WIZARD_ROBE_ICE = registerItem("wizard_robe_ice", ArmourMaterialHelper.WIZARD, ArmorType.CHESTPLATE, ElementEnum.ICE);
    public static final DeferredItem<WizardArmourItem> WIZARD_LEGGINGS_ICE = registerItem("wizard_leggings_ice", ArmourMaterialHelper.WIZARD, ArmorType.LEGGINGS, ElementEnum.ICE);
    public static final DeferredItem<WizardArmourItem> WIZARD_BOOTS_ICE = registerItem("wizard_boots_ice", ArmourMaterialHelper.WIZARD, ArmorType.BOOTS, ElementEnum.ICE);

    // --- 闪电元素 (LIGHTNING) ---
    public static final DeferredItem<WizardArmourItem> WIZARD_HAT_LIGHTNING = registerItem("wizard_hat_lightning", ArmourMaterialHelper.WIZARD, ArmorType.HELMET, ElementEnum.LIGHTNING);
    public static final DeferredItem<WizardArmourItem> WIZARD_ROBE_LIGHTNING = registerItem("wizard_robe_lightning", ArmourMaterialHelper.WIZARD, ArmorType.CHESTPLATE, ElementEnum.LIGHTNING);
    public static final DeferredItem<WizardArmourItem> WIZARD_LEGGINGS_LIGHTNING = registerItem("wizard_leggings_lightning", ArmourMaterialHelper.WIZARD, ArmorType.LEGGINGS, ElementEnum.LIGHTNING);
    public static final DeferredItem<WizardArmourItem> WIZARD_BOOTS_LIGHTNING = registerItem("wizard_boots_lightning", ArmourMaterialHelper.WIZARD, ArmorType.BOOTS, ElementEnum.LIGHTNING);

    // --- 亡灵元素 (NECROMANCY) ---
    public static final DeferredItem<WizardArmourItem> WIZARD_HAT_NECROMANCY = registerItem("wizard_hat_necromancy", ArmourMaterialHelper.WIZARD, ArmorType.HELMET, ElementEnum.NECROMANCY);
    public static final DeferredItem<WizardArmourItem> WIZARD_ROBE_NECROMANCY = registerItem("wizard_robe_necromancy", ArmourMaterialHelper.WIZARD, ArmorType.CHESTPLATE, ElementEnum.NECROMANCY);
    public static final DeferredItem<WizardArmourItem> WIZARD_LEGGINGS_NECROMANCY = registerItem("wizard_leggings_necromancy", ArmourMaterialHelper.WIZARD, ArmorType.LEGGINGS, ElementEnum.NECROMANCY);
    public static final DeferredItem<WizardArmourItem> WIZARD_BOOTS_NECROMANCY = registerItem("wizard_boots_necromancy", ArmourMaterialHelper.WIZARD, ArmorType.BOOTS, ElementEnum.NECROMANCY);

    // --- 大地元素 (EARTH) ---
    public static final DeferredItem<WizardArmourItem> WIZARD_HAT_EARTH = registerItem("wizard_hat_earth", ArmourMaterialHelper.WIZARD, ArmorType.HELMET, ElementEnum.EARTH);
    public static final DeferredItem<WizardArmourItem> WIZARD_ROBE_EARTH = registerItem("wizard_robe_earth", ArmourMaterialHelper.WIZARD, ArmorType.CHESTPLATE, ElementEnum.EARTH);
    public static final DeferredItem<WizardArmourItem> WIZARD_LEGGINGS_EARTH = registerItem("wizard_leggings_earth", ArmourMaterialHelper.WIZARD, ArmorType.LEGGINGS, ElementEnum.EARTH);
    public static final DeferredItem<WizardArmourItem> WIZARD_BOOTS_EARTH = registerItem("wizard_boots_earth", ArmourMaterialHelper.WIZARD, ArmorType.BOOTS, ElementEnum.EARTH);

    // --- 术法元素 (SORCERY) ---
    public static final DeferredItem<WizardArmourItem> WIZARD_HAT_SORCERY = registerItem("wizard_hat_sorcery", ArmourMaterialHelper.WIZARD, ArmorType.HELMET, ElementEnum.SORCERY);
    public static final DeferredItem<WizardArmourItem> WIZARD_ROBE_SORCERY = registerItem("wizard_robe_sorcery", ArmourMaterialHelper.WIZARD, ArmorType.CHESTPLATE, ElementEnum.SORCERY);
    public static final DeferredItem<WizardArmourItem> WIZARD_LEGGINGS_SORCERY = registerItem("wizard_leggings_sorcery", ArmourMaterialHelper.WIZARD, ArmorType.LEGGINGS, ElementEnum.SORCERY);
    public static final DeferredItem<WizardArmourItem> WIZARD_BOOTS_SORCERY = registerItem("wizard_boots_sorcery", ArmourMaterialHelper.WIZARD, ArmorType.BOOTS, ElementEnum.SORCERY);

    // --- 治疗元素 (HEALING) ---
    public static final DeferredItem<WizardArmourItem> WIZARD_HAT_HEALING = registerItem("wizard_hat_healing", ArmourMaterialHelper.WIZARD, ArmorType.HELMET, ElementEnum.HEALING);
    public static final DeferredItem<WizardArmourItem> WIZARD_ROBE_HEALING = registerItem("wizard_robe_healing", ArmourMaterialHelper.WIZARD, ArmorType.CHESTPLATE, ElementEnum.HEALING);
    public static final DeferredItem<WizardArmourItem> WIZARD_LEGGINGS_HEALING = registerItem("wizard_leggings_healing", ArmourMaterialHelper.WIZARD, ArmorType.LEGGINGS, ElementEnum.HEALING);
    public static final DeferredItem<WizardArmourItem> WIZARD_BOOTS_HEALING = registerItem("wizard_boots_healing", ArmourMaterialHelper.WIZARD, ArmorType.BOOTS, ElementEnum.HEALING);

    // 无元素 (None)
    public static final DeferredItem<WizardArmourItem> SAGE_HAT = registerItem("sage_hat", ArmourMaterialHelper.SAGE, ArmorType.HELMET, null);
    public static final DeferredItem<WizardArmourItem> SAGE_ROBE = registerItem("sage_robe", ArmourMaterialHelper.SAGE, ArmorType.CHESTPLATE, null);
    public static final DeferredItem<WizardArmourItem> SAGE_LEGGINGS = registerItem("sage_leggings", ArmourMaterialHelper.SAGE, ArmorType.LEGGINGS, null);
    public static final DeferredItem<WizardArmourItem> SAGE_BOOTS = registerItem("sage_boots", ArmourMaterialHelper.SAGE, ArmorType.BOOTS, null);

    // 火元素 (FIRE)
    public static final DeferredItem<WizardArmourItem> SAGE_HAT_FIRE = registerItem("sage_hat_fire", ArmourMaterialHelper.SAGE, ArmorType.HELMET, ElementEnum.FIRE);
    public static final DeferredItem<WizardArmourItem> SAGE_ROBE_FIRE = registerItem("sage_robe_fire", ArmourMaterialHelper.SAGE, ArmorType.CHESTPLATE, ElementEnum.FIRE);
    public static final DeferredItem<WizardArmourItem> SAGE_LEGGINGS_FIRE = registerItem("sage_leggings_fire", ArmourMaterialHelper.SAGE, ArmorType.LEGGINGS, ElementEnum.FIRE);
    public static final DeferredItem<WizardArmourItem> SAGE_BOOTS_FIRE = registerItem("sage_boots_fire", ArmourMaterialHelper.SAGE, ArmorType.BOOTS, ElementEnum.FIRE);

    // 冰元素 (ICE)
    public static final DeferredItem<WizardArmourItem> SAGE_HAT_ICE = registerItem("sage_hat_ice", ArmourMaterialHelper.SAGE, ArmorType.HELMET, ElementEnum.ICE);
    public static final DeferredItem<WizardArmourItem> SAGE_ROBE_ICE = registerItem("sage_robe_ice", ArmourMaterialHelper.SAGE, ArmorType.CHESTPLATE, ElementEnum.ICE);
    public static final DeferredItem<WizardArmourItem> SAGE_LEGGINGS_ICE = registerItem("sage_leggings_ice", ArmourMaterialHelper.SAGE, ArmorType.LEGGINGS, ElementEnum.ICE);
    public static final DeferredItem<WizardArmourItem> SAGE_BOOTS_ICE = registerItem("sage_boots_ice", ArmourMaterialHelper.SAGE, ArmorType.BOOTS, ElementEnum.ICE);

    // 闪电元素 (LIGHTNING)
    public static final DeferredItem<WizardArmourItem> SAGE_HAT_LIGHTNING = registerItem("sage_hat_lightning", ArmourMaterialHelper.SAGE, ArmorType.HELMET, ElementEnum.LIGHTNING);
    public static final DeferredItem<WizardArmourItem> SAGE_ROBE_LIGHTNING = registerItem("sage_robe_lightning", ArmourMaterialHelper.SAGE, ArmorType.CHESTPLATE, ElementEnum.LIGHTNING);
    public static final DeferredItem<WizardArmourItem> SAGE_LEGGINGS_LIGHTNING = registerItem("sage_leggings_lightning", ArmourMaterialHelper.SAGE, ArmorType.LEGGINGS, ElementEnum.LIGHTNING);
    public static final DeferredItem<WizardArmourItem> SAGE_BOOTS_LIGHTNING = registerItem("sage_boots_lightning", ArmourMaterialHelper.SAGE, ArmorType.BOOTS, ElementEnum.LIGHTNING);

    // 亡灵元素 (NECROMANCY)
    public static final DeferredItem<WizardArmourItem> SAGE_HAT_NECROMANCY = registerItem("sage_hat_necromancy", ArmourMaterialHelper.SAGE, ArmorType.HELMET, ElementEnum.NECROMANCY);
    public static final DeferredItem<WizardArmourItem> SAGE_ROBE_NECROMANCY = registerItem("sage_robe_necromancy", ArmourMaterialHelper.SAGE, ArmorType.CHESTPLATE, ElementEnum.NECROMANCY);
    public static final DeferredItem<WizardArmourItem> SAGE_LEGGINGS_NECROMANCY = registerItem("sage_leggings_necromancy", ArmourMaterialHelper.SAGE, ArmorType.LEGGINGS, ElementEnum.NECROMANCY);
    public static final DeferredItem<WizardArmourItem> SAGE_BOOTS_NECROMANCY = registerItem("sage_boots_necromancy", ArmourMaterialHelper.SAGE, ArmorType.BOOTS, ElementEnum.NECROMANCY);

    // 大地元素 (EARTH)
    public static final DeferredItem<WizardArmourItem> SAGE_HAT_EARTH = registerItem("sage_hat_earth", ArmourMaterialHelper.SAGE, ArmorType.HELMET, ElementEnum.EARTH);
    public static final DeferredItem<WizardArmourItem> SAGE_ROBE_EARTH = registerItem("sage_robe_earth", ArmourMaterialHelper.SAGE, ArmorType.CHESTPLATE, ElementEnum.EARTH);
    public static final DeferredItem<WizardArmourItem> SAGE_LEGGINGS_EARTH = registerItem("sage_leggings_earth", ArmourMaterialHelper.SAGE, ArmorType.LEGGINGS, ElementEnum.EARTH);
    public static final DeferredItem<WizardArmourItem> SAGE_BOOTS_EARTH = registerItem("sage_boots_earth", ArmourMaterialHelper.SAGE, ArmorType.BOOTS, ElementEnum.EARTH);

    // 术法元素 (SORCERY)
    public static final DeferredItem<WizardArmourItem> SAGE_HAT_SORCERY = registerItem("sage_hat_sorcery", ArmourMaterialHelper.SAGE, ArmorType.HELMET, ElementEnum.SORCERY);
    public static final DeferredItem<WizardArmourItem> SAGE_ROBE_SORCERY = registerItem("sage_robe_sorcery", ArmourMaterialHelper.SAGE, ArmorType.CHESTPLATE, ElementEnum.SORCERY);
    public static final DeferredItem<WizardArmourItem> SAGE_LEGGINGS_SORCERY = registerItem("sage_leggings_sorcery", ArmourMaterialHelper.SAGE, ArmorType.LEGGINGS, ElementEnum.SORCERY);
    public static final DeferredItem<WizardArmourItem> SAGE_BOOTS_SORCERY = registerItem("sage_boots_sorcery", ArmourMaterialHelper.SAGE, ArmorType.BOOTS, ElementEnum.SORCERY);

    // 治疗元素 (HEALING)
    public static final DeferredItem<WizardArmourItem> SAGE_HAT_HEALING = registerItem("sage_hat_healing", ArmourMaterialHelper.SAGE, ArmorType.HELMET, ElementEnum.HEALING);
    public static final DeferredItem<WizardArmourItem> SAGE_ROBE_HEALING = registerItem("sage_robe_healing", ArmourMaterialHelper.SAGE, ArmorType.CHESTPLATE, ElementEnum.HEALING);
    public static final DeferredItem<WizardArmourItem> SAGE_LEGGINGS_HEALING = registerItem("sage_leggings_healing", ArmourMaterialHelper.SAGE, ArmorType.LEGGINGS, ElementEnum.HEALING);
    public static final DeferredItem<WizardArmourItem> SAGE_BOOTS_HEALING = registerItem("sage_boots_healing", ArmourMaterialHelper.SAGE, ArmorType.BOOTS, ElementEnum.HEALING);

    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_HELMET = registerItem("battlemage_helmet", ArmourMaterialHelper.BATTLEMAGE, ArmorType.HELMET, null);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_CHESTPLATE = registerItem("battlemage_chestplate", ArmourMaterialHelper.BATTLEMAGE, ArmorType.CHESTPLATE, null);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_LEGGINGS = registerItem("battlemage_leggings", ArmourMaterialHelper.BATTLEMAGE, ArmorType.LEGGINGS, null);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_BOOTS = registerItem("battlemage_boots", ArmourMaterialHelper.BATTLEMAGE, ArmorType.BOOTS, null);

    // 火元素 (FIRE)
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_HELMET_FIRE = registerItem("battlemage_helmet_fire", ArmourMaterialHelper.BATTLEMAGE, ArmorType.HELMET, ElementEnum.FIRE);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_CHESTPLATE_FIRE = registerItem("battlemage_chestplate_fire", ArmourMaterialHelper.BATTLEMAGE, ArmorType.CHESTPLATE, ElementEnum.FIRE);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_LEGGINGS_FIRE = registerItem("battlemage_leggings_fire", ArmourMaterialHelper.BATTLEMAGE, ArmorType.LEGGINGS, ElementEnum.FIRE);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_BOOTS_FIRE = registerItem("battlemage_boots_fire", ArmourMaterialHelper.BATTLEMAGE, ArmorType.BOOTS, ElementEnum.FIRE);

    // 冰元素 (ICE)
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_HELMET_ICE = registerItem("battlemage_helmet_ice", ArmourMaterialHelper.BATTLEMAGE, ArmorType.HELMET, ElementEnum.ICE);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_CHESTPLATE_ICE = registerItem("battlemage_chestplate_ice", ArmourMaterialHelper.BATTLEMAGE, ArmorType.CHESTPLATE, ElementEnum.ICE);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_LEGGINGS_ICE = registerItem("battlemage_leggings_ice", ArmourMaterialHelper.BATTLEMAGE, ArmorType.LEGGINGS, ElementEnum.ICE);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_BOOTS_ICE = registerItem("battlemage_boots_ice", ArmourMaterialHelper.BATTLEMAGE, ArmorType.BOOTS, ElementEnum.ICE);

    // 闪电元素 (LIGHTNING)
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_HELMET_LIGHTNING = registerItem("battlemage_helmet_lightning", ArmourMaterialHelper.BATTLEMAGE, ArmorType.HELMET, ElementEnum.LIGHTNING);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_CHESTPLATE_LIGHTNING = registerItem("battlemage_chestplate_lightning", ArmourMaterialHelper.BATTLEMAGE, ArmorType.CHESTPLATE, ElementEnum.LIGHTNING);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_LEGGINGS_LIGHTNING = registerItem("battlemage_leggings_lightning", ArmourMaterialHelper.BATTLEMAGE, ArmorType.LEGGINGS, ElementEnum.LIGHTNING);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_BOOTS_LIGHTNING = registerItem("battlemage_boots_lightning", ArmourMaterialHelper.BATTLEMAGE, ArmorType.BOOTS, ElementEnum.LIGHTNING);

    // 亡灵元素 (NECROMANCY)
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_HELMET_NECROMANCY = registerItem("battlemage_helmet_necromancy", ArmourMaterialHelper.BATTLEMAGE, ArmorType.HELMET, ElementEnum.NECROMANCY);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_CHESTPLATE_NECROMANCY = registerItem("battlemage_chestplate_necromancy", ArmourMaterialHelper.BATTLEMAGE, ArmorType.CHESTPLATE, ElementEnum.NECROMANCY);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_LEGGINGS_NECROMANCY = registerItem("battlemage_leggings_necromancy", ArmourMaterialHelper.BATTLEMAGE, ArmorType.LEGGINGS, ElementEnum.NECROMANCY);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_BOOTS_NECROMANCY = registerItem("battlemage_boots_necromancy", ArmourMaterialHelper.BATTLEMAGE, ArmorType.BOOTS, ElementEnum.NECROMANCY);

    // 大地元素 (EARTH)
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_HELMET_EARTH = registerItem("battlemage_helmet_earth", ArmourMaterialHelper.BATTLEMAGE, ArmorType.HELMET, ElementEnum.EARTH);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_CHESTPLATE_EARTH = registerItem("battlemage_chestplate_earth", ArmourMaterialHelper.BATTLEMAGE, ArmorType.CHESTPLATE, ElementEnum.EARTH);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_LEGGINGS_EARTH = registerItem("battlemage_leggings_earth", ArmourMaterialHelper.BATTLEMAGE, ArmorType.LEGGINGS, ElementEnum.EARTH);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_BOOTS_EARTH = registerItem("battlemage_boots_earth", ArmourMaterialHelper.BATTLEMAGE, ArmorType.BOOTS, ElementEnum.EARTH);

    // 术法元素 (SORCERY)
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_HELMET_SORCERY = registerItem("battlemage_helmet_sorcery", ArmourMaterialHelper.BATTLEMAGE, ArmorType.HELMET, ElementEnum.SORCERY);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_CHESTPLATE_SORCERY = registerItem("battlemage_chestplate_sorcery", ArmourMaterialHelper.BATTLEMAGE, ArmorType.CHESTPLATE, ElementEnum.SORCERY);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_LEGGINGS_SORCERY = registerItem("battlemage_leggings_sorcery", ArmourMaterialHelper.BATTLEMAGE, ArmorType.LEGGINGS, ElementEnum.SORCERY);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_BOOTS_SORCERY = registerItem("battlemage_boots_sorcery", ArmourMaterialHelper.BATTLEMAGE, ArmorType.BOOTS, ElementEnum.SORCERY);

    // 治疗元素 (HEALING)
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_HELMET_HEALING = registerItem("battlemage_helmet_healing", ArmourMaterialHelper.BATTLEMAGE, ArmorType.HELMET, ElementEnum.HEALING);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_CHESTPLATE_HEALING = registerItem("battlemage_chestplate_healing", ArmourMaterialHelper.BATTLEMAGE, ArmorType.CHESTPLATE, ElementEnum.HEALING);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_LEGGINGS_HEALING = registerItem("battlemage_leggings_healing", ArmourMaterialHelper.BATTLEMAGE, ArmorType.LEGGINGS, ElementEnum.HEALING);
    public static final DeferredItem<WizardArmourItem> BATTLEMAGE_BOOTS_HEALING = registerItem("battlemage_boots_healing", ArmourMaterialHelper.BATTLEMAGE, ArmorType.BOOTS, ElementEnum.HEALING);

    public static final DeferredItem<WizardArmourItem> WARLOCK_HOOD = registerItem("warlock_hood", ArmourMaterialHelper.WARLOCK, ArmorType.HELMET, null);
    public static final DeferredItem<WizardArmourItem> WARLOCK_ROBE = registerItem("warlock_robe", ArmourMaterialHelper.WARLOCK, ArmorType.CHESTPLATE, null);
    public static final DeferredItem<WizardArmourItem> WARLOCK_LEGGINGS = registerItem("warlock_leggings", ArmourMaterialHelper.WARLOCK, ArmorType.LEGGINGS, null);
    public static final DeferredItem<WizardArmourItem> WARLOCK_BOOTS = registerItem("warlock_boots", ArmourMaterialHelper.WARLOCK, ArmorType.BOOTS, null);

    // 火元素 (FIRE)
    public static final DeferredItem<WizardArmourItem> WARLOCK_HOOD_FIRE = registerItem("warlock_hood_fire", ArmourMaterialHelper.WARLOCK, ArmorType.HELMET, ElementEnum.FIRE);
    public static final DeferredItem<WizardArmourItem> WARLOCK_ROBE_FIRE = registerItem("warlock_robe_fire", ArmourMaterialHelper.WARLOCK, ArmorType.CHESTPLATE, ElementEnum.FIRE);
    public static final DeferredItem<WizardArmourItem> WARLOCK_LEGGINGS_FIRE = registerItem("warlock_leggings_fire", ArmourMaterialHelper.WARLOCK, ArmorType.LEGGINGS, ElementEnum.FIRE);
    public static final DeferredItem<WizardArmourItem> WARLOCK_BOOTS_FIRE = registerItem("warlock_boots_fire", ArmourMaterialHelper.WARLOCK, ArmorType.BOOTS, ElementEnum.FIRE);

    // 冰元素 (ICE)
    public static final DeferredItem<WizardArmourItem> WARLOCK_HOOD_ICE = registerItem("warlock_hood_ice", ArmourMaterialHelper.WARLOCK, ArmorType.HELMET, ElementEnum.ICE);
    public static final DeferredItem<WizardArmourItem> WARLOCK_ROBE_ICE = registerItem("warlock_robe_ice", ArmourMaterialHelper.WARLOCK, ArmorType.CHESTPLATE, ElementEnum.ICE);
    public static final DeferredItem<WizardArmourItem> WARLOCK_LEGGINGS_ICE = registerItem("warlock_leggings_ice", ArmourMaterialHelper.WARLOCK, ArmorType.LEGGINGS, ElementEnum.ICE);
    public static final DeferredItem<WizardArmourItem> WARLOCK_BOOTS_ICE = registerItem("warlock_boots_ice", ArmourMaterialHelper.WARLOCK, ArmorType.BOOTS, ElementEnum.ICE);

    // 闪电元素 (LIGHTNING)
    public static final DeferredItem<WizardArmourItem> WARLOCK_HOOD_LIGHTNING = registerItem("warlock_hood_lightning", ArmourMaterialHelper.WARLOCK, ArmorType.HELMET, ElementEnum.LIGHTNING);
    public static final DeferredItem<WizardArmourItem> WARLOCK_ROBE_LIGHTNING = registerItem("warlock_robe_lightning", ArmourMaterialHelper.WARLOCK, ArmorType.CHESTPLATE, ElementEnum.LIGHTNING);
    public static final DeferredItem<WizardArmourItem> WARLOCK_LEGGINGS_LIGHTNING = registerItem("warlock_leggings_lightning", ArmourMaterialHelper.WARLOCK, ArmorType.LEGGINGS, ElementEnum.LIGHTNING);
    public static final DeferredItem<WizardArmourItem> WARLOCK_BOOTS_LIGHTNING = registerItem("warlock_boots_lightning", ArmourMaterialHelper.WARLOCK, ArmorType.BOOTS, ElementEnum.LIGHTNING);

    // 亡灵元素 (NECROMANCY)
    public static final DeferredItem<WizardArmourItem> WARLOCK_HOOD_NECROMANCY = registerItem("warlock_hood_necromancy", ArmourMaterialHelper.WARLOCK, ArmorType.HELMET, ElementEnum.NECROMANCY);
    public static final DeferredItem<WizardArmourItem> WARLOCK_ROBE_NECROMANCY = registerItem("warlock_robe_necromancy", ArmourMaterialHelper.WARLOCK, ArmorType.CHESTPLATE, ElementEnum.NECROMANCY);
    public static final DeferredItem<WizardArmourItem> WARLOCK_LEGGINGS_NECROMANCY = registerItem("warlock_leggings_necromancy", ArmourMaterialHelper.WARLOCK, ArmorType.LEGGINGS, ElementEnum.NECROMANCY);
    public static final DeferredItem<WizardArmourItem> WARLOCK_BOOTS_NECROMANCY = registerItem("warlock_boots_necromancy", ArmourMaterialHelper.WARLOCK, ArmorType.BOOTS, ElementEnum.NECROMANCY);

    // 大地元素 (EARTH)
    public static final DeferredItem<WizardArmourItem> WARLOCK_HOOD_EARTH = registerItem("warlock_hood_earth", ArmourMaterialHelper.WARLOCK, ArmorType.HELMET, ElementEnum.EARTH);
    public static final DeferredItem<WizardArmourItem> WARLOCK_ROBE_EARTH = registerItem("warlock_robe_earth", ArmourMaterialHelper.WARLOCK, ArmorType.CHESTPLATE, ElementEnum.EARTH);
    public static final DeferredItem<WizardArmourItem> WARLOCK_LEGGINGS_EARTH = registerItem("warlock_leggings_earth", ArmourMaterialHelper.WARLOCK, ArmorType.LEGGINGS, ElementEnum.EARTH);
    public static final DeferredItem<WizardArmourItem> WARLOCK_BOOTS_EARTH = registerItem("warlock_boots_earth", ArmourMaterialHelper.WARLOCK, ArmorType.BOOTS, ElementEnum.EARTH);

    // 术法元素 (SORCERY)
    public static final DeferredItem<WizardArmourItem> WARLOCK_HOOD_SORCERY = registerItem("warlock_hood_sorcery", ArmourMaterialHelper.WARLOCK, ArmorType.HELMET, ElementEnum.SORCERY);
    public static final DeferredItem<WizardArmourItem> WARLOCK_ROBE_SORCERY = registerItem("warlock_robe_sorcery", ArmourMaterialHelper.WARLOCK, ArmorType.CHESTPLATE, ElementEnum.SORCERY);
    public static final DeferredItem<WizardArmourItem> WARLOCK_LEGGINGS_SORCERY = registerItem("warlock_leggings_sorcery", ArmourMaterialHelper.WARLOCK, ArmorType.LEGGINGS, ElementEnum.SORCERY);
    public static final DeferredItem<WizardArmourItem> WARLOCK_BOOTS_SORCERY = registerItem("warlock_boots_sorcery", ArmourMaterialHelper.WARLOCK, ArmorType.BOOTS, ElementEnum.SORCERY);

    // 治疗元素 (HEALING)
    public static final DeferredItem<WizardArmourItem> WARLOCK_HOOD_HEALING = registerItem("warlock_hood_healing", ArmourMaterialHelper.WARLOCK, ArmorType.HELMET, ElementEnum.HEALING);
    public static final DeferredItem<WizardArmourItem> WARLOCK_ROBE_HEALING = registerItem("warlock_robe_healing", ArmourMaterialHelper.WARLOCK, ArmorType.CHESTPLATE, ElementEnum.HEALING);
    public static final DeferredItem<WizardArmourItem> WARLOCK_LEGGINGS_HEALING = registerItem("warlock_leggings_healing", ArmourMaterialHelper.WARLOCK, ArmorType.LEGGINGS, ElementEnum.HEALING);
    public static final DeferredItem<WizardArmourItem> WARLOCK_BOOTS_HEALING = registerItem("warlock_boots_healing", ArmourMaterialHelper.WARLOCK, ArmorType.BOOTS, ElementEnum.HEALING);

    //巫师手札
    public static final DeferredItem<WizardHandbookItem> WIZARD_HANDBOOK = registerItem("wizard_handbook", properties -> new WizardHandbookItem(properties.stacksTo(1)), WizardryCreativeTabs.TabsEnum.WIZARDRY);
    public static final DeferredItem<ArcaneTomeItem> ARCANE_TOME = registerItemWithSubItems(
            "arcane_tome",
            properties -> new ArcaneTomeItem(properties.stacksTo(1)),
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (TierEnum tier : TierEnum.values()) {
                    if (tier == TierEnum.NOVICE) {
                        continue;
                    }
                    ItemStack arcaneTomeStack = new ItemStack(item);
                    arcaneTomeStack.set(WizardryComponents.TIER.get(), tier.getSerializedName());
                    buildCreativeModeTabContentsEvent.accept(arcaneTomeStack);
                }
            }
    );

    //魔力水晶
    public static final DeferredItem<MagicCrystalItem> MAGIC_CRYSTAL = registerItemWithSubItems(
            "magic_crystal",
            properties -> new MagicCrystalItem(properties.stacksTo(1)),
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (ElementEnum element : ElementEnum.values()) {
                    ItemStack crystalStack = new ItemStack(item);
                    crystalStack.set(WizardryComponents.CRYSTAL_TYPE.get(), element.getSerializedName());
                    buildCreativeModeTabContentsEvent.accept(crystalStack);
                }
            }
    );
    public static final DeferredItem<Item> CRYSTAL_SHARD = registerItem("crystal_shard", Item::new, WizardryCreativeTabs.TabsEnum.WIZARDRY);
    public static final DeferredItem<Item> GRAND_CRYSTAL = registerItem("grand_crystal", Item::new, WizardryCreativeTabs.TabsEnum.WIZARDRY);

    //魔法绸缎
    public static final DeferredItem<Item> MAGIC_SILK = registerItem("magic_silk", Item::new, WizardryCreativeTabs.TabsEnum.WIZARDRY);

    //方块物品
    public static final DeferredItem<BlockItem> ARCANE_WORKBENCH = registerItem(
            "arcane_workbench",
            properties -> new BlockItem(WizardryBlocks.ARCANE_WORKBENCH.get(), properties.useBlockDescriptionPrefix()),
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<BlockItem> CRYSTAL_ORE = registerItem(
            "crystal_ore",
            properties -> new BlockItem(WizardryBlocks.CRYSTAL_ORE.get(), properties.useBlockDescriptionPrefix()),
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<BlockItem> CRYSTAL_BLOCK = registerItemWithSubItems(
            "crystal_block",
            properties -> new BlockItem(WizardryBlocks.CRYSTAL_BLOCK.get(), properties.useBlockDescriptionPrefix()),
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (ElementEnum element : ElementEnum.values()) {
                    ItemStack stack = new ItemStack(item);
                    Component name = Component.translatable("block." + Wizardry.MODID + "." + element.getSerializedName() + "_crystal_block")
                            .withStyle(style -> style.withItalic(false));
                    stack.set(DataComponents.ITEM_NAME, name);
                    stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY
                            .with(CrystalBlock.ELEMENT, element));
                    buildCreativeModeTabContentsEvent.accept(stack);
                }
            }
    );
    public static final DeferredItem<BlockItem> RUNESTONE = registerItemWithSubItems(
            "runestone",
            properties -> new BlockItem(WizardryBlocks.RUNESTONE.get(), properties.useBlockDescriptionPrefix()),
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (ElementEnum element : ElementEnum.values()) {
                    if (element == ElementEnum.MAGIC) {
                        continue;
                    }
                    ItemStack stack = new ItemStack(item);
                    stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY
                            .with(CrystalBlock.ELEMENT, element));
                    buildCreativeModeTabContentsEvent.accept(stack);
                }
            }
    );
    public static final DeferredItem<BlockItem> RUNESTONE_PEDESTAL = registerItemWithSubItems(
            "runestone_pedestal",
            properties -> new BlockItem(WizardryBlocks.RUNESTONE_PEDESTAL.get(), properties.useBlockDescriptionPrefix()),
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (ElementEnum element : ElementEnum.values()) {
                    if (element == ElementEnum.MAGIC) {
                        continue;
                    }
                    ItemStack stack = new ItemStack(item);
                    stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY
                            .with(CrystalBlock.ELEMENT, element));
                    buildCreativeModeTabContentsEvent.accept(stack);
                }
            }
    );
    public static final DeferredItem<BlockItem> GILDED_WOOD = registerItemWithSubItems(
            "gilded_wood",
            props -> new BlockItem(WizardryBlocks.GILDED_WOOD.get(), props.useBlockDescriptionPrefix()),
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (WoodTypeEnum wood : WoodTypeEnum.values()) {
                    ItemStack stack = new ItemStack(item);
                    stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(GildedWoodBlock.GILDED_WOOD_TYPE, wood));
                    Component name = Component.translatable("block." + Wizardry.MODID + "." + wood.getSerializedName() + "_gilded_wood")
                            .withStyle(style -> style.withItalic(false));
                    stack.set(DataComponents.ITEM_NAME, name);
                    buildCreativeModeTabContentsEvent.accept(stack);
                }
            }
    );
    public static final DeferredItem<BlockItem> BOOKSHELF = registerItemWithSubItems(
            "bookshelf",
            props -> new BlockItem(WizardryBlocks.BOOKSHELF.get(), props.useBlockDescriptionPrefix()),
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (WoodTypeEnum wood : WoodTypeEnum.values()) {
                    ItemStack stack = new ItemStack(item);
                    stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(BookshelfBlock.BOOKSHELF_WOOD_TYPE, wood));
                    Component name = Component.translatable("block." + Wizardry.MODID + "." + wood.getSerializedName() + "_bookshelf")
                            .withStyle(style -> style.withItalic(false));
                    stack.set(DataComponents.ITEM_NAME, name);
                    buildCreativeModeTabContentsEvent.accept(stack);
                }
            }
    );
    public static final DeferredItem<BlockItem> LECTERN = registerItemWithSubItems(
            "lectern",
            props -> new BlockItem(WizardryBlocks.LECTERN.get(), props.useBlockDescriptionPrefix()),
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (WoodTypeEnum wood : WoodTypeEnum.values()) {
                    ItemStack stack = new ItemStack(item);
                    stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(LecternBlock.LECTERN_WOOD_TYPE, wood));
                    Component name = Component.translatable("block." + Wizardry.MODID + "." + wood.getSerializedName() + "_lectern")
                            .withStyle(style -> style.withItalic(false));
                    stack.set(DataComponents.ITEM_NAME, name);
                    buildCreativeModeTabContentsEvent.accept(stack);
                }
            }
    );
    public static final DeferredItem<WandUpgradeItem> STORAGE_UPGRADE = registerItem(
            "storage_upgrade",
            WandUpgradeItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<WandUpgradeItem> SIPHON_UPGRADE = registerItem(
            "siphon_upgrade",
            WandUpgradeItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<WandUpgradeItem> CONDENSER_UPGRADE = registerItem(
            "condenser_upgrade",
            WandUpgradeItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<WandUpgradeItem> RANGE_UPGRADE = registerItem(
            "range_upgrade",
            WandUpgradeItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<WandUpgradeItem> DURATION_UPGRADE = registerItem(
            "duration_upgrade",
            WandUpgradeItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<WandUpgradeItem> COOLDOWN_UPGRADE = registerItem(
            "cooldown_upgrade",
            WandUpgradeItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<WandUpgradeItem> BLAST_UPGRADE = registerItem(
            "blast_upgrade",
            WandUpgradeItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<WandUpgradeItem> ATTUNEMENT_UPGRADE = registerItem(
            "attunement_upgrade",
            WandUpgradeItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<WandUpgradeItem> MELEE_UPGRADE = registerItem(
            "melee_upgrade",
            WandUpgradeItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );

    public static final DeferredItem<FireBombItem> FIRE_BOMB = registerItem(
            "fire_bomb",
            FireBombItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<PoisonBombItem> POISON_BOMB = registerItem(
            "poison_bomb",
            PoisonBombItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<SmokeBombItem> SMOKE_BOMB = registerItem(
            "smoke_bomb",
            SmokeBombItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<SparkBombItem> SPARK_BOMB = registerItem(
            "spark_bomb",
            SparkBombItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<SpectralDustItem> spectral_dust = registerItemWithSubItems(
            "spectral_dust",
            SpectralDustItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (ElementEnum element : ElementEnum.values()) {
                    if (element == ElementEnum.MAGIC) {
                        continue;
                    }
                    ItemStack stack = new ItemStack(item);
                    stack.set(WizardryComponents.ELEMENT.get(), element.getSerializedName());
                    buildCreativeModeTabContentsEvent.accept(stack);
                }
            }
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}