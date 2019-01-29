package ru.progrm_jarvis.minecraft.commons.control;

import org.bukkit.entity.Player;
import ru.progrm_jarvis.minecraft.commons.player.collection.PlayerContainer;

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
    S startSession(final Player player);

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
