package top.begonia.wizardry.client.renderer.entity.state.block;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ImbuementAltarRenderState extends BlockEntityRenderState {
    public float timer;
    public final ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
}
