package top.begonia.wizardry.core.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.constants.ElementEnum;

public class CrystalBlock extends Block {
    public static final EnumProperty<ElementEnum> ELEMENT = EnumProperty.create("element", ElementEnum.class);

    public CrystalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ELEMENT, ElementEnum.MAGIC));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        builder.add(ELEMENT);
    }
}
