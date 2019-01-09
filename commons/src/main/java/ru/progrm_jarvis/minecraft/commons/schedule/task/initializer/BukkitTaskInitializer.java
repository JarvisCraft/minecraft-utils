package ru.progrm_jarvis.minecraft.commons.schedule.task.initializer;

import org.bukkit.scheduler.BukkitTask;

@FunctionalInterface
public interface BukkitTaskInitializer {

    BukkitTask initialize();

    default void shutdown() {}
}
