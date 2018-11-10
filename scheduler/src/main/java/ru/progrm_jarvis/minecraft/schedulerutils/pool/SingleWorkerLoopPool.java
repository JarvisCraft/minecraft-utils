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
public class SingleWorkerLoopPool<K> implements KeyedLoopPool<K> {

    boolean concurrent;

    @NonNull @Getter Plugin plugin;
    KeyedSchedulerGroup<CountingTask, K> asyncWorker;
    KeyedSchedulerGroup<CountingTask, K> syncWorker;

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
    public void addTask(final TaskOptions taskOptions, final Runnable task) {
        if (taskOptions.isAsync()) {
            initAsyncWorker();

            asyncWorker.addTask(new CountingTask(task, taskOptions.getInterval()));
        } else {
            initSyncWorker();

            syncWorker.addTask(new CountingTask(task, taskOptions.getInterval()));
        }
    }

    @Override
    public void addTask(final TaskOptions taskOptions, final K key, final Runnable task) {
        if (taskOptions.isAsync()) {
            initAsyncWorker();

            asyncWorker.addTask(key, new CountingTask(task, taskOptions.getInterval()));
        } else {
            initSyncWorker();

            syncWorker.addTask(key, new CountingTask(task, taskOptions.getInterval()));
        }
    }

    @Override
    public Runnable removeTask(final Runnable task) {
        final Predicate<CountingTask> predicate = testedTask -> testedTask.task.equals(task);

        var removedTask = asyncWorker.removeTask(predicate);
        if (removedTask == null) {
            removedTask = syncWorker.removeTask(predicate);
            if (removedTask == null) return null;

            checkAsync();

            return removedTask;
        }

        checkAsync();
        return removedTask;
    }

    @Override
    public Collection<Runnable> removeTasks(final Runnable task) {
        final Predicate<CountingTask> predicate = testedTask -> testedTask.task.equals(task);
        var removedTasks = new ArrayList<Runnable>(asyncWorker.removeTasks(predicate));
        checkAsync();
        removedTasks.addAll(syncWorker.removeTasks(predicate));
        checkSync();

        return removedTasks;
    }

    @Override
    public Collection<Runnable> removeTasks(final K key) {
        val tasks = new ArrayList<Runnable>();

        var removedTasks = asyncWorker.removeTasks(key);
        if (removedTasks != null) tasks.addAll(removedTasks);
        checkAsync();

        removedTasks = syncWorker.removeTasks(key);
        if (removedTasks != null) tasks.addAll(removedTasks);
        checkSync();

        return tasks;
    }

    @Override
    public Runnable removeTask(final TaskOptions taskOptions, final K key) {
        val async = taskOptions.isAsync();
        val tasks = (async ? asyncWorker.tasks() : syncWorker.tasks()).iterator();
        val interval = taskOptions.getInterval();

        while (tasks.hasNext()) {
            val task = tasks.next();
            if (task.interval == interval) return task.task;
        }

        if (async) checkAsync();
        else checkSync();

        return null;
    }

    @Override
    public Collection<Runnable> removeTasks(final TaskOptions taskOptions) {
        val async = taskOptions.isAsync();
        val interval = taskOptions.getInterval();

        // remove all tasks
        val removedTasks = new ArrayList<CountingTask>();
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

        return removedTasks.stream()
                .map(task -> task.task)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Runnable> clearTasks() {
        val tasks = asyncWorker.clearTasks().stream()
                .map(task -> task.task)
                .collect(Collectors.toCollection(ArrayList::new));
        checkAsync();

        tasks.addAll(syncWorker.clearTasks().stream()
                .map(task -> task.task)
                .collect(Collectors.toList()));
        checkSync();

        return tasks;
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
    private static class CountingTask implements Runnable {

        final Runnable task;
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
