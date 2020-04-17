package ru.progrm_jarvis.minecraft.commons.schedule.task.timer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import ru.progrm_jarvis.minecraft.commons.schedule.task.AbstractSchedulerRunnable;
import ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@link ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable}
 * which is run for a specified amount of times after which it is cancelled.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class ConcurrentBukkitTimer extends AbstractSchedulerRunnable {

    @NonNull AtomicLong counter;

    @Override
    public void run() {
        if (counter.decrementAndGet() == 0) cancel();
        tick();
    }

    protected abstract void tick();

    public static SchedulerRunnable create(@NonNull final Runnable task, final long counter) {
        return new FunctionalAbstractSchedulerRunnable(task, new AtomicLong(counter));
    }

    public static SchedulerRunnable create(@NonNull final Runnable task, @NonNull final AtomicLong counter) {
        return new FunctionalAbstractSchedulerRunnable(task, counter);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class FunctionalAbstractSchedulerRunnable extends ConcurrentBukkitTimer {

        @NotNull Runnable task;

        public FunctionalAbstractSchedulerRunnable(@NotNull final Runnable task,
                                                   @NotNull final AtomicLong counter) {
            super(counter);
            this.task = task;
        }

        @Override
        protected void tick() {
            task.run();
        }
    }
}
