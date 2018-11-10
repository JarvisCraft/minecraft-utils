package ru.progrm_jarvis.minecraft.schedulerutils.misc;

import lombok.NonNull;
import lombok.val;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.function.Predicate;

public abstract class SchedulerGroup<T extends Runnable> extends BukkitRunnable {

    public abstract int size();

    public abstract Collection<T> tasks();

    public abstract void addTask(@NonNull T task);

    // should remove all occurrences of this task
    public abstract boolean removeTask(@NonNull T task);

    public abstract int removeTasks(@NonNull T task);

    public abstract Collection<T> clearTasks();

    public T findTask(@NonNull final Predicate<T> predicate) {

        for (final T task : tasks()) if (predicate.test(task)) return task;

        return null;
    }

    public T removeTask(@NonNull final Predicate<T> predicate) {
        val tasks = tasks().iterator();

        while (tasks.hasNext()) {
            val task = tasks.next();

            if (predicate.test(task)) {
                tasks.remove();

                return task;
            }
        }

        return null;
    }
}
