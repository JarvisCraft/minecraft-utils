package ru.progrm_jarvis.minecraft.commons.schedule.task.counter;

import lombok.*;
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
public class ConcurrentBukkitCallbackTimer extends AbstractSchedulerRunnable {

    @NonNull LongConsumer task;
    @NonNull AtomicLong counter;

    @Override
    public void run() {
        val value = counter.decrementAndGet();

        if (counter.decrementAndGet() == 0) cancel();
        task.accept(value);
    }

    public static SchedulerRunnable create(@NonNull final LongConsumer task, final long counter) {
        return new ConcurrentBukkitCallbackTimer(task, new AtomicLong(counter));
    }

    public static SchedulerRunnable create(@NonNull final LongConsumer task, @NonNull final AtomicLong counter) {
        return new ConcurrentBukkitCallbackTimer(task, counter);
    }
}
