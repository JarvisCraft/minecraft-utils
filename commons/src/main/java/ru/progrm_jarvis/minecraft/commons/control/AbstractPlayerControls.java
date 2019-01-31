package ru.progrm_jarvis.minecraft.commons.control;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

/**
 * Abstract implementation of {@link PlayerControls} providing its common mechanisms.
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractPlayerControls<S extends AbstractPlayerControls.Session> implements PlayerControls<S> {

    /**
     * Map of player's currently managed by this player controls and their current active sessions
     */
    @NonNull Map<Player, @NonNull S> sessions;

    /**
     * Whether this control session is a <i>global player container</i> or not
     */
    @Getter boolean global;

    // a more optimal solution without allocating unneeded Optional objects
    @Override
    public boolean containsPlayer(@NonNull final Player player) {
        return sessions.containsKey(player);
    }

    // a more optimal solution without allocating unneeded Optional objects
    @Override
    public void removePlayer(@NonNull final Player player) {
        val session = sessions.get(player);
        if (session != null) session.end();
    }

    @Override
    public Collection<? extends Player> getPlayers() {
        return sessions.keySet();
    }

    @Override
    @NonNull public S startSession(@NonNull final Player player) {
        val session = createSession(player);
        sessions.put(player, session);

        return session;
    }

    /**
     * Creates the controls session for the player specified.
     *
     * @param player player for whom to initialize the controls session
     * @return created controls session
     *
     * @implSpec implementations are not required to add the created session to {@link #sessions}
     * as this is done by this method's caller
     *
     * @see Session#startSession(Player) this method's default caller
     */
    @NonNull protected abstract S createSession(Player player);

    /**
     * Finalizer called in {@link S#end()} in order to cleanup everything needed when the player end his session.
     *
     * @param session session to release
     * @implSpec should <b>not</b> call to {@link S#end()} as this will (most definitely) lead to infinite recursion
     *
     * @implSpec implementations are not required to remove the session from {@link #sessions}
     * as this is done by this method's caller
     *
     * @see Session#end() this method's default caller
     */
    protected abstract void releaseControls(S session);

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    @FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
    protected abstract class Session implements PlayerControls.Session {

        @Getter Player player;

        @Override
        public void end() {
            //noinspection unchecked
            releaseControls((S) this);
            sessions.remove(player);
        }
    }
}
