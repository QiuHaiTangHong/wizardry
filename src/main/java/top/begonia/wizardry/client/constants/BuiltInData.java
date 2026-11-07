package top.begonia.wizardry.client.constants;

import net.minecraft.resources.Identifier;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.WizardryClientApi;
import top.begonia.wizardry.client.data.definition.handbook.HandbookData;
import top.begonia.wizardry.client.data.definition.hub.SpellHubConfigData;
import top.begonia.wizardry.api.data.manager.WizardryClientDataManager;

import java.util.Optional;
import java.util.function.Supplier;

public final class BuiltInData {
    public static final Supplier<HandbookData> HANDBOOK_DATA = () -> WizardryClientApi.getInstance()
            .getData(Identifier.fromNamespaceAndPath(Wizardry.MODID, "handbook"), HandbookData.class)
            .orElse(HandbookData.DEFAULT);

    public static Optional<SpellHubConfigData> SpellHub(Identifier metadata) {
        return WizardryClientApi.getInstance().getData(metadata, SpellHubConfigData.class);
    }
}
