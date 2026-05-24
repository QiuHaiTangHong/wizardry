package top.begonia.wizardry.core.data.spell.parser;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.api.data.IStaticDataParser;
import top.begonia.wizardry.core.data.spell.definition.spell.SpellProperties;

public class SpellPropertiesParser implements IStaticDataParser<SpellProperties> {
    public static final Identifier PARSER_NAME = Identifier.fromNamespaceAndPath(Wizardry.MODID, "spell_properties_parser");

    @Override
    public Dist getSupportedDist() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public Identifier getIdentifier() {
        return PARSER_NAME;
    }

    @Override
    public SpellProperties parserItem(JsonElement json) {
        return SpellProperties.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> Wizardry.LOGGER.error("法术配置解析出错: {}", error))
                .orElse(null);
    }
}
