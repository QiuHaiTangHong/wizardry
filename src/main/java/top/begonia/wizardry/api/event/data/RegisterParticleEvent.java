package top.begonia.wizardry.api.event.data;

import net.neoforged.bus.api.Event;
import top.begonia.wizardry.api.particle.manager.SimpleParticleProvider;
import top.begonia.wizardry.api.particle.manager.WizardryParticleProvider;
import top.begonia.wizardry.api.particle.options.IParticleOptionsExtension;
import top.begonia.wizardry.api.particle.options.impl.QuadParticleOptions;
import top.begonia.wizardry.api.particle.type.ParticleTypeExtension;

import java.util.Map;

/**
 * 用于注册自定义粒子提供程序的事件。
 * <p>
 * 此事件在模组初始化阶段触发，允许开发者将自定义的 {@link WizardryParticleProvider}
 * 或 {@link SimpleParticleProvider} 注册到系统的粒子管理器中。
 * </p>
 * <h3>使用示例</h3>
 * <pre>{@code
 * @SubscribeEvent
 * public void onRegisterParticles(RegisterParticleEvent event) {
 *     // 方式 1：注册简单粒子（适用于基于 QuadParticleOptions 的标准粒子）
 *     // 系统会自动处理 SpriteSet 的获取和绑定
 *     event.registerSimpleProvider(
 *         MyCustomParticle.TYPE,
 *         (level, options, x, y, z) -> new MyCustomParticle(level, options, x, y, z)
 *     );
 *
 *     // 方式 2：注册复杂粒子（需要自定义资源加载或特殊初始化逻辑）
 *     event.registerProvider(
 *         ComplexParticle.TYPE,
 *         new ComplexParticleProvider()
 *     );
 * }
 * }</pre>
 *
 * @see WizardryParticleProvider
 * @see SimpleParticleProvider
 * @see ParticleTypeExtension
 */
public class RegisterParticleEvent extends Event {
    /**
     * 存储已注册的粒子类型与对应提供程序的映射表。
     * Key 为粒子类型，Value 为用于创建该类型粒子的提供程序实例。
     */
    private final Map<ParticleTypeExtension<?>, WizardryParticleProvider<? extends IParticleOptionsExtension>> particleProviders;

    /**
     * 构造一个新的注册粒子事件。
     *
     * @param particleProviders 用于存储注册信息的映射表。通常由系统内部维护并传递。
     */
    public RegisterParticleEvent(Map<ParticleTypeExtension<?>, WizardryParticleProvider<? extends IParticleOptionsExtension>> particleProviders) {
        this.particleProviders = particleProviders;
    }

    /**
     * 注册一个通用的粒子提供程序。
     * <p>
     * 此方法适用于需要完全控制粒子创建过程的情况，例如自定义资源加载策略、
     * 非标准的 Options 类型处理，或复杂的初始化逻辑。
     * </p>
     *
     * @param type     要注册的粒子类型。
     * @param provider 负责创建该类型粒子的提供程序实例。
     * @param <T>      Options 的类型，必须扩展自 {@link IParticleOptionsExtension}。
     */
    public <T extends IParticleOptionsExtension> void registerProvider(
            ParticleTypeExtension<T> type,
            WizardryParticleProvider<T> provider
    ) {
        this.particleProviders.put(type, provider);
    }

    /**
     * 注册一个简单的粒子提供程序。
     * <p>
     * 这是一个便捷方法，专为基于 {@link QuadParticleOptions} 的标准粒子设计。
     * 调用此方法时，系统会自动执行以下操作：
     * <ol>
     *     <li>检查传入的 Options 是否为 {@link QuadParticleOptions}。</li>
     *     <li>如果是，自动通过 {@link top.begonia.wizardry.api.particle.manager.ParticleResourceAccessor}
     *         获取对应的 SpriteSet 并设置到 Options 中。</li>
     *     <li>调用用户提供的 {@link SimpleParticleProvider#createParticle} 方法创建粒子。</li>
     * </ol>
     * </p>
     * <p>
     * 如果传入的 Options 不是 {@link QuadParticleOptions}，系统将跳过 SpriteSet 的设置步骤，
     * 直接调用用户的 Provider。
     * </p>
     *
     * @param type     要注册的粒子类型。
     * @param provider 简化的粒子创建逻辑。只需关注如何实例化粒子对象，无需关心 SpriteSet 的获取。
     * @param <T>      Options 的类型，通常应为 {@link QuadParticleOptions}。
     */
    public <T extends IParticleOptionsExtension> void registerSimpleProvider(
            ParticleTypeExtension<T> type,
            SimpleParticleProvider<T> provider
    ) {
        this.registerProvider(type, (particleResourceAccessor, clientLevel, options, x, y, z) -> {
            if (options instanceof QuadParticleOptions quadParticleOptions) {
                quadParticleOptions.setSpriteSet(particleResourceAccessor.getSpriteSet(quadParticleOptions.getType()));
            }
            return provider.createParticle(clientLevel, options, x, y, z);
        });
    }
}
