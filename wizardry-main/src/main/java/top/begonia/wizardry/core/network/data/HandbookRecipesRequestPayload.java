package top.begonia.wizardry.core.network.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.handbook.part.RecipeTagData;

import java.util.HashMap;
import java.util.Map;

public record HandbookRecipesRequestPayload(
        Map<String, RecipeTagData> recipes
) implements CustomPacketPayload {

    public static final Type<HandbookRecipesRequestPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Wizardry.MODID, "handbook_recipes_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HandbookRecipesRequestPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.STRING_UTF8,
                    RecipeTagData.STREAM_CODEC
            ),
            HandbookRecipesRequestPayload::recipes,
            HandbookRecipesRequestPayload::new
    );
    public static final String VERSION = "1.0.0";

    @Override
    public @NonNull Type<HandbookRecipesRequestPayload> type() {
        return TYPE;
    }
}
