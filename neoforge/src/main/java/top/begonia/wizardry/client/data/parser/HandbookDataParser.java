package top.begonia.wizardry.client.data.parser;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.handbook.HandbookData;
import top.begonia.wizardry.core.api.data.IStaticDataParser;

public class HandbookDataParser implements IStaticDataParser<HandbookData> {
    public static final Identifier PARSER_NAME = Identifier.fromNamespaceAndPath(Wizardry.MODID, "handbook_data_parser");

    @Override
    public Dist getSupportedDist() {
        return Dist.CLIENT;
    }

    @Override
    public Identifier getIdentifier() {
        return PARSER_NAME;
    }

    @Override
    public HandbookData parserItem(JsonElement json) {
        return HandbookData.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> Wizardry.LOGGER.error("手札解析错误: {}", error))
                .orElse(null);
    }
}
