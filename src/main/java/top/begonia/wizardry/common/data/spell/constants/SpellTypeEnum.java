package top.begonia.wizardry.common.data.spell.constants;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

public enum SpellTypeEnum implements StringRepresentable {

    ATTACK("attack"),
    DEFENCE("defence"),
    UTILITY("utility"),
    MINION("minion"),
    BUFF("buff"),
    CONSTRUCT("construct"),
    PROJECTILE("projectile"),
    ALTERATION("alteration");

    private final String unlocalisedName;

    SpellTypeEnum(String name) {
        this.unlocalisedName = name;
    }

    public static SpellTypeEnum getDefault() {
        return SpellTypeEnum.UTILITY;
    }

    public static SpellTypeEnum fromName(String name) {

        for (SpellTypeEnum type : values()) {
            if (type.unlocalisedName.equals(name)) return type;
        }

        throw new IllegalArgumentException("No such spell type with unlocalised name: " + name);
    }

    public MutableComponent getDisplayName() {
        return Component.translatable("spelltype." + unlocalisedName);
    }

    @Override
    public @NonNull String getSerializedName() {
        return unlocalisedName;
    }
}