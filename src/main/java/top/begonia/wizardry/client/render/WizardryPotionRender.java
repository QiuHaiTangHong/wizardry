package top.begonia.wizardry.client.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

public class WizardryPotionRender implements IClientMobEffectExtensions {
    public static final WizardryPotionRender INSTANCE = new WizardryPotionRender();

    @Override
    public boolean renderInventoryIcon(@NonNull MobEffectInstance instance, @NonNull AbstractContainerScreen<?> screen, @NonNull GuiGraphicsExtractor guiGraphics, int x, int y, int blitOffset) {
        Identifier texture = getTexture(instance);
        if (texture != null) {
            guiGraphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    texture,
                    x, y + 7,
                    0, 0,
                    18, 18,
                    18, 18
            );
            return true;
        }
        return false;
    }

    @Override
    public boolean renderInventoryText(@NonNull MobEffectInstance instance, @NonNull AbstractContainerScreen<?> screen, @NonNull GuiGraphicsExtractor guiGraphics, int x, int y, int blitOffset) {
        return false;
    }

    @Override
    public boolean renderGuiIcon(@NonNull MobEffectInstance instance, @NonNull Gui gui, @NonNull GuiGraphicsExtractor guiGraphics, int x, int y, float z, float alpha) {
        Identifier texture = getTexture(instance);
        if (texture != null) {
            int color = ARGB.white(alpha);
            guiGraphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    texture,
                    x + 3, y + 3,
                    0, 0,
                    18, 18, 18, 18,
                    color
            );
            return true;
        }
        return false;
    }

    private Identifier getTexture(MobEffectInstance instance) {
        Identifier effectId = BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect().value());
        if (effectId != null && effectId.getNamespace().equals(Wizardry.MODID)) {
            return Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/mob_effect/" + effectId.getPath() + ".png");
        }
        return null;
    }
}
