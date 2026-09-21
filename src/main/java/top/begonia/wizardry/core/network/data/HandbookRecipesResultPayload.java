package top.begonia.wizardry.core.network.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record HandbookRecipesResultPayload(
        Map<Identifier, List<RecipeDisplay>> allDisplays
) implements CustomPacketPayload {
    public static final Type<HandbookRecipesResultPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Wizardry.MODID, "handbook_recipes_result")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HandbookRecipesResultPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    Identifier.STREAM_CODEC,
                    RecipeDisplay.STREAM_CODEC.apply(ByteBufCodecs.list())
            ),
            HandbookRecipesResultPayload::allDisplays,
            HandbookRecipesResultPayload::new
    );
    public static final String VERSION = "1.0.0";

    @Override
    public @NonNull Type<HandbookRecipesResultPayload> type() {
        return TYPE;
    }
}
