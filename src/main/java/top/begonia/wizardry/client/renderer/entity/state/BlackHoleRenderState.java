package top.begonia.wizardry.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import top.begonia.wizardry.client.renderer.entity.BlackHoleRenderer;

import java.util.List;

public class BlackHoleRenderState extends EntityRenderState {
    public int[] randomiser;
    public int[] randomiser2;
    public int ticksCount;
    public int lifetime;
    public boolean thirdPersonFront;
    public List<BlackHoleRenderer.RayData> rays;
}
