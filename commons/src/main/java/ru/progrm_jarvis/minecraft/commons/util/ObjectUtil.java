package ru.progrm_jarvis.minecraft.commons.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import ru.progrm_jarvis.minecraft.commons.util.function.UncheckedFunction;

@UtilityClass
public class ObjectUtil {

    @SafeVarargs
    public <T> T nonNull(final T... variants) {
        for (val variant : variants) if (variant != null) return variant;

        return null;
    }

    @SafeVarargs
    public <T> T onlyNonNull(final T... variants) {
        for (val variant : variants) if (variant != null) return variant;

        throw new NullPointerException("No nonnull value found among variants");
    }

    public <T, R> R map(final T value, @NonNull final UncheckedFunction<T, R> mappingFunction) {
        return mappingFunction.apply(value);
    }
}
