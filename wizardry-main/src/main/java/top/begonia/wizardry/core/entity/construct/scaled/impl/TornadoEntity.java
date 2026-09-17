package top.begonia.wizardry.core.entity.construct.scaled.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.options.IParticleOptionsExtension;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.client.util.ClientHelper;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.entity.construct.scaled.ScaledConstructEntity;
import top.begonia.wizardry.core.item.ArtefactItem;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.BlockUtils;
import top.begonia.wizardry.core.util.EntityUtils;

import java.util.List;

public class TornadoEntity extends ScaledConstructEntity {
    private static final EntityDataAccessor<Float> DATA_VEL_X = SynchedEntityData.defineId(
            TornadoEntity.class,
            EntityDataSerializers.FLOAT
    );
    private static final EntityDataAccessor<Float> DATA_VEL_Z = SynchedEntityData.defineId(
            TornadoEntity.class,
            EntityDataSerializers.FLOAT
    );
    public static final String UPWARD_ACCELERATION = "upward_acceleration";

    public TornadoEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    private static boolean canTornadoPickUpBitsOf(@NonNull BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(BlockTags.SAND)
                || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.FLOWERS)
                || state.is(Blocks.SNOW)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.SNOW_BLOCK)
                || state.is(Blocks.WATER)
                || state.is(Blocks.LAVA)
                || state.is(Blocks.VINE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VEL_X, 0.0F)
                .define(DATA_VEL_Z, 0.0F);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.setHorizontalVelocity(
                valueInput.getFloatOr("velX", this.getVelX()),
                valueInput.getFloatOr("velZ", this.getVelZ())
        );
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putFloat("velX", this.getVelX());
        valueOutput.putFloat("velZ", this.getVelZ());
    }

    public void setHorizontalVelocity(float velX, float velZ) {
        this.entityData.set(DATA_VEL_X, velX);
        this.entityData.set(DATA_VEL_Z, velZ);
    }

    public float getVelX() {
        return this.entityData.get(DATA_VEL_X);
    }

    public float getVelZ() {
        return this.entityData.get(DATA_VEL_Z);
    }

    @Override
    public void tick() {

        super.tick();

        double radius = this.getBbWidth() / 2;

        if (this.tickCount % 120 == 1 && this.level().isClientSide()) {
            // Repeat is false so that the sound fades out when the tornado does rather than stopping suddenly
            ClientHelper.playMovingSound(
                    this,
                    WizardrySounds.ENTITY_TORNADO_AMBIENT.get(),
                    WizardrySounds.SPELLS,
                    1.0f,
                    1.0f,
                    false
            );
        }

        this.move(
                MoverType.SELF,
                this.getDeltaMovement()
                        .multiply(0.0F, 1.0F, 0.0F)
                        .add(this.getVelX(), 0, this.getVelZ())
        );

        BlockPos pos = this.blockPosition();
        Integer y = BlockUtils.getNearestSurface(
                this.level(),
                pos.above(3),
                Direction.UP,
                5,
                true,
                BlockUtils.SurfaceCriteria.NOT_AIR_TO_AIR
        );

        if (y != null) {

            pos = new BlockPos(pos.getX(), y, pos.getZ());

            if (this.level().getBlockState(pos).is(Blocks.LAVA)) {
                // Fire tornado!
                this.setRemainingFireTicks(5 * 20);
            }
        }

        if (!this.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            List<LivingEntity> targets = EntityUtils.getLivingWithinRadius(
                    radius,
                    this.getX(), this.getY(), this.getZ(),
                    this.level()
            );

            for (LivingEntity target : targets) {

                if (target instanceof Player
                        && ((this.getOwner() instanceof Player && !ServerConfig.playersMoveEachOther)
                        || ArtefactItem.isArtefactActive((Player) target, WizardryItems.AMULET_ANCHORING.get()))
                ) {
                    continue;
                }

                if (this.isValidTarget(target)) {
                    Vec3 velocity = target.getDeltaMovement();
                    double velY = velocity.y;

                    // TODO: This doesn't seem right...
                    double dx = (this.getX() - target.getX() > 0 ? 0.5 : -0.5) - (this.getX() - target.getX()) * 0.125;
                    double dz = (this.getZ() - target.getZ() > 0 ? 0.5 : -0.5) - (this.getZ() - target.getZ()) * 0.125;

                    if (this.isOnFire()) {
                        target.setRemainingFireTicks(4 * 20); // Just a fun Easter egg so no properties here!
                    }

                    float damage = WizardrySpells.TORNADO.get().getBaseProperty(AbstractSpell.DAMAGE) * this.getDamageMultiplier();

                    if (this.getOwner() != null) {
                        target.hurtServer(
                                serverLevel,
                                WizardryDamageSource.causeIndirectMagicDamage(
                                        WizardryDamageTypes.MAGIC.apply(this.registryAccess()),
                                        this,
                                        this.getOwner(),
                                        false
                                ),
                                damage
                        );
                    } else {
                        target.hurtServer(
                                serverLevel,
                                new DamageSources(this.registryAccess()).magic(),
                                damage
                        );
                    }

                    target.setDeltaMovement(
                            dx,
                            velY + WizardrySpells.TORNADO.get().getBaseProperty(UPWARD_ACCELERATION),
                            dz
                    );

                    // Player motion is handled on that player's client so needs packets
                    if (target instanceof ServerPlayer player) {
                        player.connection.send(
                                new ClientboundSetEntityMotionPacket(target)
                        );
                    }
                }
            }
        } else {
            for (int i = 1; i < 10; i++) {

                double yPos = this.random.nextDouble() * 8;

                int blockX = (int) this.getX() - 2 + this.random.nextInt(4);
                int blockZ = (int) this.getZ() - 2 + this.random.nextInt(4);

                BlockPos pos1 = new BlockPos(blockX, (int) (this.getY() + 3), blockZ);

                Integer blockY = BlockUtils.getNearestSurface(
                        this.level(),
                        pos1,
                        Direction.UP,
                        5,
                        true,
                        BlockUtils.SurfaceCriteria.NOT_AIR_TO_AIR
                );

                if (blockY != null) {

                    blockY--;

                    pos1 = new BlockPos(pos1.getX(), blockY, pos1.getZ());

                    BlockState block = this.level().getBlockState(pos1);

                    // If the block it found was air or something it can't pick up, it makes a best guess based on the biome
                    if (!canTornadoPickUpBitsOf(block)) {
//                        block = this.level().getBiome(pos1).topBlock;
                    }

                    ClientHelper.spawnTornadoParticle(this.level(), this.getX(), this.getY() + yPos, this.getZ(), this.getVelX(), this.getVelZ(),
                            yPos / 3 + 0.5d, 100, block, pos1);
                    ClientHelper.spawnTornadoParticle(this.level(), this.getX(), this.getY() + yPos, this.getZ(), this.getVelX(), this.getVelZ(),
                            yPos / 3 + 0.5d, 100, block, pos1);

                    // Sometimes spawns leaf particles if the block is leaves, or snow particles if the block is snow
                    if (this.random.nextInt(3) == 0) {

                        IParticleOptionsExtension type = null;

                        if (block.is(BlockTags.LEAVES)) {
                            type = WizardryParticles.LEAF.get();
                        }
                        if (block.is(BlockTags.SNOW) || block.is(Blocks.SNOW_BLOCK)) {
                            type = WizardryParticles.SNOW.get();
                        }

                        if (type != null) {
                            double yPos1 = this.random.nextDouble() * 8;
                            WizardryClient.particleManager.getParticle(
                                    (ClientLevel) this.level(),
                                    type,
                                    this.getX() + (this.random.nextDouble() * 2 - 1) * (yPos1 / 3 + 0.5d),
                                    this.getY() + yPos1,
                                    this.getZ() + (this.random.nextDouble() * 2 - 1) * (yPos1 / 3 + 0.5d)
                            ).ifPresent(p -> p.time(40 + this.random.nextInt(10)).spawn());
                        }
                    }
                }
            }
        }
    }
}
