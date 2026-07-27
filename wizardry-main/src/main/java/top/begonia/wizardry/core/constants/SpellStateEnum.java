package top.begonia.wizardry.core.constants;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum SpellStateEnum implements StringRepresentable {
    DISCOVERED("discovered"),
    UNDISCOVERED("undiscovered"),
    FESTIVE("festive");
    public static final Codec<SpellStateEnum> CODEC = StringRepresentable.fromEnum(SpellStateEnum::values);
    public static final StreamCodec<RegistryFriendlyByteBuf, SpellStateEnum> STREAM_CODEC = ByteBufCodecs.idMapper(
            id -> id >= 0 && id < SpellStateEnum.values().length ? SpellStateEnum.values()[id] : SpellStateEnum.UNDISCOVERED,
            SpellStateEnum::ordinal
    ).cast();
    final String value;

    SpellStateEnum(String value) {
        this.value = value;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.value;
    }
}
