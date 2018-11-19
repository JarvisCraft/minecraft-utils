package ru.progrm_jarvis.minecraft.fakeentitylib.observer;

import com.google.common.base.Preconditions;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.ObservableFakeEntity;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Builder
@ToString
@EqualsAndHashCode
public class PeriodicFakeEntityObserver<E extends ObservableFakeEntity> implements FakeEntityObserver<E> {

    protected final Set<RedrawEntitiesRunnable> runnables = new HashSet<>();
    private final Lock lock = new ReentrantLock();

    @NonNull private final Plugin plugin;

    private final int maxDistance;

    protected final long interval;
    protected final boolean async;
    protected final int minEntitiesForNewThread;
    protected final int maxThreads;

    @Builder
    public PeriodicFakeEntityObserver(@NonNull final Plugin plugin, final int maxDistance,
                                      final long interval, final boolean async,
                                      final int minEntitiesForNewThread, final int maxThreads) {
        Preconditions.checkArgument(maxDistance > 0, "maxDistance should be positive");
        Preconditions.checkArgument(interval > 0, "interval should be positive");
        Preconditions.checkArgument(minEntitiesForNewThread > 0, "minEntitiesForNewThread should be positive");
        Preconditions.checkArgument(maxThreads > 0, "maxThreads should be positive");

        this.maxDistance = maxDistance;
        this.plugin = plugin;
        this.interval = interval;
        this.async = async;
        this.minEntitiesForNewThread = minEntitiesForNewThread;
        this.maxThreads = maxThreads;

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPluginDisable(final PluginDisableEvent event) {
                if (event.getPlugin() == plugin) shutdown();
            }
        }, plugin);
    }

    protected RedrawEntitiesRunnable getRedrawEntitiesRunnable() {
        lock.lock();
        try {
            if (runnables.isEmpty()) return newRedrawEntitiesRunnable();
            else {
                RedrawEntitiesRunnable minRunnable = null;
                Integer minEntitiesInRunnable = null;
                for (val task : runnables) {
                    val taskEntitiesSize = task.entities.size();

                    // if task has no even reached its entity minimum then use it
                    if (taskEntitiesSize < minEntitiesForNewThread) return task;
                        // else assign min values if the task is the smallest
                    else if (minEntitiesInRunnable == null || taskEntitiesSize < minEntitiesInRunnable) {
                        minEntitiesInRunnable = taskEntitiesSize;
                        minRunnable = task;
                    }
                }

                // add new thread if there is yet space for it or the minimal if not
                return runnables.size() < maxThreads ? newRedrawEntitiesRunnable() : minRunnable;
            }
        } finally {
            lock.unlock();
        }
    }

    protected RedrawEntitiesRunnable newRedrawEntitiesRunnable() {
        val runnable = new RedrawEntitiesRunnable();
        if (async) runnable.runTaskTimerAsynchronously(plugin, interval, interval);
        else runnable.runTaskTimer(plugin, interval, interval);

        runnables.add(runnable);

        return runnable;
    }

    @Override
    public E observe(final E entity) {
        getRedrawEntitiesRunnable().addEntity(entity);

        return entity;
    }

    @Override
    public E unobserve(final E entity) {
        lock.lock();
        try {
            val iterator = runnables.iterator();
            for (val task : runnables) if (task.removeEntity(entity)) {
                if (task.entities.size() == 0) iterator.remove();

                break;
            }
        } finally {
            lock.unlock();
        }

        return entity;
    }

    @Override
    public void shutdown() {
        for (val task : runnables) task.cancel();
    }

    protected class RedrawEntitiesRunnable extends BukkitRunnable {

        protected final Collection<ObservableFakeEntity> entities = Collections.newSetFromMap(new WeakHashMap<>());
        private final ReadWriteLock lock  = new ReentrantReadWriteLock();

        public int size() {
            return entities.size();
        }

        public void addEntity(final ObservableFakeEntity entity) {
            lock.writeLock().lock();
            try {
                entities.add(entity);
            } finally {
                lock.writeLock().unlock();
            }
        }

        public boolean removeEntity(final ObservableFakeEntity entity) {
            lock.writeLock().lock();
            try {
                return entities.remove(entity);
            } finally {
                lock.writeLock().unlock();
            }
        }

        @Override
        public void run() {
            lock.readLock().lock();
            try {
                for (val entity : entities) entity.attemptRerenderForAll();
            } finally {
                lock.readLock().unlock();
            }
        }
    }
}
