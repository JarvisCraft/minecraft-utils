package ru.progrm_jarvis.minecraft.commons.util.concurrent;

import lombok.NonNull;

import java.util.Set;

public class ConcurrentSetWrapper<E, T extends Set<E>>
        extends ConcurrentCollectionWrapper<E, T> implements Set<E> {

    public ConcurrentSetWrapper(@NonNull final T wrapped) {
        super(wrapped);
    }
}
