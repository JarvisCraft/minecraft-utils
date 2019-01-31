package ru.progrm_jarvis.minecraft.commons.control;

import lombok.NonNull;
import org.bukkit.entity.Player;
import ru.progrm_jarvis.minecraft.commons.player.collection.PlayerContainer;

import java.util.Optional;

/**
 * An object which manages the player
 */
public interface PlayerControls<S extends PlayerControls.Session> extends PlayerContainer {

    /**
     * Starts the new controls session for the player.
     *
     * @param player player for whom to start the controls session
     * @return created controls session for the player (or the one he currently has in this player controls)
     *
     * @apiNote the session should be ended using {@link S#end()}
     */
    @NonNull S startSession(@NonNull Player player);

    @Override
    default void addPlayer(@NonNull final Player player) {
        startSession(player);
    }

    /**
     * Gets the current controls session of the specified player.
     *
     * @param player player for whom to get the controls sessions
     * @return optional of the player's current controls session or empty if it doesn't have one
     */
    @NonNull Optional<S> getSession(@NonNull Player player);

    @Override
    default void removePlayer(@NonNull final Player player) {
        getSession(player).ifPresent(Session::end);
    }

    @Override
    default boolean containsPlayer(@NonNull final Player player) {
        return getSession(player).isPresent();
    }

    /**
     * Session of player controls, responsible for handling the player's controls
     */
    interface Session {

        /**
         * Gets the player manages by this controls session.
         *
         * @return MANAGED PLAYER
         */
        Player getPlayer();

        /**
         * Ends this player controls session.
         */
        void end();
    }
}
