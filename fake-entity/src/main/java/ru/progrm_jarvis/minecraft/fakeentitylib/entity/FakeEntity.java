package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.progrm_jarvis.minecraft.commons.player.collection.PlayerContainer;

import java.util.Collection;

public interface FakeEntity extends PlayerContainer {

    /**
     * Gets the world of this fake entity.
     *
     * @return world of this fake entity
     */
    World getWorld();

    /**
     * Gets location of this fake entity, the object returned should not be modified without cloning
     * as it may be an actual fake entity's location object.
     *
     * @return location of this fake entity
     */
    Location getLocation();

    /**
     * Gets the players associated with this fake entity.
     *
     * @return all players associated with this entity
     */
    Collection<Player> getPlayers();

    /**
     * Gets whether this fake entity is visible or not.
     *
     * @return {@code true} if this fake entity is visible or {@code false} otherwise
     */
    boolean isVisible();

    /**
     * Makes this fake entity visible or not invisible.
     *
     * @param visible {@code true} if this fake entity should be visible or {@code false} if it should be invisible
     */
    void setVisible(boolean visible);
}
