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

public class RayParticleOptions implements IParticleOptionsExtension {
    private final ParticleType<RayParticleOptions> type;

    @Contract("_ -> new")
    public static @NonNull MapCodec<RayParticleOptions> codec(ParticleType<RayParticleOptions> type) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.DOUBLE.fieldOf("targetX").forGetter(RayParticleOptions::targetX),
                        Codec.DOUBLE.fieldOf("targetY").forGetter(RayParticleOptions::targetY),
                        Codec.DOUBLE.fieldOf("targetZ").forGetter(RayParticleOptions::targetZ)
                ).apply(instance, (tx, ty, tz) -> new RayParticleOptions(type, tx, ty, tz))
        );
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NonNull StreamCodec<RegistryFriendlyByteBuf, RayParticleOptions> streamCodec(ParticleType<RayParticleOptions> type) {
        return StreamCodec.composite(
                ByteBufCodecs.DOUBLE, RayParticleOptions::targetX,
                ByteBufCodecs.DOUBLE, RayParticleOptions::targetY,
                ByteBufCodecs.DOUBLE, RayParticleOptions::targetZ,
                (tx, ty, tz) -> new RayParticleOptions(type, tx, ty, tz)
        );
    }

    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public RayParticleOptions(ParticleType<RayParticleOptions> type, double targetX, double targetY, double targetZ) {
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
    public @NonNull ParticleType<RayParticleOptions> getType() {
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
