package ru.progrm_jarvis.minecraft.schedulerutils.task.initializer;

import org.bukkit.scheduler.BukkitTask;

@FunctionalInterface
public interface BukkitTaskInitializer {

    BukkitTask initialize();

    default void shutdown() {}
}
