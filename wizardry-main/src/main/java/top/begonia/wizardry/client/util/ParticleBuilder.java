package top.begonia.wizardry.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.particle.ParticleResultData;
import top.begonia.wizardry.client.data.manager.WizardryClientDataManager;
import top.begonia.wizardry.client.particle.AbstractParticle;
import top.begonia.wizardry.client.particle.WizardryParticleOptions;
import top.begonia.wizardry.core.registry.WizardryParticles;

public final class ParticleBuilder {
    public static final ParticleBuilder instance = new ParticleBuilder();
    private ParticleType<? extends WizardryParticleOptions> type;
    private boolean building = false;
    private double x, y, z;
    private double vx, vy, vz;
    private float r, g, b;
    private float fr, fg, fb;
    private double radius;
    private double rpt;
    private int lifetime;
    private boolean gravity;
    private boolean shaded;
    private boolean collide;
    private float scale;
    private Entity entity;
    private float yaw, pitch;
    private double tx, ty, tz;
    private double tvx, tvy, tvz;
    private Entity target;
    private long seed;
    private double length;

    private ParticleBuilder() {
        reset();
    }

    public static ParticleBuilder create(ParticleType<? extends WizardryParticleOptions> type) {
        return ParticleBuilder.instance.particle(type);
    }

    public ParticleBuilder particle(ParticleType<? extends WizardryParticleOptions> type) {
        if (building) {
            throw new IllegalStateException("Already building! Particle being built: " + getCurrentParticleString());
        }
        this.type = type;
        this.building = true;
        return this;
    }

    private String getCurrentParticleString() {
        return String.format("[ Type: %s, Position: (%s, %s, %s), Velocity: (%s, %s, %s), Colour: (%s, %s, %s), "
                        + "Fade Colour: (%s, %s, %s), Radius: %s, Revs/tick: %s, Lifetime: %s, Gravity: %s, Shaded: %s, "
                        + "Scale: %s, Entity: %s ]",
                type, x, y, z, vx, vy, vz, r, g, b, fr, fg, fb, radius, rpt, lifetime, gravity, shaded, scale, entity);
    }

    public ParticleBuilder pos(double x, double y, double z) {
        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public ParticleBuilder pos(Vec3 pos) {
        return pos(pos.x, pos.y, pos.z);
    }

    public ParticleBuilder vel(double vx, double vy, double vz) {
        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        return this;
    }

    public ParticleBuilder vel(Vec3 vel) {
        return vel(vel.x, vel.y, vel.z);
    }

    public ParticleBuilder clr(float r, float g, float b) {
        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }
        this.r = Mth.clamp(r, 0, 1);
        this.g = Mth.clamp(g, 0, 1);
        this.b = Mth.clamp(b, 0, 1);
        return this;
    }

    public ParticleBuilder clr(int r, int g, int b) {
        return this.clr(r / 255f, g / 255f, b / 255f); // Yes, 255 is correct and not 256, or else we can't have pure white
    }

    public ParticleBuilder clr(int hex) {
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);
        return this.clr(r, g, b);
    }

    public ParticleBuilder fade(float r, float g, float b) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.fr = Mth.clamp(r, 0, 1);
        this.fg = Mth.clamp(g, 0, 1);
        this.fb = Mth.clamp(b, 0, 1);
        return this;
    }

    public ParticleBuilder fade(int r, int g, int b) {
        return this.fade(r / 255f, g / 255f, b / 255f);
    }

    public ParticleBuilder fade(int hex) {
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);
        return this.fade(r, g, b);
    }

    public ParticleBuilder scale(float scale) {
        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }
        this.scale = scale;
        return this;
    }

    public ParticleBuilder time(int lifetime) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.lifetime = lifetime;
        return this;
    }

    public ParticleBuilder seed(long seed) {
        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }
        this.seed = seed;
        return this;
    }

    public ParticleBuilder spin(double radius, double speed) {
        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }
        this.radius = radius;
        this.rpt = speed;
        return this;
    }

    public ParticleBuilder gravity(boolean gravity) {
        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }
        this.gravity = gravity;
        return this;
    }

    public ParticleBuilder shaded(boolean shaded) {
        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }
        this.shaded = shaded;
        return this;
    }

    public ParticleBuilder collide(boolean collide) {
        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }
        this.collide = collide;
        return this;
    }

    public ParticleBuilder entity(Entity entity) {
        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }
        this.entity = entity;
        return this;
    }

    public ParticleBuilder face(float yaw, float pitch) {
        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }
        this.yaw = yaw;
        this.pitch = pitch;
        return this;
    }

    public ParticleBuilder face(@NonNull Direction direction) {
        return this.face(direction.toYRot(), direction.getAxis().isVertical() ? direction.getAxisDirection().getStep() * -90.0F : 0.0F);
    }

    public ParticleBuilder target(double x, double y, double z) {
        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }
        this.tx = x;
        this.ty = y;
        this.tz = z;
        return this;
    }

    public ParticleBuilder target(Vec3 pos) {
        return target(pos.x, pos.y, pos.z);
    }

    public ParticleBuilder tvel(double vx, double vy, double vz) {
        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }
        this.tvx = vx;
        this.tvy = vy;
        this.tvz = vz;
        return this;
    }

    public ParticleBuilder tvel(Vec3 vel) {
        return tvel(vel.x, vel.y, vel.z);
    }

    public ParticleBuilder length(double length) {
        this.length = length;
        return this;
    }

    public ParticleBuilder target(Entity target) {
        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }
        this.target = target;
        return this;
    }

    public void spawn(Level level) {

        if (!building) {
            throw new IllegalStateException("Not building yet!");
        }

        if (x == 0 && y == 0 && z == 0 && entity == null)
            Wizardry.LOGGER.warn("Spawning particle at (0, 0, 0) - are you"
                    + " sure the position/entity has been set correctly?");

        if (!level.isClientSide()) {
            Wizardry.LOGGER.warn("ParticleBuilder.spawn(...) called on the server side! ParticleBuilder has prevented a "
                    + "server crash, but calling it on the server will do nothing. Consider adding a world.isRemote check.");
            reset();
            return;
        }

        ParticleResultData particleData = WizardryClientDataManager.getInstance().getData(BuiltInRegistries.PARTICLE_TYPE.getKey(this.type), ParticleResultData.class).orElse(null);
        if (particleData == null) {
            reset();
            return;
        }

        Particle particle = particleData.particleHolder().create(
                new WizardryParticleOptions(this.type),
                (ClientLevel) level,
                this.x, this.y, this.z,
                0, 0, 0
        );

        if (particle == null) {
            reset();
            return;
        }

        if (particle instanceof AbstractParticle abstractParticle) {
            if (!Double.isNaN(vx) && !Double.isNaN(vy) && !Double.isNaN(vz)) {
                abstractParticle.setParticleSpeed(vx, vy, vz);
            }
            if (r >= 0 && g >= 0 && b >= 0) {
                abstractParticle.setColor(r, g, b);
                abstractParticle.setFadeColor(r, g, b);
                abstractParticle.setInitialColor(r, g, b);
            }
            if (fr >= 0 && fg >= 0 && fb >= 0) {
                abstractParticle.setFadeColor(fr, fg, fb);
            }
            if (lifetime >= 0) {
                abstractParticle.setLifetime(lifetime);
            }
            if (radius > 0) {
                abstractParticle.setSpin(radius, rpt);
            }
            if (!Float.isNaN(yaw) && !Float.isNaN(pitch)) {
                abstractParticle.setFacing(yaw, pitch);
            }
            if (seed != 0) {
                abstractParticle.setSeed(seed);
            }
            if (!Double.isNaN(tvx) && !Double.isNaN(tvy) && !Double.isNaN(tvz)) {
                abstractParticle.setTargetVelocity(tvx, tvy, tvz);
            }
            if (length > 0) {
                abstractParticle.setLength(length);
            }

            abstractParticle.scale(scale);
            abstractParticle.setGravity(gravity);
            abstractParticle.setShaded(shaded);
            abstractParticle.setCollisions(collide);
            abstractParticle.setEntity(entity);
            abstractParticle.setTargetPosition(tx, ty, tz);
            abstractParticle.setTargetEntity(target);

            Minecraft.getInstance().particleEngine.add(abstractParticle);

            reset();
        }
    }

    private void reset() {
        building = false;
        type = null;
        x = 0;
        y = 0;
        z = 0;
        vx = Double.NaN;
        vy = Double.NaN;
        vz = Double.NaN;
        r = -1;
        g = -1;
        b = -1;
        fr = -1;
        fg = -1;
        fb = -1;
        radius = 0;
        rpt = 0;
        lifetime = -1;
        gravity = false;
        shaded = false;
        collide = false;
        scale = 1;
        entity = null;
        yaw = Float.NaN;
        pitch = Float.NaN;
        tx = Double.NaN;
        ty = Double.NaN;
        tz = Double.NaN;
        tvx = Double.NaN;
        tvy = Double.NaN;
        tvz = Double.NaN;
        target = null;
        seed = 0;
        length = -1;
    }

    public static ParticleBuilder create(ParticleType<? extends WizardryParticleOptions> type, Entity entity) {

        double x = entity.getX() + (entity.level().getRandom().nextDouble() - 0.5D) * (double) entity.getBbWidth();
        double y = entity.getY() + entity.level().getRandom().nextDouble() * (double) entity.getBbHeight();
        double z = entity.getZ() + (entity.level().getRandom().nextDouble() - 0.5D) * (double) entity.getBbWidth();

        return ParticleBuilder.instance.particle(type).pos(x, y, z);
    }

    public static ParticleBuilder create(ParticleType<? extends WizardryParticleOptions> type, RandomSource random, double x, double y, double z, double radius, boolean move) {

        double px = x + (random.nextDouble() * 2 - 1) * radius;
        double py = y + (random.nextDouble() * 2 - 1) * radius;
        double pz = z + (random.nextDouble() * 2 - 1) * radius;

        if (move) return ParticleBuilder.instance.particle(type).pos(px, py, pz).vel(px - x, py - y, pz - z);

        return ParticleBuilder.instance.particle(type).pos(px, py, pz);
    }

    public static void spawnShockParticles(Level level, double x, double y, double z) {
        double px, py, pz;
        for (int i = 0; i < 8; i++) {
            px = x + level.getRandom().nextDouble() - 0.5;
            py = y + level.getRandom().nextDouble() - 0.5;
            pz = z + level.getRandom().nextDouble() - 0.5;
            ParticleBuilder.create(WizardryParticles.SPARK.get()).pos(px, py, pz).spawn(level);
            px = x + level.getRandom().nextDouble() - 0.5;
            py = y + level.getRandom().nextDouble() - 0.5;
            pz = z + level.getRandom().nextDouble() - 0.5;
            level.addParticle(ParticleTypes.LARGE_SMOKE, px, py, pz, 0, 0, 0);
        }
    }

    public static void spawnHealParticles(Level level, LivingEntity entity) {

        for (int i = 0; i < 10; i++) {
            double x = entity.getX() + level.getRandom().nextDouble() * 2 - 1;
            double y = entity.getY() + entity.getEyeHeight() - 0.5 + level.getRandom().nextDouble();
            double z = entity.getZ() + level.getRandom().nextDouble() * 2 - 1;
            ParticleBuilder.create(WizardryParticles.SPARKLE.get()).pos(x, y, z).vel(0, 0.1, 0).clr(1, 1, 0.3f).spawn(level);
        }

        ParticleBuilder.create(WizardryParticles.BUFF.get()).entity(entity).clr(1, 1, 0.3f).spawn(level);
    }

}
