package top.begonia.wizardry.core.block;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CrystalOreBlock extends DropExperienceBlock {
    public CrystalOreBlock(BlockBehaviour.Properties properties) {
        super(UniformInt.of(1, 4), properties);
    }
}
