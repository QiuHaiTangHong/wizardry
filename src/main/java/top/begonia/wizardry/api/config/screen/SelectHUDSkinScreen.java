package top.begonia.wizardry.api.config.screen;

import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.config.utils.Context;

public class SelectHUDSkinScreen extends ConfigSectionScreen {

    public SelectHUDSkinScreen(@NonNull Context context, Component title) {
        super(context, title);
    }

    @Override
    protected ConfigSectionScreen rebuild() {
        return this;
    }
}
