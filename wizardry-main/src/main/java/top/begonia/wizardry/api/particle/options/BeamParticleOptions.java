package top.begonia.wizardry.api.particle.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public class BeamParticleOptions implements IParticleOptionsExtension {
    private final ParticleType<BeamParticleOptions> type;

    @Contract("_ -> new")
    public static @NonNull MapCodec<BeamParticleOptions> codec(ParticleType<BeamParticleOptions> type) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.DOUBLE.fieldOf("targetX").forGetter(BeamParticleOptions::targetX),
                        Codec.DOUBLE.fieldOf("targetY").forGetter(BeamParticleOptions::targetY),
                        Codec.DOUBLE.fieldOf("targetZ").forGetter(BeamParticleOptions::targetZ)
                ).apply(instance, (tx, ty, tz) -> new BeamParticleOptions(type, tx, ty, tz))
        );
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NonNull StreamCodec<RegistryFriendlyByteBuf, BeamParticleOptions> streamCodec(ParticleType<BeamParticleOptions> type) {
        return StreamCodec.composite(
                ByteBufCodecs.DOUBLE, BeamParticleOptions::targetX,
                ByteBufCodecs.DOUBLE, BeamParticleOptions::targetY,
                ByteBufCodecs.DOUBLE, BeamParticleOptions::targetZ,
                (tx, ty, tz) -> new BeamParticleOptions(type, tx, ty, tz)
        );
    }

    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public BeamParticleOptions(ParticleType<BeamParticleOptions> type, double targetX, double targetY, double targetZ) {
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.type = type;
    }

    @Override
    public double xa() {
        return 0;
    }

    @Override
    public double ya() {
        return 0;
    }

    @Override
    public double za() {
        return 0;
    }

    @Override
    public @NonNull ParticleType<BeamParticleOptions> getType() {
        return this.type;
    }

    public double targetX() {
        return targetX;
    }

    public double targetY() {
        return targetY;
    }

    public double targetZ() {
        return targetZ;
    }
}
