package top.begonia.wizardry.client.constants;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;
import top.begonia.wizardry.Wizardry;

public class WizardryKeyMappings {
    public static final KeyMapping.Category WIZARDRY_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(Wizardry.MODID, "main")
    );

    public static final KeyMapping NEXT_SPELL = new KeyMapping(
            "key." + Wizardry.MODID + ".next_spell",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            WIZARDRY_CATEGORY
    );
    public static final KeyMapping PREVIOUS_SPELL = new KeyMapping(
            "key." + Wizardry.MODID + ".previous_spell",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            WIZARDRY_CATEGORY
    );
    public static final KeyMapping[] SPELL_QUICK_ACCESS = new KeyMapping[3];

    static {
        for (int i = 0; i < SPELL_QUICK_ACCESS.length; i++) {
            SPELL_QUICK_ACCESS[i] = new KeyMapping(
                    "key." + Wizardry.MODID + ".spell_" + (i + 1),
                    KeyConflictContext.IN_GAME,
                    KeyModifier.ALT,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_1 + i,
                    WIZARDRY_CATEGORY
            );
        }
    }

    public static void register(@NonNull RegisterKeyMappingsEvent event) {
        event.register(WizardryKeyMappings.NEXT_SPELL);
        event.register(WizardryKeyMappings.PREVIOUS_SPELL);
        for (KeyMapping qaMapping : SPELL_QUICK_ACCESS) {
            event.register(qaMapping);
        }
    }
}
