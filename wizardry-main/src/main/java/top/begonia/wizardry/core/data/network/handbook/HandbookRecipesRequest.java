package top.begonia.wizardry.core.data.network.handbook;

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

public record HandbookRecipesRequest(Map<String, RecipeTagData> recipes) implements CustomPacketPayload {

    public static final Type<HandbookRecipesRequest> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Wizardry.MODID, "sync_client_recipes"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HandbookRecipesRequest> STREAM_CODEC = ByteBufCodecs.map(
            HashMap::new,
            ByteBufCodecs.STRING_UTF8,
            RecipeTagData.STREAM_CODEC
    ).map(
            HandbookRecipesRequest::new,
            payload -> {
                Map<String, RecipeTagData> map = payload.recipes();
                if (map instanceof HashMap<String, RecipeTagData> hashMap) {
                    return hashMap;
                }
                return new HashMap<>(map);
            }
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
