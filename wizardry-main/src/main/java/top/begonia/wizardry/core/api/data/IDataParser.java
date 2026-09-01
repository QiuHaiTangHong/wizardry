package top.begonia.wizardry.core.api.data;

import com.google.gson.JsonElement;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface IDataParser<P, C extends IParserContext, R extends IResultData> {
    Identifier getIdentifier();

    P parserItem(JsonElement json);

    R transformItemToResult(Identifier id, P data, C context, PreparableReloadListener.SharedState currentReload);
}
