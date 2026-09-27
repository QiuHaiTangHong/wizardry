package top.begonia.wizardry.api.constants;

import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class WizardryBlockStateProperties {
    public static final EnumProperty<WoodTypeEnum> WOOD_TYPE = EnumProperty.create(
            "wood_type",
            WoodTypeEnum.class
    );
    public static final EnumProperty<ElementEnum> ELEMENT = EnumProperty.create(
            "element",
            ElementEnum.class,
            e -> e != ElementEnum.MAGIC
    );
}
