package top.begonia.wizardry.common.spell;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.common.data.spell.SpellPropertiesData;
import top.begonia.wizardry.common.data.spell.constants.ElementEnum;
import top.begonia.wizardry.common.data.spell.constants.EnabledEnum;
import top.begonia.wizardry.common.data.spell.constants.SpellTypeEnum;
import top.begonia.wizardry.common.data.spell.constants.TierEnum;
import top.begonia.wizardry.common.registry.WizardryItems;
import top.begonia.wizardry.common.registry.WizardrySpells;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

public abstract class AbstractSpell implements Comparable<AbstractSpell> {
    public static ImmutableMap<Identifier, SpellPropertiesData> spellProperties;
    private SpellPropertiesData properties;
    public final ItemUseAnimation action;
    public final boolean isContinuous;
    private final Identifier icon;
    private boolean enabled = true;
    @Nullable
    protected final Identifier[] sounds;
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
        this.action = action;
        this.isContinuous = isContinuous;
        this.icon = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/spells/" + this.identifier.getPath() + ".png");
        this.items(WizardryItems.SPELL_BOOK.get(), WizardryItems.SCROLL.get());
        this.npcSelector((e, o) -> false);
        this.sounds = createSounds();
    }

    protected Identifier[] createSounds() {
        return new Identifier[]{this.identifier.withPrefix("spell.")};
    }

    public SpellPropertiesData getProperties() {
        if (spellProperties == null || !spellProperties.containsKey(this.identifier)) {
            return SpellPropertiesData.getDefault();
        } else if (properties == null) {
            properties = spellProperties.getOrDefault(this.identifier, SpellPropertiesData.getDefault());
            return properties;
        } else {
            return properties;
        }
    }

    public final Identifier getIcon() {
        return icon;
    }

    public final Identifier[] getSounds() {
        return sounds;
    }

    public final TierEnum getTier() {
        return getProperties().tier();
    }

    public final ElementEnum getElement() {
        return getProperties().element();
    }

    public final SpellTypeEnum getType() {
        return getProperties().type();
    }

    public final int getCost() {
        return getProperties().cost();
    }

    public final int getChargeup() {
        return getProperties().chargeup();
    }

    public final int getCooldown() {
        return getProperties().cooldown();
    }

    public final boolean isEnabled(EnabledEnum... contexts) {
        return enabled && (contexts.length == 0 || properties.isEnabled(contexts));
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
