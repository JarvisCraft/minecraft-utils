package ru.progrm_jarvis.minecraft.commons.player.collection;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

/**
 * An object which may store players.
 */
public interface PlayerContainer {

    /**
     * Adds a player to this container.
     *
     * @param player player to add
     */
    void addPlayer(Player player);

    /**
     * Adds players to this container.
     *
     * @param players players to add
     */
    default void addPlayers(final Player... players) {
        for (val player : players) addPlayer(player);
    }

    /**
     * Adds players to this container. {@link Collection} is used rather than {@link Iterable}
     * as there are usual cases when it is a minimal requirement for an effective solution.
     *
     * @param players players to add
     */
    default void addPlayers(final Collection<Player> players) {
        for (val player : players) addPlayer(player);
    }

    default void addOnlinePlayers() {
        addPlayers(Collections.unmodifiableCollection(Bukkit.getOnlinePlayers()));
    }

    /**
     * Removes a player from this container.
     *
     * @param player player to remove
     */
    void removePlayer(Player player);

    /**
     * Removes players from this container.
     *
     * @param players players to remove
     */
    default void removePlayesr(final Player... players) {
        for (val player : players) removePlayer(player);
    }

    /**
     * Removes players from this container.
     *
     * @param players players to remove
     */
    default void removePlayers(final Collection<Player> players) {
        for (val player : players) removePlayer(player);
    }

    /**
     * Returns {@code true} if this container contains the specified player and {@code false} otherwise.
     *
     * @param player player to check for containment
     */
    boolean containsPlayer(Player player);

    /**
     * Gets all players available in this container.
     *
     * @return all players contained
     */
    Collection<? extends Player> getPlayers();
}
