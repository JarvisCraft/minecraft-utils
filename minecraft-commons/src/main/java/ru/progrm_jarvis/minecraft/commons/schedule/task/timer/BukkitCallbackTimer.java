package ru.progrm_jarvis.minecraft.commons.schedule.task.timer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import ru.progrm_jarvis.minecraft.commons.schedule.task.AbstractSchedulerRunnable;
import ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable;

import java.util.function.LongConsumer;

/**
 * A {@link ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable}
 * which is run for a specified amount of times after which it is cancelled.
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BukkitCallbackTimer extends AbstractSchedulerRunnable {

    long counter;

    @Override
    public void run() {
        final long value;

        if ((value = --counter) == 0) cancel();
        tick(value);
    }

    protected abstract void tick(long counter);

    public static SchedulerRunnable create(@NonNull final LongConsumer task, final long counter) {
        return new FunctionalBukkitCallbackTimer(task, counter);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class FunctionalBukkitCallbackTimer extends BukkitCallbackTimer {

        @NotNull LongConsumer task;

        public FunctionalBukkitCallbackTimer(@NotNull final LongConsumer task,
                                             final long counter) {
            super(counter);
            this.task = task;
        }

        @Override
        protected void tick(final long counter) {
            task.accept(counter);
        }
    }
}
