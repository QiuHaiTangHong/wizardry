package top.begonia.wizardry.client.data.parser;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.particle.ParticleDescriptionData;
import top.begonia.wizardry.core.api.data.IStaticDataParser;

public class ParticleParser implements IStaticDataParser<ParticleDescriptionData> {
    public static final Identifier PARSER_NAME = Identifier.fromNamespaceAndPath(Wizardry.MODID, "particle");

    @Override
    public Identifier getIdentifier() {
        return PARSER_NAME;
    }

    @Override
    public ParticleDescriptionData parserItem(JsonElement json) {
        return ParticleDescriptionData.CODEC
                .parse(JsonOps.INSTANCE, json)
                .getOrThrow(error -> new IllegalArgumentException("粒子 JSON 资产格式非法: " + error));
    }
}
