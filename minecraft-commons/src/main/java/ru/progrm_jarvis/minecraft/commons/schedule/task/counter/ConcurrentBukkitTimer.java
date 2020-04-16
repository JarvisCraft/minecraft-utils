package ru.progrm_jarvis.minecraft.commons.schedule.task.counter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.scheduler.BukkitRunnable;
import ru.progrm_jarvis.minecraft.commons.schedule.task.AbstractSchedulerRunnable;
import ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

/**
 * A {@link ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable}
 * which is run for a specified amount of times after which it is cancelled.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ConcurrentBukkitTimer extends AbstractSchedulerRunnable {

    @NonNull Runnable task;
    @NonNull AtomicLong counter;

    public ConcurrentBukkitTimer(@NonNull final Runnable task, final long counter) {
        this.task = task;
        this.counter = new AtomicLong(counter);
    }

    @Override
    public void run() {
        if (counter.decrementAndGet() == 0) cancel();
        task.run();
    }

    public static SchedulerRunnable create(@NonNull final Runnable task, final long counter) {
        return new ConcurrentBukkitTimer(task, new AtomicLong(counter));
    }

    public static SchedulerRunnable create(@NonNull final Runnable task, @NonNull final AtomicLong counter) {
        return new ConcurrentBukkitTimer(task, counter);
    }
}
