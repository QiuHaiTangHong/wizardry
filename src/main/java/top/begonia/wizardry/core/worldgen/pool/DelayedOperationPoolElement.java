package top.begonia.wizardry.core.worldgen.pool;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import top.begonia.wizardry.core.registry.WizardryBlocks;
import top.begonia.wizardry.core.registry.WizardryStructures;
import top.begonia.wizardry.core.worldgen.processor.IDelayedOperation;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class DelayedOperationPoolElement extends StructurePoolElement {
    public static final MapCodec<DelayedOperationPoolElement> CODEC = RecordCodecBuilder.mapCodec((i) ->
            i.group(
                    templateCodec(),
                    processorsCodec(),
                    projectionCodec(),
                    overrideLiquidSettingsCodec()
            ).apply(i, DelayedOperationPoolElement::new)
    );
    private static final Comparator<StructureTemplate.JigsawBlockInfo> HIGHEST_SELECTION_PRIORITY_FIRST = Comparator.comparingInt(StructureTemplate.JigsawBlockInfo::selectionPriority).reversed();
    private static final Codec<Either<Identifier, StructureTemplate>> TEMPLATE_CODEC = Codec.of(
            DelayedOperationPoolElement::encodeTemplate,
            Identifier.CODEC.map(Either::left)
    );
    protected final Either<Identifier, StructureTemplate> template;
    protected final Holder<StructureProcessorList> processors;
    protected final Optional<LiquidSettings> overrideLiquidSettings;

    protected DelayedOperationPoolElement(
            Either<Identifier, StructureTemplate> template,
            Holder<StructureProcessorList> processors,
            StructureTemplatePool.Projection projection,
            Optional<LiquidSettings> overrideLiquidSettings
    ) {
        super(projection);
        this.template = template;
        this.processors = processors;
        this.overrideLiquidSettings = overrideLiquidSettings;
    }

    private static <T> DataResult<T> encodeTemplate(@NonNull Either<Identifier, StructureTemplate> template, DynamicOps<T> ops, T prefix) {
        Optional<Identifier> location = template.left();
        return location.isEmpty() ? DataResult.error(() -> "Can not serialize a runtime pool element") : Identifier.CODEC.encode((Identifier) location.get(), ops, prefix);
    }

    @Contract(" -> new")
    protected static <E extends DelayedOperationPoolElement> @NonNull RecordCodecBuilder<E, Holder<StructureProcessorList>> processorsCodec() {
        return StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter((t) -> t.processors);
    }

    @Contract(" -> new")
    protected static <E extends DelayedOperationPoolElement> @NonNull RecordCodecBuilder<E, Optional<LiquidSettings>> overrideLiquidSettingsCodec() {
        return LiquidSettings.CODEC.optionalFieldOf("override_liquid_settings").forGetter((t) -> t.overrideLiquidSettings);
    }

    @Contract(" -> new")
    protected static <E extends DelayedOperationPoolElement> @NonNull RecordCodecBuilder<E, Either<Identifier, StructureTemplate>> templateCodec() {
        return TEMPLATE_CODEC.fieldOf("location").forGetter((t) -> t.template);
    }

    @VisibleForTesting
    static void sortBySelectionPriority(@NonNull List<StructureTemplate.JigsawBlockInfo> blocks) {
        blocks.sort(HIGHEST_SELECTION_PRIORITY_FIRST);
    }

    @Override
    @NullMarked
    public Vec3i getSize(
            StructureTemplateManager structureTemplateManager,
            Rotation rotation
    ) {
        StructureTemplate template = this.getTemplate(structureTemplateManager);
        return template.getSize(rotation);
    }

    @Override
    public @NonNull List<StructureTemplate.JigsawBlockInfo> getShuffledJigsawBlocks(
            @NonNull StructureTemplateManager structureTemplateManager,
            @NonNull BlockPos position,
            @NonNull Rotation rotation,
            @NonNull RandomSource random
    ) {
        List<StructureTemplate.JigsawBlockInfo> jigsaws = this.getTemplate(structureTemplateManager).getJigsaws(position, rotation);
        Util.shuffle(jigsaws, random);
        sortBySelectionPriority(jigsaws);
        return jigsaws;
    }

    @Override
    @NullMarked
    public BoundingBox getBoundingBox(
            StructureTemplateManager structureTemplateManager,
            BlockPos position,
            Rotation rotation
    ) {
        StructureTemplate template = this.getTemplate(structureTemplateManager);
        return template.getBoundingBox((new StructurePlaceSettings()).setRotation(rotation), position);
    }

    @Override
    @NullMarked
    public boolean place(
            StructureTemplateManager structureTemplateManager,
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator generator,
            BlockPos position,
            BlockPos referencePos,
            Rotation rotation,
            BoundingBox chunkBB,
            RandomSource random,
            LiquidSettings liquidSettings,
            boolean keepJigsaws
    ) {
        StructureTemplate template = this.getTemplate(structureTemplateManager);
        StructurePlaceSettings settings = this.getSettings(rotation, chunkBB, liquidSettings, keepJigsaws);
        if (!template.placeInWorld(level, position, referencePos, settings, random, 18)) {
            return false;
        } else {
            for (StructureTemplate.StructureBlockInfo dataMarker : StructureTemplate.processBlockInfos(
                    level,
                    position,
                    referencePos,
                    settings,
                    this.getDelayedOperationDataMarker(structureTemplateManager, position, rotation, false),
                    template
            )) {
                this.handleDelayedOperationDataMarker(level, dataMarker, position, rotation, random, chunkBB);
            }

            return true;
        }
    }

    @Override
    public @NonNull StructurePoolElementType<?> getType() {
        return WizardryStructures.DELAYED_OPERATION.get();
    }

    private StructureTemplate getTemplate(StructureTemplateManager structureTemplateManager) {
        Objects.requireNonNull(structureTemplateManager);
        return this.template.map(structureTemplateManager::getOrCreate, Function.identity());
    }

    protected StructurePlaceSettings getSettings(Rotation rotation, BoundingBox chunkBB, LiquidSettings liquidSettings, boolean keepJigsaws) {
        StructurePlaceSettings settings = new StructurePlaceSettings();
        settings.setBoundingBox(chunkBB);
        settings.setRotation(rotation);
        settings.setKnownShape(true);
        settings.setIgnoreEntities(false);
        settings.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
        settings.setFinalizeEntities(true);
        settings.setLiquidSettings(this.overrideLiquidSettings.orElse(liquidSettings));
        if (!keepJigsaws) {
            settings.addProcessor(JigsawReplacementProcessor.INSTANCE);
        }

        List<StructureProcessor> processors = this.processors.value().list();
        Objects.requireNonNull(settings);
        processors.forEach(settings::addProcessor);
        ImmutableList<StructureProcessor> projectionProcessors = this.getProjection().getProcessors();
        Objects.requireNonNull(settings);
        projectionProcessors.forEach(settings::addProcessor);
        return settings;
    }

    @Override
    public String toString() {
        return "DelayedOperation[" + this.template + "]";
    }

    public void handleDelayedOperationDataMarker(
            @NonNull LevelAccessor level,
            StructureTemplate.@NonNull StructureBlockInfo dataMarker,
            @NonNull BlockPos position,
            @NonNull Rotation rotation,
            @NonNull RandomSource random,
            @NonNull BoundingBox chunkBB
    ) {
        if (this.processors.isBound()) {
            List<StructureProcessor> processorLists = this.processors.value().list();
            for (StructureProcessor processorList : processorLists) {
                if (processorList instanceof IDelayedOperation delayedOperation) {
                    delayedOperation.processDelayedOperation(level, dataMarker, position, rotation, random, chunkBB);
                }
            }
        }
    }

    public List<StructureTemplate.StructureBlockInfo> getDelayedOperationDataMarker(
            StructureTemplateManager structureTemplateManager,
            BlockPos position,
            Rotation rotation,
            boolean absolute
    ) {
        StructureTemplate template = this.getTemplate(structureTemplateManager);
        // TODO
        return List.of();
//        return template.filterBlocks(
//                position,
//                (new StructurePlaceSettings()).setRotation(rotation),
//                WizardryBlocks.DELAYED_OPERATION_BLOCK,
//                absolute
//        );
    }
}
