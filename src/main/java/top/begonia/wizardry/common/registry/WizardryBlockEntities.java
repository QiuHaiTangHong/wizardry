package top.begonia.wizardry.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.common.entity.block.ArcaneWorkbenchBlockEntity;
import top.begonia.wizardry.common.entity.block.BookshelfBlockEntity;

import java.util.function.Supplier;

public class WizardryBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Wizardry.MODID);

    @SafeVarargs
    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerBlockEntity(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory, Supplier<? extends Block>... blocks) {
        return BLOCK_ENTITIES.register(name, () -> {
            Block[] validBlocks = java.util.Arrays.stream(blocks).map(Supplier::get).toArray(Block[]::new);
            return new BlockEntityType<>(factory, validBlocks);
        });
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArcaneWorkbenchBlockEntity>> ARCANE_WORKBENCH = registerBlockEntity(
            "arcane_workbench",
            ArcaneWorkbenchBlockEntity::new,
            WizardryBlocks.ARCANE_WORKBENCH
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BookshelfBlockEntity>> BOOKSHELF = registerBlockEntity(
            "bookshelf",
            BookshelfBlockEntity::new,
            WizardryBlocks.BOOKSHELF
    );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
