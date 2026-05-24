package top.begonia.wizardry.core.api.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.api.data.event.DataParserBefore;

import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class AbstractWizardryDataManager extends ContextAwareReloadListener implements PreparableReloadListener {
    protected final Map<Identifier, IDataParser<?, ? extends IParserContext, ? extends IResultData>> parserRegistry = new HashMap<>();
    protected volatile Map<Class<? extends IResultData>, Map<Identifier, ? extends IResultData>> storageSnapshot = Map.of();
    protected final static Codec<JsonElement> CODEC = Codec.PASSTHROUGH.xmap(
            dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(),
            json -> new Dynamic<>(JsonOps.INSTANCE, json)
    );
    protected final FileToIdConverter lister;

    public AbstractWizardryDataManager(FileToIdConverter path) {
        this.lister = path;
        this.loadParsersSPI();
    }

    private void loadParsersSPI() {
        Dist currentDist = this.getSupportedDist();
        @SuppressWarnings("rawtypes")
        ServiceLoader<IDataParser> loader = ServiceLoader.load(IDataParser.class, IDataParser.class.getClassLoader());
        for (IDataParser<?, ?, ?> parser : loader) {
            Dist targetDist = parser.getSupportedDist();
            if (targetDist != null && targetDist != currentDist) {
                continue;
            }

            Identifier id = parser.getIdentifier();
            if (id != null) {
                parserRegistry.put(id, parser);
                Wizardry.LOGGER.info("已注册 Wizardry 动态解析器 [{}] : {} -> {}",
                        currentDist.name(), id, parser.getClass().getName());
            }
        }
    }

    protected abstract Dist getSupportedDist();

    public <T extends IResultData> Optional<T> getData(Identifier id, Class<T> expectedType) {
        Map<Class<? extends IResultData>, Map<Identifier, ? extends IResultData>> currentStorage = storageSnapshot;
        Map<Identifier, ? extends IResultData> typeMap = currentStorage.get(expectedType);
        if (typeMap != null) {
            Object data = typeMap.get(id);
            if (expectedType.isInstance(data)) {
                return Optional.of(expectedType.cast(data));
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T extends IResultData> Map<Identifier, T> getAllDataByType(Class<T> expectedType) {
        Map<Identifier, ? extends IResultData> typeMap = this.storageSnapshot.get(expectedType);
        if (typeMap != null) {
            return (Map<Identifier, T>) typeMap;
        }
        return Map.of();
    }

    protected boolean listerFilter(Identifier finalId, Identifier location, Resource resource) {
        return true;
    }

    protected Identifier cleanseIdentifier(Identifier originalId) {
        return originalId;
    }

    @Override
    public final @NonNull CompletableFuture<Void> reload(
            PreparableReloadListener.@NonNull SharedState currentReload,
            @NonNull Executor taskExecutor,
            PreparableReloadListener.@NonNull PreparationBarrier preparationBarrier,
            @NonNull Executor reloadExecutor
    ) {
        ResourceManager manager = currentReload.resourceManager();
        CompletableFuture<Map<Identifier, JsonElement>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> this.prepare(manager, Profiler.get()), taskExecutor);
        Objects.requireNonNull(preparationBarrier);
        return mapCompletableFuture.thenCompose(preparationBarrier::wait).thenAcceptAsync((preparations) -> this.apply(preparations, manager, Profiler.get(), currentReload), reloadExecutor);
    }

    protected @NonNull Map<Identifier, JsonElement> prepare(@NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profilerFiller) {
        Map<Identifier, JsonElement> result = new HashMap<>();
        Codec<Optional<JsonElement>> conditionalCodec = ConditionalOps.createConditionalCodec(CODEC);
        for (Map.Entry<Identifier, Resource> entry : this.lister.listMatchingResources(resourceManager).entrySet()) {
            Identifier location = entry.getKey();
            Identifier originalId = this.lister.fileToId(location);
            Identifier finalId = this.cleanseIdentifier(originalId);
            if (!this.listerFilter(finalId, location, entry.getValue())) {
                Wizardry.LOGGER.debug("资源 '{}' (物理路径: '{}') 未通过 ListerFilter 测试，已跳过", finalId, location);
                continue;
            }
            try (Reader reader = entry.getValue().openAsReader()) {
                conditionalCodec.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader)).ifSuccess((parsed) -> {
                    if (parsed.isEmpty()) {
                        Wizardry.LOGGER.debug("跳过加载数据文件 '{}'，因为不满足 forge:conditional 加载条件", finalId);
                    } else if (result.putIfAbsent(finalId, parsed.get()) != null) {
                        throw new IllegalStateException("重复的 Wizardry 数据文件，洗涤后逻辑 ID 冲突: " + finalId);
                    }
                }).ifError((error) -> Wizardry.LOGGER.error("无法解析数据文件 '{}': {}", finalId, error));
            } catch (Exception e) {
                Wizardry.LOGGER.error("从物理路径 '{}' 解析 Wizardry 数据时发生异常", location, e);
            }
        }

        return result;
    }

    protected void apply(@NonNull Map<Identifier, JsonElement> identifierJsonElementMap, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profilerFiller, PreparableReloadListener.@NonNull SharedState currentReload) {
        Map<Identifier, IParserContext> parserContexts = new HashMap<>();
        ModLoader.postEvent(new DataParserBefore(parserContexts));
        Map<Class<? extends IResultData>, Map<Identifier, IResultData>> workingMap = new HashMap<>();
        int[] totalLoaded = new int[]{0};
        int failedCount = 0;
        for (Map.Entry<Identifier, JsonElement> entry : identifierJsonElementMap.entrySet()) {
            Identifier location = entry.getKey();
            JsonElement element = entry.getValue();
            if (!element.isJsonObject()) {
                Wizardry.LOGGER.warn("wizardry:拒绝解析数据 '{}': 根节点必须是一个 JSON 对象", location);
                failedCount++;
                continue;
            }
            JsonObject jsonObject = element.getAsJsonObject();
            if (!jsonObject.has("parser")) {
                Wizardry.LOGGER.warn("wizardry:拒绝解析数据 '{}': 找不到必填的 \"parser\" 标识字段", location);
                failedCount++;
                continue;
            }
            String parserStr = jsonObject.get("parser").getAsString();
            Identifier parserId = Identifier.tryParse(parserStr);
            if (parserId == null) {
                Wizardry.LOGGER.error("wizardry:解析数据 '{}' 失败: 声明的 parser 命名空间格式非法 -> '{}'", location, parserStr);
                failedCount++;
                continue;
            }
            @SuppressWarnings("rawtypes")
            IDataParser parser = parserRegistry.get(parserId);
            IParserContext context = parserContexts.get(parserId);
            if (parser == null) {
                Wizardry.LOGGER.error("wizardry:解析数据 '{}' 失败: 找不到已注册的解析器 '{}'。请检查 SPI 配置或当前端(Dist)是否支持！", location, parserId);
                failedCount++;
                continue;
            }
            try {
                @SuppressWarnings("unchecked")
                IResultData result = this.safeExecutePipeline(location, parser, element, context, currentReload);
                if (result == null) {
                    Wizardry.LOGGER.error("wizardry:解析数据 '{}' 失败: 解析器 [{}] 无法反序列化此数据，返回了 null (请检查控制台上的 Codec 具体报错)",
                            parser.getClass().getSimpleName(), location);
                    failedCount++;
                    continue;
                }
                workingMap.computeIfAbsent(result.getDataClass(), _ -> new HashMap<>()).put(location, result);
                totalLoaded[0]++;

            } catch (Exception e) {
                Wizardry.LOGGER.error("wizardry:解析数据 '{}' 时发生严重崩溃！正在使用的解析器: [{}]",
                        location, parser.getClass().getName(), e);
                failedCount++;
            }
        }
        Map<Class<? extends IResultData>, Map<Identifier, ? extends IResultData>> immutableStorage = new HashMap<>();
        workingMap.forEach((clazz, innerMap) -> immutableStorage.put(clazz, Map.copyOf(innerMap)));
        storageSnapshot = Map.copyOf(immutableStorage);
        if (failedCount > 0) {
            Wizardry.LOGGER.warn("{} 数据重载完成: 成功加载 {} 项配置，🌟 发现 {} 项配置解析失败（请查看上方警告日志）",
                    Wizardry.MODID, totalLoaded[0], failedCount);
        } else {
            Wizardry.LOGGER.info("{} 数据重载完成: 共通过类型隔离成功加载了 {} 项新配置",
                    Wizardry.MODID, totalLoaded[0]);
        }
    }

    private <P, C extends IParserContext, R extends IResultData> R safeExecutePipeline(
            Identifier identifier,
            @NonNull IDataParser<P, C, R> parser,
            JsonElement element,
            @Nullable C context,
            PreparableReloadListener.SharedState currentReload
    ) {
        P rawData = parser.parserItem(element);
        return parser.transformItemToResult(identifier, rawData, context, currentReload);
    }
}
