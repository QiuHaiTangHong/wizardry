package top.begonia.wizardry.api.particle.extension;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.CompositeQuadParticle;
import top.begonia.wizardry.api.particle.manager.WizardryParticleManager;

/**
 * 粒子配置访问器接口。
 * <p>
 * 提供链式调用 API，用于在粒子添加到世界前配置其属性（如颜色、速度、生命周期等）。
 * 实现此接口的类通常由 {@link WizardryParticleManager} 创建并返回。
 * </p>
 *
 * @see CompositeQuadParticle
 */
@SuppressWarnings("UnusedReturnValue")
public interface ParticleBuilder {
    /**
     * 设置粒子的缩放比例。
     *
     * @param scale 缩放因子
     * @return 当前访问器实例
     */
    @NonNull ParticleBuilder scaleValue(float scale);

    /**
     * 设置粒子的生命周期（tick 数）。
     *
     * @param time 生命周期时长
     * @return 当前访问器实例
     */
    ParticleBuilder time(int time);

    /**
     * 设置粒子的初始速度向量。
     *
     * @param xd X 轴速度
     * @param yd Y 轴速度
     * @param zd Z 轴速度
     * @return 当前访问器实例
     */
    ParticleBuilder speed(double xd, double yd, double zd);

    /**
     * 设置粒子的初始速度向量。
     *
     * @param vel 速度向量
     * @return 当前访问器实例
     */
    default ParticleBuilder speed(@NonNull Vec3 vel) {
        return this.speed(vel.x, vel.y, vel.z);
    }

    /**
     * 设置粒子的开始、当前和结束颜色（ARGB Hex 格式）。
     * <p>
     * 该默认实现会将提供的颜色同时应用于以下三个方法：
     * <ul>
     *     <li>{@link #startColor(float, float, float)}</li>
     *     <li>{@link #currentColor(float, float, float)}</li>
     *     <li>{@link #endColor(float, float, float)}</li>
     * </ul>
     * </p>
     *
     * @param hex ARGB 颜色值 (例如 {@code 0xFF0000FF} 表示不透明的红色)
     * @return 当前访问器实例
     */
    default ParticleBuilder color(int hex) {
        float r = ((hex & 0xFF0000) >> 16) / 255.0F;
        float g = ((hex & 0xFF00) >> 8) / 255.0F;
        float b = ((hex & 0xFF)) / 255.0F;
        this.startColor(r, g, b);
        this.currentColor(r, g, b);
        this.endColor(r, g, b);
        return this;
    }

    /**
     * 设置粒子的开始、当前和结束颜色（RGB 浮点数格式，范围 0.0-1.0）。
     * <p>
     * 该默认实现会将提供的颜色同时应用于以下三个方法：
     * <ul>
     *     <li>{@link #startColor(float, float, float)}</li>
     *     <li>{@link #currentColor(float, float, float)}</li>
     *     <li>{@link #endColor(float, float, float)}</li>
     * </ul>
     * </p>
     *
     * @param red   红色分量
     * @param green 绿色分量
     * @param blue  蓝色分量
     * @return 当前访问器实例
     */
    default ParticleBuilder color(float red, float green, float blue) {
        this.startColor(red, green, blue);
        this.currentColor(red, green, blue);
        this.endColor(red, green, blue);
        return this;
    }

    /**
     * 设置粒子的起始颜色。
     *
     * @param red   红色分量 (0.0-1.0)
     * @param green 绿色分量 (0.0-1.0)
     * @param blue  蓝色分量 (0.0-1.0)
     * @return 当前访问器实例
     */
    ParticleBuilder startColor(float red, float green, float blue);

    /**
     * 设置粒子的当前颜色。
     * <p>
     * 注意：许多粒子类型会在 tick() 中自动插值计算当前颜色，直接设置可能仅作为初始值或覆盖值。
     * </p>
     *
     * @param red   红色分量 (0.0-1.0)
     * @param green 绿色分量 (0.0-1.0)
     * @param blue  蓝色分量 (0.0-1.0)
     * @return 当前访问器实例
     */
    ParticleBuilder currentColor(float red, float green, float blue);

    /**
     * 设置粒子的结束颜色。
     *
     * @param red   红色分量 (0.0-1.0)
     * @param green 绿色分量 (0.0-1.0)
     * @param blue  蓝色分量 (0.0-1.0)
     * @return 当前访问器实例
     */
    ParticleBuilder endColor(float red, float green, float blue);

    /**
     * 设置粒子的结束颜色（ARGB Hex 格式）。
     *
     * @param hex ARGB 颜色值
     * @return 当前访问器实例
     */
    default ParticleBuilder endColor(int hex) {
        float r = ((hex & 0xFF0000) >> 16) / 255.0F;
        float g = ((hex & 0xFF00) >> 8) / 255.0F;
        float b = ((hex & 0xFF)) / 255.0F;
        return this.endColor(r, g, b);
    }

    /**
     * 设置粒子的全局透明度。
     *
     * @param alpha 透明度 (0.0-1.0)，0.0 为完全透明，1.0 为完全不透明
     * @return 当前访问器实例
     */
    ParticleBuilder alpha(float alpha);

    /**
     * 设置粒子是否启用阴影效果。
     *
     * @param shaded true 如果粒子应根据光照产生阴影，false 否则
     * @return 当前访问器实例
     */
    ParticleBuilder shaded(boolean shaded);

    /**
     * 设置粒子是否受重力影响。
     *
     * @param gravity true 如果粒子应受重力向下加速，false 否则
     * @return 当前访问器实例
     */
    ParticleBuilder gravity(boolean gravity);

    /**
     * 设置粒子绕自身轴旋转的效果。
     *
     * @param radius 旋转半径
     * @param speed  旋转角速度
     * @return 当前访问器实例
     */
    ParticleBuilder spin(double radius, double speed);

    /**
     * 设置粒子的朝向（偏航角和俯仰角）。
     *
     * @param yaw   偏航角 (Y 轴旋转角度)
     * @param pitch 俯仰角 (X 轴旋转角度)
     * @return 当前访问器实例
     */
    ParticleBuilder facing(float yaw, float pitch);

    /**
     * 根据方向设置粒子的朝向。
     *
     * @param direction 目标方向
     * @return 当前访问器实例
     */
    default ParticleBuilder facing(@NonNull Direction direction) {
        return this.facing(direction.toYRot(), direction.getAxis().isVertical() ? direction.getAxisDirection().getStep() * -90.0F : 0.0F);
    }

    /**
     * 设置粒子的目标位置。
     * <p>
     * 具体行为取决于具体的粒子实现，通常用于追踪粒子。
     * </p>
     *
     * @param x 目标 X 坐标
     * @param y 目标 Y 坐标
     * @param z 目标 Z 坐标
     * @return 当前访问器实例
     */
    ParticleBuilder targetPosition(double x, double y, double z);

    /**
     * 设置粒子的目标速度。
     * <p>
     * 具体行为取决于具体的粒子实现，通常用于追踪粒子。
     * </p>
     *
     * @param vx 目标 X 速度
     * @param vy 目标 Y 速度
     * @param vz 目标 Z 速度
     * @return 当前访问器实例
     */
    ParticleBuilder targetVelocity(double vx, double vy, double vz);

    /**
     * 设置粒子的目标实体。
     * <p>
     * 粒子将尝试追踪该实体的位置。
     * </p>
     *
     * @param target 目标实体
     * @return 当前访问器实例
     */
    ParticleBuilder targetEntity(Entity target);

    /**
     * 设置粒子的长度或拉伸程度。
     * <p>
     * 具体行为取决于具体的粒子实现。
     * </p>
     *
     * @param length 长度值
     * @return 当前访问器实例
     */
    ParticleBuilder length(double length);

    /**
     * 设置粒子是否启用物理碰撞检测。
     *
     * @param hasPhysics true 如果粒子应与世界发生物理交互（如碰撞反弹），否则 false
     * @return 当前访问器实例
     */
    ParticleBuilder physics(boolean hasPhysics);

    /**
     * 将配置好的粒子添加到粒子引擎中进行渲染。
     * <p>
     * 调用此方法后，粒子将被提交到客户端渲染队列，无法再修改其配置。
     * </p>
     */
    void spawn();
}
