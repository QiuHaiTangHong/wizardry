package top.begonia.wizardry.client.data.parser;

import com.google.gson.JsonElement;
import net.minecraft.resources.Identifier;
import top.begonia.wizardry.client.data.definition.IData;

import java.util.List;

public interface IDataParser<T extends IData> {
    Identifier getIdentifier();

    T parser(JsonElement json);
}
