package ru.progrm_jarvis.minecraft.commons.player.collection;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@UtilityClass
public class PlayerContainers {

    public PlayerContainer wrap(@NonNull final Collection<Player> collectionOfPlayers,
                                final boolean global) {
        return new PlayerContainerCollectionWrapper(collectionOfPlayers, global);
    }

    public <T> PlayerContainer wrap(@NonNull final Map<Player, T> mapOfPlayers,
                                    @NonNull final Function<Player, T> defaultValueSupplier,
                                    final boolean global) {
        return new PlayerContainerMapWrapper<>(mapOfPlayers, defaultValueSupplier, global);
    }

    @Value
    protected class PlayerContainerCollectionWrapper implements PlayerContainer {

        @NonNull private Collection<Player> collection;
        boolean global;

        public PlayerContainerCollectionWrapper(@NonNull final Collection<Player> collection, final boolean global) {
            this.collection = collection;
            this.global = global;

            if (global) addOnlinePlayers();
        }

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
        public Collection<? extends Player> getPlayers() {
            return collection;
        }
    }

    @Value
    protected class PlayerContainerMapWrapper<T> implements PlayerContainer {

        @NonNull private Map<Player, T> map;
        @NonNull private Function<Player, T> defaultValueSupplier;
        boolean global;

        public PlayerContainerMapWrapper(@NonNull final Map<Player, T> map,
                                         @NonNull final Function<Player, T> defaultValueSupplier,
                                         final boolean global) {
            this.map = map;
            this.defaultValueSupplier = defaultValueSupplier;
            this.global = global;

            if (global) addOnlinePlayers();
        }

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
        public Collection<? extends Player> getPlayers() {
            return map.keySet();
        }
    }
}
