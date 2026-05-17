package top.begonia.wizardry.common.block;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum WoodTypeEnum implements StringRepresentable {
    OAK("oak"),
    SPRUCE("spruce"),
    BIRCH("birch"),
    JUNGLE("jungle"),
    ACACIA("acacia"),
    DARK_OAK("dark_oak");
    final String value;

    WoodTypeEnum(String value) {
        this.value = value;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.value;
    }
}
