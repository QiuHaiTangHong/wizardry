package top.begonia.wizardry.core.registry;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.block.*;

public final class WizardryBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Wizardry.MODID);

    public static final DeferredBlock<ArcaneWorkbenchBlock> ARCANE_WORKBENCH = BLOCKS.registerBlock(
            "arcane_workbench",
            ArcaneWorkbenchBlock::new,
            () -> BlockBehaviour.Properties
                    .ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .noOcclusion()
                    .lightLevel(_ -> 0)
    );

    public static final DeferredBlock<CrystalOreBlock> CRYSTAL_ORE = BLOCKS.registerBlock(
            "crystal_ore",
            CrystalOreBlock::new,
            () -> BlockBehaviour.Properties
                    .ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .noOcclusion()
                    .lightLevel(_ -> 0)
    );

    public static final DeferredBlock<CrystalBlock> CRYSTAL_BLOCK = BLOCKS.registerBlock(
            "crystal_block",
            CrystalBlock::new,
            () -> BlockBehaviour.Properties
                    .ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .noOcclusion()
                    .lightLevel(_ -> 0)
    );

    public static final DeferredBlock<RunestoneBlock> RUNESTONE = BLOCKS.registerBlock(
            "runestone",
            RunestoneBlock::new,
            () -> BlockBehaviour.Properties
                    .ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .noOcclusion()
                    .lightLevel(_ -> 0)
    );

    public static final DeferredBlock<RunestoneBlock> RUNESTONE_PEDESTAL = BLOCKS.registerBlock(
            "runestone_pedestal",
            RunestoneBlock::new,
            () -> BlockBehaviour.Properties
                    .ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .noOcclusion()
                    .lightLevel(_ -> 0)
    );

    public static final DeferredBlock<GildedWoodBlock> GILDED_WOOD = BLOCKS.registerBlock(
            "gilded_wood",
            GildedWoodBlock::new,
            () -> BlockBehaviour.Properties
                    .ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .noOcclusion()
                    .lightLevel(_ -> 0)
    );

    public static final DeferredBlock<BookshelfBlock> BOOKSHELF = BLOCKS.registerBlock(
            "bookshelf",
            BookshelfBlock::new,
            () -> BlockBehaviour.Properties
                    .ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .noOcclusion()
                    .lightLevel(_ -> 0)
    );

    public static final DeferredBlock<LecternBlock> LECTERN = BLOCKS.registerBlock(
            "lectern",
            LecternBlock::new,
            () -> BlockBehaviour.Properties
                    .ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .noOcclusion()
                    .lightLevel(_ -> 0)
    );

    public static final DeferredBlock<CrystalFlowerBlock> CRYSTAL_FLOWER = BLOCKS.registerBlock(
            "crystal_flower",
            CrystalFlowerBlock::new,
            () -> BlockBehaviour.Properties
                    .ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .noOcclusion()
                    .instabreak()
                    .sound(SoundType.BAMBOO_SAPLING)
                    .lightLevel(_ -> 7)
                    .randomTicks()
    );

    public static final DeferredBlock<TransportationStoneBlock> TRANSPORTATION_STONE = BLOCKS.registerBlock(
            "transportation_stone",
            TransportationStoneBlock::new,
            () -> BlockBehaviour.Properties
                    .ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .noOcclusion()
                    .lightLevel(_ -> 0)
    );

    public static final DeferredBlock<ReceptacleBlock> RECEPTACLE = BLOCKS.registerBlock(
            "receptacle",
            ReceptacleBlock::new,
            () -> BlockBehaviour.Properties
                    .ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .noOcclusion()
                    .lightLevel(_ -> 0)
    );

    public static final DeferredBlock<ImbuementAltarBlock> IMBUEMENT_ALTAR = BLOCKS.registerBlock(
            "imbuement_altar",
            ImbuementAltarBlock::new,
            () -> BlockBehaviour.Properties
                    .ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .noOcclusion()
                    .lightLevel(_ -> 0)
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
