package top.begonia.wizardry.core.entity.living.wizard.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
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
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.config.CommonConfig;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.EnabledEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.data.constant.WizardryServerDataManager;
import top.begonia.wizardry.core.data.constant.definition.currency.Currency;
import top.begonia.wizardry.api.entity.ai.goal.LookAtTradePlayer;
import top.begonia.wizardry.api.entity.ai.goal.TradePlayer;
import top.begonia.wizardry.api.entity.hybrid.ISummonedCreature;
import top.begonia.wizardry.core.entity.living.wizard.AbstractWizardEntity;
import top.begonia.wizardry.core.registry.*;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.*;

import java.util.*;

public class WizardEntity extends AbstractWizardEntity implements Merchant {
    public static final float LOOK_DISTANCE = 8.0f;
    public static final Identifier[] TEXTURES = new Identifier[6];
    protected @Nullable MerchantOffers offers;
    TargetingConditions.@Nullable Selector targetSelector;
    @Nullable
    private Player trading;
    private Set<BlockPos> towerBlocks;
    private int villagerXp = 0;
    private int timeUntilReset = 0;
    private boolean updateRecipes = false;

    public WizardEntity(EntityType<? extends WizardEntity> type, Level level) {
        super(type, level);
        if (level.isClientSide()) {
            for (int i = 0; i < TEXTURES.length; i++) {
                TEXTURES[i] = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/wizard/wizard_" + i + ".png");
            }
        }
    }

    public static void onBlockBreakEvent(@NonNull BreakBlockEvent event) {
        if (event.getPlayer() instanceof FakePlayer) {
            return;
        }
        List<WizardEntity> wizards = EntityUtils.getEntitiesWithinRadius(
                64,
                event.getPos().getX(),
                event.getPos().getY(),
                event.getPos().getZ(),
                event.getPlayer().level(),
                WizardEntity.class
        );

        if (!wizards.isEmpty()) {
            for (WizardEntity wizard : wizards) {
                if (wizard.isBlockPartOfTower(event.getPos())) {
                    wizard.setTarget(event.getPlayer());
                    WizardryAdvancementTriggers.ANGER_WIZARD.get().triggerFor(event.getPlayer());
                }
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new TradePlayer(this));
        this.goalSelector.addGoal(1, new LookAtTradePlayer(this, LOOK_DISTANCE));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, WizardEntity.class, 5.0F, 0.02F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, LivingEntity.class, 8.0F));

        this.targetSelector = (entity, _) -> {
            if (!entity.isInvisible()
                    && AllyDesignationSystem.isValidTarget(WizardEntity.this, entity)
            ) {
                return (
                        entity instanceof Mob
                                && !(entity instanceof ISummonedCreature)
                                || entity instanceof ISummonedCreature
                                && (((ISummonedCreature) entity).getOwner() instanceof Mob
                                || ((ISummonedCreature) entity).getOwner() == this.getLastHurtByMob()
                                || ((ISummonedCreature) entity).getOwner() == this.getTarget())
                                || ServerConfig.summonedCreatureTargetsWhitelist
                                .contains(EntityUtils.getIdentifier(entity))
                )
                        && !ServerConfig.summonedCreatureTargetsBlacklist
                        .contains(EntityUtils.getIdentifier(entity));
            }

            return false;
        };

        this.goalSelector.addGoal(1, new HurtByTargetGoal(this));
        // By default, wizards don't attack players unless the player has attacked them.
        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                0,
                false,
                true,
                this.targetSelector
        ));
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.storeNullable("offers", MerchantOffers.CODEC, this.offers);
        if (this.towerBlocks != null && !this.towerBlocks.isEmpty()) {
            var blocksOutput = valueOutput.list("towerBlocks", BlockPos.CODEC);
            this.towerBlocks.forEach(blocksOutput::add);
        }
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        valueInput.read("offers", MerchantOffers.CODEC).ifPresent(offers -> this.offers = offers);
        var blocksInput = valueInput.listOrEmpty("towerBlocks", BlockPos.CODEC);
        blocksInput.forEach(blockPos -> this.towerBlocks.add(blockPos));
    }

    public @Nullable SpawnGroupData finalizeSpawn(
            @NonNull ServerLevelAccessor level,
            @NonNull DifficultyInstance difficulty,
            @NonNull EntitySpawnReason spawnReason,
            @Nullable SpawnGroupData groupData
    ) {
        groupData = super.finalizeSpawn(level, difficulty, spawnReason, groupData);
        this.setHealCooldown(50);
        return groupData;
    }

    // 替代 processInteract
    protected @NonNull InteractionResult mobInteract(@NonNull Player player, @NonNull InteractionHand hand) {
        super.mobInteract(player, hand);
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
    public Identifier[] getWizardTextures() {
        return TEXTURES;
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        this.trading = player;
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
    public @Nullable Player getTradingPlayer() {
        return this.trading;
    }

    @Override
    protected void customServerAiStep(@NonNull ServerLevel level) {
        if (this.trading != null && this.timeUntilReset > 0) {
            --this.timeUntilReset;
            if (this.timeUntilReset <= 0) {
                if (this.updateRecipes) {
                    MerchantOffers offers = this.getOffers();
                    for (MerchantOffer offer : offers) {
                        if (offer.isOutOfStock()) {
                            offer.resetUses();
                        }
                    }
                    if (offers.size() < 12) {
                        this.addRandomRecipes(1);
                    }
                    this.updateRecipes = false;
                }
                this.addEffect(
                        new MobEffectInstance(
                                MobEffects.REGENERATION,
                                200,
                                0
                        )
                );
            }
        }
        super.customServerAiStep(level);
    }

    public boolean stillValid(@NonNull Player player) {
        return this.getTradingPlayer() != null && this.isAlive() && player.isWithinEntityInteractionRange(this, LOOK_DISTANCE);
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

    @SuppressWarnings("unused")
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

    @Override
    protected SoundEvent getHurtSound(@NonNull DamageSource source) {
        return WizardrySounds.ENTITY_WIZARD_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return WizardrySounds.ENTITY_WIZARD_DEATH.get();
    }


    @Override
    public void overrideOffers(@NonNull MerchantOffers merchantOffers) {
        this.offers = merchantOffers;
    }

    @Override
    public void notifyTrade(@NonNull MerchantOffer merchantOffer) {
        if (merchantOffer.isOutOfStock()) {
            this.timeUntilReset = 40;
            this.updateRecipes = true;
        }
    }

    @Override
    public void notifyTradeUpdated(@NonNull ItemStack itemStack) {
        // Copied from EntityVillager
        if (this.level().isClientSide()) {
            return;
        }
        if (itemStack.isEmpty()) {
            this.playSound(
                    WizardrySounds.ENTITY_WIZARD_NO.get(),
                    this.getSoundVolume(),
                    this.getVoicePitch()
            );
        }
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
        return Wizardry.tisTheSeason
                ? WizardrySounds.ENTITY_WIZARD_HOHOHO.get()
                : WizardrySounds.ENTITY_WIZARD_YES.get();
    }

    @Override
    public boolean isClientSide() {
        return false;
    }
}
