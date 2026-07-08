package top.begonia.wizardry.client.network;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.data.network.handbook.HandbookRecipesResult;
import top.begonia.wizardry.core.entity.block.BookshelfBlockEntity;
import top.begonia.wizardry.core.network.data.SyncSlotPayload;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientPayloadHandler {
    /**
     * 显示缓存, 存储配方显示数据
     */
    private static final Map<Identifier, List<RecipeDisplay>> DISPLAY_CACHE = new ConcurrentHashMap<>();

    /**
     * 处理手稿配方结果并更新显示缓存.
     * <p> 此方法将接收到的手稿配方结果解析并存储到全局显示缓存中, 同时在日志中记录同步的配方数量.
     *
     * @param payload 手稿配方结果对象
     * @param context 实体载荷上下文
     */
    public static void handleHandbookRecipesResult(final HandbookRecipesResult payload, final @NonNull IPayloadContext context) {
        context.enqueueWork(() -> {
            DISPLAY_CACHE.putAll(payload.allDisplays());
            Wizardry.LOGGER.info("已成功同步 {} 个配方的显示数据至客户端。", payload.allDisplays().size());
        });
    }

    @Contract(pure = true)
    public static void handleItemResourceHandlerPayload(final @NonNull SyncSlotPayload payload, final @NonNull IPayloadContext context) {
        BlockPos blockPos = payload.blockPos();
        BlockEntity blockEntity = context.player().level().getBlockEntity(blockPos);
        ItemStack itemStack = payload.stack();
        int index = payload.index();
        Wizardry.LOGGER.info("同步服务侧ItemHandlerResource到客户侧...");
        if (blockEntity instanceof BookshelfBlockEntity bookshelfBlockEntity) {
            ItemStacksResourceHandler itemStacksResourceHandler = bookshelfBlockEntity.getInventory();
            itemStacksResourceHandler.set(index, ItemResource.of(itemStack), itemStack.getCount());
        }
    }

    public static List<RecipeDisplay> getDisplays(Identifier id) {
        return DISPLAY_CACHE.get(id);
    }

    public static void clearCache() {
        DISPLAY_CACHE.clear();
    }
}
