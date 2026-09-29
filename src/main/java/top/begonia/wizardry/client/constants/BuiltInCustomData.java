package top.begonia.wizardry.client.constants;

import net.minecraft.resources.Identifier;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.handbook.HandbookData;
import top.begonia.wizardry.client.data.manager.WizardryClientDataManager;

import java.util.function.Supplier;

public final class BuiltInCustomData {
    public static final Supplier<HandbookData> HANDBOOK_DATA = () -> WizardryClientDataManager.getInstance()
            .getData(Identifier.fromNamespaceAndPath(Wizardry.MODID, "handbook"), HandbookData.class)
            .orElse(HandbookData.DEFAULT);
}
