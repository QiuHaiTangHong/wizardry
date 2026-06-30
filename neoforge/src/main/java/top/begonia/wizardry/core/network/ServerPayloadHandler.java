package top.begonia.wizardry.core.network;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.util.GlyphGenerator;
import top.begonia.wizardry.core.data.network.handbook.HandbookRecipesRequest;
import top.begonia.wizardry.core.data.network.handbook.HandbookRecipesResult;
import top.begonia.wizardry.core.inventory.menu.ArcaneWorkbenchMenu;
import top.begonia.wizardry.core.item.ISpellCastingItem;
import top.begonia.wizardry.core.network.data.ControlInputPayload;
import top.begonia.wizardry.core.network.data.GlyphDataPayload;
import top.begonia.wizardry.core.network.data.SpellQuickAccessPayload;

import java.util.*;

public class ServerPayloadHandler {
    public static void handleRequest(final HandbookRecipesRequest data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                RecipeManager recipeManager = player.level().getServer().getRecipeManager();
                Map<Identifier, List<RecipeDisplay>> allDisplays = new HashMap<>();
                data.recipes().forEach((_, recipeData) -> recipeData.locations().forEach(identifier -> {
                    ResourceKey<Recipe<?>> recipeResourceKey = ResourceKey.create(Registries.RECIPE, identifier);
                    recipeManager.byKey(recipeResourceKey).ifPresent(recipeHolder -> {
                        List<RecipeDisplay> recipeDisplays = recipeHolder.value().display();
                        allDisplays.put(identifier, recipeDisplays);
                    });
                }));

                if (!allDisplays.isEmpty()) {
                    context.reply(new HandbookRecipesResult(allDisplays));
                    Wizardry.LOGGER.info("已完成手册配方数据收集，统一同步至客户端。共计 {} 个配方。", allDisplays.size());
                }
            }
        });
    }

    public static void handleGlyphData(final GlyphDataPayload payload, final @NonNull IPayloadContext context) {
        context.enqueueWork(() -> {
            GlyphGenerator.update(payload.names(), payload.descriptions());
            Wizardry.LOGGER.info("已同步咒语乱码数据至客户端。");
        });
    }

    public static void handleSpellQuickAccess(final SpellQuickAccessPayload payload, final @NonNull IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            ItemStack wand = player.getMainHandItem();
            if (!(wand.getItem() instanceof ISpellCastingItem)) {
                wand = player.getOffhandItem();
            }
            if (wand.getItem() instanceof ISpellCastingItem iSpellCastingItem) {
                iSpellCastingItem.selectSpell(wand, payload.index());
                player.stopUsingItem();
            }
        }
    }

    public static void handleControlInput(final ControlInputPayload payload, final @NonNull IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ItemStack wand = player.getMainHandItem();

            if (!(wand.getItem() instanceof ISpellCastingItem)) {
                wand = player.getOffhandItem();
            }
            switch (payload.controlType()) {

                case APPLY_BUTTON:

                    if (!(player.containerMenu instanceof ArcaneWorkbenchMenu arcaneWorkbenchMenu)) {
                        Wizardry.LOGGER.warn("Received a PacketControlInput, but the player that sent it was not " +
                                "currently using an arcane workbench. This should not happen!");
                    } else {
                        arcaneWorkbenchMenu.onApplyButtonPressed(player);
                    }

                    break;

                case CLEAR_BUTTON:

                    if (!(player.containerMenu instanceof ArcaneWorkbenchMenu arcaneWorkbenchMenu)) {
                        Wizardry.LOGGER.warn("Received a PacketControlInput, but the player that sent it was not " +
                                "currently using an arcane workbench. This should not happen!");
                    } else {
                        arcaneWorkbenchMenu.onClearButtonPressed(player);
                    }

                    break;

                case NEXT_SPELL_KEY:

                    if (wand.getItem() instanceof ISpellCastingItem iSpellCastingItem) {

                        iSpellCastingItem.selectNextSpell(wand);
                        // This line fixes the bug with continuous spells casting when they shouldn't be
                        player.stopUsingItem();
                    }

                    break;

                case PREVIOUS_SPELL_KEY:

                    if (wand.getItem() instanceof ISpellCastingItem iSpellCastingItem) {

                        iSpellCastingItem.selectPreviousSpell(wand);
                        // This line fixes the bug with continuous spells casting when they shouldn't be
                        player.stopUsingItem();
                    }

                    break;

                case RESURRECT_BUTTON:

                    Wizardry.LOGGER.warn("Received a resurrect button packet, but the player that sent it was not" +
                            " currently able to resurrect. This should not happen!");
                    break;
            }
        });
    }
}
