package ru.progrm_jarvis.minecraft.commons.schedule.pool;

import lombok.NonNull;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

/**
 * A loop pool which allows storing its elements by keys.
 *
 * @param <K> type of key for identifying tasks
 * (may be unnecessary in some implementations so {@code null} may be used then)
 */
public interface KeyedLoopPool<T extends Runnable, K> extends LoopPool<T> {

    Plugin getPlugin();

    void addTask(@NonNull final TaskOptions taskOptions, final K key, @NonNull final T task);

    @Override
    default void addTask(final TaskOptions taskOptions, final T task) {
        addTask(taskOptions, null, task);
    }

    T removeTask(@NonNull final TaskOptions taskOptions, @NonNull final K key);

    Collection<T> removeTasks(@NonNull final K key);
}
