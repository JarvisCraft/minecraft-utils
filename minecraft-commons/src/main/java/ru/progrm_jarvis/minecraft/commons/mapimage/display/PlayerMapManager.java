package ru.progrm_jarvis.minecraft.commons.mapimage.display;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import ru.progrm_jarvis.minecraft.commons.MinecraftCommons;
import ru.progrm_jarvis.minecraft.commons.util.SystemPropertyUtil;
import ru.progrm_jarvis.reflector.Reflector;
import ru.progrm_jarvis.reflector.wrapper.MethodWrapper;
import ru.progrm_jarvis.reflector.wrapper.fast.FastMethodWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility responsible to allocate minimal amount of {@link MapView} for internal usage.
 * <p>
 * This allocates IDs once needed and reuses them as much as possible, so that:
 * <ul>
 *     <li>Two players can see different images by one same ID at the same time</li>
 *     <li>Any player can see different images by single ID at different time</li>
 *     <li>IDs are stored and reused at server restarts so that no useless IDs allocations occur</li>
 * </ul>
 * <p>
 * This requires the developer to free maps using {@link #freeMap(Player, MapView)}
 * (obtained from {@link #allocateMap(Player)}) once those are no longer required in order to use those optimally.
 */
@UtilityClass
public class PlayerMapManager {

    /**
     * Single lock for all operations.
     */
    private final Lock lock = new ReentrantLock();

    /**
     * Method wrapper for {@link MapView#getId()} because it returns
     * {@link short} and {@link int} on different Bukkit API versions.
     */
    private final MethodWrapper<MapView, Number> MAP_VIEW__GET_ID__METHOD = FastMethodWrapper.from(
            Reflector.getDeclaredMethod(MapView.class, "getId")
    );

    /**
     * Root directory of {@link PlayerMapManager} in which internal data is stored between sessions.
     */
    private final File ROOT_DIRECTORY = new File(MinecraftCommons.ROOT_DIRECTORY,
            SystemPropertyUtil.getSystemProperty(
                    PlayerMapManager.class.getCanonicalName() + ".internal-ids-list-file-name", Function.identity(),
                    "player_map_id_manager/"
            )
    );

    /**
     * File containing IDs of allocated maps.
     */
    private final File IDS_LIST_FILE = new File(ROOT_DIRECTORY, "map_ids.list");

    /**
     * Maps allocated in Bukkit for internal usage.
     */
    private final Set<MapView> allocatedMaps;

    /**
     * Maps of {@link #allocatedMaps} available for the player.
     * Similar maps may be related to different players as the image logic doesn't intersect.
     */
    private final SetMultimap<Player, MapView> playerMaps;

    // static initialization of file-related stuff
    static {
        if (!ROOT_DIRECTORY.isFile()) try {
            Files.createDirectories(ROOT_DIRECTORY.toPath());
        } catch (final IOException e) {
            throw new RuntimeException("Couldn't create source directory of PlayerMapManager", e);
        }

        // Loads the maps stored in a file between sessions
        if (IDS_LIST_FILE.isFile()) {
            final List<String> fileLines;
            try {
                fileLines = Files.readAllLines(IDS_LIST_FILE.toPath());
            } catch (final IOException e) {
                throw new RuntimeException(
                        "Couldn't read file " + IDS_LIST_FILE.getName() + " of PlayerMapManager ", e
                );
            }

            allocatedMaps = fileLines.stream()
                    .filter(line -> !line.isEmpty() && line.indexOf('#') != 0)
                    .map(line -> Bukkit.getMap(Short.parseShort(line)))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet()); // HashSet is used by default

            Bukkit.getLogger().info(
                    "Loaded " + allocatedMaps.size() + " internally allocated world map IDs: "+ allocatedMaps.toString()
            );
        } else {
            try {
                Files.createFile(IDS_LIST_FILE.toPath());
            } catch (final IOException e) {
                throw new RuntimeException(
                        "Couldn't create file " + IDS_LIST_FILE.getName() + " of PlayerMapManager ", e
                );
            }
            allocatedMaps = new HashSet<>();
        }

        // use optimal key size for allocatedMap
        playerMaps = HashMultimap.create(
                Math.max(8, allocatedMaps.size()), Math.max(16, Bukkit.getOnlinePlayers().size())
        );
    }

    /**
     * Gets {@link MapView}'s ID independently on version.
     *
     * @param mapView {@link MapView} whose {@link MapView#getId()} to invoke
     * @return map view's ID
     */
    public Number getMapId(@NonNull final MapView mapView) {
        return MAP_VIEW__GET_ID__METHOD.invoke(mapView);
    }

    /**
     * Allocates new map instance.
     *
     * @return newly allocated map with only blank renderer applied
     *
     * @apiNote should be called only in case of need
     */
    private MapView allocateNewMap() {
        lock.lock();
        try {
            final MapView map;
            {
                val worlds = Bukkit.getWorlds();
                if (worlds.isEmpty()) throw new IllegalStateException("There are no Bukkit worlds available");
                map = Bukkit.createMap(worlds.get(0));
            }

            try {
                Files.write(
                        IDS_LIST_FILE.toPath(),
                        (getMapId(map).toString() + System.lineSeparator()).getBytes(),
                        StandardOpenOption.APPEND
                );
            } catch (final IOException e) {
                throw new RuntimeException(
                        "Couldn't write newly allocated ID to " + IDS_LIST_FILE.getName() + " of PlayerMapManager", e
                );
            }

            // clear renderers for map
            for (val renderer : map.getRenderers()) map.removeRenderer(renderer);
            allocatedMaps.add(map);

            return map;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Allocates new map view for player.
     * This returns a map view which is stored in this manager and is free for the player at the moment.
     * The map should be freed for the player once he can't see it / he leaves the server.
     *
     * @param player player for whom to allocate the map
     * @return map allocated for the player
     *
     * @see #freeMap(Player, MapView) should be called whenever the player stops seeing this map or leaves the server
     */
    public MapView allocateMap(@NonNull final Player player) {
        lock.lock();
        try {
            val mapsOfPlayer = playerMaps.get(player);

            // if the player has all maps allocated of available than allocate another one (specially for him <3)
            // this is a fast equivalent of iterating through each allocated map
            // because only maps from allocatedMaps may be contained in mapsOfPlayers which is a Set
            if (mapsOfPlayer.size() == allocatedMaps.size()) {
                val map = allocateNewMap();
                mapsOfPlayer.add(map);

                return map;
            }
            //otherwise take one of the available maps for him
            for (val map : allocatedMaps) if (!mapsOfPlayer.contains(map)) {
                mapsOfPlayer.add(map);

                return map;
            }

            // this should never be reached
            throw new IllegalStateException(
                    "Something went wrong, could not find any allocated map for the player although there should be one"
            );
        } finally {
            lock.unlock();
        }
    }

    /**
     * Frees the map for the player so that it can be reused.
     * This should be called whenever the player stops seeing this map or leaves the server.
     *
     * @param player player for whom to free the map
     * @apiNote this <b>must</b> be called for any map allocation once it can be free
     *
     * @see #allocateMap(Player) only obtained by calling this method should be freed
     */
    public void freeMap(@NonNull final Player player, @NonNull final MapView map) {
        lock.lock();
        try {
            playerMaps.remove(player, map);
            for (val renderer : map.getRenderers()) map.removeRenderer(renderer);
        } finally {
            lock.unlock();
        }
    }
}
