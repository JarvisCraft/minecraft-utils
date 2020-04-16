package ru.progrm_jarvis.minecraft.commons.schedule.task.counter;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.scheduler.BukkitRunnable;
import ru.progrm_jarvis.minecraft.commons.schedule.task.AbstractSchedulerRunnable;
import ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable;

import java.util.function.LongConsumer;

/**
 * A {@link ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable}
 * which is run for a specified amount of times after which it is cancelled.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class BukkitCallbackTimer extends AbstractSchedulerRunnable {

    @NonNull LongConsumer task;
    @NonFinal long counter;

    @Override
    public void run() {
        final long value;

        if ((value = --counter) == 0) cancel();
        task.accept(value);
    }

    public static SchedulerRunnable create(@NonNull final LongConsumer task, final long counter) {
        return new BukkitCallbackTimer(task, counter);
    }
}
