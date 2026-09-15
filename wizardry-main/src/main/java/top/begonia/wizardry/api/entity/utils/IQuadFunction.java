package top.begonia.wizardry.api.entity.utils;

@FunctionalInterface
public interface IQuadFunction<T, U, V, Q, R> {
    R apply(T t, U u, V v, Q q);
}
