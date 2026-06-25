package top.begonia.wizardry.core.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.block.*;
import top.begonia.wizardry.core.constants.ArtefactTypeEnum;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.ManaFlaskTypeEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.item.impl.*;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.ArmourHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class WizardryItems {
    private WizardryItems() {
    }

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Wizardry.MODID);

    public static <T extends Item> @NonNull DeferredItem<T> registerItem(String name, Function<Item.Properties, T> itemFactory, WizardryCreativeTabs.TabsEnum tab) {
        DeferredItem<T> item = ITEMS.register(name, (identifier) -> {
            Item.Properties properties = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, identifier));
            return itemFactory.apply(properties);
        });
        WizardryCreativeTabs.addToTabs(tab, item);
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

    //法杖
    public static final DeferredHolder<Item, WandItem> WAND = registerItemWithSubItems(
            "wand",
            properties -> new WandItem(properties.stacksTo(1)),
            WizardryCreativeTabs.TabsEnum.GEAR,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (ElementEnum element : ElementEnum.values()) {
                    for (TierEnum tier : TierEnum.values()) {
                        buildCreativeModeTabContentsEvent.accept(WandItem.getWand(tier, element));
                    }
                }
            }
    );

    //法术书
    public static final DeferredHolder<Item, SpellBookItem> SPELL_BOOK = registerItemWithSubItems(
            "spell_book",
            properties -> new SpellBookItem(properties.stacksTo(16)),
            WizardryCreativeTabs.TabsEnum.SPELLS,
            (buildCreativeModeTabContentsEvent, item) -> buildCreativeModeTabContentsEvent.getParameters().holders().lookup(WizardrySpells.SPELLS_KEY).ifPresent(registry -> {
                List<Holder.Reference<AbstractSpell>> sortedSpells = new ArrayList<>(registry.listElements().toList());
                sortedSpells.sort(Comparator.comparing(Holder.Reference::value));
                for (Holder.Reference<AbstractSpell> spellHolder : sortedSpells) {
                    if (spellHolder.is(WizardrySpells.NONE)) {
                        continue;
                    }
                    AbstractSpell spell = spellHolder.value();
                    if (spell.applicableForItem(item)) {
                        ItemStack spellBook = new ItemStack(item);
                        spellBook.set(WizardryComponents.SPELL.get(), spellHolder);
                        buildCreativeModeTabContentsEvent.accept(spellBook);
                    }
                }
            })
    );

    //法术卷轴
    public static final DeferredHolder<Item, ScrollItem> SCROLL = registerItemWithSubItems(
            "scroll",
            properties -> new ScrollItem(properties.stacksTo(16)),
            WizardryCreativeTabs.TabsEnum.SPELLS,
            (buildCreativeModeTabContentsEvent, item) -> buildCreativeModeTabContentsEvent.getParameters().holders().lookup(WizardrySpells.SPELLS_KEY).ifPresent(registry -> {
                List<Holder.Reference<AbstractSpell>> sortedSpells = new ArrayList<>(registry.listElements().toList());
                sortedSpells.sort(Comparator.comparing(Holder.Reference::value));
                for (Holder.Reference<AbstractSpell> spellHolder : sortedSpells) {
                    if (spellHolder.is(WizardrySpells.NONE)) {
                        continue;
                    }
                    AbstractSpell spell = spellHolder.value();
                    if (spell.applicableForItem(item)) {
                        ItemStack scroll = new ItemStack(item);
                        scroll.set(WizardryComponents.SPELL.get(), spellHolder);
                        buildCreativeModeTabContentsEvent.accept(scroll);
                    }
                }
            })
    );

    public static final DeferredItem<WizardArmourItem> ARMOUR = registerItemWithSubItems(
            "armour",
            WizardArmourItem::new,
            WizardryCreativeTabs.TabsEnum.GEAR,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (ArmourHelper.ArmourMaterialType armourMaterialType : ArmourHelper.ArmourMaterialType.values()) {
                    for (ElementEnum element : ElementEnum.values()) {
                        for (ArmorType armorType : ArmorType.values()) {
                            if (armorType == ArmorType.BODY) {
                                continue;
                            }
                            buildCreativeModeTabContentsEvent.accept(ArmourHelper.generateArmour(item, element, armourMaterialType, armorType));
                        }
                    }
                }
            }
    );

    public static final DeferredItem<ArtefactItem> RING_CONDENSING = registerItem(
            "ring_condensing",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_SIPHONING = registerItem(
            "ring_siphoning",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_BATTLEMAGE = registerItem(
            "ring_battlemage",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_COMBUSTION = registerItem(
            "ring_combustion",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_FIRE_MELEE = registerItem(
            "ring_fire_melee",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_FIRE_BIOME = registerItem(
            "ring_fire_biome",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_DISINTEGRATION = registerItem(
            "ring_disintegration",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_METEOR = registerItem(
            "ring_meteor",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_ICE_MELEE = registerItem(
            "ring_ice_melee",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_ICE_BIOME = registerItem(
            "ring_ice_biome",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_ARCANE_FROST = registerItem(
            "ring_arcane_frost",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_SHATTERING = registerItem(
            "ring_shattering",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_LIGHTNING_MELEE = registerItem(
            "ring_lightning_melee",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_STORM = registerItem(
            "ring_storm",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_SEEKING = registerItem(
            "ring_seeking",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_HAMMER = registerItem(
            "ring_hammer",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_STORMCLOUD = registerItem(
            "ring_stormcloud",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_SOULBINDING = registerItem(
            "ring_soulbinding",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_LEECHING = registerItem(
            "ring_leeching",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_NECROMANCY_MELEE = registerItem(
            "ring_necromancy_melee",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_MIND_CONTROL = registerItem(
            "ring_mind_control",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_POISON = registerItem(
            "ring_poison",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_EARTH_MELEE = registerItem(
            "ring_earth_melee",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_EARTH_BIOME = registerItem(
            "ring_earth_biome",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_FULL_MOON = registerItem(
            "ring_full_moon",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_EVOKER = registerItem(
            "ring_evoker",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_EXTRACTION = registerItem(
            "ring_extraction",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_MANA_RETURN = registerItem(
            "ring_mana_return",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_BLOCKWRANGLER = registerItem(
            "ring_blockwrangler",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_CONJURER = registerItem(
            "ring_conjurer",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_DEFENDER = registerItem(
            "ring_defender",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_PALADIN = registerItem(
            "ring_paladin",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> RING_INTERDICTION = registerItem(
            "ring_interdiction",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.RING)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );

    public static final DeferredItem<ArtefactItem> AMULET_ARCANE_DEFENCE = registerItem(
            "amulet_arcane_defence",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_WARDING = registerItem(
            "amulet_warding",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_WISDOM = registerItem(
            "amulet_wisdom",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_FIRE_PROTECTION = registerItem(
            "amulet_fire_protection",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_FIRE_CLOAKING = registerItem(
            "amulet_fire_cloaking",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_ICE_IMMUNITY = registerItem(
            "amulet_ice_immunity",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_ICE_PROTECTION = registerItem(
            "amulet_ice_protection",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_FROST_WARDING = registerItem(
            "amulet_frost_warding",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_POTENTIAL = registerItem(
            "amulet_potential",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_CHANNELING = registerItem(
            "amulet_channeling",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_LICH = registerItem(
            "amulet_lich",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_WITHER_IMMUNITY = registerItem(
            "amulet_wither_immunity",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_GLIDE = registerItem(
            "amulet_glide",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_BANISHING = registerItem(
            "amulet_banishing",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_ANCHORING = registerItem(
            "amulet_anchoring",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_RECOVERY = registerItem(
            "amulet_recovery",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_TRANSIENCE = registerItem(
            "amulet_transience",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_RESURRECTION = registerItem(
            "amulet_resurrection",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_AUTO_SHIELD = registerItem(
            "amulet_auto_shield",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> AMULET_ABSORPTION = registerItem(
            "amulet_absorption",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.AMULET)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );

    public static final DeferredItem<ArtefactItem> CHARM_HAGGLER = registerItem(
            "charm_haggler",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_EXPERIENCE_TOME = registerItem(
            "charm_experience_tome",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_MOVE_SPEED = registerItem(
            "charm_move_speed",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_SPELL_DISCOVERY = registerItem(
            "charm_spell_discovery",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_AUTO_SMELT = registerItem(
            "charm_auto_smelt",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_LAVA_WALKING = registerItem(
            "charm_lava_walking",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_STORM = registerItem(
            "charm_storm",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_MINION_HEALTH = registerItem(
            "charm_minion_health",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_MINION_VARIANTS = registerItem(
            "charm_minion_variants",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_UNDEAD_HELMETS = registerItem(
            "charm_undead_helmets",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_HUNGER_CASTING = registerItem(
            "charm_hunger_casting",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_FLIGHT = registerItem(
            "charm_flight",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_GROWTH = registerItem(
            "charm_growth",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_ABSEILING = registerItem(
            "charm_abseiling",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_SILK_TOUCH = registerItem(
            "charm_silk_touch",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_SIXTH_SENSE = registerItem(
            "charm_sixth_sense",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_STOP_TIME = registerItem(
            "charm_stop_time",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_LIGHT = registerItem(
            "charm_light",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_TRANSPORTATION = registerItem(
            "charm_transportation",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_BLACK_HOLE = registerItem(
            "charm_black_hole",
            properties -> new ArtefactItem(properties.rarity(Rarity.EPIC).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_MOUNT_TELEPORTING = registerItem(
            "charm_mount_teleporting",
            properties -> new ArtefactItem(properties.rarity(Rarity.RARE).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );
    public static final DeferredItem<ArtefactItem> CHARM_FEEDING = registerItem(
            "charm_feeding",
            properties -> new ArtefactItem(properties.rarity(Rarity.UNCOMMON).component(WizardryComponents.ARTEFACT_TYPE.get(), ArtefactTypeEnum.CHARM)),
            WizardryCreativeTabs.TabsEnum.GEAR
    );

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
    public static final DeferredItem<BlockItem> CRYSTAL_FLOWER = registerItem(
            "crystal_flower",
            properties -> new BlockItem(
                    WizardryBlocks.CRYSTAL_FLOWER.get(),
                    properties.useBlockDescriptionPrefix()
            ),
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<BlockItem> TRANSPORTATION_STONE = registerItem(
            "transportation_stone",
            properties -> new BlockItem(WizardryBlocks.TRANSPORTATION_STONE.get(), properties.useBlockDescriptionPrefix()),
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<BlockItem> CRYSTAL_BLOCK = registerItemWithSubItems(
            "crystal_block",
            properties -> new BlockItem(WizardryBlocks.CRYSTAL_BLOCK.get(), properties.useBlockDescriptionPrefix()) {
                @Override
                public @NonNull Component getName(@NonNull ItemStack itemStack) {
                    ElementEnum element = ElementEnum.MAGIC;
                    BlockItemStateProperties stateProperties = itemStack.get(DataComponents.BLOCK_STATE);
                    if (stateProperties != null) {
                        ElementEnum foundElement = stateProperties.get(CrystalBlock.ELEMENT);
                        if (foundElement != null) {
                            element = foundElement;
                        }
                    }
                    return Component.translatable("block." + Wizardry.MODID + "." + element.getSerializedName() + "_crystal_block");
                }
            },
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (ElementEnum element : ElementEnum.values()) {
                    ItemStack stack = new ItemStack(item);
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
                    stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(CrystalBlock.ELEMENT, element));
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
            props -> new BlockItem(WizardryBlocks.GILDED_WOOD.get(), props.useBlockDescriptionPrefix()) {
                @Override
                public @NonNull Component getName(@NonNull ItemStack itemStack) {
                    BlockItemStateProperties stateProperties = itemStack.get(DataComponents.BLOCK_STATE);
                    String woodName = "";
                    if (stateProperties != null) {
                        WoodTypeEnum woodType = stateProperties.get(GildedWoodBlock.GILDED_WOOD_TYPE);
                        if (woodType != null) {
                            woodName = woodType.getSerializedName();
                        }
                    }
                    return Component.translatable("block." + Wizardry.MODID + "." + woodName + "_gilded_wood");
                }
            },
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (WoodTypeEnum wood : WoodTypeEnum.values()) {
                    ItemStack stack = new ItemStack(item);
                    stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(GildedWoodBlock.GILDED_WOOD_TYPE, wood));
                    buildCreativeModeTabContentsEvent.accept(stack);
                }
            }
    );
    public static final DeferredItem<BlockItem> BOOKSHELF = registerItemWithSubItems(
            "bookshelf",
            props -> new BlockItem(WizardryBlocks.BOOKSHELF.get(), props.useBlockDescriptionPrefix()) {
                @Override
                public @NonNull Component getName(@NonNull ItemStack itemStack) {
                    BlockItemStateProperties stateProperties = itemStack.get(DataComponents.BLOCK_STATE);
                    String woodName = "";
                    if (stateProperties != null) {
                        WoodTypeEnum woodType = stateProperties.get(BookshelfBlock.BOOKSHELF_WOOD_TYPE);
                        if (woodType != null) {
                            woodName = woodType.getSerializedName();
                        }
                    }
                    return Component.translatable("block." + Wizardry.MODID + "." + woodName + "_bookshelf");
                }
            },
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (WoodTypeEnum wood : WoodTypeEnum.values()) {
                    ItemStack stack = new ItemStack(item);
                    stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(BookshelfBlock.BOOKSHELF_WOOD_TYPE, wood));
                    buildCreativeModeTabContentsEvent.accept(stack);
                }
            }
    );
    public static final DeferredItem<BlockItem> LECTERN = registerItemWithSubItems(
            "lectern",
            props -> new BlockItem(WizardryBlocks.LECTERN.get(), props.useBlockDescriptionPrefix()) {
                @Override
                public @NonNull Component getName(@NonNull ItemStack itemStack) {
                    BlockItemStateProperties stateProperties = itemStack.get(DataComponents.BLOCK_STATE);
                    String woodName = "";
                    if (stateProperties != null) {
                        WoodTypeEnum woodType = stateProperties.get(LecternBlock.LECTERN_WOOD_TYPE);
                        if (woodType != null) {
                            woodName = woodType.getSerializedName();
                        }
                    }
                    return Component.translatable("block." + Wizardry.MODID + "." + woodName + "_lectern");
                }
            },
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (WoodTypeEnum wood : WoodTypeEnum.values()) {
                    ItemStack stack = new ItemStack(item);
                    stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(LecternBlock.LECTERN_WOOD_TYPE, wood));
                    buildCreativeModeTabContentsEvent.accept(stack);
                }
            }
    );
    public static final DeferredItem<BlockItem> IMBUEMENT_ALTAR = registerItem(
            "imbuement_altar",
            props -> new BlockItem(WizardryBlocks.IMBUEMENT_ALTAR.get(), props.useBlockDescriptionPrefix()),
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<BlockItem> RECEPTACLE = registerItem(
            "receptacle",
            props -> new BlockItem(WizardryBlocks.RECEPTACLE.get(), props.useBlockDescriptionPrefix()),
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<MagicCrystalItem> MAGIC_CRYSTAL = registerItemWithSubItems(
            "magic_crystal",
            MagicCrystalItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (ElementEnum element : ElementEnum.values()) {
                    ItemStack crystalStack = new ItemStack(item);
                    crystalStack.set(WizardryComponents.ELEMENT.get(), element);
                    buildCreativeModeTabContentsEvent.accept(crystalStack);
                }
            }
    );
    public static final DeferredItem<Item> CRYSTAL_SHARD = registerItem(
            "crystal_shard",
            Item::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<Item> GRAND_CRYSTAL = registerItem(
            "grand_crystal",
            Item::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<WizardHandbookItem> WIZARD_HANDBOOK = registerItem(
            "wizard_handbook",
            properties -> new WizardHandbookItem(properties.stacksTo(1)),
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
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
                    arcaneTomeStack.set(WizardryComponents.TIER.get(), tier);
                    buildCreativeModeTabContentsEvent.accept(arcaneTomeStack);
                }
            }
    );
    public static final DeferredItem<Item> RUINED_SPELL_BOOK = registerItem(
            "ruined_spell_book",
            SpellBookItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<Item> BLANK_SCROLL = registerItem(
            "blank_scroll",
            BlankScrollItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<Item> MAGIC_SILK = registerItem(
            "magic_silk",
            Item::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<ManaFlaskItem> MANA_FLASK = registerItemWithSubItems(
            "mana_flask",
            properties -> new ManaFlaskItem(properties.stacksTo(16)),
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (ManaFlaskTypeEnum type : ManaFlaskTypeEnum.values()) {
                    ItemStack crystalStack = new ItemStack(item);
                    crystalStack.set(WizardryComponents.MANA_FLASK_TYPE.get(), type);
                    buildCreativeModeTabContentsEvent.accept(crystalStack);
                }
            }
    );
    public static final DeferredItem<Item> IDENTIFICATION_SCROLL = registerItem(
            "identification_scroll",
            properties -> new IdentificationScrollItem(properties.rarity(Rarity.UNCOMMON)),
            WizardryCreativeTabs.TabsEnum.WIZARDRY
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
    public static final DeferredHolder<Item, ArmourUpgradeItem> RESPLENDENT_THREAD = registerItem(
            "resplendent_thread",
            properties -> new ArmourUpgradeItem(properties.stacksTo(1).rarity(Rarity.EPIC)),
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredHolder<Item, ArmourUpgradeItem> CRYSTAL_SILVER_PLATING = registerItem(
            "crystal_silver_plating",
            properties -> new ArmourUpgradeItem(properties.stacksTo(1).rarity(Rarity.EPIC)),
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredHolder<Item, ArmourUpgradeItem> ETHEREAL_CRYSTALWEAVE = registerItem(
            "ethereal_crystalweave",
            properties -> new ArmourUpgradeItem(properties.stacksTo(1).rarity(Rarity.EPIC)),
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<Item> ASTRAL_DIAMOND = registerItem(
            "astral_diamond",
            properties -> new Item(properties.rarity(Rarity.RARE)),
            WizardryCreativeTabs.TabsEnum.WIZARDRY
    );
    public static final DeferredItem<Item> PURIFYING_ELIXIR = registerItem(
            "purifying_elixir",
            properties -> new PurifyingElixirItem(properties.rarity(Rarity.RARE)),
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
    public static final DeferredItem<SpectralDustItem> SPECTRAL_DUST = registerItemWithSubItems(
            "spectral_dust",
            SpectralDustItem::new,
            WizardryCreativeTabs.TabsEnum.WIZARDRY,
            (buildCreativeModeTabContentsEvent, item) -> {
                for (ElementEnum element : ElementEnum.values()) {
                    if (element == ElementEnum.MAGIC) {
                        continue;
                    }
                    ItemStack stack = new ItemStack(item);
                    stack.set(WizardryComponents.ELEMENT.get(), element);
                    buildCreativeModeTabContentsEvent.accept(stack);
                }
            }
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}