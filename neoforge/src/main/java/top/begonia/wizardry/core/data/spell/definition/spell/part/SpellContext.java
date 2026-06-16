package top.begonia.wizardry.core.data.spell.definition.spell.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.HashMap;
import java.util.Map;

public record SpellContext(
        float potency,
        float cost,
        float chargeUp,
        float progression,
        Map<String, Float> multiplyProperties
) {
    public static final SpellContext DEFAULT = new SpellContext(1.0f, 1.0f, 1.0f, 1.0f, Map.of());
    public static final Codec<SpellContext> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("potency", 1.0f).forGetter(SpellContext::potency),
            Codec.FLOAT.optionalFieldOf("cost", 1.0f).forGetter(SpellContext::cost),
            Codec.FLOAT.optionalFieldOf("charge_up", 1.0f).forGetter(SpellContext::chargeUp),
            Codec.FLOAT.optionalFieldOf("progression", 1.0f).forGetter(SpellContext::progression),
            Codec.unboundedMap(Codec.STRING, Codec.FLOAT).optionalFieldOf("multiplyProperties", Map.of()).forGetter(SpellContext::multiplyProperties)
    ).apply(instance, SpellContext::new));

    public static final StreamCodec<FriendlyByteBuf, SpellContext> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, SpellContext::potency,
            ByteBufCodecs.FLOAT, SpellContext::cost,
            ByteBufCodecs.FLOAT, SpellContext::chargeUp,
            ByteBufCodecs.FLOAT, SpellContext::progression,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.FLOAT), SpellContext::multiplyProperties,
            SpellContext::new
    );

    public float getWandUpgrade(Item itemWand) {
        return this.multiplyProperties.getOrDefault(itemWand.getDescriptionId(), 1.0f);
    }
}
