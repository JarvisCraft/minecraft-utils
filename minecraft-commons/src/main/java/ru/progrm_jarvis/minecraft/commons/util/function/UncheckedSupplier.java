package ru.progrm_jarvis.minecraft.commons.util.function;

import lombok.SneakyThrows;

import java.util.function.Supplier;

/**
 * Supplier which allows having checked exceptions in its method body.
 *
 * @param <T> {@inheritDoc}
 */
public interface UncheckedSupplier<T> extends Supplier<T> {

    /**
     * Gets a result allowing any checked exceptions in method body.
     *
     * @return a result
     */
    T supply() throws Throwable;

    @Override
    @SneakyThrows
    default T get() {
        return supply();
    }
}
