package ru.progrm_jarvis.minecraft.commons.schedule.pool;

import com.google.common.base.Preconditions;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

/**
 * A pool of schedulers which groups tasks with similar parameters
 * (asynchrony and interval stored as {@link TaskOptions}}) into groups so that they are executed altogether.
 *
 * @param <T> supported task type
 */
public interface LoopPool<T extends Runnable> {

    Plugin getPlugin();

    int tasksSize();

    TaskRemover addTask(@NonNull final TaskOptions taskOptions, @NonNull final T task);

    Collection<T> clearTasks();

    @Value
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    class TaskOptions {

        private static TLongObjectMap<TaskOptions> optionsPool = new TLongObjectHashMap<>();

        boolean async;
        long interval;

        public static TaskOptions of(final boolean async, long interval) {
            Preconditions.checkArgument(interval > 0, "interval should be a positive number");

            val key = async ? -interval : interval;

            var options = optionsPool.get(key);
            if (options == null) optionsPool.put(key, options = new TaskOptions(async, interval));

            return options;
        }
    }

    @FunctionalInterface
    interface TaskRemover {
        void removeTask();
    }
}
