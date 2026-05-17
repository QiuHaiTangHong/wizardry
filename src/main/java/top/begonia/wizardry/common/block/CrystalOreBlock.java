package top.begonia.wizardry.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jspecify.annotations.NonNull;

public class CrystalOreBlock extends DropExperienceBlock {
    public static final MapCodec<CrystalOreBlock> CODEC = simpleCodec(CrystalOreBlock::new);

    public CrystalOreBlock(BlockBehaviour.Properties properties) {
        super(UniformInt.of(1, 4), properties);
    }

    @Override
    public @NonNull MapCodec<? extends DropExperienceBlock> codec() {
        return CODEC;
    }
}
