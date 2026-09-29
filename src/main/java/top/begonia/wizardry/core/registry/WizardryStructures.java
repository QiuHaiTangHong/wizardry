package top.begonia.wizardry.core.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.worldgen.pool.DelayedOperationPoolElement;
import top.begonia.wizardry.core.worldgen.processor.*;

public final class WizardryStructures {
    public static final DeferredRegister<MapCodec<? extends StructureProcessor>> PROCESSORS = DeferredRegister.create(
            Registries.STRUCTURE_PROCESSOR,
            Wizardry.MODID
    );

    public static final DeferredRegister<StructurePoolElementType<?>> POOL_ELEMENTS = DeferredRegister.create(
            Registries.STRUCTURE_POOL_ELEMENT,
            Wizardry.MODID
    );

    // StructureProcessor
    public static final DeferredHolder<MapCodec<? extends StructureProcessor>, MapCodec<WoodTypeProcessor>> WOOD_TYPE = PROCESSORS.register(
            "wood_type",
            () -> WoodTypeProcessor.CODEC
    );

    public static final DeferredHolder<MapCodec<? extends StructureProcessor>, MapCodec<StoneBrickProcessor>> STONE_BRICK = PROCESSORS.register(
            "stone_brick",
            () -> StoneBrickProcessor.CODEC
    );

    public static final DeferredHolder<MapCodec<? extends StructureProcessor>, MapCodec<CrackedStoneProcessor>> CRACKED_STONE = PROCESSORS.register(
            "cracked_stone",
            () -> CrackedStoneProcessor.CODEC
    );

    public static final DeferredHolder<MapCodec<? extends StructureProcessor>, MapCodec<ObeliskProcessor>> OBELISK = PROCESSORS.register(
            "obelisk",
            () -> ObeliskProcessor.CODEC
    );

    public static final DeferredHolder<MapCodec<? extends StructureProcessor>, MapCodec<ReplaceProcessor>> REPLACE = PROCESSORS.register(
            "replace",
            () -> ReplaceProcessor.CODEC
    );

    public static final DeferredHolder<MapCodec<? extends StructureProcessor>, MapCodec<BookshelfMarkerProcessor>> BOOKSHELF_MARKER = PROCESSORS.register(
            "bookshelf_marker",
            () -> BookshelfMarkerProcessor.CODEC
    );

    public static final DeferredHolder<MapCodec<? extends StructureProcessor>, MapCodec<Processor550W>> MOSSIFIER = PROCESSORS.register(
            "550w",
            () -> Processor550W.CODEC
    );

    // StructurePoolElementType
    public static final DeferredHolder<StructurePoolElementType<?>, StructurePoolElementType<DelayedOperationPoolElement>> DELAYED_OPERATION = POOL_ELEMENTS.register(
            "delayed_operation",
            () -> new StructurePoolElementType<>() {
                @Override
                public @NonNull MapCodec<DelayedOperationPoolElement> codec() {
                    return DelayedOperationPoolElement.CODEC;
                }
            }
    );

    private WizardryStructures() {
    }

    public static void register(IEventBus eventBus) {
        WizardryStructures.PROCESSORS.register(eventBus);
        WizardryStructures.POOL_ELEMENTS.register(eventBus);
    }
}
