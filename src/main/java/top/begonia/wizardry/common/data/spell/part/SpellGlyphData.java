package top.begonia.wizardry.common.data.spell.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.RandomStringUtils;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.common.network.data.GlyphDataPayload;
import top.begonia.wizardry.common.registry.WizardrySpells;
import top.begonia.wizardry.common.spell.AbstractSpell;

import java.util.*;

@EventBusSubscriber(modid = Wizardry.MODID)
public class SpellGlyphData extends SavedData {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Wizardry.MODID, "glyph_data");
    private final Map<Identifier, String> randomNames = new HashMap<>();
    private final Map<Identifier, String> randomDescriptions = new HashMap<>();

    public static final SavedDataType<SpellGlyphData> TYPE = new SavedDataType<>(
            ID,
            _ -> new SpellGlyphData(),
            SpellGlyphData::codec,
            null
    );

    public static Codec<SpellGlyphData> codec(ServerLevel level) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Identifier.CODEC, Codec.STRING).fieldOf("randomNames").forGetter(d -> d.randomNames),
                Codec.unboundedMap(Identifier.CODEC, Codec.STRING).fieldOf("randomDescriptions").forGetter(d -> d.randomDescriptions)
        ).apply(instance, (names, descs) -> {
            SpellGlyphData data = new SpellGlyphData();
            data.randomNames.putAll(names);
            data.randomDescriptions.putAll(descs);
            return data;
        }));
    }

    public SpellGlyphData() {
    }

    public void generateGlyphNames(Level level) {
        RandomSource random = level.getRandom();
        boolean changed = false;

        for (Holder<AbstractSpell> holder : WizardrySpells.SPELLS.getEntries()) {
            Identifier id = holder.value().getIdentifier();
            if (!randomNames.containsKey(id)) {
                randomNames.put(id, generateRandomName(random));
                changed = true;
            }
            if (!randomDescriptions.containsKey(id)) {
                randomDescriptions.put(id, generateRandomDescription(random));
                changed = true;
            }
        }
        if (changed) {
            this.setDirty();
        }
    }

    private String generateRandomName(RandomSource random) {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < random.nextInt(2) + 2; i++) {
            name
                    .append(RandomStringUtils.random(3 + random.nextInt(5), "abcdefghijklmnopqrstuvwxyz"))
                    .append(" ");
        }

        return name.toString().trim();
    }

    private String generateRandomDescription(RandomSource random) {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < random.nextInt(16) + 8; i++) {
            name
                    .append(RandomStringUtils.random(2 + random.nextInt(7), "abcdefghijklmnopqrstuvwxyz"))
                    .append(" ");
        }
        return name.toString().trim();
    }

    public static SpellGlyphData get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            SpellGlyphData data = serverLevel.getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
            data.generateGlyphNames(serverLevel);
            return data;
        }
        return null;
    }

    public void sync(ServerPlayer player) {
        GlyphDataPayload payload = new GlyphDataPayload(
                new HashMap<>(this.randomNames),
                new HashMap<>(this.randomDescriptions)
        );
        PacketDistributor.sendToPlayer(player, payload);
        Wizardry.LOGGER.info("Synchronising spell glyph data for {}", player.getScoreboardName());
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel
                && serverLevel.dimension() == Level.OVERWORLD) {
            SpellGlyphData.get(serverLevel);
            Wizardry.LOGGER.info("Spell glyph data initialized for the overworld.");
        }
    }
}