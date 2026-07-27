package top.begonia.wizardry.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

public final class WizardryTags {
    public static final TagKey<Item> BOOKS = create("books");

    private static @NonNull TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Wizardry.MODID, name));
    }
}
