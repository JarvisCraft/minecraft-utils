package ru.progrm_jarvis.minecraft.commons.async;

import ru.progrm_jarvis.minecraft.commons.util.function.UncheckedRunnable;
import ru.progrm_jarvis.minecraft.commons.util.function.UncheckedConsumer;
import ru.progrm_jarvis.minecraft.commons.util.function.UncheckedSupplier;

/**
 * An object capable of performing asynchronous operations.
 */
@FunctionalInterface
public interface AsyncRunner {

    /**
     * Performs the specified operations asynchronously.
     *
     * @param operation operation to perform asynchronously
     */
    void runAsynchronously(final UncheckedRunnable operation);

    /**
     * Performs the specified operations asynchronously.
     *
     * @param operation operation to perform asynchronously
     * @param callback callback to handle the resulting value of the operation
     * @param <T> type of value returned by the operation
     */
    default <T> void runAsynchronously(final UncheckedSupplier<T> operation, final UncheckedConsumer<T> callback) {
        runAsynchronously(() -> callback.accept(operation.get()));
    }
}
