package ru.progrm_jarvis.playerutils.collection;

import lombok.NonNull;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.playerutils.registry.PlayerRegistries;

public abstract class AbstractPlayerAutoContainer implements PlayerContainer {

    public AbstractPlayerAutoContainer(@NonNull final Plugin plugin) {
        PlayerRegistries.defaultRegistry(plugin).register(this);
    }
}
