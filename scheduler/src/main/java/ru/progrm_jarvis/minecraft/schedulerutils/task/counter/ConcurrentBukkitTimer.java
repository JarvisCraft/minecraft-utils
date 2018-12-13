package ru.progrm_jarvis.minecraft.schedulerutils.task.counter;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@link BukkitRunnable} which is run for a specified amount of times after which it is cancelled.
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ConcurrentBukkitTimer extends BukkitRunnable {

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
}
