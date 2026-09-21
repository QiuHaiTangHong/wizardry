package top.begonia.wizardry.api.data;

import com.google.gson.JsonElement;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

public interface IDataParser<P, C extends IParserContext, R extends IResultData> {
    Identifier getIdentifier();

    P parserItem(JsonElement json);

    R transformItemToResult(
            Identifier id,
            P data,
            C context,
            PreparableReloadListener.SharedState currentReload,
            ResourceManager resourceManager
    );
}
