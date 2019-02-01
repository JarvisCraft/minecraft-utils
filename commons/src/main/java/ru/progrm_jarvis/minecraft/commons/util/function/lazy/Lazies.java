package ru.progrm_jarvis.minecraft.commons.util.function.lazy;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import ru.progrm_jarvis.minecraft.commons.util.function.UncheckedSupplier;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

@UtilityClass
public class Lazies {

    public <T> Lazy<T> lazy(@NonNull final UncheckedSupplier<T> valueSupplier) {
        return new SimpleLazy<>(valueSupplier);
    }

    public <T> Lazy<T> concurrentLazy(@NonNull final UncheckedSupplier<T> valueSupplier) {
        return new ConcurrentLazy<>(valueSupplier);
    }

    public <T> Lazy<T> weakLazy(@NonNull final UncheckedSupplier<T> valueSupplier) {
        return new WeakLazy<>(valueSupplier);
    }

    public <T> Lazy<T> concurrentWeakLazy(@NonNull final UncheckedSupplier<T> valueSupplier) {
        return new ConcurrentWeakLazy<>(valueSupplier);
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

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class WeakLazy<@NonNull T> implements Lazy<T> {

        @NonNull final transient Supplier<T> valueSupplier;
        @Nullable transient WeakReference<T> value;

        @Override
        public boolean isInitialized() {
            return value != null && value.get() != null;
        }

        @Override
        public T get() {
            T value;
            if (this.value == null || (value = this.value.get()) == null) this.value
                    = new WeakReference<>(value = valueSupplier.get());

            return value;
        }
    }

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class ConcurrentWeakLazy<@NonNull T> implements Lazy<T> {

        @NonNull final transient Supplier<T> valueSupplier;
        @Nullable transient WeakReference<T> value;
        @NonNull transient final Lock readLock, writeLock;

        private ConcurrentWeakLazy(@NonNull final Supplier<T> valueSupplier) {
            this.valueSupplier = valueSupplier;

            val lock = new ReentrantReadWriteLock();
            readLock = lock.readLock();
            writeLock = lock.writeLock();
        }

        @Override
        public boolean isInitialized() {
            readLock.lock();
            try {
                return value != null && value.get() != null;
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public T get() {
            readLock.lock();
            try {
                T value;
                if (this.value == null || (value = this.value.get()) == null) {
                    writeLock.lock();
                    try {
                        this.value = new WeakReference<>(value = valueSupplier.get());
                    } finally {
                        writeLock.unlock();
                    }
                }

                return value;
            } finally {
                readLock.unlock();
            }
        }
    }
}
