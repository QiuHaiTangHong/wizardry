package top.begonia.wizardry.api.data.manager;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModLoader;
import top.begonia.wizardry.api.data.AbstractDataManager;
import top.begonia.wizardry.api.data.IDataParser;
import top.begonia.wizardry.api.data.IParserContext;
import top.begonia.wizardry.api.data.IResultData;
import top.begonia.wizardry.api.event.data.RegisterDataParserEvent;

import java.util.HashMap;
import java.util.Map;

public class ServerDataManager extends AbstractDataManager {
    public ServerDataManager(
    ) {
        super(FileToIdConverter.json("custom"));
    }

    @Override
    protected Map<Identifier, IDataParser<IResultData, IParserContext, IResultData>> getParserRegistry() {
        Map<Identifier, IDataParser<IResultData, IParserContext, IResultData>> result = new HashMap<>();
        ModLoader.postEvent(new RegisterDataParserEvent.CommonRegisterDataParserEvent(result));
        return result;
    }
}
