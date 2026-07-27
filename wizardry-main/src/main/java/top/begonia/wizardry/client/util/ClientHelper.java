package top.begonia.wizardry.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.begonia.wizardry.core.config.CommonConfig;
import top.begonia.wizardry.core.registry.WizardryAttachment;
import top.begonia.wizardry.core.spell.AbstractSpell;

import javax.annotation.Nullable;

public final class ClientHelper {
    public static boolean shouldDisplayDiscovered(AbstractSpell spell, @Nullable ItemStack stack) {
        if (!CommonConfig.discoveryMode) {
            return true;
        }
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        if (player.isCreative()) {
            return true;
        }
        return player.getData(WizardryAttachment.WIZARD_PLAYER_DATA.get()).hasSpellBeenDiscovered(spell);
    }

    public static boolean isFirstPerson(Entity entity) {
        return entity == Minecraft.getInstance().getCameraEntity() && Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }
}
