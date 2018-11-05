package ru.progrm_jarvis.minecraft.schedulerutils.misc;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@UtilityClass
public class SchedulerGroups {

    public <T extends Runnable, K> KeyedSchedulerGroup<T, K> keyedSchedulerGroup(@NonNull final Plugin plugin,
                                                                                 final boolean async, final long delay,
                                                                                 final long interval) {
        return new MapBasedKeyedSchedulerGroup<>(plugin, async, delay, interval, new HashMap<>());
    }

    public <T extends Runnable, K> KeyedSchedulerGroup<T, K> concurrentKeyedSchedulerGroup(@NonNull final Plugin plugin,
                                                                                           final boolean async,
                                                                                           final long delay,
                                                                                           final long interval) {
        return new ConcurrentMapBasedKeyedSchedulerGroup<>(plugin, async, delay, interval, new HashMap<>());
    }

    @ToString
    @EqualsAndHashCode(callSuper = true)
    @FieldDefaults(level = AccessLevel.PROTECTED)
    private static class MapBasedKeyedSchedulerGroup<T extends Runnable, K> extends KeyedSchedulerGroup<T, K> {

        final AtomicReference<BukkitTask> runnable = new AtomicReference<>();

        @NonNull final Plugin plugin;

        final boolean async;
        final long delay, interval;

        final Map<K, T> tasks;

        public MapBasedKeyedSchedulerGroup(@NonNull final Plugin plugin, final boolean async, final long delay,
                                           final long interval, @NonNull final Map<K, T> tasks) {
            this.plugin = plugin;
            this.async = async;
            this.delay = delay;
            this.interval = interval;
            this.tasks = tasks;

            // set up plugin disable hook
            Bukkit.getPluginManager().registerEvents(new Listener() {

                @EventHandler
                public void onPluginDisable(final PluginDisableEvent event) {
                    if (plugin == event.getPlugin()) shutdown();
                }
            }, plugin);
        }

        @Override
        public Collection<T> tasks() {
            return tasks.values();
        }

        @Override
        public Collection<T> clearTasks() {
            val tasks = new ArrayList<T>(this.tasks.values());

            tasks.clear();

            return tasks;
        }

        protected void shutdown() {
            if (isCancelled()) throw new IllegalStateException(
                    "Attempt to shutdown an already shut down MapBasedKeyedSchedulerGroup"
            );
            cancel();
        }

        @Override
        public void run() {
            for (val task : tasks.values()) task.run();
        }

        @Override
        public void addTask(final K key, @NonNull final T task) {
            runnable.compareAndSet(null, async
                    ? runTaskTimerAsynchronously(plugin, delay, interval) : runTaskTimer(plugin, delay, interval));

            tasks.put(key, task);
        }

        @Override
        public void removeTask(@NonNull final T task) {
            tasks.values().remove(task);
        }

        @Override
        public T removeTask(final K key) {
            return tasks.remove(key);
        }
    }

    @ToString
    @EqualsAndHashCode(callSuper = true)
    @FieldDefaults(level = AccessLevel.PROTECTED)
    private static class ConcurrentMapBasedKeyedSchedulerGroup<T extends Runnable, K>
            extends MapBasedKeyedSchedulerGroup<T, K> {

        ReadWriteLock lock = new ReentrantReadWriteLock();
        Lock readLock = lock.readLock();
        Lock writeLock = lock.writeLock();

        public ConcurrentMapBasedKeyedSchedulerGroup(@NonNull final Plugin plugin,
                                                     final boolean async, final long delay, final long interval,
                                                     @NonNull final Map<K, T> tasks) {
            super(plugin, async, delay, interval, tasks);
        }

        @Override
        public Collection<T> tasks() {
            readLock.lock();
            try {
                return super.tasks();
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public Collection<T> clearTasks() {
            readLock.lock();
            try {
                return super.clearTasks();
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public void run() {
            readLock.lock();
            try {
                super.run();
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public void addTask(final K key, @NonNull final T task) {
            writeLock.lock();
            try {
                super.addTask(key, task);
            } finally {
                writeLock.unlock();
            }
        }

        @Override
        public void removeTask(@NonNull final T task) {
            writeLock.lock();
            try {
                super.removeTask(task);
            } finally {
                writeLock.unlock();
            }
        }

        @Override
        public T removeTask(final K key) {
            writeLock.lock();
            try {
                return super.removeTask(key);
            } finally {
                writeLock.unlock();
            }
        }
    }
}
