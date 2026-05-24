package top.begonia.wizardry.core.item.impl;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.api.event.SpellCastEvent;
import top.begonia.wizardry.core.data.runtime.SpellContextFlow;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.item.ISpellCastingItem;
import top.begonia.wizardry.core.item.IWorkbenchItem;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.CommonHelper;

import java.util.function.Consumer;

public class ScrollItem extends Item implements ISpellCastingItem, IWorkbenchItem {
    public static final int CASTING_TIME = 120;

    public ScrollItem(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        AbstractSpell spell = this.getCurrentSpell(itemStack);
        builder.accept(spell.getTier().getDisplayName().withStyle(ChatFormatting.GRAY));
        builder.accept(spell.getElement().getDisplayName().withStyle(ChatFormatting.GRAY));
        builder.accept(spell.getType().getDisplayName().withStyle(ChatFormatting.GRAY));
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        ItemStack handItem = player.getItemInHand(hand);
        AbstractSpell spell = this.getCurrentSpell(handItem);
        SpellContextFlow spellContextFlow = SpellContextFlow.create(player, hand, handItem, spell);
        if (this.canCast(0, spellContextFlow)) {
            if (spell.isContinuous) {

            } else if (this.cast(handItem, spell, player, hand, 0, spellContextFlow.packing())) {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public @NonNull AbstractSpell getCurrentSpell(@NonNull ItemStack stack) {
        Holder<AbstractSpell> holder = stack.get(WizardryComponents.SPELL);
        return (holder != null) ? holder.value() : WizardrySpells.NONE.get();
    }

    @Override
    public boolean isFoil(@NonNull ItemStack stack) {
        return true;
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack stack) {
        return CommonHelper.getScrollDisplayName(stack);
    }

    @Override
    public boolean showSpellHUD(Player player, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canCast(int castingTick, SpellContextFlow spellContextFlow) {
        if (castingTick == 0) {
            return !NeoForge.EVENT_BUS.post(new SpellCastEvent.Pre(SpellCastEvent.Source.SCROLL, spellContextFlow)).isCanceled();
        } else {
            return !NeoForge.EVENT_BUS.post(new SpellCastEvent.Tick(SpellCastEvent.Source.SCROLL, spellContextFlow, castingTick)).isCanceled();
        }
    }

    @Override
    public boolean cast(ItemStack stack, AbstractSpell spell, @NonNull Player caster, InteractionHand hand, int castingTick, SpellContext spellContext) {
        Level level = caster.level();

        if (level.isClientSide() && !spell.isContinuous && spell.requiresPacket()) {
            return false;
        }

        if (spell.cast(level, caster, hand, castingTick, spellContext)) {

            if (castingTick == 0) {
                NeoForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.SCROLL, spell, caster, spellContext));
            }

            if (!level.isClientSide()) {
//                if (!spell.isContinuous && spell.requiresPacket()) {
//                    IMessage msg = new PacketCastSpell.Message(caster.getEntityId(), hand, spell, modifiers);
//                    WizardryPacketHandler.net.sendToDimension(msg, world.provider.getDimension());
//                }

                if (!spell.isContinuous && !caster.isCreative()) {
                    stack.shrink(1);
                }

                if (!spell.isContinuous && !caster.isCreative()) {
                    caster.getCooldowns().addCooldown(stack, spell.getCooldown());
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        return false;
    }

    @Override
    public boolean showTooltip(ItemStack stack) {
        return false;
    }
}
