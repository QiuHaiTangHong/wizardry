package top.begonia.wizardry.api.particle.options;

import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.extension.MutableDoubleSpriteSet;
import top.begonia.wizardry.api.particle.type.ParticleTypeExtension;

public class QuadParticleOptions implements IParticleOptionsExtension {
    private Vec3 acceleration = new Vec3(0.0f, 0.0f, 0.0f);
    private final ParticleTypeExtension<QuadParticleOptions> type;
    private MutableDoubleSpriteSet spriteSet;

    public QuadParticleOptions(
            ParticleTypeExtension<QuadParticleOptions> type
    ) {
        this.type = type;
    }

    public void setSpriteSet(MutableDoubleSpriteSet spriteSet){
        this.spriteSet = spriteSet;
    }

    public MutableDoubleSpriteSet getSpriteSet(){
        return this.spriteSet;
    }

    @Override
    public double xa() {
        return acceleration.x;
    }

    @Override
    public double ya() {
        return acceleration.y;
    }

    @Override
    public double za() {
        return acceleration.x;
    }

    @Override
    public @NonNull ParticleTypeExtension<QuadParticleOptions> getType() {
        return this.type;
    }

    public void setAcceleration(double xa, double ya, double za) {
        this.acceleration = new Vec3(xa, ya, za);
    }
}
