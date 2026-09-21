package top.begonia.wizardry.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.config.ClientConfig;

@EventBusSubscriber(modid = Wizardry.MODID)
public class BlinkEffectRenderer {
    private static int blinkEffectTimer;
    private static final int BLINK_EFFECT_DURATION = 8;
    private static final Identifier SCREEN_OVERLAY_TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/blink_overlay.png");
    public static void playBlinkEffect() {
        if (ClientConfig.blinkEffect) {
            blinkEffectTimer = BLINK_EFFECT_DURATION;
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player != null && !Minecraft.getInstance().isPaused()) {
            if (ClientConfig.blinkEffect) {
                if (blinkEffectTimer > 0) {
                    blinkEffectTimer--;
                }
            } else {
                blinkEffectTimer = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onComputeFovModifier(ComputeFovModifierEvent event) {
        if (blinkEffectTimer > 0) {
            float f = ((float) Math.max(blinkEffectTimer - 2, 0)) / BLINK_EFFECT_DURATION;
            event.setNewFovModifier(event.getFovModifier() + f * f * 0.7f);
        }
    }

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.@NonNull Post event) {
        if (VanillaGuiLayers.CAMERA_OVERLAYS.equals(event.getName())) {

            if (blinkEffectTimer > 0) {
                float alpha = ((float) blinkEffectTimer) / BLINK_EFFECT_DURATION;

                GuiGraphicsExtractor guiGraphics = event.getGuiGraphics();
                int screenWidth = guiGraphics.guiWidth();
                int screenHeight = guiGraphics.guiHeight();

                int alphaInt = (int) (alpha * 255.0F) & 255;
                int color = (alphaInt << 24) | 0x00FFFFFF;

                guiGraphics.blit(
                        RenderPipelines.GUI_TEXTURED,
                        SCREEN_OVERLAY_TEXTURE,
                        0, 0,
                        0.0F, 0.0F,
                        screenWidth, screenHeight,
                        screenWidth, screenHeight,
                        color
                );
            }
        }
    }
}
