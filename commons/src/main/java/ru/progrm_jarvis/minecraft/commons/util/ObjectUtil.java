package ru.progrm_jarvis.minecraft.commons.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import ru.progrm_jarvis.minecraft.commons.util.function.UncheckedFunction;

import java.util.Optional;

/**
 * Utilities for common object operations.
 */
@UtilityClass
public class ObjectUtil {

    /**
     * Returns the first nonnull value of specified variants or {@code null} if none found.
     *
     * @param variants variants of which one may be nonnull
     * @param <T> type of value
     * @return first nonnull value found or {@code null} if none
     */
    @SafeVarargs
    public <T> T nonNull(final T... variants) {
        for (val variant : variants) if (variant != null) return variant;

        return null;
    }

    /**
     * Returns the first nonnull value of specified variants wrapped in {@link Optional} or empty if none found.
     *
     * @param variants variants of which one may be nonnull
     * @param <T> type of value
     * @return {@link Optional} containing first nonnull value found or empty if none
     */
    @SafeVarargs
    public <T> Optional<T> optionalNonNull(final T... variants) {
        for (val variant : variants) if (variant != null) return Optional.of(variant);

        return Optional.empty();
    }

    /**
     * Returns the first nonnull value of specified variants or throws {@link NullPointerException} if none found.
     *
     * @param variants variants of which one may be nonnull
     * @param <T> type of value
     * @return first nonnull value found
     * @throws NullPointerException if none of the variants specified is nonnull
     */
    @SafeVarargs
    public <T> T onlyNonNull(final T... variants) throws NullPointerException {
        for (val variant : variants) if (variant != null) return variant;

        throw new NullPointerException("No nonnull value found among variants");
    }

    /**
     * Maps (transforms) the value specified using the mapping function.
     * This may come in handy in case of initializing fields with expressions which have checked exceptions.
     *
     * @param value value to map
     * @param mappingFunction function to map the value to the required type
     * @param <T> type of source value
     * @param <R> type of resulting value
     * @return mapped (transformed) value
     */
    public <T, R> R map(final T value, @NonNull final UncheckedFunction<T, R> mappingFunction) {
        return mappingFunction.apply(value);
    }
}
