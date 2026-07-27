package top.begonia.wizardry.core.api.data;

import com.google.gson.JsonElement;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.api.distmarker.Dist;

public interface IDataParser<P, C extends IParserContext, R extends IResultData> {
    Dist getSupportedDist();

    Identifier getIdentifier();

    P parserItem(JsonElement json);

    R transformItemToResult(Identifier id, P data, C context, PreparableReloadListener.SharedState currentReload);
}
