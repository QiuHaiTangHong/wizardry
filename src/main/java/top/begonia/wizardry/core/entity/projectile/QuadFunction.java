package top.begonia.wizardry.core.entity.projectile;

@FunctionalInterface
public interface QuadFunction<T, U, V, Q, R> {
    R apply(T t, U u, V v, Q q);
}
