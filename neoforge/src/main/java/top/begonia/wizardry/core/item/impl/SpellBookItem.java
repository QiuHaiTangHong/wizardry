package top.begonia.wizardry.core.item.impl;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.gui.SpellBookScreen;
import top.begonia.wizardry.client.util.ClientHelper;
import top.begonia.wizardry.client.util.GlyphGenerator;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.Map;
import java.util.function.Consumer;

@EventBusSubscriber(modid = Wizardry.MODID)
public class SpellBookItem extends Item {
    private static final Map<TierEnum, Identifier> GUI_TEXTURES = Map.of(
            TierEnum.NOVICE, Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/spell_book_novice.png"),
            TierEnum.APPRENTICE, Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/spell_book_apprentice.png"),
            TierEnum.ADVANCED, Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/spell_book_advanced.png"),
            TierEnum.MASTER, Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/spell_book_master.png")
    );

    public SpellBookItem(Item.Properties properties) {
        super(properties);
    }

    public AbstractSpell getCurrentSpell(@NonNull ItemStack itemStack) {
        Holder<AbstractSpell> abstractSpellHolder = itemStack.get(WizardryComponents.SPELL);
        return abstractSpellHolder != null ? abstractSpellHolder.value() : WizardrySpells.NONE.get();
    }

    public AbstractSpell getCurrentSpell(@NonNull ItemResource itemResource) {
        Holder<AbstractSpell> abstractSpellHolder = itemResource.get(WizardryComponents.SPELL);
        return abstractSpellHolder != null ? abstractSpellHolder.value() : WizardrySpells.NONE.get();
    }

    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            Minecraft.getInstance().gui.setScreen(new SpellBookScreen(stack));
        }
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    public void appendHoverText(
            @NonNull ItemStack itemStack,
            @NonNull TooltipContext context,
            @NonNull TooltipDisplay display,
            @NonNull Consumer<Component> builder,
            @NonNull TooltipFlag tooltipFlag
    ) {
        AbstractSpell spell = this.getCurrentSpell(itemStack);
        Level level = context.level();
        Player player = Minecraft.getInstance().player;
        boolean discovered = ClientHelper.shouldDisplayDiscovered(spell, itemStack);
        builder.accept(discovered ? spell.getDisplayNameWithFormatting() : GlyphGenerator.getGlyphName(spell).withStyle(ChatFormatting.GRAY));
        builder.accept(spell.getTier().getDisplayNameWithFormatting());
    }

    public Identifier getGuiTexture(AbstractSpell spell) {
        return GUI_TEXTURES.get(spell.getTier());
    }
}
