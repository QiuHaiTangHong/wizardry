package top.begonia.wizardry.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.common.data.spell.constants.ElementEnum;

public class RunestoneBlock extends Block {

    public static final MapCodec<RunestoneBlock> CODEC = simpleCodec(RunestoneBlock::new);

    public static final EnumProperty<ElementEnum> ELEMENT = EnumProperty.create("element", ElementEnum.class,
            e -> e != ElementEnum.MAGIC);

    public RunestoneBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ELEMENT, ElementEnum.FIRE));
    }

    @Override
    public @NonNull MapColor getMapColor(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull MapColor defaultColor) {
        ElementEnum element = state.getValue(ELEMENT);
        return switch (element) {
            case FIRE -> MapColor.COLOR_RED;
            case ICE -> MapColor.COLOR_LIGHT_BLUE;
            case LIGHTNING -> MapColor.COLOR_CYAN;
            case NECROMANCY -> MapColor.COLOR_PURPLE;
            case EARTH -> MapColor.DIRT;
            case SORCERY -> MapColor.COLOR_GRAY;
            case HEALING -> MapColor.COLOR_YELLOW;
            default -> defaultColor;
        };
    }

    @Override
    protected @NonNull MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        builder.add(ELEMENT);
    }
}
