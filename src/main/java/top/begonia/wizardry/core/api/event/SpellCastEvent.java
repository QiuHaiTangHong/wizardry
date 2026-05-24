package top.begonia.wizardry.core.api.event;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.data.runtime.SpellContextFlow;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.spell.AbstractSpell;

import javax.annotation.Nullable;

public abstract class SpellCastEvent extends Event {

    private final AbstractSpell spell;
    private final Source source;
    @Nullable
    private final LivingEntity caster;
    private final Level level;
    private final double x, y, z;
    private final Direction direction;

    public SpellCastEvent(Source source, AbstractSpell spell, @NonNull LivingEntity caster) {
        super();
        this.spell = spell;
        this.source = source;
        this.caster = caster;
        this.level = caster.level();
        this.x = caster.getX();
        this.y = caster.getY();
        this.z = caster.getZ();
        this.direction = caster.getDirection();
    }

    public SpellCastEvent(Source source, AbstractSpell spell, Level level, double x, double y, double z, Direction direction) {
        super();
        this.spell = spell;
        this.source = source;
        this.caster = null;
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = direction;
    }

    public AbstractSpell getSpell() {
        return this.spell;
    }

    public Source getSource() {
        return this.source;
    }

    @Nullable
    public LivingEntity getCaster() {
        return this.caster;
    }

    public Level getLevel() {
        return this.level;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    @Nullable
    public Direction getDirection() {
        return this.direction;
    }

    public enum Source {
        WAND, SCROLL, COMMAND, NPC, DISPENSER, OTHER
    }

    public static class Pre extends SpellCastEvent implements ICancellableEvent {
        private final SpellContextFlow contextFlow;

        public Pre(Source source, SpellContextFlow contextFlow) {
            super(source, contextFlow.currentSpell(), contextFlow.caster());
            this.contextFlow = contextFlow;
        }

        public Pre(Source source, AbstractSpell spell, Level level, double x, double y, double z, Direction direction, SpellContextFlow contextFlow) {
            super(source, spell, level, x, y, z, direction);
            this.contextFlow = contextFlow;
        }

        public SpellContextFlow getContextFlow() {
            return this.contextFlow;
        }
    }

    public static class Post extends SpellCastEvent {
        private final SpellContext context;

        public Post(Source source, AbstractSpell spell, LivingEntity caster, SpellContext context) {
            super(source, spell, caster);
            this.context = context;
        }

        public Post(Source source, AbstractSpell spell, Level level, double x, double y, double z, Direction direction, SpellContext context) {
            super(source, spell, level, x, y, z, direction);
            this.context = context;
        }

        public SpellContext getContext() {
            return this.context;
        }
    }

    public static class Tick extends SpellCastEvent implements ICancellableEvent {
        private final SpellContextFlow spellContextFlow;
        private final int count;

        public Tick(Source source, SpellContextFlow spellContextFlow, int count) {
            super(source, spellContextFlow.currentSpell(), spellContextFlow.caster());
            this.count = count;
            this.spellContextFlow = spellContextFlow;
        }

        public Tick(Source source, AbstractSpell spell, Level level, double x, double y, double z, Direction direction, SpellContextFlow spellContextFlow, int count) {
            super(source, spell, level, x, y, z, direction);
            this.count = count;
            this.spellContextFlow = spellContextFlow;
        }

        public int getCount() {
            return count;
        }

        public SpellContextFlow getContext() {
            return this.spellContextFlow;
        }

    }

    public static class Finish extends SpellCastEvent {
        private final SpellContext context;
        private final int count;

        public Finish(Source source, AbstractSpell spell, LivingEntity caster, SpellContext context, int count) {
            super(source, spell, caster);
            this.count = count;
            this.context = context;
        }

        public Finish(Source source, AbstractSpell spell, Level level, double x, double y, double z, Direction direction, SpellContext context, int count) {
            super(source, spell, level, x, y, z, direction);
            this.count = count;
            this.context = context;
        }

        public int getCount() {
            return count;
        }
    }
}
