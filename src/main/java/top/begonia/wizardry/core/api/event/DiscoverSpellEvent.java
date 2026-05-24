package top.begonia.wizardry.core.api.event;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.spell.AbstractSpell;

import javax.annotation.Nullable;

public class DiscoverSpellEvent extends Event implements ICancellableEvent {

    private final Player player;
    private final AbstractSpell spell;
    private final Source source;

    public DiscoverSpellEvent(@NonNull Player player, @NonNull AbstractSpell spell, @NonNull Source source) {
        super();
        this.player = player;
        this.spell = spell;
        this.source = source;
    }

    public @NonNull Player getPlayer() {
        return this.player;
    }

    public @NonNull AbstractSpell getSpell() {
        return this.spell;
    }

    public @NonNull Source getSource() {
        return this.source;
    }

    public enum Source implements StringRepresentable {
        /**
         * 盲凑释放未识别的法术卷轴或法杖时意外发现
         */
        CASTING("casting"),
        /**
         * 使用鉴定卷轴（Scroll of Identification）识别后发现
         */
        IDENTIFICATION_SCROLL("identification_scroll"),
        /**
         * 通过管理员命令（如 /wizardry discover）直接解锁
         */
        COMMAND("command"),
        /**
         * 从流浪巫师（Wizard NPC）处交易购买法术书/卷轴时解锁
         */
        PURCHASE("purchase"),
        /**
         * 其他模组联动或未定义途径
         */
        OTHER("other");

        public static final EnumCodec<Source> CODEC = StringRepresentable.fromEnum(Source::values);
        private final String name;

        Source(String name) {
            this.name = name;
        }

        @Override
        public @NonNull String getSerializedName() {
            return this.name;
        }

        public static @Nullable Source byName(String name) {
            return CODEC.byName(name);
        }
    }
}
