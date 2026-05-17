package top.begonia.wizardry.client.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.IData;
import top.begonia.wizardry.client.data.parser.IDataParser;

import java.io.Reader;
import java.util.*;

public class WizardryDataManager extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {
    private final Map<Identifier, IDataParser<?>> parserRegistry = new HashMap<>();
    private static volatile Map<Class<? extends IData>, Map<Identifier, ? extends IData>> storageSnapshot = Map.of();
    private final static Codec<JsonElement> CODEC = Codec.PASSTHROUGH.xmap(
            dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(),
            json -> new Dynamic<>(JsonOps.INSTANCE, json)
    );
    private final FileToIdConverter lister;
    public static final WizardryDataManager INSTANCE = new WizardryDataManager();

    private WizardryDataManager() {
        this.loadParsersSPI();
        this.lister = FileToIdConverter.json("custom");
    }

    @Override
    protected @NonNull Map<Identifier, JsonElement> prepare(@NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profilerFiller) {
        Map<Identifier, JsonElement> result = new HashMap<>();
        String currentLang = Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase(Locale.ROOT);
        String expectedLanguagePath = "texts/" + currentLang + "/";
        String langPrefix = "texts/" + currentLang + "/";
        Codec<Optional<JsonElement>> conditionalCodec = ConditionalOps.createConditionalCodec(CODEC);
        for (Map.Entry<Identifier, Resource> entry : this.lister.listMatchingResources(resourceManager).entrySet()) {
            Identifier location = entry.getKey();
            String path = location.getPath();
            if (path.contains("texts/")) {
                if (!path.contains(expectedLanguagePath)) {
                    continue;
                }
            }
            Identifier originalId = this.lister.fileToId(location);
            Identifier finalId;
            String originalPath = originalId.getPath();
            if (originalPath.startsWith(langPrefix)) {
                finalId = originalId.withPath(originalPath.substring(langPrefix.length()));
            } else {
                finalId = originalId;
            }
            try (Reader reader = entry.getValue().openAsReader()) {
                conditionalCodec.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader)).ifSuccess((parsed) -> {
                    if (parsed.isEmpty()) {
                        Wizardry.LOGGER.debug("跳过加载数据文件 '{}'，因为不满足 NeoForge 加载条件", finalId);
                    } else if (result.putIfAbsent(finalId, parsed.get()) != null) {
                        throw new IllegalStateException("重复的 Wizardry 数据文件被忽略，洗涤后 ID: " + finalId);
                    }
                }).ifError((error) -> Wizardry.LOGGER.error("无法解析数据文件 '{}': {}", finalId, error));
            } catch (Exception e) {
                Wizardry.LOGGER.error("从物理路径 '{}' 解析 Wizardry 数据时发生异常", location, e);
            }
        }

        return result;
    }

    private void loadParsersSPI() {
        @SuppressWarnings("rawtypes")
        ServiceLoader<IDataParser> loader = ServiceLoader.load(IDataParser.class, IDataParser.class.getClassLoader());
        for (IDataParser<?> parser : loader) {
            Identifier id = parser.getIdentifier();
            if (id != null) {
                parserRegistry.put(id, parser);
                Wizardry.LOGGER.info("已注册 Wizardry 解析器: {} -> {}", id, parser.getClass().getName());
            }
        }
    }

    public static <T extends IData> Optional<T> getData(Identifier id, Class<T> expectedType) {
        Map<Class<? extends IData>, Map<Identifier, ? extends IData>> currentStorage = storageSnapshot;
        Map<Identifier, ? extends IData> typeMap = currentStorage.get(expectedType);
        if (typeMap != null) {
            Object data = typeMap.get(id);
            if (expectedType.isInstance(data)) {
                return Optional.of(expectedType.cast(data));
            }
        }
        return Optional.empty();
    }

    @Override
    protected void apply(@NonNull Map<Identifier, JsonElement> resources, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profilerFiller) {
        Map<Class<? extends IData>, Map<Identifier, IData>> workingMap = new HashMap<>();
        int[] totalLoaded = new int[]{0};
        resources.forEach((location, element) -> {
            if (!element.isJsonObject()) return;
            JsonObject jsonObject = element.getAsJsonObject();
            if (!jsonObject.has("parser")) return;
            Identifier parserId = Identifier.tryParse(jsonObject.get("parser").getAsString());
            if (parserId == null) return;

            IDataParser<?> parser = parserRegistry.get(parserId);
            if (parser != null) {
                try {
                    IData result = parser.parser(element);
                    if (result != null) {
                        workingMap.computeIfAbsent(result.getDataClass(), _ -> new HashMap<>()).put(location, result);
                        totalLoaded[0]++;
                    }
                } catch (Exception e) {
                    Wizardry.LOGGER.error("解析器 {} 处理文件 {} 时发生崩溃", parser.getClass().getSimpleName(), location, e);
                }
            }
        });
        Map<Class<? extends IData>, Map<Identifier, ? extends IData>> immutableStorage = new HashMap<>();
        workingMap.forEach((clazz, innerMap) -> immutableStorage.put(clazz, Map.copyOf(innerMap)));
        storageSnapshot = Map.copyOf(immutableStorage);
        Wizardry.LOGGER.info(Wizardry.MODID + " 数据重载完成，共通过类型隔离加载了 {} 项新配置", totalLoaded[0]);
    }
}
