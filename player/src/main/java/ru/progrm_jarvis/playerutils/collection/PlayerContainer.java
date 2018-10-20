package ru.progrm_jarvis.playerutils.collection;

import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * An object which may store players.
 */
public interface PlayerContainer {

    /**
     * Adds a player to this object.
     *
     * @param player player to add
     */
    void addPlayer(Player player);

    /**
     * Removes a player from this object.
     *
     * @param player player to remove
     */
    void removePlayer(Player player);

    /**
     * Returns {@code true} if this object contains the specified player and {@code false} otherwise.
     *
     * @param player player to check for containment
     */
    boolean containsPlayer(Player player);

    /**
     * Gets all players available in this collection.
     *
     * @return all players contained
     */
    Collection<Player> getPlayers();
}
