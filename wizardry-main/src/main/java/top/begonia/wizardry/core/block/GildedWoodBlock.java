package top.begonia.wizardry.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.constants.WoodTypeEnum;

public class GildedWoodBlock extends Block {
    public static final MapCodec<GildedWoodBlock> CODEC = simpleCodec(GildedWoodBlock::new);
    public static final EnumProperty<WoodTypeEnum> GILDED_WOOD_TYPE = EnumProperty.create("wood_type", WoodTypeEnum.class);

    public GildedWoodBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(GILDED_WOOD_TYPE, WoodTypeEnum.OAK));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        builder.add(GILDED_WOOD_TYPE);
    }

    @Override
    protected @NonNull MapCodec<? extends Block> codec() {
        return CODEC;
    }
}
