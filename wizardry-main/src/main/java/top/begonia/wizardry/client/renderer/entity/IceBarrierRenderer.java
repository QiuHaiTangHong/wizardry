package top.begonia.wizardry.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.model.entity.IceBarrierModel;
import top.begonia.wizardry.client.util.EntityLayerLocations;
import top.begonia.wizardry.core.entity.IceBarrierEntity;

public class IceBarrierRenderer extends LivingEntityRenderer<IceBarrierEntity, LivingEntityRenderState, IceBarrierModel<LivingEntityRenderState>> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/ice_barrier.png");

    public IceBarrierRenderer(EntityRendererProvider.Context context, IceBarrierModel<LivingEntityRenderState> model, float shadow) {
        super(context, model, shadow);
    }

    public IceBarrierRenderer(EntityRendererProvider.Context context){
        this(context, new IceBarrierModel<>(context.bakeLayer(EntityLayerLocations.ICE_BARRIER_ENTITY)), 0.0F);
    }

    @Override
    public @NonNull LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public @NonNull Identifier getTextureLocation(@NonNull LivingEntityRenderState state) {
        return TEXTURE;
    }
}
