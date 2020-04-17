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

        if ((value = --counter) == 0) {
            cancelTask();
            onOver();
        }
        onTick(value);
    }

    protected final void cancelTask() {
        super.cancel();
    }

    @Override
    public void cancel() {
        cancelTask();
        onAbort();
    }

    protected void onTick(long counter) {}

    protected void onAbort() {}

    protected void onOver() {}

    public static SchedulerRunnable create(@NonNull final LongConsumer task, final long counter) {
        return new CompactCallbackTimer(task, counter);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class CompactCallbackTimer extends BukkitCallbackTimer {

        @NotNull LongConsumer task;

        public CompactCallbackTimer(@NotNull final LongConsumer task,
                                    final long counter) {
            super(counter);
            this.task = task;
        }

        @Override
        protected void onTick(final long counter) {
            task.accept(counter);
        }
    }
}
