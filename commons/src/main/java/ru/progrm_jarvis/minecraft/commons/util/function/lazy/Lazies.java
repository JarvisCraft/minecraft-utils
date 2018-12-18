package ru.progrm_jarvis.minecraft.commons.util.function.lazy;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import ru.progrm_jarvis.minecraft.commons.util.function.UncheckedSupplier;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@UtilityClass
public class Lazies {

    public <T> Lazy<T> lazy(@NonNull final UncheckedSupplier<T> valueSupplier) {
        return new SimpleLazy<>(valueSupplier);
    }

    public <T> Lazy<T> concurrentLazy(@NonNull final UncheckedSupplier<T> valueSupplier) {
        return new ConcurrentLazy<>(valueSupplier);
    }

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class SimpleLazy<T> implements Lazy<T> {

        @Nullable transient Supplier<T> valueSupplier;
        @Nullable transient T value;

        private SimpleLazy(@NonNull final Supplier<T> supplier) {
            this.valueSupplier = supplier;
        }

        @Override
        public boolean isInitialized() {
            return valueSupplier == null;
        }

        @Override
        public T get() {
            if (valueSupplier != null) { // not initialized
                value = valueSupplier.get();
                valueSupplier = null;
            }

            return value;
        }
    }

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class ConcurrentLazy<T> implements Lazy<T> {

        @Nullable transient Supplier<T> valueSupplier;
        @Nullable transient T value;

        private ConcurrentLazy(@NonNull final Supplier<T> supplier) {
            this.valueSupplier = supplier;
        }

        @Override
        public boolean isInitialized() {
            if (valueSupplier != null) { // might be not initialized
                synchronized (this) { // synchronize and return sure value
                    return valueSupplier == null;
                }
            }

            return true;
        }

        @Override
        public T get() {
            if (valueSupplier != null) { // might be not initialized
                synchronized (this) { // synchronize to make sure
                    if (valueSupplier != null) {
                        value = valueSupplier.get();
                        valueSupplier = null; // no longer needed
                    }
                }
            }

            return value;
        }
    }
}
