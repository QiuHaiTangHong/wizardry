package top.begonia.wizardry.core.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.constants.ArtefactTypeEnum;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.ManaFlaskTypeEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.data.WandUpgrades;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.ArmourHelper;

import java.util.List;

public final class WizardryComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Wizardry.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WandUpgrades>> UPGRADES_KEY =
            COMPONENTS.registerComponentType("upgrades",
                    builder -> builder
                            .persistent(WandUpgrades.CODEC)
                            .networkSynchronized(WandUpgrades.STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PROGRESSION_KEY =
            COMPONENTS.registerComponentType("progression",
                    builder -> builder
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.VAR_INT)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Holder<AbstractSpell>>>> SPELLS =
            COMPONENTS.registerComponentType("spells",
                    builder -> builder
                            .persistent(AbstractSpell.CODEC.listOf())
                            .networkSynchronized(AbstractSpell.STREAM_CODEC.apply(ByteBufCodecs.list()))
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<AbstractSpell>>> SPELL =
            COMPONENTS.registerComponentType("spell",
                    builder -> builder
                            .persistent(AbstractSpell.CODEC)
                            .networkSynchronized(AbstractSpell.STREAM_CODEC)
                            .cacheEncoding()
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> SPELL_STATE =
            COMPONENTS.registerComponentType("spell_state",
                    builder -> builder.persistent(Codec.STRING)
                            .networkSynchronized(ByteBufCodecs.STRING_UTF8)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CURRENT_SPELL =
            COMPONENTS.registerComponentType("current_spell",
                    builder -> builder
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.VAR_INT)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Integer>>> COOLDOWN_ARRAY_KEY =
            COMPONENTS.registerComponentType("cooldown",
                    builder -> builder
                            .persistent(Codec.INT.listOf())
                            .networkSynchronized(ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()))
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Integer>>> MAX_COOLDOWN_ARRAY_KEY =
            COMPONENTS.registerComponentType("max_cooldown",
                    builder -> builder
                            .persistent(Codec.INT.listOf())
                            .networkSynchronized(ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()))
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> DURATION_MULTIPLIER_KEY =
            COMPONENTS.register("duration_multiplier",
                    () -> DataComponentType
                            .<Float>builder()
                            .persistent(Codec.FLOAT)
                            .build()
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> DAMAGE_MULTIPLIER =
            COMPONENTS.register("damage_multiplier",
                    () -> DataComponentType
                            .<Float>builder()
                            .persistent(Codec.FLOAT)
                            .build()
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MANA =
            COMPONENTS.register("mana", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build()
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ArmourHelper.ArmourMaterialType>> ARMOR_MATERIAL_TYPE =
            COMPONENTS.registerComponentType("armor_material_type",
                    builder -> builder.persistent(ArmourHelper.ArmourMaterialType.CODEC)
                            .networkSynchronized(ByteBufCodecs.fromCodec(ArmourHelper.ArmourMaterialType.CODEC))
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ArmorType>> ARMOR_TYPE =
            COMPONENTS.registerComponentType("armor_type",
                    builder -> builder.persistent(ArmorType.CODEC)
                            .networkSynchronized(ByteBufCodecs.fromCodec(ArmorType.CODEC))
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TierEnum>> TIER =
            COMPONENTS.registerComponentType("tier",
                    builder -> builder
                            .persistent(TierEnum.CODEC)
                            .networkSynchronized(TierEnum.STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ElementEnum>> ELEMENT =
            COMPONENTS.registerComponentType("element",
                    builder -> builder
                            .persistent(ElementEnum.CODEC)
                            .networkSynchronized(ElementEnum.STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ManaFlaskTypeEnum>> MANA_FLASK_TYPE =
            COMPONENTS.registerComponentType("mana_flask_type",
                    builder -> builder
                            .persistent(ManaFlaskTypeEnum.CODEC)
                            .networkSynchronized(ManaFlaskTypeEnum.STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ArtefactTypeEnum>> ARTEFACT_TYPE =
            COMPONENTS.registerComponentType("artefact_type",
                    builder -> builder
                            .persistent(ArtefactTypeEnum.CODEC)
                            .networkSynchronized(ArtefactTypeEnum.STREAM_CODEC)
            );

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
}
