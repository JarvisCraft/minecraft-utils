package ru.progrm_jarvis.minecraft.commons.plugin;

import org.bukkit.plugin.Plugin;

/**
 * An object which contains a bukkit plugin instance.
 *
 * @param <P> plugin on which this object depends
 */
public interface BukkitPluginContainer<P extends Plugin> {

    /**
     * Gets the bukkit plugin contained by this object.
     *
     * @return bukkit plugin of this object
     */
    P getBukkitPlugin();
}
