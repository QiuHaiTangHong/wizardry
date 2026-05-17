package top.begonia.wizardry.common.data.spell;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.common.spell.AbstractSpell;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class SpellPropertiesLoader extends SimpleJsonResourceReloadListener<SpellPropertiesData> {
    public static final ResourceKey<Registry<SpellPropertiesData>> SPELLS_KEY =
            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(Wizardry.MODID, "spells"));

    public SpellPropertiesLoader(HolderLookup.Provider registries) {
        super(
                registries,
                SpellPropertiesData.CODEC,
                SPELLS_KEY
        );
    }

    @Override
    protected @NonNull Map<Identifier, SpellPropertiesData> prepare(@NonNull ResourceManager manager, @NonNull ProfilerFiller profiler) {
        Map<Identifier, SpellPropertiesData> map = new HashMap<>();
        FileToIdConverter lister = FileToIdConverter.json("spells");

        for (Map.Entry<Identifier, Resource> entry : lister.listMatchingResources(manager).entrySet()) {
            Identifier id = lister.fileToId(entry.getKey());
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                SpellPropertiesData.CODEC.parse(JsonOps.INSTANCE, json)
                        .ifSuccess(prop -> map.put(id, prop))
                        .ifError(err -> Wizardry.LOGGER.error("解析法术 {} 失败: {}", id, err.message()));
            } catch (Exception e) {
                Wizardry.LOGGER.error("读取法术文件失败: {}", id, e);
            }
        }
        return map;
    }

    @Override
    protected void apply(
            @NonNull Map<Identifier, SpellPropertiesData> spellProperties,
            @NonNull ResourceManager resourceManager,
            @NonNull ProfilerFiller profilerFiller) {
        AbstractSpell.spellProperties = ImmutableMap.copyOf(spellProperties);
        Wizardry.LOGGER.info("Codec 成功解析并加载的法术总数: {}", spellProperties.size());
    }
}
