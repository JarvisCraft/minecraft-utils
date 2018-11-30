package ru.progrm_jarvis.minecraft.schedulerutils.pool;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.schedulerutils.misc.KeyedSchedulerGroup;
import ru.progrm_jarvis.minecraft.schedulerutils.misc.SchedulerGroups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A loop pool which creates only one {@link org.bukkit.scheduler.BukkitTask} per sync-mode.
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class SingleWorkerLoopPool<T extends Runnable, K> implements KeyedLoopPool<T, K> {

    boolean concurrent;

    @NonNull @Getter Plugin plugin;
    KeyedSchedulerGroup<CountingTask<T>, K> asyncWorker;
    KeyedSchedulerGroup<CountingTask<T>, K> syncWorker;

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
    public void addTask(final TaskOptions taskOptions, final T task) {
        if (taskOptions.isAsync()) {
            initAsyncWorker();

            asyncWorker.addTask(new CountingTask<>(task, taskOptions.getInterval()));
        } else {
            initSyncWorker();

            syncWorker.addTask(new CountingTask<>(task, taskOptions.getInterval()));
        }
    }

    @Override
    public void addTask(final TaskOptions taskOptions, final K key, final T task) {
        if (taskOptions.isAsync()) {
            initAsyncWorker();

            asyncWorker.addTask(key, new CountingTask<>(task, taskOptions.getInterval()));
        } else {
            initSyncWorker();

            syncWorker.addTask(key, new CountingTask<>(task, taskOptions.getInterval()));
        }
    }

    @Override
    public T removeTask(final T task) {
        final Predicate<CountingTask<T>> predicate = testedTask -> testedTask.task.equals(task);

        var removedTask = asyncWorker.removeTask(predicate);
        if (removedTask == null) {
            removedTask = syncWorker.removeTask(predicate);
            if (removedTask == null) return null;

            checkAsync();

            return removedTask.task;
        }

        checkAsync();
        return removedTask.task;
    }

    @Override
    public Collection<T> removeTasks(final T task) {
        final Predicate<CountingTask<T>> predicate = testedTask -> testedTask.task.equals(task);
        var removedTasks = new ArrayList<CountingTask<T>>(asyncWorker.removeTasks(predicate));
        checkAsync();
        removedTasks.addAll(syncWorker.removeTasks(predicate));
        checkSync();

        return mapToTasks(removedTasks);
    }

    @Override
    public Collection<T> removeTasks(final K key) {
        val tasks = new ArrayList<CountingTask<T>>();

        var removedTasks = asyncWorker.removeTasks(key);
        if (removedTasks != null) tasks.addAll(removedTasks);
        checkAsync();

        removedTasks = syncWorker.removeTasks(key);
        if (removedTasks != null) tasks.addAll(removedTasks);
        checkSync();

        return mapToTasks(tasks);
    }

    @Override
    public T removeTask(final TaskOptions taskOptions, final K key) {
        val async = taskOptions.isAsync();
        val tasks = (async ? asyncWorker.tasks() : syncWorker.tasks()).iterator();
        val interval = taskOptions.getInterval();

        while (tasks.hasNext()) {
            val task = tasks.next();
            if (task.interval == interval) {
                if (async) checkAsync();
                else checkSync();

                return task.task;
            }
        }

        return null;
    }

    @Override
    public Collection<T> removeTasks(final TaskOptions taskOptions) {
        val async = taskOptions.isAsync();
        val interval = taskOptions.getInterval();

        // remove all tasks
        val removedTasks = new ArrayList<CountingTask<T>>();
        val tasks = (async ? asyncWorker : syncWorker).tasks().iterator();
        while (tasks.hasNext()) {
            val task = tasks.next();
            if (task.getInterval() == interval) {
                tasks.remove();
                removedTasks.add(task);
            }
        }

        if (async) checkAsync();
        else checkSync();

        return mapToTasks(removedTasks);
    }

    @Override
    public Collection<T> clearTasks() {
        val tasks = asyncWorker.clearTasks();
        checkAsync();

        tasks.addAll(syncWorker.clearTasks());
        checkSync();

        return mapToTasks(tasks);
    }

    protected void checkAsync() {
        if (asyncWorker.isCancelled()) asyncWorker = null;
    }

    protected void checkSync() {
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
