package top.begonia.wizardry.client.data.parser;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.bookshelf.BookshelfBookSettings;

public class BookshelfBookSettingsParser implements IDataParser<BookshelfBookSettings> {
    public static final Identifier PARSER_NAME = Identifier.fromNamespaceAndPath(Wizardry.MODID, "bookshelf_book_settings_parser");

    public BookshelfBookSettingsParser() {
    }

    @Override
    public Identifier getIdentifier() {
        return PARSER_NAME;
    }

    @Override
    public BookshelfBookSettings parser(JsonElement json) {
        return BookshelfBookSettings.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> Wizardry.LOGGER.error("书架配置解析出错: {}", error))
                .orElse(null);
    }
}
