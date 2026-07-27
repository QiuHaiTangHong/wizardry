package top.begonia.wizardry.client.plugin;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.gui.handlers.IScreenHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.gui.ArcaneWorkbenchScreen;

@JeiPlugin
public class WizardryJeiPlugin implements IModPlugin {
    private static final Identifier PLUGIN_ID = Identifier.fromNamespaceAndPath(Wizardry.MODID, "jei_plugin");

    @Override
    public @NonNull Identifier getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerGuiHandlers(@NonNull IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(ArcaneWorkbenchScreen.class, new IScreenHandler<>() {
            @Override
            public @Nullable IGuiProperties apply(@NonNull ArcaneWorkbenchScreen screen) {
                return new IGuiProperties() {
                    @Override
                    public @NonNull Class<? extends Screen> screenClass() {
                        return ArcaneWorkbenchScreen.class;
                    }

                    @Override
                    public int guiLeft() {
                        return screen.getLeftPos() - screen.leftExtensionWidth();
                    }

                    @Override
                    public int guiTop() {
                        return screen.getTopPos();
                    }

                    @Override
                    public int guiXSize() {
                        return screen.getImageWidth() + screen.rightExtensionWidth() + screen.leftExtensionWidth();
                    }

                    @Override
                    public int guiYSize() {
                        return screen.getImageHeight();
                    }

                    @Override
                    public int screenWidth() {
                        return screen.width;
                    }

                    @Override
                    public int screenHeight() {
                        return screen.height;
                    }
                };
            }
        });
    }
}
