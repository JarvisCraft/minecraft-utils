package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

/**
 * Base for most common implementations of {@link ObservableFakeEntity} containing player logic base.
 */
@ToString
@EqualsAndHashCode(callSuper = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractPlayerContainingFakeEntity extends AbstractObservableFakeEntity {

    @NonNull final Map<Player, Boolean> players;

    public AbstractPlayerContainingFakeEntity(final int viewDistance, final boolean global,
                                              @NonNull final Location location,
                                              @NonNull final Map<Player, Boolean> players) {
        super(global, viewDistance, location);

        if (!players.isEmpty()) players.clear();
        this.players = players;
    }

    @Override
    public Collection<Player> getPlayers() {
        return players.keySet();
    }

    @Override
    public boolean isRendered(@NonNull final Player player) {
        return players.getOrDefault(player, false);
    }

    @Override
    public boolean containsPlayer(final Player player) {
        return players.containsKey(player);
    }

    @Override
    public void addPlayer(final Player player) {
        if (!players.containsKey(player)) {
            if (canSee(player)) render(player);
            else players.put(player, false);
        }
    }

    @Override
    public void removePlayer(final Player player) {
        val canSee = players.get(player);
        if (canSee != null) {
            if (canSee) unrender(player);
            else players.remove(player);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Rendering
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Renders the entity for player making him see it.
     *
     * @param player player for whom to render the entity
     */
    protected abstract void render(Player player);

    /**
     * Unrenders the entity for player which means temporarily despawning the entity for him.
     *
     * @param player player for whom to unrender the entity
     */
    protected abstract void unrender(Player player);

    @Override
    public void attemptRerender(final Player player) {
        val sees = players.get(player);
        if (sees == null) return;

        if (sees) {
            if (!canSee(player)) render(player);
        } else if (canSee(player)) unrender(player);
    }


    @Override
    public void attemptRerenderForAll() {
        for (val entry : players.entrySet()) {
            val player = entry.getKey();

            if (entry.getValue()) { // sees
                if (!canSee(player)) unrender(player);
            } else if (canSee(player)) render(player);
        }
    }
}
