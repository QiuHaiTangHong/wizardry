package top.begonia.wizardry.api.config.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.config.NeoForgeClientConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class TranslationChecker {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Set<String> untranslatables = new HashSet<>();
    private final Set<String> untranslatablesWithFallback = new HashSet<>();

    public String check(final String translationKey) {
        if (!Language.getInstance().has(translationKey)) {
            untranslatables.add(translationKey);
        }
        return translationKey;
    }

    public String check(final String translationKey, final String fallback) {
        if (!Language.getInstance().has(translationKey)) {
            untranslatablesWithFallback.add(translationKey);
            return check(fallback);
        }
        return translationKey;
    }

    public boolean existsWithFallback(final String translationKey) {
        if (!Language.getInstance().has(translationKey)) {
            untranslatablesWithFallback.add(translationKey);
            return false;
        }
        return true;
    }

    /**
     * If the given translation key exists, returns it formatted with the given style(s) and as prefixed with the given component.
     * Otherwise returns an empty Component.
     */
    public Component optional(final Component prefix, final String translationKey, final ChatFormatting... style) {
        if (Language.getInstance().has(translationKey)) {
            return Component.empty().append(prefix).append(Component.translatable(translationKey).withStyle(style));
        }
        return Component.empty();
    }

    public void finish() {
        if (NeoForgeClientConfig.INSTANCE.logUntranslatedConfigurationWarnings.get() && !FMLEnvironment.isProduction() && (!untranslatables.isEmpty() || !untranslatablesWithFallback.isEmpty())) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("""
                    \n	Dev warning - Untranslated configuration keys encountered. Please translate your configuration keys so users can properly configure your mod.
                    """);
            if (!untranslatables.isEmpty()) {
                stringBuilder.append("\nUntranslated keys:");
                for (String key : untranslatables) {
                    stringBuilder.append("\n  \"").append(key).append("\": \"\",");
                }
            }
            if (!untranslatablesWithFallback.isEmpty()) {
                stringBuilder.append("\nThe following keys have fallbacks. Please check if those are suitable, and translate them if they're not.");
                for (String key : untranslatablesWithFallback) {
                    stringBuilder.append("\n  \"").append(key).append("\": \"\",");
                }
            }

            LOGGER.warn(stringBuilder);
        }
        untranslatables.clear();
    }
}
