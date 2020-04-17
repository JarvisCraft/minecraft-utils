package ru.progrm_jarvis.minecraft.commons.schedule.task.timer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import ru.progrm_jarvis.minecraft.commons.schedule.task.AbstractSchedulerRunnable;
import ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable;

/**
 * A {@link ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable}
 * which is run for a specified amount of times after which it is cancelled.
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BukkitTimer extends AbstractSchedulerRunnable {

    long counter;

    @Override
    public void run() {
        if (--counter == 0) cancel();
        tick();
    }

    protected abstract void tick();

    public static SchedulerRunnable create(@NonNull final Runnable task, final long counter) {
        return new FunctionalBukkitTimer(task, counter);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class FunctionalBukkitTimer extends BukkitTimer {

        @NotNull Runnable task;

        public FunctionalBukkitTimer(@NotNull final Runnable task,
                                     final long counter) {
            super(counter);
            this.task = task;
        }

        @Override
        protected void tick() {
            task.run();
        }
    }
}
