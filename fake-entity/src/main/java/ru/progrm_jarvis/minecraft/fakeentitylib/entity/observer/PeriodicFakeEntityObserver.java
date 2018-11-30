package ru.progrm_jarvis.minecraft.fakeentitylib.entity.observer;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.ObservableFakeEntity;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.management.AbstractSetBasedEntityManager;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.progrm_jarvis.minecraft.commons.util.hack.PreSuperCheck.beforeSuper;

@ToString
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class PeriodicFakeEntityObserver<P extends Plugin, E extends ObservableFakeEntity>
        extends AbstractSetBasedEntityManager<P, E> implements FakeEntityObserver<P, E> {

    Set<RedrawEntitiesRunnable> tasks = new HashSet<>();
    Lock lock = new ReentrantLock();

    @NonNull Plugin plugin;

    long interval;
    boolean async;
    int minEntitiesForNewThread;
    int maxThreads;

    @Builder
    public PeriodicFakeEntityObserver(@Nonnull final P plugin, final boolean concurrent,
                                      final long interval, final boolean async,
                                      final int minEntitiesForNewThread, final int maxThreads) {
        super(plugin, beforeSuper(concurrent,
                () -> checkArgument(interval > 0, "interval should be positive"),
                () -> checkArgument(minEntitiesForNewThread > 0, "minEntitiesForNewThread should be positive"),
                () -> checkArgument(maxThreads > 0, "maxThreads should be positive")
        ));

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
            if (tasks.isEmpty()) return newRedrawEntitiesRunnable();
            else {
                RedrawEntitiesRunnable minRunnable = null;
                Integer minEntitiesInRunnable = null;
                for (val task : tasks) {
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
                return tasks.size() < maxThreads ? newRedrawEntitiesRunnable() : minRunnable;
            }
        } finally {
            lock.unlock();
        }
    }

    protected RedrawEntitiesRunnable newRedrawEntitiesRunnable() {
        val runnable = new RedrawEntitiesRunnable();
        if (async) runnable.runTaskTimerAsynchronously(plugin, interval, interval);
        else runnable.runTaskTimer(plugin, interval, interval);

        tasks.add(runnable);

        return runnable;
    }

    @Override
    public void manageEntity(@NonNull final E entity) {
        getRedrawEntitiesRunnable().addEntity(entity);
    }

    @Override
    public void unmanageEntity(@NonNull final E entity) {
        lock.lock();
        try {
            val iterator = tasks.iterator();
            for (val task : tasks) if (task.removeEntity(entity)) {
                if (task.entities.size() == 0) iterator.remove();

                break;
            }
        } finally {
            lock.unlock();
        }
    }

    protected void shutdown() {
        lock.lock();
        try {
            for (val task : tasks) task.cancel();
        } finally {
            lock.unlock();
        }
    }

    @ToString
    @EqualsAndHashCode(callSuper = true)
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