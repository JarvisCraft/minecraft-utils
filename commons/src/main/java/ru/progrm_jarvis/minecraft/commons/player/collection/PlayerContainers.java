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

    public PlayerContainer wrap(@NonNull final Collection<Player> collectionOfPlayers) {
        return new PlayerContainerCollectionWrapper(collectionOfPlayers);
    }

    public <T> PlayerContainer wrap(@NonNull final Map<Player, T> mapOfPlayers,
                                    @NonNull final Function<Player, T> defaultValueSupplier) {
        return new PlayerContainerMapWrapper<>(mapOfPlayers, defaultValueSupplier);
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
        public Collection<? extends Player> getPlayers() {
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
        public Collection<? extends Player> getPlayers() {
            return map.keySet();
        }
    }
}
