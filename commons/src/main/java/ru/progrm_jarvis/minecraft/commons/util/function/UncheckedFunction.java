package ru.progrm_jarvis.minecraft.commons.util.function;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.function.Function;

/**
 * Function which allows having checked exceptions in its body.
 *
 * @param <T> {@inheritDoc}
 * @param <R> {@inheritDoc}
 */
@FunctionalInterface
public interface UncheckedFunction<T, R> extends Function<T, R> {

    /**
     * Applies this function to the given argument allowing any checked exceptions in method body.
     *
     * @param t the function argument
     * @return the function result
     */
    R operate(T t) throws Throwable;

    @Override
    @SneakyThrows
    default R apply(T t) {
        return operate(t);
    }

    @Override
    default <V> UncheckedFunction<V, R> compose(@NonNull Function<? super V, ? extends T> before) {
        return v -> apply(before.apply(v));
    }

    @Override
    default <V> UncheckedFunction<T, V> andThen(@NonNull final Function<? super R, ? extends V> after) {
        return t -> after.apply(apply(t));
    }
}
