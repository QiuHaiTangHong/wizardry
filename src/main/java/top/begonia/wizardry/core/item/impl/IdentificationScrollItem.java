package top.begonia.wizardry.core.item.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.api.event.DiscoverSpellEvent;
import top.begonia.wizardry.core.data.player.WizardPlayerData;
import top.begonia.wizardry.core.registry.WizardryAttachment;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.TooltipBuilder;

import java.util.function.Consumer;

/**
 * <p>
 * 鉴定卷轴物品的实现类
 * </p>
 * 自 2026.6.14 迁移自 ItemIdentificationScroll
 *
 * @author 秋海棠红
 * @since 1.0.0
 */
public class IdentificationScrollItem extends Item {
    public IdentificationScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(@NonNull ItemStack itemStack) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        TooltipBuilder.addMultiLineDescription(
                builder,
                this.getDescriptionId() + ".desc",
                Style.EMPTY
        );
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        ItemStack playerItemInHand = player.getItemInHand(hand);
        WizardPlayerData playerData = player.getData(WizardryAttachment.WIZARD_PLAYER_DATA);
        for (ItemStack itemStack : player.getInventory().getNonEquipmentItems()) {
            if (!itemStack.isEmpty()) {
                AbstractSpell spell = itemStack.getOrDefault(WizardryComponents.SPELL.get(), WizardrySpells.NONE).value();
                if ((itemStack.getItem() instanceof SpellBookItem || itemStack.getItem() instanceof ScrollItem) && !playerData.hasSpellBeenDiscovered(spell)) {
                    if (!NeoForge.EVENT_BUS.post(new DiscoverSpellEvent(player, spell, DiscoverSpellEvent.Source.IDENTIFICATION_SCROLL)).isCanceled()) {
                        playerData.discoverSpell(spell);
                        player.playSound(WizardrySounds.MISC_DISCOVER_SPELL.get(), 1.25f, 1);
                        if (!player.isCreative()) playerItemInHand.shrink(1);
                        if (!level.isClientSide()) {
                            player.sendSystemMessage(Component.translatable("spell.discover", spell.getDisplayNameWithFormatting()));
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        if (!level.isClientSide()) {
            player.sendSystemMessage(Component.translatable("item." + Wizardry.MODID + ".identification_scroll.nothing_to_identify"));
        }
        return InteractionResult.FAIL;
    }
}
