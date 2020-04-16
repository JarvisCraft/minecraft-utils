package ru.progrm_jarvis.minecraft.commons.schedule.task.counter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.progrm_jarvis.minecraft.commons.schedule.task.AbstractSchedulerRunnable;
import ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable;

/**
 * A {@link ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable}
 * which is run for a specified amount of times after which it is cancelled.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class BukkitTimer extends AbstractSchedulerRunnable {

    @NonNull Runnable task;
    @NonFinal long counter;

    @Override
    public void run() {
        if (--counter == 0) cancel();
        task.run();
    }

    public static SchedulerRunnable create(@NonNull final Runnable task, final long counter) {
        return new BukkitTimer(task, counter);
    }
}
