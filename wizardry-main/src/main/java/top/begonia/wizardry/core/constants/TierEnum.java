package top.begonia.wizardry.core.constants;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.config.CommonConfig;

import java.util.Random;
import java.util.function.IntSupplier;

public enum TierEnum implements StringRepresentable {

    NOVICE(() -> CommonConfig.noviceMaxCharge, () -> CommonConfig.noviceUpgradeLimit, 12, Style.EMPTY.withColor(ChatFormatting.WHITE), "novice"),
    APPRENTICE(() -> CommonConfig.apprenticeMaxCharge, () -> CommonConfig.apprenticeUpgradeLimit, 5, Style.EMPTY.withColor(ChatFormatting.AQUA), "apprentice"),
    ADVANCED(() -> CommonConfig.advancedMaxCharge, () -> CommonConfig.advancedUpgradeLimit, 2, Style.EMPTY.withColor(ChatFormatting.DARK_BLUE), "advanced"),
    MASTER(() -> CommonConfig.masterMaxCharge, () -> CommonConfig.masterUpgradeLimit, 1, Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE), "master");

    public final IntSupplier maxChargeSupplier;
    public final IntSupplier upgradeLimitSupplier;
    public final int level;
    public final int weight;
    private final Style colour;
    private final String unlocalisedName;

    public static final Codec<TierEnum> CODEC = StringRepresentable.fromEnum(TierEnum::values);
    public static final StreamCodec<RegistryFriendlyByteBuf, TierEnum> STREAM_CODEC = ByteBufCodecs.idMapper(
            id -> id >= 0 && id < TierEnum.values().length ? TierEnum.values()[id] : TierEnum.NOVICE,
            TierEnum::ordinal
    ).cast();
    public static final TierEnum DEFAULT = TierEnum.NOVICE;

    TierEnum(IntSupplier maxChargeSupplier, IntSupplier upgradeLimitSupplier, int weight, Style colour, String name) {
        this.maxChargeSupplier = maxChargeSupplier;
        this.upgradeLimitSupplier = upgradeLimitSupplier;
        this.level = ordinal();
        this.weight = weight;
        this.colour = colour;
        this.unlocalisedName = name;
    }

    public int getMaxCharge() {
        return this.maxChargeSupplier.getAsInt();
    }

    public int getUpgradeLimit() {
        return this.upgradeLimitSupplier.getAsInt();
    }

    public static @NonNull TierEnum fromName(String name) {

        for (TierEnum tier : values()) {
            if (tier.unlocalisedName.equals(name)) return tier;
        }

        throw new IllegalArgumentException("No such tier with unlocalised name: " + name);
    }

    public TierEnum next() {
        return ordinal() + 1 < values().length ? values()[ordinal() + 1] : this;
    }

    public TierEnum previous() {
        return ordinal() > 0 ? values()[ordinal() - 1] : this;
    }

    @Contract(" -> new")
    public @NonNull MutableComponent getDisplayName() {
        return Component.translatable("tier." + unlocalisedName);
    }

    @Contract(" -> new")
    public @NonNull MutableComponent getNameForTranslation() {
        return Component.translatable("tier." + unlocalisedName);
    }

    public @NonNull MutableComponent getDisplayNameWithFormatting() {
        return Component.translatable("tier." + unlocalisedName).withStyle(this.colour);
    }

    public @NonNull MutableComponent getNameForTranslationFormatted() {
        return Component.translatable("tier." + unlocalisedName).setStyle(this.colour);
    }

    @Contract(pure = true)
    public @NonNull String getFormattingCode() {
        return this.colour.toString();
    }

    public Style getStyle() {
        return this.colour;
    }

    public String getColor() {
        if (colour.getColor() != null) {
            return colour.getColor().serialize();
        }
        return "";
    }

    public int getProgression() {
        return CommonConfig.progressionRequirements[this.ordinal() - 1];
    }

    public static TierEnum getWeightedRandomTier(Random random, TierEnum @NonNull ... tiers) {

        if (tiers.length == 0) tiers = values();

        int totalWeight = 0;

        for (TierEnum tier : tiers) totalWeight += tier.weight;

        int randomiser = random.nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (TierEnum tier : tiers) {
            cumulativeWeight += tier.weight;
            if (randomiser < cumulativeWeight) return tier;
        }

        return tiers[tiers.length - 1];
    }

    @Override
    public @NonNull String getSerializedName() {
        return unlocalisedName;
    }
}
