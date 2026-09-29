package top.begonia.wizardry.api.entity.utils;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

/**
 * 实体状态标志位枚举，用于紧凑地编码实体的多个布尔状态。
 */
public enum EntityFlags implements StringRepresentable {
    CHARGING(0x01);
    private final int flag;

    EntityFlags(int flag) {
        this.flag = flag;
    }

    public int flag() {
        return this.flag;
    }

    public boolean has(int flags) {
        return (flags & this.flag) != 0;
    }

    public int set(int flags) {
        return flags | this.flag;
    }

    public int clear(int flags) {
        return flags & ~this.flag;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
