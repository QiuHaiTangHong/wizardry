package top.begonia.wizardry.core.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record LibraryRuinsMiscProcessor(
        float stoneBrickChance
) implements StructureProcessor {

    public static final MapCodec<LibraryRuinsMiscProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("stone_brick_chance").forGetter(LibraryRuinsMiscProcessor::stoneBrickChance)
    ).apply(instance, LibraryRuinsMiscProcessor::new));

    @Override
    public @NonNull MapCodec<? extends StructureProcessor> codec() {
        return CODEC;
    }

    @Override
    public StructureTemplate.@NonNull StructureBlockInfo process(
            @NonNull LevelReader level,
            @NonNull BlockPos targetPosition,
            @NonNull BlockPos referencePos,
            StructureTemplate.@NonNull StructureBlockInfo originalBlockInfo,
            StructureTemplate.@NonNull StructureBlockInfo processedBlockInfo,
            @NonNull StructurePlaceSettings settings,
            @Nullable StructureTemplate template
    ) {
        BlockState state = processedBlockInfo.state();
        BlockPos targetPos = processedBlockInfo.pos();
        RandomSource random = settings.getRandom(targetPos);

        // ======= 1. 圆石动态变石砖系列（对应旧版第一个匿名处理器） =======
        if (random.nextFloat() > this.stoneBrickChance) {
            if (state.is(Blocks.COBBLESTONE)) {
                return new StructureTemplate.StructureBlockInfo(targetPos, Blocks.STONE_BRICKS.defaultBlockState(), processedBlockInfo.nbt());
            } else if (state.is(Blocks.COBBLESTONE_SLAB)) {
                return new StructureTemplate.StructureBlockInfo(targetPos, copyProperties(state, Blocks.STONE_BRICK_SLAB.defaultBlockState()), processedBlockInfo.nbt());
            } else if (state.is(Blocks.COBBLESTONE_STAIRS)) {
                return new StructureTemplate.StructureBlockInfo(targetPos, copyProperties(state, Blocks.STONE_BRICK_STAIRS.defaultBlockState()), processedBlockInfo.nbt());
            }
        }

        // ======= 2. 石砖 10% 概率变成裂石砖 =======
        if (state.is(Blocks.STONE_BRICKS) && random.nextFloat() < 0.1f) {
            return new StructureTemplate.StructureBlockInfo(targetPos, Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), processedBlockInfo.nbt());
        }

        // ======= 3. 书架标记 NBT（对应旧版最后一个匿名处理器） =======
        // 1.21 中检测自定义书架（假设名字叫 wizardry:oak_bookshelf 等，可以用标签或名字包含判断）
        String blockPath = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        if (blockPath.contains("bookshelf")) {
            var nbt = processedBlockInfo.nbt() != null ? processedBlockInfo.nbt().copy() : new net.minecraft.nbt.CompoundTag();
            // 写入旧版 TileEntityBookshelf.markAsNatural 的 NBT 标签
            nbt.putBoolean("isNatural", true);
            return new StructureTemplate.StructureBlockInfo(targetPos, state, nbt);
        }

        return processedBlockInfo;
    }

    // 辅助属性克隆方法
    @SuppressWarnings("unchecked")
    private static BlockState copyProperties(BlockState from, BlockState to) {
        for (Property<?> property : from.getProperties()) {
            if (to.hasProperty(property)) {
                to = to.setValue((Property) property, from.getValue(property));
            }
        }
        return to;
    }
}
