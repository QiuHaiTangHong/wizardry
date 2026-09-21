package top.begonia.wizardry.api.data;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

public interface IStaticDataParser<T extends IResultData> extends IDataParser<T, IParserContext, T> {
    @Override
    default T transformItemToResult(
            Identifier id,
            T data,
            IParserContext context,
            PreparableReloadListener.SharedState currentReload,
            ResourceManager resourceManager
    ) {
        return data;
    }
}
