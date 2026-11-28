package top.begonia.wizardry.api.client.constants;

import net.minecraft.resources.Identifier;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.client.WizardryClientApi;
import top.begonia.wizardry.client.data.definition.hub.SkinsData;
import top.begonia.wizardry.core.config.ClientConfig;

import javax.annotation.Nullable;
import java.util.Map;

public class BuiltInData {
    private static final Identifier SKINS_ID = Identifier.fromNamespaceAndPath(Wizardry.MODID, "spell_hud/index");

    public static SkinsData getSkins() {
        return WizardryClientApi.getInstance()
                .getData(SKINS_ID, SkinsData.class)
                .orElse(SkinsData.DEFAULT);
    }

    @Nullable
    public static SkinsData.Skin getSkin(String key) {
        Map<String, SkinsData.Skin> skins = BuiltInData.getSkins().skins();
        SkinsData.Skin skin = skins.get(key);
        if (skin == null) {
            Wizardry.LOGGER.info("The spell HUD skin '{}' specified in the config did not match any of the loaded skins; using the default skin as a fallback.", ClientConfig.spellHUDSkin);

            skin = skins.get(ClientConfig.DEFAULT_HUD_SKIN_KEY);

            if (skin == null) {
                Wizardry.LOGGER.warn("The default spell HUD skin is missing! A resource pack must have overridden it"
                        + " with an invalid JSON file (default.json), please try again without any resource packs.");
                return null;
            }
        }
        return skin;
    }
}
