package top.begonia.wizardry.core.inventory.settings;

import net.minecraft.resources.Identifier;
import top.begonia.wizardry.Wizardry;

public record ArcaneWorkbenchSettings(
        GuiPartSettings mainGui,
        Identifier crystalSlotIcon,
        Identifier upgradeSlotIcon,
        int crystalSlot,
        int centreSlot,
        int upgradeSlot,
        int slotRadius,
        int bookshelfSlotsX,
        int bookshelfSlotsY,
        int bookshelfUiWidth,
        int playerInvSize,
        ButtonSettings applyButton,
        ButtonSettings cleanButton
) {
    public static final Identifier MAIN_TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/container/arcane_workbench.png");
    public static final ArcaneWorkbenchSettings DEFAULT = new ArcaneWorkbenchSettings(
            new GuiPartSettings(
                    MAIN_TEXTURE,
                    176, 220,
                    0, 0,
                    512, 512
            ),
            Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/container/empty_slot_crystal.png"),
            Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/container/empty_slot_upgrade.png"),
            8, 9, 10,
            42, 5, 10, 122, 36,
            new ButtonSettings(
                    MAIN_TEXTURE,
                    16, 16,
                    72, 220,
                    512, 512
            ),
            new ButtonSettings(
                    MAIN_TEXTURE,
                    16, 16,
                    72, 236,
                    512, 512
            )
    );

    public record ButtonSettings(
            Identifier identifier,
            int buttonWidthSize,
            int buttonHeightSize,
            int buttonU,
            int buttonV,
            int textureWidth,
            int textureHeight
    ) {
    }

    public record GuiPartSettings(
            Identifier identifier,
            int guiWidth, int guiHeight,
            int textureU, int textureV,
            int textureWidth, int textureHeight
    ) {
    }
}
