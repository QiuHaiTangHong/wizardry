package top.begonia.wizardry.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.constants.ElementEnum;
import top.begonia.wizardry.api.constants.WizardryBlockStateProperties;

public class RunestoneBlock extends Block {
    public RunestoneBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WizardryBlockStateProperties.ELEMENT, ElementEnum.FIRE));
    }

    @Override
    public @NonNull MapColor getMapColor(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull MapColor defaultColor) {
        ElementEnum element = state.getValue(WizardryBlockStateProperties.ELEMENT);
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
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        builder.add(WizardryBlockStateProperties.ELEMENT);
    }
}
