package top.begonia.wizardry.common.data.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import top.begonia.wizardry.common.data.spell.constants.TierEnum;
import top.begonia.wizardry.common.registry.WizardrySpells;
import top.begonia.wizardry.common.spell.AbstractSpell;

import java.util.*;

public record WizardPlayerData(
        Set<AbstractSpell> spellsDiscovered,
        TierEnum maxTierReached,
        Set<UUID> allies,
        Set<String> allyNames,
        Map<String, Integer> imbuementDurations,
        List<RecentSpellEntry> recentSpells
) {
    private static WizardPlayerData defaultInstance;

    public static WizardPlayerData getDefault() {
        if (defaultInstance == null) {
            defaultInstance = new WizardPlayerData(
                    new HashSet<>(List.of(WizardrySpells.MAGIC_MISSILE.get())),
                    TierEnum.NOVICE,
                    new HashSet<>(),
                    new HashSet<>(),
                    new HashMap<>(),
                    new ArrayList<>()
            );
        }
        return defaultInstance;
    }

    public static final MapCodec<WizardPlayerData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WizardrySpells.SPELLS.getRegistry().get().byNameCodec().listOf()
                    .<Set<AbstractSpell>>xmap(HashSet::new, ArrayList::new)
                    .fieldOf("discoveredSpells").forGetter(WizardPlayerData::spellsDiscovered),
            TierEnum.CODEC.fieldOf("maxTierReached").forGetter(WizardPlayerData::maxTierReached),
            UUIDUtil.CODEC.listOf().<Set<UUID>>xmap(HashSet::new, ArrayList::new)
                    .fieldOf("allies").forGetter(WizardPlayerData::allies),
            Codec.STRING.listOf().<Set<String>>xmap(HashSet::new, ArrayList::new)
                    .fieldOf("allyNames").forGetter(WizardPlayerData::allyNames),
            Codec.unboundedMap(Codec.STRING, Codec.INT)
                    .fieldOf("imbuements").forGetter(WizardPlayerData::imbuementDurations),
            RecentSpellEntry.CODEC.listOf()
                    .fieldOf("recentSpells").forGetter(WizardPlayerData::recentSpells)
    ).apply(instance, WizardPlayerData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WizardPlayerData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.registry(WizardrySpells.SPELLS_KEY)),
            WizardPlayerData::spellsDiscovered,
            TierEnum.STREAM_CODEC,
            WizardPlayerData::maxTierReached,
            ByteBufCodecs.collection(HashSet::new, UUIDUtil.STREAM_CODEC),
            WizardPlayerData::allies,
            ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.stringUtf8(32767)),
            WizardPlayerData::allyNames,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.stringUtf8(32767), ByteBufCodecs.VAR_INT),
            WizardPlayerData::imbuementDurations,
            RecentSpellEntry.STREAM_CODEC.apply(ByteBufCodecs.list()),
            WizardPlayerData::recentSpells,
            WizardPlayerData::new
    );

    public record RecentSpellEntry(AbstractSpell spell, long timestamp) {
        public static final Codec<RecentSpellEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                WizardrySpells.SPELLS.getRegistry().get().byNameCodec().fieldOf("spell").forGetter(RecentSpellEntry::spell),
                Codec.LONG.fieldOf("timestamp").forGetter(RecentSpellEntry::timestamp)
        ).apply(inst, RecentSpellEntry::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, RecentSpellEntry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(WizardrySpells.SPELLS_KEY),
                RecentSpellEntry::spell,
                ByteBufCodecs.VAR_LONG,
                RecentSpellEntry::timestamp,
                RecentSpellEntry::new
        );
    }
}
