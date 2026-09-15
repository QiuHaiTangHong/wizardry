package top.begonia.wizardry.client.particle.quad;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.extension.extract.IColorFlowOperation;
import top.begonia.wizardry.api.particle.extension.extract.ISizeFlowOperation;
import top.begonia.wizardry.api.particle.impl.OneQuadParticle;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;

public class FlashParticle extends OneQuadParticle {
    public FlashParticle(ClientLevel level, @NonNull QuadParticleOptions options, double x, double y, double z) {
        super(level, options, x, y, z);
        this.currentColor(1.0f, 1.0f, 1.0f);
        this.startColor(1.0f, 1.0f, 1.0f);
        this.endColor(1.0f, 1.0f, 1.0f);
        this.quadSize = 0.6f;
        this.lifetime = 6;
    }

    @Override
    protected void extractColor(@NonNull IColorFlowOperation colorFlowOperation) {
        super.extractColor(colorFlowOperation);
        float alpha = 0.6F - ((float) this.age + colorFlowOperation.getPartialTick() - 1.0F) / this.lifetime * 0.5F;
        colorFlowOperation.setAlpha(alpha);
    }

    @Override
    protected void extractSize(@NonNull ISizeFlowOperation sizeFlowOperation) {
        float finalScale = sizeFlowOperation.getScale() * Mth.sin(((float) this.age + sizeFlowOperation.getPartialTick() - 1.0F) / this.lifetime * (float) Math.PI);
        sizeFlowOperation.setScale(finalScale);
    }
}
