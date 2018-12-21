package ru.progrm_jarvis.minecraft.commons.util.function;

import lombok.SneakyThrows;

import java.util.function.Consumer;

/**
 * Consumer which allows having checked exceptions in its method body.
 *
 * @param <T> {@inheritDoc}
 */
@FunctionalInterface
public interface UncheckedConsumer<T> extends Consumer<T> {

    /**
     * Performs this operation on the given argument allowing any checked exceptions in method body.
     *
     * @param t the input argument
     */
    void consume(T t) throws Throwable;

    @Override
    @SneakyThrows
    default void accept(final T t) {
        consume(t);
    }
}
