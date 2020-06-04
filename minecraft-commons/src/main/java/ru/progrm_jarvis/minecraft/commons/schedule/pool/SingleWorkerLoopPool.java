package ru.progrm_jarvis.minecraft.commons.schedule.pool;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.schedule.misc.KeyedSchedulerGroup;
import ru.progrm_jarvis.minecraft.commons.schedule.misc.SchedulerGroups;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * A loop pool which creates only one {@link org.bukkit.scheduler.BukkitTask} per sync-mode.
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class SingleWorkerLoopPool<T extends Runnable, K> implements KeyedLoopPool<T, K> {

    boolean concurrent;

    @NonNull @Getter Plugin plugin;
    @NonFinal KeyedSchedulerGroup<CountingTask<T>, K> asyncWorker;
    @NonFinal KeyedSchedulerGroup<CountingTask<T>, K> syncWorker;

    protected static <T extends Runnable> Collection<T> mapToTasks(final Collection<CountingTask<T>> tasks) {
        return tasks.stream()
                .map(task -> task.task)
                .collect(Collectors.toList());
    }

    @Override
    public int tasksSize() {
        return (asyncWorker == null ? 0 : asyncWorker.size()) + (syncWorker == null ? 0 : syncWorker.size());
    }

    protected void initAsyncWorker() {
        if (asyncWorker == null) asyncWorker = concurrent
                ? SchedulerGroups.concurrentKeyedSchedulerGroup(plugin, true, 1, 1)
                : SchedulerGroups.keyedSchedulerGroup(plugin, true, 1, 1);
    }

    protected void initSyncWorker() {
        if (syncWorker == null) syncWorker = concurrent
                ? SchedulerGroups.concurrentKeyedSchedulerGroup(plugin, false, 1, 1)
                : SchedulerGroups.keyedSchedulerGroup(plugin, false, 1, 1);
    }

    @Override
    public TaskRemover addTask(@NonNull final TaskOptions taskOptions, @NonNull final T task) {
        if (taskOptions.isAsync()) {
            initAsyncWorker();

            val countingTask = new CountingTask<>(task, taskOptions.getInterval());
            asyncWorker.addTask(countingTask);

            return () -> removeTask(countingTask, true);
        } else {
            initSyncWorker();

            val countingTask = new CountingTask<>(task, taskOptions.getInterval());
            syncWorker.addTask(countingTask);

            return () -> removeTask(countingTask, false);
        }
    }

    protected void removeTask(@NonNull final CountingTask<T> countingTask, final boolean async) {
        if (async) if (syncWorker != null) syncWorker.removeTask(countingTask);
        else if (asyncWorker != null) asyncWorker.removeTask(countingTask);
    }

    @Override
    public TaskRemover addTask(@NonNull final TaskOptions taskOptions, final K key, @NonNull final T task) {
        if (taskOptions.isAsync()) {
            initAsyncWorker();

            val countingTask = new CountingTask<>(task, taskOptions.getInterval());
            asyncWorker.addTask(key, countingTask);

            return () -> removeTask(countingTask, true);
        } else {
            initSyncWorker();

            val countingTask = new CountingTask<>(task, taskOptions.getInterval());
            syncWorker.addTask(key, countingTask);

            return () -> removeTask(countingTask, false);
        }
    }

    @Override
    public Collection<T> clearTasks() {
        val tasks = asyncWorker.clearTasks();
        attemptCancelAsync();

        tasks.addAll(syncWorker.clearTasks());
        attemptCancelSync();

        return mapToTasks(tasks);
    }

    protected void attemptCancelAsync() {
        if (asyncWorker.isCancelled()) asyncWorker = null;
    }

    protected void attemptCancelSync() {
        if (syncWorker.size() == 0) syncWorker = null;
    }

    @Value
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class CountingTask<T extends Runnable> implements Runnable {

        final T task;
        final long interval;
        @NonFinal long counter;

        @Override
        public void run() {
            if (++counter == interval) {
                counter = 0;

                task.run();
            }
        }
    }
}
