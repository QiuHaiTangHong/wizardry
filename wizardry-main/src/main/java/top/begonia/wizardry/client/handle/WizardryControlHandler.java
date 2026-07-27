package top.begonia.wizardry.client.handle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.client.constants.WizardryKeyMappings;
import top.begonia.wizardry.client.gui.SpellHud;
import top.begonia.wizardry.core.config.ClientConfig;
import top.begonia.wizardry.core.item.ISpellCastingItem;
import top.begonia.wizardry.core.network.data.ControlInputPayload;
import top.begonia.wizardry.core.network.data.SpellQuickAccessPayload;
import top.begonia.wizardry.core.registry.WizardrySounds;

public class WizardryControlHandler {

    private static boolean NkeyPressed = false;
    private static boolean BkeyPressed = false;
    private static final boolean[] quickAccessKeyPressed = new boolean[WizardryKeyMappings.SPELL_QUICK_ACCESS.length];

    // Changed to a tick event to allow mouse button keybinds
    // The 'lag' that happened previously was actually because the code only fired when a keyboard key was pressed!
    public static void onMouseScrollingEvent(InputEvent.MouseScrollingEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return;
        }
        ItemStack wand = getWandInUse(player);
        if (wand == null) {
            return;
        }

        if (minecraft.mouseHandler.isMouseGrabbed() && !wand.isEmpty() && event.getScrollDeltaY() != 0 && player.isShiftKeyDown() && ClientConfig.shiftScrolling) {
            event.setCanceled(true);
            double d = ClientConfig.reverseScrollDirection ? -event.getScrollDeltaY() : event.getScrollDeltaY();

            if (d > 0.0) {
                selectNextSpell(wand);
            } else if (d < 0.0) {
                selectPreviousSpell(wand);
            }
        }
    }

    public static void onClientTickEvent(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player != null) {

            ItemStack wand = getWandInUse(player);
            if (wand == null) return;

            if (WizardryKeyMappings.NEXT_SPELL.isDown() && minecraft.mouseHandler.isMouseGrabbed()) {
                if (!NkeyPressed) {
                    NkeyPressed = true;
                    selectNextSpell(wand);
                }
            } else {
                NkeyPressed = false;
            }

            if (WizardryKeyMappings.PREVIOUS_SPELL.isDown() && minecraft.mouseHandler.isMouseGrabbed()) {
                if (!BkeyPressed) {
                    BkeyPressed = true;
                    // Packet building
                    selectPreviousSpell(wand);
                }
            } else {
                BkeyPressed = false;
            }

            for (int i = 0; i < WizardryKeyMappings.SPELL_QUICK_ACCESS.length; i++) {
                if (WizardryKeyMappings.SPELL_QUICK_ACCESS[i].isDown() && minecraft.mouseHandler.isMouseGrabbed()) {
                    if (!quickAccessKeyPressed[i]) {
                        quickAccessKeyPressed[i] = true;
                        // Packet building
                        selectSpell(wand, i);
                    }
                } else {
                    quickAccessKeyPressed[i] = false;
                }
            }

        }
    }

    private static @Nullable ItemStack getWandInUse(@NonNull Player player) {

        ItemStack wand = player.getMainHandItem();

        // Only bother sending packets if the player is holding a spellcasting item with more than one spell slot
        if (!(wand.getItem() instanceof ISpellCastingItem mainISpellCastingItem) || mainISpellCastingItem.getSpells(wand).length < 2) {
            wand = player.getOffhandItem();
            if (!(wand.getItem() instanceof ISpellCastingItem offISpellCastingItem) || offISpellCastingItem.getSpells(wand).length < 2) {
                return null;
            }
        }
        return wand;
    }

    private static void selectNextSpell(@NonNull ItemStack wand) {
        // Packet building
        ClientPacketDistributor.sendToServer(new ControlInputPayload(ControlInputPayload.ControlType.NEXT_SPELL_KEY));
        // GUI switch animation
        ((ISpellCastingItem) wand.getItem()).selectNextSpell(wand); // Makes sure the spell is set immediately for the client
        SpellHud.playSpellSwitchAnimation(true);
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(WizardrySounds.ITEM_WAND_SWITCH_SPELL.get(), 1.0F)
        );
    }

    private static void selectPreviousSpell(@NonNull ItemStack wand) {
        // Packet building
        ClientPacketDistributor.sendToServer(new ControlInputPayload(ControlInputPayload.ControlType.PREVIOUS_SPELL_KEY));
        // GUI switch animation
        ((ISpellCastingItem) wand.getItem()).selectPreviousSpell(wand); // Makes sure the spell is set immediately for the client
        SpellHud.playSpellSwitchAnimation(false);
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(WizardrySounds.ITEM_WAND_SWITCH_SPELL.get(), 1.0F)
        );
    }

    private static void selectSpell(@NonNull ItemStack wand, int index) {
        // GUI switch animation
        if (((ISpellCastingItem) wand.getItem()).selectSpell(wand, index)) { // Makes sure the spell is set immediately for the client
            // Packet building (no point sending it unless the client-side spell selection succeeded
            ClientPacketDistributor.sendToServer(new SpellQuickAccessPayload(index));
            SpellHud.playSpellSwitchAnimation(true); // This will do, it's only an animation
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(WizardrySounds.ITEM_WAND_SWITCH_SPELL.get(), 1.0F)
            );
        }
    }

}
