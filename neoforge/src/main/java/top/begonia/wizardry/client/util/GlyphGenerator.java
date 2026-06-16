package top.begonia.wizardry.client.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.HashMap;
import java.util.Map;

public final class GlyphGenerator {
    private static final Map<Identifier, MutableComponent> randomNames = new HashMap<>();
    private static final Map<Identifier, MutableComponent> randomDescriptions = new HashMap<>();
    private static final FontDescription enchantFont = new FontDescription.Resource(Identifier.withDefaultNamespace("alt"));
    private static final Style defaultStyle = Style.EMPTY.withFont(enchantFont);

    private GlyphGenerator() {
    }

    public static FontDescription getFontDescription() {
        return enchantFont;
    }

    public static void update(Map<Identifier, String> names, Map<Identifier, String> descriptions) {
        names.forEach((identifier, value) -> randomNames.put(identifier, Component.literal(value).withStyle(defaultStyle)));
        descriptions.forEach((identifier, value) -> randomDescriptions.put(identifier, Component.literal(value).withStyle(defaultStyle)));
    }

    public static MutableComponent getGlyphName(AbstractSpell spell) {
        Identifier id = WizardrySpells.SPELLS.getRegistry().get().getKey(spell);
        return id != null ? randomNames.getOrDefault(id, Component.literal("unknown")) : Component.empty();
    }

    public static MutableComponent getGlyphDescription(AbstractSpell spell) {
        Identifier id = WizardrySpells.SPELLS.getRegistry().get().getKey(spell);
        return id != null ? randomDescriptions.getOrDefault(id, Component.literal("unknown description")) : Component.empty();
    }
}
