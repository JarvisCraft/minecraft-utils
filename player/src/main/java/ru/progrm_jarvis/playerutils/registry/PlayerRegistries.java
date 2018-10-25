package ru.progrm_jarvis.playerutils.registry;

import com.google.common.base.MoreObjects;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.playerutils.collection.PlayerContainer;
import ru.progrm_jarvis.playerutils.collection.PlayerContainers;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@UtilityClass
public class PlayerRegistries {

    private final Map<Plugin, PlayerRegistry> DEFAULT_REGISTRIES = new ConcurrentHashMap<>();

    private static final String DEFAULT_CHECK_INTERVAL_PROPERTY_NAME
            = PlayerContainers.class.getTypeName().concat(".DEFAULT_CHECK_INTERVAL");

    private static final long DEFAULT_CHECK_INTERVAL = Long.parseLong(MoreObjects.firstNonNull(
            System.getProperty(DEFAULT_CHECK_INTERVAL_PROPERTY_NAME), "5")
    );

    public PlayerRegistry defaultRegistry(@NonNull final Plugin plugin) {
        return DEFAULT_REGISTRIES.computeIfAbsent(plugin, pl -> new DefaultPlayerRegistry(pl, DEFAULT_CHECK_INTERVAL));
    }

    public <C extends PlayerContainer> C registerInDefaultRegistry(@NonNull final Plugin plugin,
                                                                 @NonNull final C playerContainer) {
        return defaultRegistry(plugin).register(playerContainer);
    }

    public PlayerContainer registerInDefaultRegistry(@NonNull final Plugin plugin,
                                          @NonNull final Collection<Player> collectionOfPlayers) {
        return registerInDefaultRegistry(plugin, PlayerContainers.wrap(collectionOfPlayers));
    }

    public <T> PlayerContainer registerInDefaultRegistry(@NonNull final Plugin plugin,
                                              @NonNull final Map<Player, T> mapOfPlayers,
                                              @NonNull final Function<Player, T> defaultValueSupplier) {
        return registerInDefaultRegistry(plugin, PlayerContainers.wrap(mapOfPlayers, defaultValueSupplier));
    }
}
