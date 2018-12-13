package ru.progrm_jarvis.minecraft.schedulerutils.task.counter;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

/**
 * A {@link BukkitRunnable} which is run for a specified amount of times after which it is cancelled.
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ConcurrentBukkitCallbackTimer extends BukkitRunnable {

    @NonNull LongConsumer callback;
    @NonNull AtomicLong counter;

    public ConcurrentBukkitCallbackTimer(@NonNull final LongConsumer callback, final long counter) {
        this.callback = callback;
        this.counter = new AtomicLong(counter);
    }

    @Override
    public void run() {
        val value = counter.decrementAndGet();

        if (counter.decrementAndGet() == 0) cancel();
        callback.accept(value);
    }
}
