package top.begonia.wizardry.common.data.spell.constants;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

import javax.annotation.Nullable;

public enum ElementEnum implements StringRepresentable {
    MAGIC(Style.EMPTY.withColor(ChatFormatting.GRAY), "magic"),
    FIRE(Style.EMPTY.withColor(ChatFormatting.DARK_RED), "fire"),
    ICE(Style.EMPTY.withColor(ChatFormatting.AQUA), "ice"),
    LIGHTNING(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA), "lightning"),
    NECROMANCY(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE), "necromancy"),
    EARTH(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN), "earth"),
    SORCERY(Style.EMPTY.withColor(ChatFormatting.GREEN), "sorcery"),
    HEALING(Style.EMPTY.withColor(ChatFormatting.YELLOW), "healing");

    private final Style colour;
    private final String unlocalisedName;
    private final Identifier icon;

    ElementEnum(Style colour, String name) {
        this(colour, name, Wizardry.MODID);
    }

    ElementEnum(Style colour, String name, String modid) {
        this.colour = colour;
        this.unlocalisedName = name;
        this.icon = Identifier.fromNamespaceAndPath(modid, "textures/gui/container/element_icon_" + unlocalisedName + ".png");
    }

    public static ElementEnum getDefault() {
        return ElementEnum.MAGIC;
    }

    public static ElementEnum fromName(String name) {

        for (ElementEnum element : values()) {
            if (element.unlocalisedName.equals(name)) return element;
        }

        throw new IllegalArgumentException("No such element with unlocalised name: " + name);
    }

    public String getFormattingCode() {
        return this.colour.toString();
    }

    @Nullable
    public static ElementEnum fromName(String name, @Nullable ElementEnum fallback) {

        for (ElementEnum element : values()) {
            if (element.unlocalisedName.equals(name)) return element;
        }

        return fallback;
    }

    public MutableComponent getDisplayNameWithFormatting() {
        return Component.translatable("element." + unlocalisedName).withStyle(this.colour);
    }

    public MutableComponent getDisplayName() {
        return Component.translatable("element." + getSerializedName());
    }

    public Style getStyle() {
        return this.colour;
    }

    public String getColor(){
        if (colour.getColor() != null) {
            return colour.getColor().serialize();
        }
        return "";
    }

    public Component getWizardName() {
        return Component.translatable("element." + getSerializedName() + ".wizard");
    }

    public Identifier getIcon() {
        return icon;
    }

    @Override
    public @NonNull String getSerializedName() {
        return unlocalisedName;
    }
}
