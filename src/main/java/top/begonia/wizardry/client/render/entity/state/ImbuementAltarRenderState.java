package top.begonia.wizardry.client.render.entity.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ImbuementAltarRenderState extends BlockEntityRenderState {
    public float timer;
    public final ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
}
