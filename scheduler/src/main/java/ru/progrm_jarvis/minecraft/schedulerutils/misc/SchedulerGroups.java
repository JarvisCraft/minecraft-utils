package ru.progrm_jarvis.minecraft.schedulerutils.misc;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.bukkit.Utility;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@UtilityClass
public class SchedulerGroups {

    public <K> KeyedSchedulerGroup<K> keyedSchedulerGroup() {
        return new MapBasedKeyedSchedulerGroup<>(new HashMap<>());
    }@Utility

    public <K> KeyedSchedulerGroup<K> concurrentKeyedSchedulerGroup() {
        return new ConcurrentMapBasedKeyedSchedulerGroup<>(new HashMap<>());
    }

    @ToString
    @EqualsAndHashCode(callSuper = true)
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PROTECTED)
    private static class MapBasedKeyedSchedulerGroup<K> extends KeyedSchedulerGroup<K> {

        final Map<K, Runnable> tasks;

        @Override
        public void run() {
            for (val task : tasks.values()) task.run();
        }

        public void addTask(K key, @NonNull Runnable task) {
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

        public ConcurrentMapBasedKeyedSchedulerGroup(@NonNull final Map<K, Runnable> tasks) {
            super(tasks);
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
