package ru.progrm_jarvis.minecraft.schedulerutils.misc;

import lombok.NonNull;

public abstract class KeyedSchedulerGroup<K> extends SchedulerGroup {

    public abstract void addTask(K key, @NonNull Runnable task);

    @Override
    public void addTask(@NonNull final Runnable task) {
        addTask(null, task);
    }

    public abstract void removeTask(K key);
}
