package top.begonia.wizardry.core.api.event.data;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import top.begonia.wizardry.core.api.data.IDataParser;

import java.util.Map;

public class ClientRegisterDataParserEvent extends Event implements IModBusEvent {
    private final Map<Identifier, IDataParser<?, ?, ?>> registry;

    public ClientRegisterDataParserEvent(Map<Identifier, IDataParser<?, ?, ?>> registry) {
        this.registry = registry;
    }

    public void register(IDataParser<?, ?, ?> parser) {
        Identifier id = parser.getIdentifier();
        if (id != null) {
            this.registry.put(id, parser);
        }
    }
}
