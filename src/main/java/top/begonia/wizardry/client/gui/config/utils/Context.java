package top.begonia.wizardry.client.gui.config.utils;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record Context(
        String modId,
        Screen parent,
        ModConfig modConfig,
        ModConfigSpec modSpec,
        Set<? extends UnmodifiableConfig.Entry> entries,
        Map<String, Object> valueSpecs,
        List<String> keylist,
        Filter filter
) {
    @ApiStatus.Internal
    public Context {
    }

    public static Context top(
            final String modId,
            final Screen parent,
            final ModConfig modConfig,
            Filter filter
    ) {
        return new Context(
                modId,
                parent,
                modConfig,
                (ModConfigSpec) modConfig.getSpec(),
                ((ModConfigSpec) modConfig.getSpec()).getValues().entrySet(),
                ((ModConfigSpec) modConfig.getSpec()).getSpec().valueMap(),
                List.of(),
                filter
        );
    }

    public static Context section(
            final Context parentContext,
            final Screen parent,
            final Set<? extends UnmodifiableConfig.Entry> entries,
            final Map<String, Object> valueSpecs,
            final String key
    ) {
        return new Context(
                parentContext.modId,
                parent,
                parentContext.modConfig,
                parentContext.modSpec,
                entries,
                valueSpecs,
                parentContext.makeKeyList(key),
                parentContext.filter
        );
    }

    public static Context list(final Context parentContext, final Screen parent) {
        return new Context(
                parentContext.modId,
                parent,
                parentContext.modConfig,
                parentContext.modSpec,
                parentContext.entries,
                parentContext.valueSpecs,
                parentContext.keylist,
                null
        );
    }

    private ArrayList<String> makeKeyList(final String key) {
        final ArrayList<String> result = new ArrayList<>(keylist);
        result.add(key);
        return result;
    }
}
