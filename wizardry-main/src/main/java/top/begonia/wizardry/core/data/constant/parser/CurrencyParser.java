package top.begonia.wizardry.core.data.constant.parser;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.data.IStaticDataParser;
import top.begonia.wizardry.core.data.constant.definition.currency.Currency;

public class CurrencyParser implements IStaticDataParser<Currency> {
    public static final Identifier PARSER_NAME = Identifier.fromNamespaceAndPath(Wizardry.MODID, "currency_parser");
    @Override
    public Identifier getIdentifier() {
        return PARSER_NAME;
    }

    @Override
    public Currency parserItem(JsonElement json) {
        return Currency.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> Wizardry.LOGGER.error("货币配置解析出错: {}", error))
                .orElse(null);
    }
}
