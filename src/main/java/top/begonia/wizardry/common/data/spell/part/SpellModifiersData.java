package top.begonia.wizardry.common.data.spell.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import top.begonia.wizardry.common.data.spell.IWizardryProperty;

public record SpellModifiersData(
        Modifier potency,
        Modifier cost,
        Modifier chargeup,
        Modifier progression
) implements IWizardryProperty<SpellModifiersData> {
    public static final SpellModifiersData DEFAULT = new SpellModifiersData(
            new Modifier(1.0f),
            new Modifier(1.0f),
            new Modifier(1.0f),
            new Modifier(1.0f)
    );
    public static final Codec<SpellModifiersData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Modifier.CODEC.optionalFieldOf("potency", new Modifier(1.0f)).forGetter(SpellModifiersData::potency),
            Modifier.CODEC.optionalFieldOf("cost", new Modifier(1.0f)).forGetter(SpellModifiersData::cost),
            Modifier.CODEC.optionalFieldOf("chargeup", new Modifier(1.0f)).forGetter(SpellModifiersData::chargeup),
            Modifier.CODEC.optionalFieldOf("progression", new Modifier(1.0f)).forGetter(SpellModifiersData::progression)
    ).apply(instance, SpellModifiersData::new));

    public static final StreamCodec<FriendlyByteBuf, SpellModifiersData> STREAM_CODEC = StreamCodec.composite(
            Modifier.STREAM_CODEC, SpellModifiersData::potency,
            Modifier.STREAM_CODEC, SpellModifiersData::cost,
            Modifier.STREAM_CODEC, SpellModifiersData::chargeup,
            Modifier.STREAM_CODEC, SpellModifiersData::progression,
            SpellModifiersData::new
    );

    public SpellModifiersData combine(SpellModifiersData other) {
        return new SpellModifiersData(
                this.potency.multiply(other.potency),
                this.cost.multiply(other.cost),
                this.chargeup.multiply(other.chargeup),
                this.progression.multiply(other.progression)
        );
    }

    @Override
    public Codec<SpellModifiersData> getCodec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super FriendlyByteBuf, SpellModifiersData> getStreamCodec() {
        return STREAM_CODEC;
    }

    public record Modifier(float value) {
        public static final Codec<Modifier> CODEC = Codec.FLOAT.xmap(
                Modifier::new,
                Modifier::value
        );
        public static final StreamCodec<FriendlyByteBuf, Modifier> STREAM_CODEC = ByteBufCodecs.FLOAT.map(
                Modifier::new,
                Modifier::value
        ).cast();

        public float amplified(float scalar) {
            return (this.value - 1) * scalar + 1;
        }

        public float value() {
            return value;
        }

        public Modifier multiply(Modifier other) {
            return new Modifier(this.value * other.value);
        }
    }
}
