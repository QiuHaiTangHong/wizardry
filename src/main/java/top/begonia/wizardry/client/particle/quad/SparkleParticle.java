package top.begonia.wizardry.client.particle.quad;

import net.minecraft.client.multiplayer.ClientLevel;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.extension.extract.IColorFlowOperation;
import top.begonia.wizardry.api.particle.impl.OneQuadParticle;
import top.begonia.wizardry.api.particle.options.impl.QuadParticleOptions;

public class SparkleParticle extends OneQuadParticle {
    public SparkleParticle(ClientLevel level, @NonNull QuadParticleOptions options, double x, double y, double z) {
        super(level, options, x, y, z);
        this.lifetime = 48 + this.random.nextInt(12);
        this.quadSize *= 0.75f;
        this.gravity = 0;
        this.hasPhysics = false;
        this.shaded = false;
    }

    @Override
    protected void extractColor(@NonNull IColorFlowOperation colorFlowOperation) {
        super.extractColor(colorFlowOperation);
        float alpha = 1 - ((float) this.age - (float) (this.lifetime / 2)) / (float) this.lifetime;
        colorFlowOperation.setAlpha(alpha);
    }
}
