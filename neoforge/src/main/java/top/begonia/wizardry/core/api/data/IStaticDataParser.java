package top.begonia.wizardry.core.api.data;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface IStaticDataParser<T extends IResultData> extends IDataParser<T, IParserContext, T> {

    @Override
    default T transformItemToResult(Identifier id, T data, IParserContext context, PreparableReloadListener.SharedState currentReload) {
        return data;
    }
}
