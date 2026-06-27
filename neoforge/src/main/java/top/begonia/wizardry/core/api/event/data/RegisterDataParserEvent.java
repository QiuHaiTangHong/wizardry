package top.begonia.wizardry.core.api.event.data;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.api.data.IDataParser;

import java.util.Map;

/**
 * 数据解析器注册事件
 * <p> 用于处理 {@code IDataParser} 实例的注册逻辑, 作为 {@link Event} 事件总线的一部分
 * 实现了 {@link IModBusEvent} 接口, 提供与模组事件总线的集成
 * <p> 定义了两种注册作用域的子类:
 * <ul>
 *   <li>{@link ClientRegisterDataParserEvent} - 客户端侧注册事件 </li>
 *   <li>{@link CommonRegisterDataParserEvent} - 通用 / 双端侧注册事件 </li>
 * </ul>
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @date 2026.06.27
 */
public class RegisterDataParserEvent extends Event implements IModBusEvent {
    /**
     * 客户端数据解析器注册事件类
     * <p> 继承自 {@code RegisterDataParserEvent}, 负责将客户端数据解析器注册到指定的注册表中
     * <p> 该类封装了注册逻辑, 通过 {@link #register(IDataParser)} 方法将解析器及其标识符存入 {@code registry} 映射,
     * 以实现数据解析器的集中管理和查找
     * <p> 主要使用场景:
     * <ul>
     *   <li> 在客户端初始化时注册所有可用的数据解析器 </li>
     *   <li> 作为桥梁连接具体的解析器实现与注册表容器 </li>
     * </ul>
     *
     * @author 秋海棠红
     * @version 1.0.0
     * @date 2026.06.27
     * @since 1.0.0
     */
    public static class ClientRegisterDataParserEvent extends RegisterDataParserEvent {
        private final Map<Identifier, IDataParser<?, ?, ?>> registry;

        public ClientRegisterDataParserEvent(Map<Identifier, IDataParser<?, ?, ?>> registry) {
            this.registry = registry;
        }

        public void register(@NonNull IDataParser<?, ?, ?> parser) {
            Identifier id = parser.getIdentifier();
            if (id != null) {
                this.registry.put(id, parser);
            }
        }
    }

    public static class CommonRegisterDataParserEvent extends RegisterDataParserEvent {
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
}
