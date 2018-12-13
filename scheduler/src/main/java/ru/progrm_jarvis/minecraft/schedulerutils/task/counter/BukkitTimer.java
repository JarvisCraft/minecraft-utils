package ru.progrm_jarvis.minecraft.schedulerutils.task.counter;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A {@link BukkitRunnable} which is run for a specified amount of times after which it is cancelled.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class BukkitTimer extends BukkitRunnable {

    @NonNull Runnable task;
    @NonFinal long counter;

    @Override
    public void run() {
        if (--counter == 0) cancel();
        task.run();
    }
}
