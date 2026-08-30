package top.begonia.wizardry.client.plugin;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.gui.ArcaneWorkbenchScreen;

import java.util.List;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
    private static final Identifier PLUGIN_ID = Identifier.fromNamespaceAndPath(Wizardry.MODID, "jei_plugin");

    @Override
    public @NonNull Identifier getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerGuiHandlers(@NonNull IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(
                ArcaneWorkbenchScreen.class,
                new IGuiContainerHandler<>() {
                    @Override
                    public @NonNull List<Rect2i> getGuiExtraAreas(@NonNull ArcaneWorkbenchScreen containerScreen) {
                        return containerScreen.getGuiExtraAreas();
                    }
                }
        );
    }
}
