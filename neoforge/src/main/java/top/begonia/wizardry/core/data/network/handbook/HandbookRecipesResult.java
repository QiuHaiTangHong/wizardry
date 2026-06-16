package top.begonia.wizardry.core.data.network.handbook;

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

public record HandbookRecipesResult(Map<Identifier, List<RecipeDisplay>> allDisplays) implements CustomPacketPayload {
    public static final Type<HandbookRecipesResult> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Wizardry.MODID, "result_recipe"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HandbookRecipesResult> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    Identifier.STREAM_CODEC,
                    RecipeDisplay.STREAM_CODEC.apply(ByteBufCodecs.list())
            ),
            HandbookRecipesResult::allDisplays,
            HandbookRecipesResult::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
