package top.begonia.wizardry.api.renderer;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public final class WizardryRenderTypes {
    public static final Function<Identifier, RenderType> DOUBLE_LAYER_OVERLY = Util.memoize((atlasLocation) -> {
        RenderSetup setup = RenderSetup.builder(WizardryRenderPipelines.EMISSION_ITEM_CUTOUT)
                .withTexture("Sampler0", atlasLocation)
                .useOverlay()
                .useLightmap()
                .createRenderSetup();
        return RenderType.create("overly", setup);
    });
    public static final Function<Identifier, RenderType> DOUBLE_LAYER_BASE = Util.memoize((atlasLocation) -> {
        RenderSetup setup = RenderSetup.builder(WizardryRenderPipelines.ITEM_CUTOUT)
                .withTexture("Sampler0", atlasLocation)
                .useOverlay()
                .useLightmap()
                .createRenderSetup();
        return RenderType.create("base", setup);
    });

    public static RenderType doubleLayerOverly(Identifier texture) {
        return WizardryRenderTypes.DOUBLE_LAYER_OVERLY.apply(texture);
    }

    public static RenderType doubleLayerBase(Identifier texture) {
        return WizardryRenderTypes.DOUBLE_LAYER_BASE.apply(texture);
    }
}
