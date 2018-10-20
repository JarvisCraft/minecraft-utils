package ru.progrm_jarvis.playerutils.collection;

import lombok.NonNull;
import org.bukkit.plugin.Plugin;

public abstract class AbstractPlayerAutoContainer implements PlayerContainer {

    public AbstractPlayerAutoContainer(@NonNull final Plugin plugin) {
        PlayerContainers.defaultRegistry(plugin).register(this);
    }
}
