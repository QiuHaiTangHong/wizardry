package top.begonia.wizardry.common.data.spell.constants;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum EnabledEnum implements StringRepresentable {
    BOOK("book"),
    SCROLL("scroll"),
    WANDS("wands"),
    NPCS("npcs"),
    DISPENSERS("dispensers"),
    COMMANDS("commands"),
    TREASURE("treasure"),
    TRADES("trades"),
    LOOTING("looting");
    public final String name;
    public static final Codec<EnabledEnum> CODEC = StringRepresentable.fromEnum(EnabledEnum::values);

    EnabledEnum(String name) {
        this.name = name;
    }

    @Override
    public @NonNull String getSerializedName() {
        return name;
    }
}
