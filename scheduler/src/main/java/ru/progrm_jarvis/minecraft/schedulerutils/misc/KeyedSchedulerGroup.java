package ru.progrm_jarvis.minecraft.schedulerutils.misc;

import lombok.NonNull;

public abstract class KeyedSchedulerGroup<T extends Runnable, K> extends SchedulerGroup<T> {

    public abstract void addTask(K key, @NonNull T task);

    @Override
    public void addTask(@NonNull final T task) {
        addTask(null, task);
    }

    public abstract T removeTask(K key);
}
