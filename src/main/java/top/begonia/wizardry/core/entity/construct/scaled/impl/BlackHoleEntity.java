package top.begonia.wizardry.core.entity.construct.scaled.impl;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.entity.LevitatingBlockEntity;
import top.begonia.wizardry.core.entity.construct.scaled.ScaledConstructEntity;
import top.begonia.wizardry.core.item.ArtefactItem;
import top.begonia.wizardry.core.registry.WizardryEntities;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.BlockUtils;
import top.begonia.wizardry.core.util.EntityUtils;

import java.util.List;

public class BlackHoleEntity extends ScaledConstructEntity {
    private static final double SUCTION_STRENGTH = 0.075;
    private static final int BLOCK_UNHOOK_LIMIT = 3;
    public int[] randomiser;
    public int[] randomiser2;

    public BlackHoleEntity(EntityType<? extends BlackHoleEntity> type, Level level) {
        super(type, level);
        float r = WizardrySpells.BLACK_HOLE.get().getBaseProperty(AbstractSpell.EFFECT_RADIUS);
        this.setSizeMultiplier(r);
        randomiser = new int[30];
        for (int i = 0; i < randomiser.length; i++) {
            randomiser[i] = this.getRandom().nextInt(10);
        }
        randomiser2 = new int[30];
        for (int i = 0; i < randomiser2.length; i++) {
            randomiser2[i] = this.getRandom().nextInt(10);
        }
    }

    public BlackHoleEntity(Level level) {
        this(WizardryEntities.BLACK_HOLE.get(), level);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount + 40 < this.getLifetime()) {
            for (int i = 0; i < 5; i++) {
                this.level().addParticle(ParticleTypes.PORTAL, this.getX(), this.getY(), this.getZ(),
                        (this.getRandom().nextDouble() - 0.5D) * 4.0D, (this.getRandom().nextDouble() - 0.5D) * 4.0D - 1,
                        (this.getRandom().nextDouble() - 0.5D) * 4.0D);
            }
        }

        if (this.getLifetime() - this.tickCount == 75) {
            this.playSound(WizardrySounds.ENTITY_BLACK_HOLE_VANISH.get(), 1.5f, 1.0f);
        } else if (this.tickCount % 80 == 1 && this.tickCount + 80 < this.getLifetime()) {
            this.playSound(WizardrySounds.ENTITY_BLACK_HOLE_AMBIENT.get(), 1.5f, 1.0f);
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            double radius = 2 * this.getBbHeight() * this.getSizeMultiplier();

            boolean suckInBlocks = this.getOwner() instanceof Player player && EntityUtils.canDamageBlocks(this.getOwner(), serverLevel)
                    && ArtefactItem.isArtefactActive(player, WizardryItems.CHARM_BLACK_HOLE.get());

            if (suckInBlocks) {
                BlockUtils.getBlockSphereStream(this.blockPosition(), radius)
                        .filter(pos -> this.random.nextInt(Math.max(1, (int) this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) * 3)) == 0)
                        .filter(pos -> {
                            BlockState state = this.level().getBlockState(pos);
                            return !EntityUtils.isBlockUnbreakable(serverLevel, pos) && !state.isAir()
                                    && state.isCollisionShapeFullBlock(this.level(), pos)
                                    && EntityUtils.canDamageBlocks(this.getOwner(), serverLevel);
                        })
                        .limit(BLOCK_UNHOOK_LIMIT)
                        .forEach(pos -> {
                            BlockState state = this.level().getBlockState(pos);
                            FallingBlockEntity fallingBlock = LevitatingBlockEntity.fall(this.level(), pos, state);
                            fallingBlock.time = 1;
                            this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        });

            }

            List<Entity> targets = EntityUtils.getEntitiesWithinRadius(
                    radius,
                    this.getX(), this.getY(), this.getZ(),
                    this.level(),
                    Entity.class
            );
            targets.removeIf(t -> !(t instanceof LivingEntity || (suckInBlocks && t instanceof FallingBlockEntity)));

            for (Entity target : targets) {
                if (this.isValidTarget(target)) {
                    if (!(target instanceof Player
                            && ((this.getOwner() instanceof Player && !ServerConfig.playersMoveEachOther)
                            || ArtefactItem.isArtefactActive((Player) target, WizardryItems.AMULET_ANCHORING.get())
                    ))) {

                        EntityUtils.undoGravity(target);
                        if (target instanceof LevitatingBlockEntity levitatingBlockEntity) {
                            levitatingBlockEntity.suspend();
                        }

                        double motionX = target.getDeltaMovement().x;
                        double motionY = target.getDeltaMovement().y;
                        double motionZ = target.getDeltaMovement().z;

                        if (this.getX() > target.getX() && motionX < 1.0D) {
                            motionX += SUCTION_STRENGTH;
                        } else if (this.getX() < target.getX() && motionX > -1.0D) {
                            motionX -= SUCTION_STRENGTH;
                        }

                        if (this.getY() > target.getY() && motionY < 1.0D) {
                            motionY += SUCTION_STRENGTH;
                        } else if (this.getY() < target.getY() && motionY > -1.0D) {
                            motionY -= SUCTION_STRENGTH;
                        }

                        if (this.getZ() > target.getZ() && motionZ < 1.0D) {
                            motionZ += SUCTION_STRENGTH;
                        } else if (this.getZ() < target.getZ() && motionZ > -1.0D) {
                            motionZ -= SUCTION_STRENGTH;
                        }
                        target.setDeltaMovement(motionX, motionY, motionZ);

                        if (target instanceof ServerPlayer serverPlayer) {
                            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(target));
                        }
                    }

                    if (this.distanceTo(target) <= 2) {
                        if (target instanceof FallingBlockEntity fallingBlockEntity) {
                            target.playSound(WizardrySounds.ENTITY_BLACK_HOLE_BREAK_BLOCK.get(), 0.5f,
                                    (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2f + 1);
                            BlockState state = fallingBlockEntity.getBlockState();
                            this.level().levelEvent(2001, fallingBlockEntity.blockPosition(), Block.getId(state));
                            target.discard();

                        } else {
                            if (this.getOwner() != null) {
                                target.hurtServer(
                                        serverLevel,
                                        WizardryDamageSource.causeIndirectMagicDamage(
                                                WizardryDamageTypes.MAGIC.apply(registryAccess()),
                                                this,
                                                this.getOwner(),
                                                false
                                        ),
                                        2 * this.getDamageMultiplier()
                                );
                            } else {
                                target.hurtServer(
                                        serverLevel,
                                        this.damageSources().magic(),
                                        2 * this.getDamageMultiplier()
                                );
                            }
                        }
                    }
                }
            }
        }
    }
}
