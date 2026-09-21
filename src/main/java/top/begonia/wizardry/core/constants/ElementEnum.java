package top.begonia.wizardry.core.constants;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

import javax.annotation.Nullable;

public enum ElementEnum implements StringRepresentable {
    MAGIC(Style.EMPTY.withColor(ChatFormatting.WHITE), "magic"),
    FIRE(Style.EMPTY.withColor(ChatFormatting.DARK_RED), "fire"),
    ICE(Style.EMPTY.withColor(ChatFormatting.AQUA), "ice"),
    LIGHTNING(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA), "lightning"),
    NECROMANCY(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE), "necromancy"),
    EARTH(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN), "earth"),
    SORCERY(Style.EMPTY.withColor(ChatFormatting.GREEN), "sorcery"),
    HEALING(Style.EMPTY.withColor(ChatFormatting.YELLOW), "healing");

    public static final Codec<ElementEnum> CODEC = StringRepresentable.fromEnum(ElementEnum::values);
    public static final StreamCodec<RegistryFriendlyByteBuf, ElementEnum> STREAM_CODEC = ByteBufCodecs.idMapper(
            id -> id >= 0 && id < ElementEnum.values().length ? ElementEnum.values()[id] : ElementEnum.MAGIC,
            ElementEnum::ordinal
    ).cast();
    public static final ElementEnum DEFAULT = ElementEnum.MAGIC;
    private final Style colour;
    private final String unlocalisedName;
    private final Identifier icon;

    ElementEnum(Style colour, String name) {
        this(colour, name, Wizardry.MODID);
    }

    ElementEnum(Style colour, String name, String mod_id) {
        this.colour = colour;
        this.unlocalisedName = name;
        this.icon = Identifier.fromNamespaceAndPath(mod_id, "textures/gui/container/element_icon_" + unlocalisedName + ".png");
    }

    public static ElementEnum fromName(String name) {

        for (ElementEnum element : values()) {
            if (element.unlocalisedName.equals(name)) return element;
        }

        throw new IllegalArgumentException("No such element with unlocalised name: " + name);
    }

    @Nullable
    public static ElementEnum fromName(String name, @Nullable ElementEnum fallback) {

        for (ElementEnum element : values()) {
            if (element.unlocalisedName.equals(name)) return element;
        }

        return fallback;
    }

    public String getFormattingCode() {
        return this.colour.toString();
    }

    public MutableComponent getDisplayNameWithFormatting() {
        return Component.translatable("element." + unlocalisedName).withStyle(this.colour);
    }

    public MutableComponent getDisplayName() {
        return Component.translatable("element." + getSerializedName());
    }

    @Override
    public @NonNull String getSerializedName() {
        return unlocalisedName;
    }

    public Style getStyle() {
        return this.colour;
    }

    public String getColor() {
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
}
