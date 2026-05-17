package top.begonia.wizardry.server.network;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.server.data.handbook.HandbookRecipesRequest;
import top.begonia.wizardry.server.data.handbook.HandbookRecipesResult;

import java.util.*;

public class ServerPayloadHandler {
    public static void handleRequest(final HandbookRecipesRequest data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                RecipeManager recipeManager = player.level().getServer().getRecipeManager();
                Map<Identifier, List<RecipeDisplay>> allDisplays = new HashMap<>();
                data.recipes().forEach((_, recipeData) -> {
                    recipeData.locations().forEach(identifier -> {
                        ResourceKey<Recipe<?>> recipeResourceKey = ResourceKey.create(Registries.RECIPE, identifier);
                        recipeManager.byKey(recipeResourceKey).ifPresent(recipeHolder -> {
                            List<RecipeDisplay> recipeDisplays = recipeHolder.value().display();
                            allDisplays.put(identifier, recipeDisplays);
                        });
                    });
                });

                if (!allDisplays.isEmpty()) {
                    context.reply(new HandbookRecipesResult(allDisplays));
                    Wizardry.LOGGER.info("已完成手册配方数据收集，统一同步至客户端。共计 {} 个配方。", allDisplays.size());
                }
            }
        });
    }
}
