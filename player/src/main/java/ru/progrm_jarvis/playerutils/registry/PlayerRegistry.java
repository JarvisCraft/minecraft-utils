package ru.progrm_jarvis.playerutils.registry;

import lombok.NonNull;
import lombok.val;
import org.bukkit.entity.Player;
import ru.progrm_jarvis.playerutils.collection.PlayerContainer;
import ru.progrm_jarvis.playerutils.collection.PlayerContainers;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public interface PlayerRegistry extends PlayerContainer {

    <C extends PlayerContainer> C register(C playerContainer);

    <C extends PlayerContainer> C unregister(C playerContainer);

    default PlayerContainer register(@NonNull final Collection<Player> playerCollection) {
        val playerContainer = PlayerContainers.wrap(playerCollection);
        register(playerContainer);

        return playerContainer;
    }

    default <T> PlayerContainer register(@NonNull final Map<Player, T> playerCollection,
                                         @NonNull final Function<Player, T> defaultValueSupplier) {
        val playerContainer = PlayerContainers.wrap(playerCollection, defaultValueSupplier);
        register(playerContainer);

        return playerContainer;
    }
}
