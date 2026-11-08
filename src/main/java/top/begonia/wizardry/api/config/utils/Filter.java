package top.begonia.wizardry.api.config.utils;

import org.jspecify.annotations.Nullable;

/**
 * A filter callback to suppress certain elements from being shown in the configuration UI.
 * <p>
 * Return null to suppress the element or return a modified Element.
 */
public interface Filter {
    @Nullable Element filterEntry(Context context, String key, Element original);
}
