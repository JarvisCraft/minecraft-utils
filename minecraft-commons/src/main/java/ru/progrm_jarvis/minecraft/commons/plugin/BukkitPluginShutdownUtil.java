package ru.progrm_jarvis.minecraft.commons.plugin;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.util.concurrent.ConcurrentCollections;
import ru.progrm_jarvis.minecraft.commons.util.shutdown.Shutdownable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class BukkitPluginShutdownUtil {

    private final Map<Plugin, PluginShutdownHandler> PLUGIN_SHUTDOWN_HANDLERS = new ConcurrentHashMap<>();

    private PluginShutdownHandler getOrCreateShutdownHandler(final Plugin plugin) {
        return PLUGIN_SHUTDOWN_HANDLERS.computeIfAbsent(plugin, PluginShutdownHandler::new);
    }

    private Optional<PluginShutdownHandler> getOptionalShutdownHandler(final Plugin plugin) {
        return Optional.ofNullable(PLUGIN_SHUTDOWN_HANDLERS.get(plugin));
    }

    public void addShutdownHook(final Plugin plugin, final Shutdownable callback) {
        getOrCreateShutdownHandler(plugin).callbacks.add(callback);
    }

    public void addShutdownHooks(final Plugin plugin, final Shutdownable... callbacks) {
        getOrCreateShutdownHandler(plugin).callbacks.addAll(Arrays.asList(callbacks));
    }

    public void addShutdownHooks(final Plugin plugin, final Collection<Shutdownable> callbacks) {
        getOrCreateShutdownHandler(plugin).callbacks.addAll(callbacks);
    }

    public void removeShutdownHook(final Plugin plugin, final Shutdownable callback) {
        getOptionalShutdownHandler(plugin).ifPresent(handler -> handler.callbacks.remove(callback));
    }

    public void removeShutdownHooks(final Plugin plugin, final Shutdownable... callbacks) {
        getOptionalShutdownHandler(plugin).ifPresent(handler -> handler.callbacks.removeAll(Arrays.asList(callbacks)));
    }

    public void removeShutdownHooks(final Plugin plugin, final Collection<Shutdownable> callback) {
        getOptionalShutdownHandler(plugin).ifPresent(handler -> handler.callbacks.removeAll(callback));
    }

    @Value
    private class PluginShutdownHandler implements Listener {

        private Collection<Shutdownable> callbacks = ConcurrentCollections.concurrentList(new ArrayList<>());

        private PluginShutdownHandler(@NonNull final Plugin plugin) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }

        @EventHandler(ignoreCancelled = true)
        public void onPluginDisable(final PluginDisableEvent event) {
            val handler = PLUGIN_SHUTDOWN_HANDLERS.get(event.getPlugin());
            if (handler != null) {
                val callbacks = new ArrayList<>(handler.callbacks);
                for (val shutdownable : callbacks) shutdownable.shutdown();
                handler.callbacks.clear();
            }
        }
    }
}