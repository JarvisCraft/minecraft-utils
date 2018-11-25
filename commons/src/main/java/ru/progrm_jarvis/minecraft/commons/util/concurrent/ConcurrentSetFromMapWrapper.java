package ru.progrm_jarvis.minecraft.commons.util.concurrent;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ConcurrentSetFromMapWrapper<E, T extends Map<E, Boolean>>
        extends ConcurrentWrapper<T> implements Set<E> {

    // maps key-set for fast access
    Set<E> keySet;

    public ConcurrentSetFromMapWrapper(@NonNull final T wrapped) {
        super(wrapped);

        keySet = wrapped.keySet();
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            return wrapped.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        readLock.lock();
        try {
            return wrapped.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean contains(final Object o) {
        readLock.lock();
        try {
            //noinspection SuspiciousMethodCalls
            return wrapped.containsKey(o);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    @Nonnull
    public Iterator<E> iterator() {
        readLock.lock();
        try {
            return keySet.iterator();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void forEach(@NonNull final Consumer<? super E> action) {
        readLock.lock();
        try {
            keySet.forEach(action);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    @Nonnull public Object[] toArray() {
        readLock.lock();
        try {
            return keySet.toArray();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    @Nonnull public <R> R[] toArray(@NonNull final R[] a) {
        readLock.lock();
        try {
            //noinspection SuspiciousToArrayCall
            return keySet.toArray(a);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean add(final E e) {
        writeLock.lock();
        try {
            return wrapped.put(e, true) == null;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean remove(final Object o) {
        writeLock.lock();
        try {
            return wrapped.remove(o) != null;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean containsAll(@NonNull final Collection<?> c) {
        readLock.lock();
        try {
            return keySet.containsAll(c);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean addAll(@NonNull final Collection<? extends E> c) {
        writeLock.lock();
        try {
            return keySet.addAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean retainAll(@NonNull final Collection<?> c) {
        writeLock.lock();
        try {
            return keySet.retainAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean removeAll(@NonNull final Collection<?> c) {
        writeLock.lock();
        try {
            return keySet.removeAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean removeIf(@NonNull final Predicate<? super E> filter) {
        writeLock.lock();
        try {
            return keySet.removeIf(filter);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            wrapped.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Spliterator<E> spliterator() {
        readLock.lock();
        try {
            return keySet.spliterator();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Stream<E> stream() {
        readLock.lock();
        try {
            return keySet.stream();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Stream<E> parallelStream() {
        readLock.lock();
        try {
            return keySet.parallelStream();
        } finally {
            readLock.unlock();
        }
    }
}
