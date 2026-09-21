package top.begonia.wizardry.core.entity.living.wizard;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.api.entity.atom.ISpellCaster;
import top.begonia.wizardry.api.entity.ai.goal.RestrictOpenDoor;
import top.begonia.wizardry.core.item.SpellBookItem;
import top.begonia.wizardry.core.registry.*;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.ArmourHelper;
import top.begonia.wizardry.core.util.EntityUtils;
import top.begonia.wizardry.core.util.ItemStackHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWizardEntity extends PathfinderMob implements ISpellCaster {
    public static final EntityDataAccessor<Integer> HEAL_COOLDOWN = SynchedEntityData.defineId(
            AbstractWizardEntity.class,
            EntityDataSerializers.INT
    );
    public static final EntityDataAccessor<Integer> ELEMENT = SynchedEntityData.defineId(
            AbstractWizardEntity.class,
            EntityDataSerializers.INT
    );
    public static final EntityDataAccessor<Identifier> CONTINUOUS_SPELL = SynchedEntityData.defineId(
            AbstractWizardEntity.class,
            WizardryEntityDataSerializers.IDENTIFIER.get()
    );
    public static final EntityDataAccessor<Integer> SPELL_COUNTER = SynchedEntityData.defineId(
            AbstractWizardEntity.class,
            EntityDataSerializers.INT
    );
    private static final EntityDataAccessor<Integer> TEXTURE_INDEX = SynchedEntityData.defineId(
            AbstractWizardEntity.class,
            EntityDataSerializers.INT
    );
    private final List<AbstractSpell> spells = new ArrayList<>(4);

    protected AbstractWizardEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.@NonNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.MAX_HEALTH, 30.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new RestrictOpenDoor(this));
        this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));  // 开门通过
        this.goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 0.6D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.6D));
    }

    protected void defineSynchedData(SynchedEntityData.@NonNull Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(HEAL_COOLDOWN, -1)
                .define(ELEMENT, 0)
                .define(CONTINUOUS_SPELL, WizardrySpells.NONE.getId())
                .define(SPELL_COUNTER, 0)
                .define(TEXTURE_INDEX, 0);
    }

    @Override
    public void tick() {
        super.tick();
        int healCooldown = this.getHealCooldown();
        if (healCooldown == 0
                && this.getHealth() < this.getMaxHealth()
                && this.getHealth() > 0
                && !this.hasEffect(WizardryMobEffects.ARCANE_JAMMER)
        ) {
            this.heal(this.getElement() == ElementEnum.HEALING ? 8 : 4);
            this.setHealCooldown(-1);
        } else if (healCooldown == -1 && this.deathTime == 0) {
            // Heal particles TODO: Change this so it uses the heal spell directly
            if (this.level() instanceof ClientLevel clientLevel) {
                WizardryClient.particleManager.spawnHealParticles(
                        clientLevel,
                        this
                );
            } else {
                if (this.getHealth() < 10) {
                    this.setHealCooldown(150);
                } else {
                    this.setHealCooldown(400);
                }
                // TODO
//                this.playSound(Spells.heal.getSounds()[0], 0.7F, rand.nextFloat() * 0.4F + 1.0F);
            }
        }

        if (healCooldown > 0) {
            this.setHealCooldown(healCooldown - 1);
        }
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);

        ElementEnum element = this.getElement();
        valueOutput.putInt("element", element == null ? 0 : element.ordinal());
        valueOutput.putInt("skin", this.entityData.get(TEXTURE_INDEX));
        var spellsOutput = valueOutput.list("spells", AbstractSpell.CODEC);
        this.spells.forEach(spell ->
                spellsOutput.add(WizardrySpells.getHolder(spell.getIdentifier()))
        );
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.setElement(ElementEnum.values()[valueInput.getIntOr("element", 0)]);
        this.entityData.set(TEXTURE_INDEX, valueInput.getIntOr("skin", 0));
        this.spells.clear();
        var spellsInput = valueInput.listOrEmpty("spells", AbstractSpell.CODEC);
        spellsInput.forEach(spell -> this.spells.add(spell.value()));
    }

    @SuppressWarnings("deprecation")
    public @Nullable SpawnGroupData finalizeSpawn(
            @NonNull ServerLevelAccessor level,
            @NonNull DifficultyInstance difficulty,
            @NonNull EntitySpawnReason spawnReason,
            @Nullable SpawnGroupData groupData
    ) {
        groupData = super.finalizeSpawn(level, difficulty, spawnReason, groupData);
        this.entityData.set(TEXTURE_INDEX, this.random.nextInt(6));

        if (this.random.nextBoolean()) {
            this.setElement(ElementEnum.values()[this.random.nextInt(ElementEnum.values().length - 1) + 1]);
        } else {
            this.setElement(ElementEnum.MAGIC);
        }
        ElementEnum element = this.getElement();

        // Adds armour.
        for (ArmorType type : ArmorType.values()) {
            this.setItemSlot(
                    type.getSlot(),
                    ItemStackHelper.generateArmour(
                            WizardryItems.ARMOUR.get(),
                            element,
                            ArmourHelper.ArmourMaterialType.WIZARD,
                            type
                    )
            );
        }
        // Default chance is 0.085f, for reference.
        for (ArmorType type : ArmorType.values()) {
            this.setDropChance(type.getSlot(), 0.0f);
        }

        // All wizards know magic missile, even if it is disabled.
        spells.add(WizardrySpells.MAGIC_MISSILE.get());
        TierEnum maxTier = EntityUtils.populateSpells(this, spells, element, false, 3, this.random);

        // Now done after the spells so it can take the tier into account.
        ItemStack wand = ItemStackHelper.getWand(maxTier, element);
        ArrayList<AbstractSpell> list = new ArrayList<>(spells);
        list.add(WizardrySpells.HEAL.get());
        ItemStackHelper.setSpells(wand, list.toArray(new AbstractSpell[5]));
        this.setItemSlot(EquipmentSlot.MAINHAND, wand);

        return groupData;
    }

    // 替代 processInteract
    protected @NonNull InteractionResult mobInteract(@NonNull Player player, @NonNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isCreative()
                && stack.getItem() instanceof SpellBookItem
        ) {
            AbstractSpell spell = stack.getOrDefault(WizardryComponents.SPELL, WizardrySpells.NONE).value();
            if (this.spells.size() >= 4 && spell.canBeCastBy(this, true)) {
                // The set(...) method returns the element that was replaced - neat!
                player.sendSystemMessage(Component.translatable("item." + Wizardry.MODID + ".spell_book.apply_to_wizard",
                        this.getDisplayName(), this.spells.set(this.random.nextInt(3) + 1, spell).getDisplayNameWithFormatting(),
                        spell.getDisplayNameWithFormatting()));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public @NonNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public abstract Identifier[] getWizardTextures();

    private int getHealCooldown() {
        return this.entityData.get(HEAL_COOLDOWN);
    }

    @SuppressWarnings("SameParameterValue")
    protected void setHealCooldown(int cooldown) {
        this.entityData.set(HEAL_COOLDOWN, cooldown);
    }

    public ElementEnum getElement() {
        return ElementEnum.values()[this.entityData.get(ELEMENT)];
    }

    public void setElement(@NonNull ElementEnum element) {
        this.entityData.set(ELEMENT, element.ordinal());
    }

    public int getTextureIndex() {
        return this.entityData.get(TEXTURE_INDEX);
    }

    @Override
    public @NonNull List<AbstractSpell> getSpells() {
        return this.spells;
    }

    @Override
    public @NonNull AbstractSpell getContinuousSpell() {
        return WizardrySpells.get(this.entityData.get(CONTINUOUS_SPELL));
    }

    @Override
    public void setContinuousSpell(@NonNull AbstractSpell spell) {
        this.entityData.set(CONTINUOUS_SPELL, spell.getIdentifier());
    }

    @Override
    public int getSpellCounter() {
        return this.entityData.get(SPELL_COUNTER);
    }

    @Override
    public void setSpellCounter(int count) {
        this.entityData.set(SPELL_COUNTER, count);
    }

    @Override
    public int getAimingError(@NonNull Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 7;
            case NORMAL -> 4;
            case HARD -> 1;
            default -> 7; // Peaceful counts as easy
        };
    }
}
