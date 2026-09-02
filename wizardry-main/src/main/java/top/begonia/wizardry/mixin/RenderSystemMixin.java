package top.begonia.wizardry.mixin;

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.begonia.wizardry.client.event.ClientEvents;
import top.begonia.wizardry.client.renderer.uniform.MouseUniform;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    @Inject(
            method = "bindDefaultUniforms",
            at = @At("TAIL")
    )
    private static void injectMouseUniform(RenderPass renderPass, CallbackInfo ci) {
        MouseUniform u = ClientEvents.getMouseUniform();
        if (u != null) {
            renderPass.setUniform("MouseInfo", u.slice());
        }
    }
}
