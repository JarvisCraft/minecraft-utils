package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import lombok.val;
import org.bukkit.entity.Player;

public interface ObservableFakeEntity extends FakeEntity {

    /**
     * Gets whether or not this fake entity should be visible for all players online
     * which means that observer will attempt to add players to it whenever  they join game and remove them on leave.
     *
     * @return whether or not this fake entity is global
     */
    boolean isGlobal();

    /**
     * Gets view distance for this fake entity. This may be not present in which case this returns {@code -1}.
     *
     * @return non-negative view distance for this fake entity if present and {@code -1} otherwise
     */
    int getViewDistance();

    /**
     * Returns {@code true} if the player has this fake entity rendered and {@code false} otherwise.
     *
     * @apiNote this method also returns {@code false} if the player specified is not associated with this fake entity
     *
     * @param player player to check
     * @return whether or not the player has this fake entity rendered
     */
    boolean isRendered(Player player);

    /**
     * Returns {@code true} if the player can theoretically see this entity and {@code false} otherwise.
     *
     * @apiNote method does not check whether the player is managed by this entity
     *
     * @param player player to check for ability to see this fake entity
     * @return whether or not the player can see this fake entity
     */
    boolean canSee(Player player);

    /**
     * Renders the entity for player which will temporarily despawn the entity for him
     * and normally prevent him from receiving any data related to this entity (except the one for rendering it back).
     *
     * @apiNote method does not check whether the player is managed by this entity
     *
     * @param player player for whom to unrender the entity
     */
    void render(Player player);

    /**
     * Unrenders the entity for player which means temporarily despawning the entity for him and <i>normally</i>
     * preventing him from receiving any data related to this entity (except the one for rendering it back).
     *
     * @apiNote method does not check whether the player is managed by this entity
     *
     * @param player player for whom to unrender the entity
     */
    void unrender(Player player);

    /**
     * Attempt to rerender this fake entity for player specified.
     * Rerendering means calling {@link #render(Player)} if the player does not see the entity although he should
     * or calling {@link #unrender(Player)} if the player sees the entity but should not.
     *
     * @implNote this implementation may be ineffective due to both
     * {@link #containsPlayer(Player)} and {@link #isRendered(Player)} calls
     * where they can be replaced with single map lookup in common implementations
     *
     * @param player player for whom to attempt to rerender this entity
     */
    default void attemptRerender(final Player player) {
        if (containsPlayer(player)) if (isRendered(player)) {
            if (!canSee(player)) unrender(player); // unrender for player if he sees but shouldn't
        } else if (canSee(player)) render(player); // render for player if doesn't see but should
    }

    /**
     * Attempts to rerender this fake entity for all players associated with it.
     * This logically means calling {@link #attemptRerender(Player)} on each player from {@link #getPlayers()}
     * although implementations are expected to provide some optimizations of this.
     */
    default void attemptRerenderForAll() {
        for (val player : getPlayers()) attemptRerender(player);
    }
}
