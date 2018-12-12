package ru.progrm_jarvis.minecraft.schedulerutils.task.initializer;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@UtilityClass
public class BukkitTaskInitializers {

    public BukkitTaskInitializer createTaskInitializer(@NonNull final Plugin plugin, final boolean async,
                                                       @NonNull final Runnable runnable) {
        return new AbstractBukkitTaskInitializer() {

            @Override
            public BukkitTask init() {
                return async
                        ? Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable)
                        : Bukkit.getScheduler().runTask(plugin, runnable);
            }
        };
    }

    public BukkitTaskInitializer createTaskInitializer(@NonNull final Plugin plugin, final boolean async,
                                                       @NonNull final BukkitRunnable runnable) {
        return new AbstractBukkitTaskInitializer() {

            @Override
            public BukkitTask init() {
                return task = async ? runnable.runTaskAsynchronously(plugin) : runnable.runTask(plugin);
            }
        };
    }

    public BukkitTaskInitializer createTimerTaskInitializer(@NonNull final Plugin plugin, final boolean async,
                                                            final long delay, final long period,
                                                            @NonNull final Runnable runnable) {
        return new AbstractBukkitTaskInitializer() {

            @Override
            public BukkitTask init() {
                return task = async
                        ? Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period)
                        : Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
            }
        };
    }

    public BukkitTaskInitializer createTimerTaskInitializer(@NonNull final Plugin plugin, final boolean async,
                                                            final long delay, final long period,
                                                            @NonNull final BukkitRunnable runnable) {
        return new AbstractBukkitTaskInitializer() {

            @Override
            public BukkitTask init() {
                return async
                        ? runnable.runTaskTimerAsynchronously(plugin, delay, period)
                        : runnable.runTaskTimer(plugin, delay, period);
            }
        };
    }

    public BukkitTaskInitializer createDelayedTaskInitializer(@NonNull final Plugin plugin, final boolean async,
                                                              final long delay,
                                                              @NonNull final Runnable runnable) {
        return new AbstractBukkitTaskInitializer() {

            @Override
            public BukkitTask init() {
                return task = async
                        ? Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay)
                        : Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
            }
        };
    }

    public BukkitTaskInitializer createDelayedTaskInitializer(@NonNull final Plugin plugin, final boolean async,
                                                              final long delay,
                                                              @NonNull final BukkitRunnable runnable) {

        return new AbstractBukkitTaskInitializer() {

            @Override
            public BukkitTask init() {
                return async
                        ? runnable.runTaskLaterAsynchronously(plugin, delay)
                        : runnable.runTaskLater(plugin, delay);
            }
        };
    }

    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    @FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
    private abstract class AbstractBukkitTaskInitializer implements BukkitTaskInitializer {

        @NonFinal BukkitTask task;

        protected abstract BukkitTask init();

        @Override
        public final BukkitTask initialize() {
            if (task == null || task.isCancelled()) synchronized (this) {
                if (task == null || task.isCancelled()) task = init();
            }

            return task;
        }

        @Override
        public final void shutdown() {
            if (!task.isCancelled()) synchronized (this) {
                if (!task.isCancelled()) task.cancel();
            }
        }
    }
}
