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
        private final SpellContextFlow spellContextFlow;

        public Pre(Source source, AbstractSpell spell, LivingEntity caster, SpellContextFlow spellContextFlow) {
            super(source, spell, caster);
            this.spellContextFlow = spellContextFlow;
        }

        public Pre(Source source, AbstractSpell spell, Level level, double x, double y, double z, Direction direction, SpellContextFlow spellContextFlow) {
            super(source, spell, level, x, y, z, direction);
            this.spellContextFlow = spellContextFlow;
        }

        public SpellContextFlow getSpellContextFlow() {
            return this.spellContextFlow;
        }
    }

    public static class Post extends SpellCastEvent {
        private final SpellContext spellContext;

        public Post(Source source, AbstractSpell spell, LivingEntity caster, SpellContext spellContext) {
            super(source, spell, caster);
            this.spellContext = spellContext;
        }

        public Post(Source source, AbstractSpell spell, Level level, double x, double y, double z, Direction direction, SpellContext spellContext) {
            super(source, spell, level, x, y, z, direction);
            this.spellContext = spellContext;
        }

        public SpellContext getSpellContext() {
            return this.spellContext;
        }
    }

    public static class Tick extends SpellCastEvent implements ICancellableEvent {
        private final int count;
        private final SpellContextFlow spellContextFlow;

        public Tick(Source source, AbstractSpell spell, LivingEntity caster, SpellContextFlow spellContextFlow, int count) {
            super(source, spell, caster);
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

        public SpellContextFlow getSpellContextFlow() {
            return this.spellContextFlow;
        }
    }

    public static class Finish extends SpellCastEvent {
        private final int count;
        private final SpellContext spellContext;

        public Finish(Source source, AbstractSpell spell, LivingEntity caster, SpellContext spellContext, int count) {
            super(source, spell, caster);
            this.count = count;
            this.spellContext = spellContext;
        }

        public Finish(Source source, AbstractSpell spell, Level level, double x, double y, double z, Direction direction, SpellContext spellContext, int count) {
            super(source, spell, level, x, y, z, direction);
            this.count = count;
            this.spellContext = spellContext;
        }

        public int getCount() {
            return count;
        }

        public SpellContext getSpellContext() {
            return this.spellContext;
        }
    }
}
