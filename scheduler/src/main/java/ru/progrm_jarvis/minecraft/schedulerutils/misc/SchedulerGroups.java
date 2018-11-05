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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@UtilityClass
public class SchedulerGroups {

    public <K> KeyedSchedulerGroup<K> keyedSchedulerGroup(@NonNull final Plugin plugin,
                                                          final boolean async, final long delay, final long interval) {
        return new MapBasedKeyedSchedulerGroup<>(plugin, async, delay, interval, new HashMap<>());
    }

    public <K> KeyedSchedulerGroup<K> concurrentKeyedSchedulerGroup(@NonNull final Plugin plugin,
                                                                    final boolean async, final long delay,
                                                                    final long interval) {
        return new ConcurrentMapBasedKeyedSchedulerGroup<>(plugin, async, delay, interval, new HashMap<>());
    }

    @ToString
    @EqualsAndHashCode(callSuper = true)
    @FieldDefaults(level = AccessLevel.PROTECTED)
    private static class MapBasedKeyedSchedulerGroup<K> extends KeyedSchedulerGroup<K> {

        final AtomicReference<BukkitTask> runnable = new AtomicReference<>();

        @NonNull final Plugin plugin;

        final boolean async;
        final long delay, interval;

        final Map<K, Runnable> tasks;

        public MapBasedKeyedSchedulerGroup(@NonNull final Plugin plugin, final boolean async, final long delay,
                                           final long interval,
                                           @NonNull final Map<K, Runnable> tasks) {
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

        public void addTask(K key, @NonNull Runnable task) {
            runnable.compareAndSet(null, async
                    ? runTaskTimerAsynchronously(plugin, delay, interval) : runTaskTimer(plugin, delay, interval));

            tasks.put(key, task);
        }

        public void removeTask(@NonNull Runnable task) {
            tasks.values().remove(task);
        }

        public void removeTask(K key) {
            tasks.remove(key);
        }
    }

    @ToString
    @EqualsAndHashCode(callSuper = true)
    @FieldDefaults(level = AccessLevel.PROTECTED)
    private static class ConcurrentMapBasedKeyedSchedulerGroup<K> extends MapBasedKeyedSchedulerGroup<K> {

        ReadWriteLock lock = new ReentrantReadWriteLock();
        Lock readLock = lock.readLock();
        Lock writeLock = lock.writeLock();

        public ConcurrentMapBasedKeyedSchedulerGroup(@NonNull final Plugin plugin,
                                                     final boolean async, final long delay, final long interval,
                                                     @NonNull final Map<K, Runnable> tasks) {
            super(plugin, async, delay, interval, tasks);
        }

        @Override
        public void run() {
            readLock.lock();
            try {
                for (val task : tasks.values()) task.run();
            } finally {
                readLock.unlock();
            }
        }

        public void addTask(K key, @NonNull Runnable task) {
            writeLock.lock();
            try {
                super.addTask(key, task);
            } finally {
                writeLock.unlock();
            }
        }

        public void removeTask(@NonNull Runnable task) {
            writeLock.lock();
            try {
                super.removeTask(task);
            } finally {
                writeLock.unlock();
            }
        }

        public void removeTask(K key) {
            writeLock.lock();
            try {
                super.removeTask(key);
            } finally {
                writeLock.unlock();
            }
        }
    }
}
