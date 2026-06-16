package top.begonia.wizardry.core.api.data.event;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import top.begonia.wizardry.core.api.data.IParserContext;

import java.util.Map;
import java.util.function.Function;

public class DataParserBefore extends Event implements IModBusEvent {
    private final Map<Identifier, IParserContext> parserContexts;

    public DataParserBefore(Map<Identifier, IParserContext> parserContexts) {
        this.parserContexts = parserContexts;
    }

    public <T extends IParserContext> void registry(Identifier identifier, Function<Identifier, T> factory) {
        this.parserContexts.computeIfAbsent(identifier, factory);
    }
}
