package ru.progrm_jarvis.fakeentitylib;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface FakeEntity {

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

    Collection<Player> getPlayers();
}
