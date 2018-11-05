package ru.progrm_jarvis.minecraft.schedulerutils.pool;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.schedulerutils.misc.KeyedSchedulerGroup;
import ru.progrm_jarvis.minecraft.schedulerutils.misc.SchedulerGroups;

import java.util.ArrayList;
import java.util.Collection;
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
    public void removeTask(final Runnable task) {
        asyncWorker.removeTask(testedTask -> testedTask.task.equals(task));
        syncWorker.removeTask(testedTask -> testedTask.task.equals(task));
    }

    @Override
    public Collection<Runnable> removeTasks(final K key) {
        val tasks = new ArrayList<Runnable>();

        var task = asyncWorker.removeTask(key);
        if (task != null) tasks.add(task);

        task = syncWorker.removeTask(key);
        if (task != null) tasks.add(task);

        return tasks;
    }

    @Override
    public Runnable removeTask(final TaskOptions taskOptions, final K key) {
        val tasks = (taskOptions.isAsync() ? asyncWorker.tasks() : syncWorker.tasks()).iterator();
        val interval = taskOptions.getInterval();

        while (tasks.hasNext()) {
            val task = tasks.next();
            if (task.interval == interval) return task.task;
        }

        return null;
    }

    @Override
    public Collection<Runnable> removeTasks(final TaskOptions taskOptions) {
        return (taskOptions.isAsync() ? asyncWorker.clearTasks() : syncWorker.clearTasks()).stream()
                .map(task -> task.task)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Runnable> clearTasks() {
        val tasks = asyncWorker.clearTasks().stream()
                .map(task -> task.task)
                .collect(Collectors.toCollection(ArrayList::new));

        tasks.addAll(syncWorker.clearTasks().stream()
                .map(task -> task.task)// FIXME: 05.11.2018
                .collect(Collectors.toList()));

        return tasks;
    }

    @Value
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class CountingTask implements Runnable {

        Runnable task;
        long interval;
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
