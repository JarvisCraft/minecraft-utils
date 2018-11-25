package ru.progrm_jarvis.minecraft.commons.util.plugin;

import net.md_5.bungee.api.plugin.Plugin;

/**
 * An object which contains a bungeecord plugin instance.
 *
 * @param <P> plugin on which this object depends
 */
public interface BungeePluginContainer<P extends Plugin> {

    /**
     * Gets the bungeecord plugin contained by this object.
     *
     * @return bungeecord plugin of this object
     */
    P getBungeePlugin();
}
