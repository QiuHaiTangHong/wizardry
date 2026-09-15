package top.begonia.wizardry.api.particle.extension.extract;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import top.begonia.wizardry.api.particle.extension.Layer;
import top.begonia.wizardry.api.particle.renderer.state.CompositeQuadParticleRenderState;

public interface IBaseFlowOperation {
    Layer getLayer();

    Camera getCamera();

    CompositeQuadParticleRenderState getState();

    Entity getLinkEntity();

    float getPartialTick();

    float getAge();

    float getLifetime();
}