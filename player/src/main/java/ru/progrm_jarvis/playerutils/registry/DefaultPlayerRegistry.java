package ru.progrm_jarvis.playerutils.registry;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.playerutils.collection.PlayerContainer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class DefaultPlayerRegistry implements PlayerRegistry {

    @NonNull Plugin plugin;
    @NonNull @Getter Set<Player> players;
    @NonNull Set<PlayerContainer> playerContainers = Collections.newSetFromMap(new WeakHashMap<>());
    private ReadWriteLock playerContainersLock = new ReentrantReadWriteLock();
    Lock playerContainersReadLock = playerContainersLock.readLock();
    Lock playerContainersWriteLock = playerContainersLock.writeLock();

    public DefaultPlayerRegistry(@NonNull final Plugin plugin, @NonNull final Set<Player> playerSet,
                                 final long checkInterval) {
        Preconditions.checkArgument(playerSet.isEmpty(), "playerSet should be empty");

        this.plugin = plugin;
        players = playerSet;

        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
            public final void onPlayerJoin(final PlayerJoinEvent event) {
                addPlayer(event.getPlayer());
            }

            @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
            public final void onPlayerQuit(final PlayerQuitEvent event) {
                removePlayer(event.getPlayer());
            }
        }, plugin);
    }

    public DefaultPlayerRegistry(@NonNull final Plugin plugin, final boolean concurrent, final long checkInterval) {
        this(plugin, concurrent ? new HashSet<>() : ConcurrentHashMap.newKeySet(), checkInterval);
    }

    public DefaultPlayerRegistry(@NonNull final Plugin plugin, final long checkInterval) {
        this(plugin, true, checkInterval);
    }

    @Override
    public void addPlayer(final Player player) {
        players.add(player);

        Bukkit.getScheduler().runTask(plugin, () -> {
            playerContainersReadLock.lock();
            try {
                for (val playerContainer : playerContainers) playerContainer.addPlayer(player);
            } finally {
                playerContainersReadLock.unlock();
            }
        });
    }

    @Override
    public void removePlayer(final Player player) {
        players.remove(player);

        Bukkit.getScheduler().runTask(plugin, () -> {
            playerContainersReadLock.lock();
            try {
                for (val playerContainer : playerContainers) playerContainer.removePlayer(player);
            } finally {
                playerContainersReadLock.unlock();
            }
        });
    }

    @Override
    public boolean containsPlayer(final Player player) {
        return players.contains(player);
    }

    @Override
    public void register(@NonNull final PlayerContainer playerContainer) {
        playerContainersWriteLock.lock();
        try {
            playerContainers.add(playerContainer);
        } finally {
            playerContainersWriteLock.unlock();
        }
    }

    @Override
    public void unregister(@NonNull final PlayerContainer playerContainer) {
        playerContainersWriteLock.lock();
        try {
            playerContainers.remove(playerContainer);
        } finally {
            playerContainersWriteLock.unlock();
        }
    }
}
