package top.begonia.wizardry.core.spell;

import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.util.ClientHelper;
import top.begonia.wizardry.core.data.spell.WizardryServerDataManager;
import top.begonia.wizardry.core.data.spell.definition.spell.SpellProperties;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.EnabledEnum;
import top.begonia.wizardry.core.constants.SpellTypeEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiPredicate;

public abstract class AbstractSpell implements Comparable<AbstractSpell> {
    public static final String DAMAGE = "damage";
    public static final String RANGE = "range";
    public static final String DURATION = "duration";
    public static final String EFFECT_RADIUS = "effect_radius";
    public static final String BLAST_RADIUS = "blast_radius";
    public static final String EFFECT_DURATION = "effect_duration";
    public static final String EFFECT_STRENGTH = "effect_strength";
    public static final String BURN_DURATION = "burn_duration";
    public static final String DIRECT_DAMAGE = "direct_damage";
    public static final String SPLASH_DAMAGE = "splash_damage";
    public static final String HEALTH = "health";
    public static final String SEEKING_STRENGTH = "seeking_strength";
    public static final String DIRECT_EFFECT_DURATION = "direct_effect_duration";
    public static final String DIRECT_EFFECT_STRENGTH = "direct_effect_strength";
    public static final String SPLASH_EFFECT_DURATION = "splash_effect_duration";
    public static final String SPLASH_EFFECT_STRENGTH = "splash_effect_strength";

    public static final String TIER_MATCH_PREFIX = "tier";
    public static final String ELEMENT_MATCH_PREFIX = "element";
    public static final String TYPE_MATCH_PREFIX = "type";
    public static final String DISCOVERED_MATCH_PREFIX = "discovered";
    public static final String MOD_ID_MATCH_PREFIX = "modid";

    public static final String TIER_MATCH_ALIAS = "t";
    public static final String ELEMENT_MATCH_ALIAS = "e";
    public static final String TYPE_MATCH_ALIAS = "p";
    public static final String DISCOVERED_MATCH_ALIAS = "d";
    public static final String MOD_ID_MATCH_ALIAS = "m";

    public static final String MATCH_CONDITION_SEPARATOR = ";";
    public static final String MATCH_KEY_VALUE_SEPARATOR = "=";
    public static final String MATCH_VALUE_SEPARATOR = ",";

    private SpellProperties properties;
    public final ItemUseAnimation action;
    public final boolean isContinuous;
    private final Identifier icon;
    private boolean enabled = true;
    @Nullable
    protected final List<DeferredHolder<SoundEvent, SoundEvent>> sounds;
    protected float volume = 1;
    protected float pitch = 1;
    protected float pitchVariation = 0;
    protected Item[] applicableItems;
    protected BiPredicate<LivingEntity, Boolean> npcSelector;
    private final Identifier identifier;

    public static final Codec<Holder<AbstractSpell>> CODEC =
            RegistryFixedCodec.create(WizardrySpells.SPELLS_KEY);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<AbstractSpell>> STREAM_CODEC =
            ByteBufCodecs.holderRegistry(WizardrySpells.SPELLS_KEY);

    public AbstractSpell(Identifier identifier, ItemUseAnimation action, boolean isContinuous) {
        this.identifier = identifier;
        this.properties = null;
        this.action = action;
        this.isContinuous = isContinuous;
        this.icon = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/spells/" + this.identifier.getPath() + ".png");
        this.items(WizardryItems.SPELL_BOOK.get(), WizardryItems.SCROLL.get());
        this.npcSelector((e, o) -> false);
        this.sounds = createSounds(Wizardry.MODID, this.identifier.getPath());
    }

    public boolean requiresPacket() {
        return true;
    }

    public boolean canBeCastBy(DispenserBlockEntity dispenser) {
        return false;
    }

    public boolean canBeCastBy(LivingEntity npc, boolean override) {
        return npcSelector.test(npc, override);
    }

    public static List<DeferredHolder<SoundEvent, SoundEvent>> createSounds(String mod_id, String name) {
        List<DeferredHolder<SoundEvent, SoundEvent>> sounds = new ArrayList<>();
//        sounds.add(WizardrySounds.createSound(mod_id, name));
        return sounds;
    }

    public boolean matches(@Nonnull String text) {
        if (text.isEmpty()) return true;
        boolean discovered = ClientHelper.shouldDisplayDiscovered(this, null);
        String[] conditions = text.split(MATCH_CONDITION_SEPARATOR);
        for (String condition : conditions) {
            String[] args = condition.split(MATCH_KEY_VALUE_SEPARATOR, 2);
            if (args.length < 2) {
                return discovered && getDisplayName().getString().toLowerCase(Locale.ROOT).contains(text);
            }
            String key = args[0];
            String[] values = args[1].split(MATCH_VALUE_SEPARATOR);
            String target;
            switch (key) {
                case TIER_MATCH_PREFIX:
                case TIER_MATCH_ALIAS:
                    target = getTier().getSerializedName().toLowerCase(Locale.ROOT);
                    break;
                case ELEMENT_MATCH_PREFIX:
                case ELEMENT_MATCH_ALIAS:
                    if (!discovered) {
                        return false;
                    }
                    target = getElement().getSerializedName().toLowerCase(Locale.ROOT);
                    break;
                case TYPE_MATCH_PREFIX:
                case TYPE_MATCH_ALIAS:
                    if (!discovered) {
                        return false;
                    }
                    target = getType().getSerializedName().toLowerCase(Locale.ROOT);
                    break;
                case MOD_ID_MATCH_PREFIX:
                case MOD_ID_MATCH_ALIAS:
                    if (!discovered) {
                        return false;
                    }
                    target = getIdentifier().getNamespace().toLowerCase(Locale.ROOT);
                    break;
                case DISCOVERED_MATCH_PREFIX:
                case DISCOVERED_MATCH_ALIAS:
                    target = Boolean.toString(discovered);
                    break;
                default:
                    return discovered && getDisplayName().getString().toLowerCase(Locale.ROOT).contains(text);
            }

            if (Arrays.stream(values).noneMatch(target::contains)) {
                return false;
            }

        }

        return true;
    }

    private SpellProperties getProperties() {
        if (this.properties == null) {
            Optional<SpellProperties> spellProperties = WizardryServerDataManager.INSTANCE.getData(this.identifier.withPrefix("spells/"), SpellProperties.class);
            spellProperties.ifPresentOrElse(
                    value -> this.properties = value,
                    () -> {
                        Wizardry.LOGGER.warn("Missing SpellProperties for Spell {}", this.identifier.withPrefix("spells/"));
                        this.properties = SpellProperties.DEFAULT;
                    }
            );
        }
        return this.properties;
    }

    public float getBaseProperty(String propertyName) {
        if (this.getProperties().baseAttributes().baseProperties().containsKey(propertyName)) {
            return this.getProperties().baseAttributes().baseProperties().get(propertyName);
        }
        return 0;
    }

    public boolean cast(Level level, Player caster, InteractionHand hand, int ticksInUse, SpellContext context) {
        return false;
    }

    public boolean cast(Level level, Mob caster, InteractionHand hand, int ticksInUse, LivingEntity target, SpellContext context) {
        return false;
    }

    public boolean cast(Level level, double x, double y, double z, Direction direction, int ticksInUse, int duration, SpellContext context) {
        return false;
    }

    protected void playSound(Level level, @NonNull LivingEntity entity, int ticksInUse, int duration, SpellContext context) {
        if (!entity.isSilent()) {
            this.playSound(level, entity.getX(), entity.getY(), entity.getZ(), ticksInUse, duration, context);
        }
    }

    protected void playSound(Level level, @NonNull Vec3 pos, int ticksInUse, int duration, SpellContext context) {
        this.playSound(level, pos.x(), pos.y(), pos.z(), ticksInUse, duration, context);
    }

    protected void playSound(Level level, double x, double y, double z, int ticksInUse, int duration, SpellContext context) {
        if (this.sounds != null) {
            for (DeferredHolder<SoundEvent, SoundEvent> soundHolder : this.sounds) {
                if (soundHolder.isBound()) {
                    SoundEvent sound = soundHolder.get();
                    level.playSound(
                            null,
                            x, y, z,
                            sound,
                            WizardrySounds.SPELLS,
                            volume,
                            pitch + pitchVariation * (level.getRandom().nextFloat() - 0.5f)
                    );
                }
            }
        }
    }

    public final Identifier getIcon() {
        return icon;
    }

    public final @Nullable List<DeferredHolder<SoundEvent, SoundEvent>> getSounds() {
        return sounds;
    }

    public final TierEnum getTier() {
        return this.getProperties().tier();
    }

    public final ElementEnum getElement() {
        return this.getProperties().element();
    }

    public final SpellTypeEnum getType() {
        return this.getProperties().type();
    }

    public final int getCost() {
        return this.getProperties().cost();
    }

    public final int getChargeUp() {
        return this.getProperties().chargeUp();
    }

    public final int getCooldown() {
        return this.getProperties().cooldown();
    }

    public final boolean isEnabled(EnabledEnum... contexts) {
        return enabled && (contexts.length == 0 || this.getProperties().isEnabled(contexts));
    }

    public boolean applicableForItem(Item item) {
        return Arrays.asList(applicableItems).contains(item);
    }

    public List<Item> getApplicableItems() {
        return Arrays.asList(applicableItems);
    }

    public AbstractSpell items(Item... applicableItems) {
        this.applicableItems = applicableItems;
        return this;
    }

    public final void setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
    }

    public AbstractSpell npcSelector(BiPredicate<LivingEntity, Boolean> selector) {
        this.npcSelector = selector;
        return this;
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    protected String getTranslationKey() {
        return "spell." + this.identifier.toLanguageKey();
    }

    public MutableComponent getDisplayName() {
        return Component.translatable(getTranslationKey());
    }

    public MutableComponent getDescription() {
        return Component.translatable(getDescriptionTranslationKey());
    }

    protected String getDescriptionTranslationKey() {
        return "spell." + this.identifier.toLanguageKey() + ".desc";
    }

    public MutableComponent getDisplayNameWithFormatting() {
        return Component.translatable(getTranslationKey()).withStyle(getElement().getStyle());
    }

    public AbstractSpell soundValues(float volume, float pitch, float pitchVariation) {
        this.volume = volume;
        this.pitch = pitch;
        this.pitchVariation = pitchVariation;
        return this;
    }

    @Override
    public int compareTo(@NonNull AbstractSpell spell) {
        if (this.getTier().ordinal() > spell.getTier().ordinal()) {
            return 1;
        } else if (this.getTier().ordinal() < spell.getTier().ordinal()) {
            return -1;
        } else {
            return Integer.compare(this.getElement().ordinal(), spell.getElement().ordinal());
        }
    }
}
