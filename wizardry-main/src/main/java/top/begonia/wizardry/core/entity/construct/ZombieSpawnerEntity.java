package top.begonia.wizardry.core.entity.construct;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import top.begonia.wizardry.core.registry.WizardryEntities;

public class ZombieSpawnerEntity extends MagicConstructEntity {
    private static final double MAX_NUDGE_DISTANCE = 0.1;
    private final int spawnTimer = 10;
    public boolean spawnHusks;

    public ZombieSpawnerEntity(EntityType<? extends ZombieSpawnerEntity> type, Level level) {
        super(type, level);
    }

    public ZombieSpawnerEntity(Level level) {
        this(WizardryEntities.ZOMBIE_SPAWNER.get(), level);
    }

//    @Override
//    public void tick() {
//
//        super.tick();
//
//        if (this.getLifetime() - tickCount > 10 && spawnTimer-- == 0) {
//
//            this.playSound(WizardrySounds.ENTITY_ZOMBIE_SPAWNER_SPAWN.get(), 1, 1);
//
//            if (!this.level().isClientSide()) {
//
//                EntityZombieMinion zombie = spawnHusks ? new EntityHuskMinion(world) : new EntityZombieMinion(world);
//
//                zombie.setPosition(this.posX + (rand.nextDouble() * 2 - 1) * MAX_NUDGE_DISTANCE, this.posY,
//                        this.posZ + (rand.nextDouble() * 2 - 1) * MAX_NUDGE_DISTANCE);
//                zombie.setCaster(this.getCaster());
//                // Modifier implementation
//                // Attribute modifiers are pretty opaque, see https://minecraft.gamepedia.com/Attribute#Modifiers
//                zombie.setLifetime(Spells.zombie_apocalypse.getProperty(SpellMinion.MINION_LIFETIME).intValue());
//                IAttributeInstance attribute = zombie.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
//                attribute.applyModifier(new AttributeModifier(SpellMinion.POTENCY_ATTRIBUTE_MODIFIER,
//                        damageMultiplier - 1, EntityUtils.Operations.MULTIPLY_CUMULATIVE));
//                zombie.setHealth(zombie.getMaxHealth()); // Need to set this because we may have just modified the value
//                zombie.hurtResistantTime = 30; // Prevent fall damage
//                zombie.hideParticles(); // Hide spawn particles or they pop out the top of the hidden box
//
//                world.spawnEntity(zombie);
//            }
//
//            spawnTimer += WizardrySpells.ZOMBIE_APOCALYPSE.get().getBaseProperty(ZombieApocalypse.MINION_SPAWN_INTERVAL).intValue() + rand.nextInt(20);
//        }
//
//        if (world.isRemote) {
//
//            float b = 0.15f;
//
//            for (double r = 1.5; r < 4; r += 0.2) {
//                ParticleBuilder.create(Type.CLOUD).clr(b -= 0.02, 0, 0).pos(posX, posY - 0.3, posZ).scale(0.5f / (float) r)
//                        .spin(r, 0.02 / r * (1 + world.rand.nextDouble())).spawn(world);
//            }
//
//        }
//
//    }

//    @Override
//    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
//        super.readAdditionalSaveData(valueInput);
//        this.spawnTimer = valueInput.getIntOr("spawnTimer", 10);
//        this.spawnHusks = valueInput.getBooleanOr("spawnHusks", false);
//    }
//
//    @Override
//    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
//        super.addAdditionalSaveData(valueOutput);
//        valueOutput.putInt("spawnTimer", this.spawnTimer);
//        valueOutput.putBoolean("spawnHusks", this.spawnHusks);
//    }
}
