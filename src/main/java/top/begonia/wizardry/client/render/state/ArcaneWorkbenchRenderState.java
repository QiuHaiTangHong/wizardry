package top.begonia.wizardry.client.render.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ArcaneWorkbenchRenderState extends BlockEntityRenderState {
    public float timer;
    public final ItemStackRenderState wandItemRenderState = new ItemStackRenderState();
}
