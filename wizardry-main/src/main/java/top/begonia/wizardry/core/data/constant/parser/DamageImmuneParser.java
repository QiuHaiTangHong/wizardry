package top.begonia.wizardry.core.data.constant.parser;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.data.IStaticDataParser;
import top.begonia.wizardry.core.data.constant.definition.DamageImmune;

public class DamageImmuneParser implements IStaticDataParser<DamageImmune> {
    public static final Identifier PARSER_NAME = Identifier.fromNamespaceAndPath(Wizardry.MODID, "damage_immune_parser");

    @Override
    public Identifier getIdentifier() {
        return PARSER_NAME;
    }

    @Override
    public DamageImmune parserItem(JsonElement json) {
        return DamageImmune.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> Wizardry.LOGGER.error("伤害免疫配置解析出错: {}", error))
                .orElse(null);
    }
}
