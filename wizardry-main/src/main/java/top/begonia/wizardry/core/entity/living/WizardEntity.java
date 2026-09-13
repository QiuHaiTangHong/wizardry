package top.begonia.wizardry.core.entity.living;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.config.CommonConfig;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.EnabledEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.data.constant.WizardryServerDataManager;
import top.begonia.wizardry.core.data.constant.definition.currency.Currency;
import top.begonia.wizardry.core.entity.ISpellCaster;
import top.begonia.wizardry.core.entity.ai.goal.LookAtTradePlayer;
import top.begonia.wizardry.core.entity.ai.goal.RestrictOpenDoor;
import top.begonia.wizardry.core.entity.ai.goal.TradePlayer;
import top.begonia.wizardry.core.item.SpellBookItem;
import top.begonia.wizardry.core.registry.*;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.ArmourHelper;
import top.begonia.wizardry.core.util.EntityUtils;
import top.begonia.wizardry.core.util.ItemStackHelper;
import top.begonia.wizardry.core.util.TierElementFilter;

import java.util.*;

public class WizardEntity extends PathfinderMob implements Merchant, ISpellCaster {
    public static final float LOOK_DISTANCE = 8.0f;
    public static final EntityDataAccessor<Integer> HEAL_COOLDOWN = SynchedEntityData.defineId(
            WizardEntity.class,
            EntityDataSerializers.INT
    );
    public static final EntityDataAccessor<Integer> ELEMENT = SynchedEntityData.defineId(
            WizardEntity.class,
            EntityDataSerializers.INT
    );
    public static final EntityDataAccessor<Identifier> CONTINUOUS_SPELL = SynchedEntityData.defineId(
            WizardEntity.class,
            WizardryEntityDataSerializers.IDENTIFIER.get()
    );
    public static final EntityDataAccessor<Integer> SPELL_COUNTER = SynchedEntityData.defineId(
            WizardEntity.class,
            EntityDataSerializers.INT
    );
    private static final EntityDataAccessor<Integer> TEXTURE_INDEX = SynchedEntityData.defineId(
            WizardEntity.class,
            EntityDataSerializers.INT
    );
    @Nullable
    private Player trading;
    private final List<AbstractSpell> spells = new ArrayList<>(4);
    protected @Nullable MerchantOffers offers;
    private Set<BlockPos> towerBlocks;
    private int villagerXp = 0;

    public WizardEntity(EntityType<? extends WizardEntity> type, Level level) {
        super(type, level);
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
    protected void registerGoals() {
        // 游泳的浮动
        this.goalSelector.addGoal(0, new FloatGoal(this));
        //交易
        this.goalSelector.addGoal(1, new TradePlayer(this));
        // 看向交易实体
        this.goalSelector.addGoal(1, new LookAtTradePlayer(this, LOOK_DISTANCE));
        this.goalSelector.addGoal(4, new RestrictOpenDoor(this));
        // 开门
        this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
//        this.goalSelector.addGoal(6, new EntityAIMoveTowardsRestriction(this, 0.6D));
        // 看向目标实体 Player
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        // 看向目标实体 WizardEntity
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, WizardEntity.class, 5.0F, 0.02F));
        // 随机漫步
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.6D));
        // 看向目标实体 LivingEntity
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, LivingEntity.class, 8.0F));
    }

    public static AttributeSupplier.@NonNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.MAX_HEALTH, 30.0);
    }

    private int getHealCooldown() {
        return this.entityData.get(HEAL_COOLDOWN);
    }

    @SuppressWarnings("SameParameterValue")
    private void setHealCooldown(int cooldown) {
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
    protected SoundEvent getAmbientSound() {
        if (Wizardry.tisTheSeason) {
            return WizardrySounds.ENTITY_WIZARD_HOHOHO.get();
        }
        return this.getTradingPlayer() != null
                ? WizardrySounds.ENTITY_WIZARD_TRADING.get()
                : WizardrySounds.ENTITY_WIZARD_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NonNull DamageSource source) {
        return WizardrySounds.ENTITY_WIZARD_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return WizardrySounds.ENTITY_WIZARD_DEATH.get();
    }

    @Override
    public @NonNull List<AbstractSpell> getSpells() {
        return this.spells;
    }

    @Override
    public void setContinuousSpell(@NonNull AbstractSpell spell) {
        this.entityData.set(CONTINUOUS_SPELL, spell.getIdentifier());
    }

    @Override
    public @NonNull AbstractSpell getContinuousSpell() {
        return WizardrySpells.get(this.entityData.get(CONTINUOUS_SPELL));
    }

    @Override
    public void setSpellCounter(int count) {
        this.entityData.set(SPELL_COUNTER, count);
    }

    @Override
    public int getSpellCounter() {
        return this.entityData.get(SPELL_COUNTER);
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

    @Override
    public @NonNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        this.trading = player;
    }

    @Override
    public @Nullable Player getTradingPlayer() {
        return this.trading;
    }

    public boolean stillValid(@NonNull Player player) {
        return this.getTradingPlayer() == player && this.isAlive() && player.isWithinEntityInteractionRange(this, LOOK_DISTANCE);
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
        // Won't trade with a player that has attacked them.
        if (this.isAlive()
                && !this.stillValid(player)
                && !this.isBaby()
                && !player.isShiftKeyDown()
                && this.getLastAttacker() != player
        ) {
            if (!this.level().isClientSide()) {
                this.setTradingPlayer(player);
                this.openTradingScreen(player, Component.empty(), 0);
            }
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.storeNullable("offers", MerchantOffers.CODEC, this.offers);

        ElementEnum element = this.getElement();
        valueOutput.putInt("element", element == null ? 0 : element.ordinal());
        valueOutput.putInt("skin", this.entityData.get(TEXTURE_INDEX));
        var spellsOutput = valueOutput.list("spells", AbstractSpell.CODEC);
        this.spells.forEach(spell ->
                spellsOutput.add(WizardrySpells.getHolder(spell.getIdentifier()))
        );

        if (this.towerBlocks != null && !this.towerBlocks.isEmpty()) {
            var blocksOutput = valueOutput.list("towerBlocks", BlockPos.CODEC);
            this.towerBlocks.forEach(blocksOutput::add);
        }
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);

        valueInput.read("offers", MerchantOffers.CODEC).ifPresent(offers -> this.offers = offers);

        this.setElement(ElementEnum.values()[valueInput.getIntOr("element", 0)]);
        this.entityData.set(TEXTURE_INDEX, valueInput.getIntOr("skin", 0));
        var spellsInput = valueInput.listOrEmpty("spells", AbstractSpell.CODEC);
        spellsInput.forEach(spell -> this.spells.add(spell.value()));

        var blocksInput = valueInput.listOrEmpty("towerBlocks", BlockPos.CODEC);
        blocksInput.forEach(blockPos -> this.towerBlocks.add(blockPos));
    }

    @Override
    public @NonNull MerchantOffers getOffers() {
        if (this.offers == null) {

            this.offers = new MerchantOffers();

            // 默认有使用法术书换魔力水晶
            ItemCost anySpellBook = new ItemCost(WizardryItems.SPELL_BOOK.get(), 1);
            ItemStack crystalStack = ItemStackHelper.getMagicCrystal(ElementEnum.DEFAULT, 5);

            this.offers.add(new MerchantOffer(
                    anySpellBook,
                    Optional.empty(),
                    crystalStack,
                    Integer.MAX_VALUE,
                    1,
                    1.0f
            ));

            // 随机商品
            this.addRandomRecipes(3);
        }

        return this.offers;
    }

    @SuppressWarnings("SameParameterValue")
    private void addRandomRecipes(int numberOfItemsToAdd) {

        MerchantOffers merchantOffers;
        merchantOffers = new MerchantOffers();

        for (int i = 0; i < numberOfItemsToAdd; i++) {

            ItemStack itemToSell = ItemStack.EMPTY;

            boolean itemAlreadySold = true;

            TierEnum tier = TierEnum.NOVICE;

            while (itemAlreadySold) {
                itemAlreadySold = false;
                /* 获取随机物品的新方式，
                根据玩家与巫师交易的次数来增加物品的等级。
                玩家与巫师交易的次数越多，
                获得更高等级物品的可能性就越大。
                -4表示忽略最初的4次交易。
                作为参考，
                概率如下：交易次数 基础 学徒 高级 大师
                0 50% 25% 18% 8%
                1 46% 25% 20% 9%
                2 42% 24% 22% 12%
                3 38% 24% 24% 14%
                4 34% 22% 26% 17%
                5 30% 21% 28% 21%
                6 26% 19% 30% 24%
                7 22% 17% 32% 28%
                8 18% 15% 34% 33%
                */
                double tierIncreaseChance = 0.5 + 0.04 * (Math.max(this.getVillagerXp() - 4, 0));

                tier = TierEnum.NOVICE;

                if (this.random.nextDouble() < tierIncreaseChance) {
                    tier = TierEnum.APPRENTICE;
                    if (this.random.nextDouble() < tierIncreaseChance) {
                        tier = TierEnum.ADVANCED;
                        if (this.random.nextDouble() < tierIncreaseChance * 0.6) {
                            tier = TierEnum.MASTER;
                        }
                    }
                }

                itemToSell = this.getRandomItemOfTier(tier);

                for (MerchantOffer offer : merchantOffers) {
                    if (ItemStack.isSameItem(offer.getResult(), itemToSell)) {
                        itemAlreadySold = true;
                    }
                }

                if (this.offers != null) {
                    for (MerchantOffer offer : this.offers) {
                        if (ItemStack.isSameItem(offer.getResult(), itemToSell)) {
                            itemAlreadySold = true;
                        }
                    }
                }
            }

            // Don't know how it can ever be empty here, but it's a failsafe.
            if (itemToSell.isEmpty()) {
                return;
            }
            ItemCost secondItemToBuy = tier == TierEnum.MASTER
                    ? new ItemCost(WizardryItems.ASTRAL_DIAMOND.get())
                    : new ItemCost(WizardryItems.MAGIC_CRYSTAL.get(), tier.ordinal() * 3 + 1 + this.random.nextInt(4))
                    .withComponents((builder) -> builder.expect(WizardryComponents.ELEMENT.get(), ElementEnum.DEFAULT));

            merchantOffers.add(new MerchantOffer(
                    this.getRandomPrice(tier),
                    Optional.of(secondItemToBuy),
                    itemToSell,
                    1,
                    Integer.MAX_VALUE,
                    1.0f
            ));
        }

        Collections.shuffle(merchantOffers);

        if (this.offers == null) {
            this.offers = new MerchantOffers();
        }

        this.offers.addAll(merchantOffers);
    }

    private @NonNull ItemCost getRandomPrice(TierEnum tier) {
        Optional<Currency> currencies = WizardryServerDataManager.getInstance()
                .getData(Identifier.fromNamespaceAndPath(Wizardry.MODID, "currency/wizardry_currency"), Currency.class);
        if (currencies.isPresent()) {
            List<Currency.UnpackEntry> itemCosts = currencies.get().unpack();
            Currency.UnpackEntry unpackEntry = itemCosts.get(this.random.nextInt(itemCosts.size()));
            return new ItemCost(unpackEntry.item(), Math.clamp((8 + tier.ordinal() * 16 + this.random.nextInt(9)) / unpackEntry.value(), 1, 64));
        } else {
            return new ItemCost(Items.EMERALD, 6);
        }
    }

    private ItemStack getRandomItemOfTier(TierEnum tier) {

        int randomiser;

        List<AbstractSpell> spells = WizardrySpells.getSpells(new TierElementFilter(tier, null, EnabledEnum.TRADES));
        List<AbstractSpell> specialismSpells = WizardrySpells.getSpells(new TierElementFilter(tier, this.getElement(), EnabledEnum.TRADES));

        spells.removeIf(s -> !s.isEnabled(EnabledEnum.BOOK));
        specialismSpells.removeIf(s -> !s.isEnabled(EnabledEnum.BOOK));

        switch (tier) {

            case NOVICE:
                randomiser = this.random.nextInt(5);
                if (randomiser < 4 && !spells.isEmpty()) {
                    return extractSpell(spells, specialismSpells);
                } else {
                    if (this.getElement() != ElementEnum.MAGIC && this.random.nextInt(4) > 0) {
                        return ItemStackHelper.getWand(tier, this.getElement());
                    } else {
                        return ItemStackHelper.getWand(tier, ElementEnum.values()[this.random.nextInt(ElementEnum.values().length)]);
                    }
                }

            case APPRENTICE:
                randomiser = this.random.nextInt(CommonConfig.discoveryMode ? 12 : 10);
                if (randomiser < 5 && !spells.isEmpty()) {
                    return extractSpell(spells, specialismSpells);
                } else if (randomiser < 6) {
                    if (this.getElement() != ElementEnum.MAGIC && this.random.nextInt(4) > 0) {
                        return ItemStackHelper.getWand(tier, this.getElement());
                    } else {
                        return ItemStackHelper.getWand(tier, ElementEnum.values()[this.random.nextInt(ElementEnum.values().length)]);
                    }
                } else if (randomiser < 8) {
                    return ItemStackHelper.getArcaneTome(TierEnum.DEFAULT, 1);
                } else if (randomiser < 10) {
                    ArmorType armorType = ArmorType.values()[this.random.nextInt(ArmorType.values().length)];
                    if (this.getElement() != ElementEnum.MAGIC && this.random.nextInt(4) > 0) {
                        return ItemStackHelper.generateArmour(
                                WizardryItems.ARMOUR.get(),
                                this.getElement(),
                                ArmourHelper.ArmourMaterialType.WIZARD,
                                armorType
                        );
                    } else {
                        return ItemStackHelper.generateArmour(
                                WizardryItems.ARMOUR.get(),
                                ElementEnum.values()[this.random.nextInt(ElementEnum.values().length)],
                                ArmourHelper.ArmourMaterialType.WIZARD,
                                armorType
                        );
                    }
                } else {
                    return new ItemStack(WizardryItems.IDENTIFICATION_SCROLL.get());
                }

            case ADVANCED:
                randomiser = this.random.nextInt(12);
                if (randomiser < 5 && !spells.isEmpty()) {
                    return extractSpell(spells, specialismSpells);
                } else if (randomiser < 6) {
                    if (this.getElement() != ElementEnum.MAGIC && this.random.nextInt(4) > 0) {
                        return ItemStackHelper.getWand(tier, this.getElement());
                    } else {
                        return ItemStackHelper.getWand(tier, ElementEnum.values()[this.random.nextInt(ElementEnum.values().length)]);
                    }
                } else if (randomiser < 8) {
                    return ItemStackHelper.getArcaneTome(TierEnum.APPRENTICE, 1);
                } else {
                    List<Item> upgrades = new ArrayList<>(ItemStackHelper.getSpecialUpgrades());
                    randomiser = this.random.nextInt(upgrades.size());
                    return new ItemStack(upgrades.get(randomiser));
                }

            case MASTER:
                randomiser = this.getElement() != ElementEnum.MAGIC ? this.random.nextInt(8) : 5 + this.random.nextInt(3);
                if (randomiser < 5 && this.getElement() != ElementEnum.MAGIC && !specialismSpells.isEmpty()) {
                    return ItemStackHelper.getSpellBook(specialismSpells.get(this.random.nextInt(specialismSpells.size())), 1);
                } else if (randomiser < 6) {
                    if (this.getElement() != ElementEnum.MAGIC && this.random.nextInt(4) > 0) {
                        return ItemStackHelper.getWand(tier, this.getElement());
                    } else {
                        return ItemStackHelper.getWand(TierEnum.DEFAULT, ElementEnum.DEFAULT);
                    }
                } else {
                    return ItemStackHelper.getArcaneTome(TierEnum.ADVANCED, 1);
                }
        }

        return new ItemStack(Blocks.STONE);
    }

    @NonNull
    private ItemStack extractSpell(List<AbstractSpell> spells, List<AbstractSpell> specialismSpells) {
        if (this.getElement() != ElementEnum.MAGIC && this.random.nextInt(4) > 0 && !specialismSpells.isEmpty()) {
            return ItemStackHelper.getSpellBook(specialismSpells.get(this.random.nextInt(specialismSpells.size())), 1);
        } else {
            return ItemStackHelper.getSpellBook(spells.get(this.random.nextInt(spells.size())), 1);
        }
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

        this.setHealCooldown(50);

        return groupData;
    }

    public void setTowerBlocks(Set<BlockPos> blocks) {
        this.towerBlocks = blocks;
    }

    public boolean isBlockPartOfTower(BlockPos pos) {
        if (this.towerBlocks == null) {
            return false;
        }
        return this.towerBlocks.contains(pos);
    }

    @Override
    public boolean hurtServer(
            @NonNull ServerLevel level,
            @NonNull DamageSource source,
            float damage
    ) {
        if (source.getEntity() instanceof ServerPlayer serverPlayer) {
            WizardryAdvancementTriggers.ANGER_WIZARD.get().triggerFor(serverPlayer);
        }
        return super.hurtServer(level, source, damage);
    }

    // TODO
    public static void onBlockBreakEvent(BlockEvent.@NonNull EntityPlaceEvent event) {
//        // Makes wizards angry if a player breaks a block in their tower
//        if(event.getEntity() instanceof ServerPlayer serverPlayer){
//            List<WizardEntity> wizards = EntityUtils.getEntitiesWithinRadius(
//                    64,
//                    event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(),
//                    event.getLevel(),
//                    WizardEntity.class
//            );
//            if(!wizards.isEmpty()){
//                for(WizardEntity wizard : wizards){
//                    if(wizard.isBlockPartOfTower(event.getPos())){
//                        wizard.setRevengeTarget(event.getPlayer());
//                        WizardryAdvancementTriggers.ANGER_WIZARD.get().triggerFor(serverPlayer);
//                    }
//                }
//            }
//        }
    }

    @Override
    public void overrideOffers(@NonNull MerchantOffers merchantOffers) {
        this.offers = merchantOffers;
    }

    @Override
    public void notifyTrade(@NonNull MerchantOffer merchantOffer) {

    }

    @Override
    public void notifyTradeUpdated(@NonNull ItemStack itemStack) {

    }

    @Override
    public int getVillagerXp() {
        return this.villagerXp;
    }

    @Override
    public void overrideXp(int villagerXp) {
        this.villagerXp = villagerXp;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public @NonNull SoundEvent getNotifyTradeSound() {
        return null;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }
}
