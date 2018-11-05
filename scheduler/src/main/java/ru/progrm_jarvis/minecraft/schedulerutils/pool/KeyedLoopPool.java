package ru.progrm_jarvis.minecraft.schedulerutils.pool;

import lombok.NonNull;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

/**
 * A loop pool which allows storing its elements by keys.
 *
 * @param <K> type of key for identifying tasks
 * (may be unnecessary in some implementations so {@code null} may be used then)
 */
public interface KeyedLoopPool<K> extends LoopPool {

    Plugin getPlugin();

    void addTask(@NonNull final TaskOptions taskOptions, final K key, @NonNull final Runnable task);

    @Override
    default void addTask(final TaskOptions taskOptions, final Runnable task) {
        addTask(taskOptions, null, task);
    }

    Runnable removeTask(@NonNull final TaskOptions taskOptions, @NonNull final K key);

    Collection<Runnable> removeTasks(@NonNull final K key);
}
