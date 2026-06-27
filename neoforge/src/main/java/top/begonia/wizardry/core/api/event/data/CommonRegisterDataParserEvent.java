package top.begonia.wizardry.core.api.event.data;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.api.data.IDataParser;

import java.util.Map;

public class CommonRegisterDataParserEvent extends Event implements IModBusEvent {
    private final Map<Identifier, IDataParser<?, ?, ?>> registry;

    public CommonRegisterDataParserEvent(Map<Identifier, IDataParser<?, ?, ?>> registry) {
        this.registry = registry;
    }

    public void register(@NonNull IDataParser<?, ?, ?> parser) {
        Identifier id = parser.getIdentifier();
        if (id != null) {
            this.registry.put(id, parser);
        }
    }
}
