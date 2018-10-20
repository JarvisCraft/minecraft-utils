package ru.progrm_jarvis.fakeentitylib;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface ObservableFakeEntity extends FakeEntity {

    /**
     * Gets whether or not this fake entity should be visible for all players online
     * which means that observer will attempt to add players to it whenever  they join game and remove them on leave.
     *
     * @return whether or not this fake entity is global
     */
    boolean isGlobal();

    boolean isRendered(Player player);

    void render(Player player);

    void unrender(Player player);

    default void onWorldChange(Consumer<World> callback) {}

    default void onLocationChange(Consumer<Location> callback) {}
}
