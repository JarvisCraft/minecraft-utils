package ru.progrm_jarvis.minecraft.commons.util.function.lazy;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A value calculated only once needed and then cached.
 *
 * @param <T> type of value
 */
public interface Lazy<T> extends Supplier<T> {

    @Override
    T get();

    /**
     * Retrieves whether or not this Lazy was initialized ({@link #get()} was called).
     *
     * @return {@code true} if this lazy was initialized and {@link false} otherwise
     */
    boolean isInitialized();

    /**
     * Gets a value wrapped in {@link Optional} only if it was initialized, otherwise returning an empty one.
     *
     * @return Optional containing the value if it was initialized or an empty one otherwise.
     *
     * @apiNote An empty optional may also mean that the value was initialized to {@code null}.
     */
    default Optional<T> getIfInitialized() {
        return isInitialized() ? Optional.ofNullable(get()) : Optional.empty();
    }
}
