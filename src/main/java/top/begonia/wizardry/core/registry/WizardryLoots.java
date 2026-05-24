package top.begonia.wizardry.core.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.loot.RandomSpell;
import top.begonia.wizardry.core.loot.WizardSpell;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public final class WizardryLoots {
    private WizardryLoots() {
    }

    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> LOOT_FUNCTIONS =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, Wizardry.MODID);

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Wizardry.MODID);

    private static @NonNull ResourceKey<LootTable> generateResourceKey(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Wizardry.MODID, path));
    }

    public static final List<ResourceKey<LootTable>> RUINED_SPELL_BOOK_LOOT_TABLES = Arrays.stream(ElementEnum.values())
            .filter(e -> e != ElementEnum.MAGIC)
            .map(e -> generateResourceKey("gameplay/imbuement_altar/ruined_spell_book_" + e.getSerializedName()))
            .toList();
    public static final ResourceKey<LootTable> WIZARD_TOWER = generateResourceKey("chests/wizard_tower");
    public static final ResourceKey<LootTable> OBELISK = generateResourceKey("chests/obelisk");
    public static final ResourceKey<LootTable> SHRINE = generateResourceKey("chests/shrine");
    public static final ResourceKey<LootTable> DUNGEON_ADDITIONS = generateResourceKey("chests/dungeon_additions");
    public static final ResourceKey<LootTable> JUNGLE_DISPENSER_ADDITIONS = generateResourceKey("chests/jungle_dispenser_additions");
    public static final ResourceKey<LootTable> ELEMENTAL_CRYSTALS = generateResourceKey("subsets/elemental_crystals");
    public static final ResourceKey<LootTable> WIZARD_ARMOUR = generateResourceKey("subsets/wizard_armour");
    public static final ResourceKey<LootTable> ARCANE_TOMES = generateResourceKey("subsets/arcane_tomes");
    public static final ResourceKey<LootTable> WAND_UPGRADES = generateResourceKey("subsets/wand_upgrades");
    public static final ResourceKey<LootTable> UNCOMMON_ARTEFACTS = generateResourceKey("subsets/uncommon_artefacts");
    public static final ResourceKey<LootTable> RARE_ARTEFACTS = generateResourceKey("subsets/rare_artefacts");
    public static final ResourceKey<LootTable> EPIC_ARTEFACTS = generateResourceKey("subsets/epic_artefacts");
    public static final ResourceKey<LootTable> EVIL_WIZARD = generateResourceKey("entities/evil_wizard");
    public static final ResourceKey<LootTable> MOB_ADDITIONS = generateResourceKey("entities/mob_additions");
    public static final ResourceKey<LootTable> JUNK_ADDITIONS = generateResourceKey("gameplay/fishing/junk_additions");
    public static final ResourceKey<LootTable> TREASURE_ADDITIONS = generateResourceKey("gameplay/fishing/treasure_additions");

    public static final Supplier<MapCodec<RandomSpell>> RANDOM_SPELL =
            LOOT_FUNCTIONS.register("random_spell", () -> RandomSpell.CODEC);
    public static final Supplier<MapCodec<WizardSpell>> WIZARD_SPELL =
            LOOT_FUNCTIONS.register("wizard_spell", () -> WizardSpell.CODEC);

    public static void register(IEventBus eventBus) {
        LOOT_FUNCTIONS.register(eventBus);
        LOOT_MODIFIERS.register(eventBus);
    }
}
