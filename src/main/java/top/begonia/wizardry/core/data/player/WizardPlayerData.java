package top.begonia.wizardry.core.data.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.spell.impl.None;

import java.util.*;

public class WizardPlayerData {
    private final Set<AbstractSpell> spellsDiscovered;
    private TierEnum maxTierReached;
    private final Set<UUID> allies;
    private final Set<String> allyNames;
    private final Map<String, Integer> imbuementDurations;
    private final List<RecentSpellEntry> recentSpells;

    public WizardPlayerData() {
        this.spellsDiscovered = new HashSet<>(List.of(WizardrySpells.MAGIC_MISSILE.get()));
        this.maxTierReached = TierEnum.NOVICE;
        this.allies = new HashSet<>();
        this.allyNames = new HashSet<>();
        this.imbuementDurations = new HashMap<>();
        this.recentSpells = new ArrayList<>();
    }

    public WizardPlayerData(
            Set<AbstractSpell> spellsDiscovered,
            TierEnum maxTierReached,
            Set<UUID> allies,
            Set<String> allyNames,
            Map<String, Integer> imbuementDurations,
            List<RecentSpellEntry> recentSpells
    ) {
        this.spellsDiscovered = new HashSet<>(spellsDiscovered);
        this.maxTierReached = maxTierReached;
        this.allies = new HashSet<>(allies);
        this.allyNames = new HashSet<>(allyNames);
        this.imbuementDurations = new HashMap<>(imbuementDurations);
        this.recentSpells = new ArrayList<>(recentSpells);
    }

    public boolean discoverSpell(AbstractSpell spell) {
        if (spell instanceof None) {
            return false;
        }
        return spellsDiscovered.add(spell);
    }

    @Contract(" -> new")
    public static @NonNull WizardPlayerData getDefault() {
        return new WizardPlayerData();
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

    public boolean hasSpellBeenDiscovered(AbstractSpell spell) {
        return this.spellsDiscovered.contains(spell) || spell instanceof None;
    }

    public Set<AbstractSpell> spellsDiscovered() {
        return this.spellsDiscovered;
    }

    public TierEnum maxTierReached() {
        return this.maxTierReached;
    }

    public void setMaxTierReached(TierEnum tier) {
        this.maxTierReached = tier;
    }

    public Set<UUID> allies() {
        return this.allies;
    }

    public Set<String> allyNames() {
        return this.allyNames;
    }

    public Map<String, Integer> imbuementDurations() {
        return this.imbuementDurations;
    }

    public List<RecentSpellEntry> recentSpells() {
        return this.recentSpells;
    }

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
