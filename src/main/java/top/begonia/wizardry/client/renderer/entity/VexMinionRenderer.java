package top.begonia.wizardry.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.vex.VexModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.VexRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.entity.utils.EntityFlags;
import top.begonia.wizardry.core.entity.living.minion.VexMinionEntity;

public class VexMinionRenderer extends MobRenderer<VexMinionEntity, VexRenderState, VexModel> {
    private static final Identifier VEX_LOCATION = Identifier.withDefaultNamespace("textures/entity/illager/vex.png");
    private static final Identifier VEX_CHARGING_LOCATION = Identifier.withDefaultNamespace("textures/entity/illager/vex_charging.png");

    public VexMinionRenderer(EntityRendererProvider.Context context) {
        super(context, new VexModel(context.bakeLayer(ModelLayers.VEX)), 0.3F);
        this.addLayer(new ItemInHandLayer<>(this));
    }

    protected int getBlockLightLevel(
            @NonNull VexMinionEntity entity,
            @NonNull BlockPos blockPos
    ) {
        return 15;
    }

    @Override
    public @NonNull VexRenderState createRenderState() {
        return new VexRenderState();
    }

    @Override
    public @NonNull Identifier getTextureLocation(@NonNull VexRenderState state) {
        return state.isCharging ? VEX_CHARGING_LOCATION : VEX_LOCATION;
    }

    @Override
    public void extractRenderState(
            @NonNull VexMinionEntity entity,
            @NonNull VexRenderState state,
            float partialTicks
    ) {
        super.extractRenderState(entity, state, partialTicks);
        ArmedEntityRenderState.extractArmedEntityRenderState(entity, state, this.itemModelResolver, partialTicks);
        state.isCharging = entity.getFlag(EntityFlags.CHARGING);
    }
}
