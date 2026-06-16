package top.begonia.wizardry.client.network;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.data.network.handbook.HandbookRecipesResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientPayloadHandler {
    private static final Map<Identifier, List<RecipeDisplay>> DISPLAY_CACHE = new ConcurrentHashMap<>();

    public static void handleResult(final HandbookRecipesResult payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            DISPLAY_CACHE.putAll(payload.allDisplays());
            Wizardry.LOGGER.info("已成功同步 {} 个配方的显示数据至客户端。", payload.allDisplays().size());
        });
    }

    public static List<RecipeDisplay> getDisplays(Identifier id) {
        return DISPLAY_CACHE.get(id);
    }

    public static Map<Identifier, List<RecipeDisplay>> getDisplayCache() {
        return DISPLAY_CACHE;
    }

    public static void clearCache() {
        DISPLAY_CACHE.clear();
    }
}
