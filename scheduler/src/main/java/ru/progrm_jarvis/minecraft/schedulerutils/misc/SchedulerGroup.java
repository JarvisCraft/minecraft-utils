package ru.progrm_jarvis.minecraft.schedulerutils.misc;

import lombok.NonNull;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class SchedulerGroup extends BukkitRunnable {

    public abstract void addTask(@NonNull Runnable task);

    public abstract void removeTask(@NonNull Runnable task);
}
