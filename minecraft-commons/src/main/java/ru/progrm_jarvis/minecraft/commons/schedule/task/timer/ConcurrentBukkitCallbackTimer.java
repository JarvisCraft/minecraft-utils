package ru.progrm_jarvis.minecraft.commons.schedule.task.timer;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
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
public abstract class ConcurrentBukkitCallbackTimer extends AbstractSchedulerRunnable {

    @NonNull AtomicLong counter;

    @Override
    public void run() {
        final long value;

        if ((value = counter.decrementAndGet()) == 0) cancel();
        tick(value);
    }

    protected abstract void tick(long counter);

    public static SchedulerRunnable create(@NonNull final LongConsumer task, final long counter) {
        return new FunctionalConcurrentBukkitCallbackTimer(task, new AtomicLong(counter));
    }

    public static SchedulerRunnable create(@NonNull final LongConsumer task, @NonNull final AtomicLong counter) {
        return new FunctionalConcurrentBukkitCallbackTimer(task, counter);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class FunctionalConcurrentBukkitCallbackTimer extends ConcurrentBukkitCallbackTimer {

        @NotNull LongConsumer task;

        public FunctionalConcurrentBukkitCallbackTimer(@NotNull final LongConsumer task,
                                                       @NotNull final AtomicLong counter) {
            super(counter);
            this.task = task;
        }

        @Override
        protected void tick(final long counter) {
            task.accept(counter);
        }
    }
}
