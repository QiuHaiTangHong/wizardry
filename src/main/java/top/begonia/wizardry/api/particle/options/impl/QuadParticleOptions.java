package top.begonia.wizardry.api.particle.options.impl;

import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.extension.MutableDoubleSpriteSet;
import top.begonia.wizardry.api.particle.options.IParticleOptionsExtension;
import top.begonia.wizardry.api.particle.type.ParticleTypeExtension;
import top.begonia.wizardry.api.particle.type.impl.QuadParticleType;

public class QuadParticleOptions extends QuadParticleType implements IParticleOptionsExtension {
    private Vec3 acceleration = new Vec3(0.0f, 0.0f, 0.0f);
    private MutableDoubleSpriteSet spriteSet;

    public QuadParticleOptions(
            Identifier identifier,
            boolean overrideLimiter
    ) {
        super(identifier, overrideLimiter);
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
        return this;
    }

    public void setAcceleration(double xa, double ya, double za) {
        this.acceleration = new Vec3(xa, ya, za);
    }
}
