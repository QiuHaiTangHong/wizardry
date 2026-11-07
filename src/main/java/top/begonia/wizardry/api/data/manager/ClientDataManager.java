package top.begonia.wizardry.api.data.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.fml.ModLoader;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.data.AbstractDataManager;
import top.begonia.wizardry.api.data.IDataParser;
import top.begonia.wizardry.api.data.IParserContext;
import top.begonia.wizardry.api.data.IResultData;
import top.begonia.wizardry.api.event.data.RegisterDataParserEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClientDataManager extends AbstractDataManager {
    public ClientDataManager() {
        super(FileToIdConverter.json("custom"));
    }

    @Override
    protected Identifier cleanseIdentifier(@NonNull Identifier originalId) {
        String currentLang = Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase(Locale.ROOT);
        String langPrefix = "texts/" + currentLang + "/";
        String originalPath = originalId.getPath();
        if (originalPath.startsWith(langPrefix)) {
            return originalId.withPath(originalPath.substring(langPrefix.length()));
        }
        return originalId;
    }

    @Override
    protected Map<Identifier, IDataParser<?, ? extends IParserContext, ? extends IResultData>> getParserRegistry() {
        Map<Identifier, IDataParser<?, ? extends IParserContext, ? extends IResultData>> result = new HashMap<>();
        ModLoader.postEvent(new RegisterDataParserEvent.ClientRegisterDataParserEvent(result));
        return result;
    }

    @Override
    protected boolean listerFilter(Identifier finalId, @NonNull Identifier location, Resource resource) {
        String currentLang = Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase(Locale.ROOT);
        String path = location.getPath();
        String expectedLanguagePath = "texts/" + currentLang + "/";
        if (path.contains("texts/")) {
            return path.contains(expectedLanguagePath);
        }
        return true;
    }
}
