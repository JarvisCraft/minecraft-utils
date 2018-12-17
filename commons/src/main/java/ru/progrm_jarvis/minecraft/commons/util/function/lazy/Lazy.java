package ru.progrm_jarvis.minecraft.commons.util.function.lazy;

import java.util.function.Supplier;

/**
 * A value calculated only once needed and then cached.
 *
 * @param <T> type of value
 */
public interface Lazy<T> extends Supplier<T> {

    /**
     * Retrieves whether or not this Lazy was initialized ({@link #get()} was called).
     *
     * @return {@code true} if this lazy was initialized and {@link false} otherwise
     */
    boolean isInitialized();
}
