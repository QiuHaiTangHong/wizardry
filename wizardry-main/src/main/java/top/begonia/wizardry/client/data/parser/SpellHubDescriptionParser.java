package top.begonia.wizardry.client.data.parser;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.hub.SpellHubDescriptionData;
import top.begonia.wizardry.core.api.data.IStaticDataParser;

public class SpellHubDescriptionParser implements IStaticDataParser<SpellHubDescriptionData> {
    public static final Identifier PARSER_NAME = Identifier.fromNamespaceAndPath(Wizardry.MODID, "spell_hub_description_parser");

    @Override
    public Dist getSupportedDist() {
        return Dist.CLIENT;
    }

    @Override
    public Identifier getIdentifier() {
        return PARSER_NAME;
    }

    @Override
    public SpellHubDescriptionData parserItem(JsonElement json) {
        return SpellHubDescriptionData.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> Wizardry.LOGGER.error("法术HUB描述数据解析错误: {}", error))
                .orElse(null);
    }
}
