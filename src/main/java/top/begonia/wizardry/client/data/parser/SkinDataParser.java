package top.begonia.wizardry.client.data.parser;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.data.IStaticDataParser;
import top.begonia.wizardry.client.data.definition.hub.SkinsData;

import java.util.Map;

public class SkinDataParser implements IStaticDataParser<SkinsData> {
    public static final Identifier PARSER_NAME = Identifier.fromNamespaceAndPath(Wizardry.MODID, "skin_data_parser");

    @Override
    public Identifier getIdentifier() {
        return PARSER_NAME;
    }

    @Override
    public JsonElement mergeElement(final @NonNull JsonElement part, ImmutableMap<Identifier, JsonElement> allJsonElement) {
        JsonObject result = new JsonObject();
        if (part instanceof JsonObject jsonObject) {
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                if (entry.getValue() instanceof JsonObject object) {
                    JsonObject merged = new JsonObject();
                    Identifier id = object.has("metadata") ? Identifier.tryParse(object.get("metadata").getAsString()) : null;
                    JsonElement part2 = id == null ? null : allJsonElement.get(id);
                    if (part2 instanceof JsonObject part2Object) {
                        for (Map.Entry<String, JsonElement> e : part2Object.entrySet()) {
                            merged.add(e.getKey(), e.getValue());
                        }
                    }
                    for (Map.Entry<String, JsonElement> e : object.entrySet()) {
                        if (!"metadata".equals(e.getKey())) {
                            merged.add(e.getKey(), e.getValue());
                        }
                    }
                    result.add(key, merged);
                } else {
                    result.add(key, entry.getValue());
                }
            }
        }
        return result;
    }

    @Override
    public SkinsData parserItem(JsonElement json) {
        return SkinsData.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> Wizardry.LOGGER.error("皮肤数据解析错误: {}", error))
                .orElse(null);
    }
}
