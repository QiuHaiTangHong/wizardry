package top.begonia.wizardry.client.model.conditional;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.client.util.ClientHelper;
import top.begonia.wizardry.core.config.ClientConfig;
import top.begonia.wizardry.core.config.CommonConfig;
import top.begonia.wizardry.core.item.ArtefactItem;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

public class DiscoveredConditional implements ConditionalItemModelProperty {
    public static final MapCodec<DiscoveredConditional> CODEC = MapCodec.unit(new DiscoveredConditional());

    @Override
    public @NonNull MapCodec<? extends ConditionalItemModelProperty> type() {
        return CODEC;
    }

    @Override
    public boolean get(
            @NonNull ItemStack itemStack,
            @Nullable ClientLevel clientLevel,
            @Nullable LivingEntity livingEntity,
            int i,
            @NonNull ItemDisplayContext itemDisplayContext
    ) {
        if (!ClientConfig.spellBookColors) {
            return false;
        }
        AbstractSpell spell = itemStack.getOrDefault(WizardryComponents.SPELL.get(), WizardrySpells.NONE).value();
        boolean discovered = ClientHelper.shouldDisplayDiscovered(spell, null);

        if (discovered && CommonConfig.spellBookColorsRequireArchivistsEyeglass) {
            // Entity can be null so we have to check
            if (livingEntity instanceof Player player) {
                return ArtefactItem.isArtefactActive(player, WizardryItems.CHARM_SPELL_DISCOVERY.get());
            }
            // If there's no entity, there's no charm, so no colour
            return false;
        }

        return discovered;
    }
}
