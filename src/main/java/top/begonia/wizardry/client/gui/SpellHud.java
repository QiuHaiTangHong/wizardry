package top.begonia.wizardry.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.hub.SpellHubDescriptionData;
import top.begonia.wizardry.client.data.manager.WizardryClientDataManager;
import top.begonia.wizardry.client.gui.widget.Skin;
import top.begonia.wizardry.client.util.DrawingUtils;
import top.begonia.wizardry.client.util.GlyphGenerator;
import top.begonia.wizardry.core.config.ClientConfig;
import top.begonia.wizardry.core.item.ISpellCastingItem;
import top.begonia.wizardry.core.registry.WizardryAttachment;
import top.begonia.wizardry.core.registry.WizardryMobEffects;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

import javax.annotation.Nullable;
import java.util.*;

public class SpellHud {
    /**
     * 法术充能计量表的纹理标识符, 指向 gui 目录下的 spell_charge_meter.png 图片资源
     */
    private static final Identifier CHARGE_METER = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/spell_charge_meter.png");
    /**
     * Width of the charge meter.
     */
    private static final int CHARGE_METER_WIDTH = 25;
    /**
     * Height of the charge meter.
     */
    private static final int CHARGE_METER_HEIGHT = 9;
    /**
     * Number of ticks the spell switching animation plays for.
     */
    private static final int SPELL_SWITCH_TIME = 4;
    private static final int HALF_HOTBAR_WIDTH = 97;
    private static final int OFFHAND_SLOT_WIDTH = 29;
    private static final Map<String, Skin> skins = new HashMap<>();
    private static int switchTimer = 0;

    public static void playSpellSwitchAnimation(boolean next) {
        switchTimer = next ? SPELL_SWITCH_TIME : -SPELL_SWITCH_TIME;
    }

    public static void renderSpellHUD(GuiGraphicsExtractor guiGraphicsExtractor, Player player, ItemStack wand, boolean mainHand, int width, int height, float partialTicks, boolean textLayer) {

        if (!ClientConfig.showSpellHUD) return;

        if (!(wand.getItem() instanceof ISpellCastingItem)) {
            throw new IllegalArgumentException("The given stack must contain an ISpellCastingItem!");
        }

        boolean flipX = ClientConfig.spellHUDPosition.flipX;
        boolean flipY = ClientConfig.spellHUDPosition.flipY;

        if (ClientConfig.spellHUDPosition.dynamic) {
            flipX = flipX == ((mainHand ? player.getMainArm() : player.getMainArm().getOpposite()) == HumanoidArm.LEFT);
        }

        Skin skin = getSkin(ClientConfig.spellHUDSkin);

        if (skin == null) {
            return;
        }

        Matrix3x2fStack poseStack = guiGraphicsExtractor.pose();
        poseStack.pushMatrix();

        int x = flipX ? width : 0;
        int y = flipY ? 0 : height;

        float xSpace = (float) (width / 2 - HALF_HOTBAR_WIDTH);
        if (!player.getOffhandItem().isEmpty()
                && (player.getMainArm() == HumanoidArm.LEFT) == flipX) {
            xSpace -= OFFHAND_SLOT_WIDTH;
        }
        if (!flipY && skin.getWidth() > xSpace) {
            float scale = xSpace / (float) skin.getWidth();
            poseStack.scale(scale, scale);
            x = Mth.ceil(x / scale);
            y = Mth.ceil(y / scale);
        }

        AbstractSpell spell = ((ISpellCastingItem) wand.getItem()).getCurrentSpell(wand);
        int cooldown = ((ISpellCastingItem) wand.getItem()).getCurrentCooldown(wand);
        int maxCooldown = ((ISpellCastingItem) wand.getItem()).getCurrentMaxCooldown(wand);

        if (textLayer) {

            float animationProgress = Math.signum(switchTimer) * ((SPELL_SWITCH_TIME - Math.abs(switchTimer) +
                    partialTicks) / SPELL_SWITCH_TIME);

            Component prevSpellName = getFormattedSpellName(((ISpellCastingItem) wand.getItem()).getPreviousSpell(wand), player, 0);
            Component spellName = getFormattedSpellName(spell, player, cooldown);
            Component nextSpellName = getFormattedSpellName(((ISpellCastingItem) wand.getItem()).getNextSpell(wand), player, 0);

            skin.drawText(
                    guiGraphicsExtractor,
                    x, y,
                    flipX, flipY,
                    prevSpellName, spellName,
                    nextSpellName, animationProgress
            );

        } else {

            boolean discovered = true;

            if (!player.isCreative()) {
                discovered = player.getData(WizardryAttachment.WIZARD_PLAYER_DATA.get()).hasSpellBeenDiscovered(spell);
            }

            Identifier icon = discovered ? spell.getIcon() : WizardrySpells.NONE.get().getIcon();

            float progress = 1;
            if (!player.isCreative()) {
                progress = maxCooldown == 0 ? 1 : (maxCooldown - (float) cooldown + partialTicks) / maxCooldown;
            }

            skin.drawBackground(
                    guiGraphicsExtractor,
                    x, y,
                    flipX, flipY,
                    icon, progress,
                    player.isCreative(), player.hasEffect(WizardryMobEffects.ARCANE_JAMMER)
            );
        }
        poseStack.popMatrix();
    }

    public static void renderChargeMeter(GuiGraphicsExtractor guiGraphicsExtractor, Player player, ItemStack wand, int width, int height, float partialTicks) {

        if (!ClientConfig.showChargeMeter) {
            return;
        }
        if (Minecraft.getInstance().getDebugOverlay().showDebugScreen()) {
            return;
        }
        if (Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON) {
            return;
        }
        if (wand != player.getUseItem()) {
            return;
        }

        if (!(wand.getItem() instanceof ISpellCastingItem)) {
            throw new IllegalArgumentException("The given stack must contain an ISpellCastingItem!");
        }

        AbstractSpell spell = ((ISpellCastingItem) wand.getItem()).getCurrentSpell(wand);

        int chargeUp = spell.getChargeUp();

        //TODO
//        if (WizardData.get(player) != null) {
//            chargeUp = (int) (chargeUp * WizardData.get(player).itemCastingModifiers.get(SpellModifiers.CHARGEUP));
//        }

        if (chargeUp <= 0) {
            return;
        }
        if (player.getTicksUsingItem() == 0) {
            return;
        }
        float charge = (player.getTicksUsingItem() + partialTicks) / chargeUp;
        if (charge > 1) {
            return;
        }

        int x1 = width / 2 - CHARGE_METER_WIDTH / 2;
        int y = height / 2 - CHARGE_METER_HEIGHT / 2;
        int w = (int) ((float) CHARGE_METER_WIDTH / 2 * charge);
        int u = CHARGE_METER_WIDTH - w;

        DrawingUtils.drawTexturedRect(
                guiGraphicsExtractor, CHARGE_METER,
                x1, y,
                0, 0,
                w, CHARGE_METER_HEIGHT,
                32, 32
        );
        DrawingUtils.drawTexturedRect(
                guiGraphicsExtractor, CHARGE_METER,
                x1 + u, y,
                u, 0,
                w, CHARGE_METER_HEIGHT,
                32, 32
        );

    }

    @Nullable
    public static Skin getSkin(String key) {
        if (skins.isEmpty()) {
            Map<Identifier, SpellHubDescriptionData> spellHudDesc = WizardryClientDataManager.getInstance().getAllDataByType(SpellHubDescriptionData.class);
            spellHudDesc.forEach((_, spellHubDescriptionData) ->
                    spellHubDescriptionData.item().forEach((name, value) ->
                            skins.put(name, new Skin(value.texture(), value.metadata()))
                    )
            );
        }

        Skin skin = skins.get(key);
        if (skin == null) {

            Wizardry.LOGGER.info("The spell HUD skin '{}' specified in the config did not match any of the loaded skins; using the default skin as a fallback.", ClientConfig.spellHUDSkin);

            skin = skins.get(ClientConfig.DEFAULT_HUD_SKIN_KEY);

            if (skin == null) {
                Wizardry.LOGGER.warn("The default spell HUD skin is missing! A resource pack must have overridden it"
                        + " with an invalid JSON file (default.json), please try again without any resource packs.");
                return null;
            }
        }
        return skin;
    }

    private static Component getFormattedSpellName(AbstractSpell spell, @NonNull Player player, int cooldown) {

        boolean discovered = true;

        if (!player.isCreative()) {
            discovered = player.getData(WizardryAttachment.WIZARD_PLAYER_DATA.get()).hasSpellBeenDiscovered(spell);
        }
        Style format = cooldown > 0 ? Style.EMPTY.withColor(ChatFormatting.DARK_GRAY) : spell.getElement().getStyle();
        if (!discovered) {
            format = Style.EMPTY.withColor(ChatFormatting.BLUE);
        }
        if (player.hasEffect(WizardryMobEffects.ARCANE_JAMMER)) {
            format.withObfuscated(true);
        }

        MutableComponent name = discovered ? spell.getDisplayName() : Component.literal(GlyphGenerator.getGlyphName(spell));
        name.withStyle(format);
        if (!discovered) {
            name = Component.literal("#").append(name).append("#");
        }
        return name;
    }
}
