package ru.progrm_jarvis.playerutils.collection;

import com.google.common.base.MoreObjects;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.playerutils.registry.DefaultPlayerRegistry;
import ru.progrm_jarvis.playerutils.registry.PlayerRegistry;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@UtilityClass
public class PlayerContainers {

    private final Map<Plugin, PlayerRegistry> DEFAULT_REGISTRIES = new ConcurrentHashMap<>();
    private static final long defaultCheckInterval = Long.parseLong(MoreObjects.firstNonNull(
            System.getProperty(PlayerContainers.class.getTypeName()).concat(".defaultCheckInterval"), "5")
    );

    public PlayerRegistry defaultRegistry(@NonNull final Plugin plugin) {
        return DEFAULT_REGISTRIES.computeIfAbsent(plugin, pl -> new DefaultPlayerRegistry(pl, defaultCheckInterval));
    }

    public PlayerContainer wrap(@NonNull final Collection<Player> collectionOfPlayers) {
        return new PlayerContainerCollectionWrapper(collectionOfPlayers);
    }

    public <T> PlayerContainer wrap(@NonNull final Map<Player, T> mapOfPlayers,
                                    @NonNull final Function<Player, T> defaultValueSupplier) {
        return new PlayerContainerMapWrapper<>(mapOfPlayers, defaultValueSupplier);
    }

    public void registerInDefaultRegistry(@NonNull final Plugin plugin,
                                          @NonNull final PlayerContainer playerContainer) {
        defaultRegistry(plugin).register(playerContainer);
    }

    public void registerInDefaultRegistry(@NonNull final Plugin plugin,
                                          @NonNull final Collection<Player> collectionOfPlayers) {
        registerInDefaultRegistry(plugin, wrap(collectionOfPlayers));
    }

    public <T> void registerInDefaultRegistry(@NonNull final Plugin plugin,
                                              @NonNull final Map<Player, T> mapOfPlayers,
                                              @NonNull final Function<Player, T> defaultValueSupplier) {
        registerInDefaultRegistry(plugin, wrap(mapOfPlayers, defaultValueSupplier));
    }

    @Value
    protected class PlayerContainerCollectionWrapper implements PlayerContainer {

        @NonNull private final Collection<Player> collection;

        @Override
        public void addPlayer(final Player player) {
            collection.add(player);
        }

        @Override
        public void removePlayer(final Player player) {
            collection.remove(player);
        }

        @Override
        public boolean containsPlayer(final Player player) {
            return collection.contains(player);
        }

        @Override
        public Collection<Player> getPlayers() {
            return collection;
        }
    }

    @Value
    protected class PlayerContainerMapWrapper<T> implements PlayerContainer {

        @NonNull private final Map<Player, T> map;
        @NonNull private Function<Player, T> defaultValueSupplier;

        @Override
        public void addPlayer(final Player player) {
            map.put(player, defaultValueSupplier.apply(player));
        }

        @Override
        public void removePlayer(final Player player) {
            map.remove(player);
        }

        @Override
        public boolean containsPlayer(final Player player) {
            return map.containsKey(player);
        }

        @Override
        public Collection<Player> getPlayers() {
            return map.keySet();
        }
    }
}
