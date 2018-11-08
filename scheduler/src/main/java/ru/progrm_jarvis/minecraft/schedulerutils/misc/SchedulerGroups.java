package ru.progrm_jarvis.minecraft.schedulerutils.misc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.schedulerutils.task.initializer.BukkitTaskInitializer;
import ru.progrm_jarvis.minecraft.schedulerutils.task.initializer.BukkitTaskInitializers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@UtilityClass
public class SchedulerGroups {

    public <T extends Runnable, K> KeyedSchedulerGroup<T, K> keyedSchedulerGroup(@NonNull final Plugin plugin,
                                                                                 final boolean async, final long delay,
                                                                                 final long interval) {
        return new MultimapBasedKeyedSchedulerGroup<>(plugin, async, delay, interval, ArrayListMultimap.create());
    }

    public <T extends Runnable, K> KeyedSchedulerGroup<T, K> concurrentKeyedSchedulerGroup(@NonNull final Plugin plugin,
                                                                                           final boolean async,
                                                                                           final long delay,
                                                                                           final long interval) {
        return new ConcurrentMultimapBasedKeyedSchedulerGroup<>(
                plugin, async, delay, interval,
                Multimaps.synchronizedMultimap(ArrayListMultimap.create())
        );
    }

    @ToString
    @EqualsAndHashCode(callSuper = true)
    @FieldDefaults(level = AccessLevel.PROTECTED)
    private static class MultimapBasedKeyedSchedulerGroup<T extends Runnable, K> extends KeyedSchedulerGroup<T, K> {

        @NonNull final Plugin plugin;

        final BukkitTaskInitializer initializer;

        final Multimap<K, T> tasks;

        public MultimapBasedKeyedSchedulerGroup(@NonNull final Plugin plugin, final boolean async, final long delay,
                                                final long interval, @NonNull final Multimap<K, T> tasks) {
            this.plugin = plugin;

            initializer = BukkitTaskInitializers.createTimerTaskInitializer(plugin, async, delay, interval, this);
            this.tasks = tasks;

            // set up plugin disable hook
            Bukkit.getPluginManager().registerEvents(new Listener() {

                @EventHandler
                public void onPluginDisable(final PluginDisableEvent event) {
                    if (plugin == event.getPlugin()) cancel();
                }
            }, plugin);
        }

        @Override // cancel should also set runnable to null
        public synchronized void cancel() {
            super.cancel();
            initializer.shutdown();
        }

        @Override
        public int size() {
            return tasks.size();
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

        @Override
        public void run() {
            for (val task : tasks.values()) task.run();
        }

        @Override
        public void addTask(final K key, @NonNull final T task) {
            initializer.initialize();

            tasks.put(key, task);
        }

        @Override
        public void removeTask(@NonNull final T task) {
            tasks.values().remove(task);
        }

        @Override
        public Collection<T> removeTasks(final K key) {
            return tasks.removeAll(key);
        }
    }

    @ToString
    @EqualsAndHashCode(callSuper = true)
    @FieldDefaults(level = AccessLevel.PROTECTED)
    private static class ConcurrentMultimapBasedKeyedSchedulerGroup<T extends Runnable, K>
            extends MultimapBasedKeyedSchedulerGroup<T, K> {

        ReadWriteLock lock = new ReentrantReadWriteLock();
        Lock readLock = lock.readLock();
        Lock writeLock = lock.writeLock();

        public ConcurrentMultimapBasedKeyedSchedulerGroup(@NonNull final Plugin plugin,
                                                          final boolean async, final long delay, final long interval,
                                                          @NonNull final Multimap<K, T> tasks) {
            super(plugin, async, delay, interval, tasks);
        }

        @Override
        public int size() {
            readLock.lock();
            try {
                return super.size();
            } finally {
                readLock.unlock();
            }
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
        public Collection<T> removeTasks(final K key) {
            writeLock.lock();
            try {
                return super.removeTasks(key);
            } finally {
                writeLock.unlock();
            }
        }
    }
}
