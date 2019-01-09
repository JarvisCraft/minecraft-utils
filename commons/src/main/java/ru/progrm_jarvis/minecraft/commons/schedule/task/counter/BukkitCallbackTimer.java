package ru.progrm_jarvis.minecraft.commons.schedule.task.counter;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.LongConsumer;

/**
 * A {@link BukkitRunnable} which is run for a specified amount of times after which it is cancelled.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class BukkitCallbackTimer extends BukkitRunnable {

    @NonNull LongConsumer callback;
    @NonFinal long counter;

    @Override
    public void run() {
        val value = --counter;

        if (value == 0) cancel();
        callback.accept(value);
    }
}
