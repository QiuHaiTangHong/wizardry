package top.begonia.wizardry.core.data.runtime;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 施法上下文流对象, 用于构建和管理施法过程中的上下文参数
 * <p> 该类封装了施法所需的各项参数, 包括效能, 消耗, 蓄力时间和进程值, 以及施法者和施法工具等信息
 * 通过流式 API (Builder Pattern) 动态调整这些参数, 并支持通过物品升级对施法效果进行乘数调整
 * 使用示例:
 * <pre>{@code
 * SpellContextFlow.create(player, hand, stack)
 *     .potency(1.5f).cost(0.8f)
 *     .upgrade(upgradeItem, 1.2f)
 *     .packing()*}</pre>
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SpellContextFlow {
    private float potency = 1.0f;
    private float cost = 1.0f;
    private float chargeUp = 1.0f;
    private float progression = 1.0f;

    private final Map<String, Float> multiplyProperties = new HashMap<>();

    private SpellContextFlow() {
    }

    public static @NonNull SpellContextFlow create() {
        return new SpellContextFlow();
    }

    public @NonNull SpellContextFlow accept(@NonNull SpellContextOperation spellContextBuilder) {
        spellContextBuilder.configure(new BuilderImpl());
        return this;
    }

    @Contract(" -> new")
    public @NonNull SpellContext packing() {
        return new SpellContext(
                this.potency,
                this.cost,
                this.chargeUp,
                this.progression,
                this.multiplyProperties
        );
    }

    private final class BuilderImpl implements SpellContextOperation.Builder {
        @Override
        public SpellContextOperation.@NonNull Builder potency(float value) {
            SpellContextFlow.this.potency = value;
            return this;
        }

        @Override
        public SpellContextOperation.@NonNull Builder cost(float value) {
            SpellContextFlow.this.cost = value;
            return this;
        }

        @Override
        public SpellContextOperation.@NonNull Builder chargeUp(float value) {
            SpellContextFlow.this.chargeUp = value;
            return this;
        }

        @Override
        public SpellContextOperation.@NonNull Builder progression(float value) {
            SpellContextFlow.this.progression = value;
            return this;
        }

        @Override
        public SpellContextOperation.@NonNull Builder multiplyPotency(float multiplier) {
            SpellContextFlow.this.potency *= multiplier;
            return this;
        }

        @Override
        public SpellContextOperation.@NonNull Builder multiplyCost(float multiplier) {
            SpellContextFlow.this.cost *= multiplier;
            return this;
        }

        @Override
        public SpellContextOperation.@NonNull Builder upgrade(@NonNull Item upgradeItem, float multiplier) {
            String key = BuiltInRegistries.ITEM.getKey(upgradeItem).toString();
            return this.upgrade(key, multiplier);
        }

        @Override
        public SpellContextOperation.@NonNull Builder upgrade(String key, float multiplier) {
            SpellContextFlow.this.multiplyProperties.merge(key, multiplier, (a, b) -> a * b);
            return this;
        }
    }

    public float potency() {
        return this.potency;
    }

    public float cost() {
        return this.cost;
    }

    public float chargeUp() {
        return this.chargeUp;
    }

    public float progression() {
        return this.progression;
    }

    public float multiplyProperties(String key) {
        return this.multiplyProperties.getOrDefault(key, 1.0f);
    }

    @FunctionalInterface
    public interface SpellContextOperation {
        void configure(@NonNull Builder builder);

        interface Builder {
            @NonNull Builder potency(float value);

            @NonNull Builder cost(float value);

            @NonNull Builder chargeUp(float value);

            @NonNull Builder progression(float value);

            @NonNull Builder multiplyPotency(float multiplier);

            @NonNull Builder multiplyCost(float multiplier);

            @NonNull Builder upgrade(@NonNull Item upgradeItem, float multiplier);

            @NonNull Builder upgrade(String key, float multiplier);
        }
    }
}
