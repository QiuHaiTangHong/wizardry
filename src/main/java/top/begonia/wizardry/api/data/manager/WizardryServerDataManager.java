package top.begonia.wizardry.api.data.manager;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModLoader;
import top.begonia.wizardry.api.data.AbstractWizardryDataManager;
import top.begonia.wizardry.api.data.IDataParser;
import top.begonia.wizardry.api.data.IParserContext;
import top.begonia.wizardry.api.data.IResultData;
import top.begonia.wizardry.api.event.data.RegisterDataParserEvent;

import java.util.HashMap;
import java.util.Map;

public class WizardryServerDataManager extends AbstractWizardryDataManager {
    public WizardryServerDataManager(
    ) {
        super(FileToIdConverter.json("custom"));
    }

    @Override
    protected Map<Identifier, IDataParser<?, ? extends IParserContext, ? extends IResultData>> getParserRegistry() {
        Map<Identifier, IDataParser<?, ? extends IParserContext, ? extends IResultData>> result = new HashMap<>();
        ModLoader.postEvent(new RegisterDataParserEvent.CommonRegisterDataParserEvent(result));
        return result;
    }
}
