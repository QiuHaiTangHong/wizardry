package top.begonia.wizardry.client.renderer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.Wizardry;

public class WizardryPotionRender implements IClientMobEffectExtensions {
    private static final WizardryPotionRender INSTANCE = new WizardryPotionRender();

    public static WizardryPotionRender getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean extractInventoryIcon(
            @NonNull MobEffectInstance instance,
            @NonNull AbstractContainerScreen<?> screen,
            @NonNull GuiGraphicsExtractor graphics,
            int x, int y,
            int width, int height,
            int color
    ) {
        Identifier texture = getTexture(instance);
        if (texture != null) {
            graphics.blit(
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
    public boolean extractHudIcon(
            @NonNull MobEffectInstance instance,
            @NonNull Hud hud,
            @NonNull GuiGraphicsExtractor graphics,
            int x, int y,
            int width, int height,
            int color
    ) {
        Identifier texture = getTexture(instance);
        if (texture != null) {
            graphics.blit(
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

    private @Nullable Identifier getTexture(@NonNull MobEffectInstance instance) {
        Identifier effectId = BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect().value());
        if (effectId != null && effectId.getNamespace().equals(Wizardry.MODID)) {
            return Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/mob_effect/" + effectId.getPath() + ".png");
        }
        return null;
    }
}
