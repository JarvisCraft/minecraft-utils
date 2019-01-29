package ru.progrm_jarvis.minecraft.commons.control;

import org.bukkit.entity.Player;

/**
 * An object which manages the player
 */
public interface PlayerControl<S extends PlayerControl.Session> {

    /**
     * Starts the new control session for the player.
     *
     * @param player player for whom to start the control session
     * @return created control session for the player (or the one he currently has in this player control)
     *
     * @apiNote the session should be ended using {@link S#end()}
     */
    S startSession(final Player player);

    /**
     * Session of player control, responsible for handling the player's controls
     */
    interface Session {

        /**
         * Gets the player manages by this control session.
         *
         * @return MANAGED PLAYER
         */
        Player getPlayer();

        /**
         * Ends this player control session.
         */
        void end();
    }
}
