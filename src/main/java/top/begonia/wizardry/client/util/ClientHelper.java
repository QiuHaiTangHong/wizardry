package top.begonia.wizardry.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.begonia.wizardry.common.config.CommonConfig;
import top.begonia.wizardry.common.data.player.WizardPlayerData;
import top.begonia.wizardry.common.registry.WizardryAttachment;
import top.begonia.wizardry.common.spell.AbstractSpell;

import javax.annotation.Nullable;

public class ClientHelper {
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
        WizardPlayerData data = player.getData(WizardryAttachment.WIZARD_PLAYER_DATA.get());
        return false;
    }
}
