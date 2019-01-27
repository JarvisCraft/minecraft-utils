package ru.progrm_jarvis.minecraft.commons.player.registry;

import lombok.NonNull;
import lombok.val;
import org.bukkit.entity.Player;
import ru.progrm_jarvis.minecraft.commons.player.collection.PlayerContainer;
import ru.progrm_jarvis.minecraft.commons.player.collection.PlayerContainers;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public interface PlayerRegistry extends PlayerContainer {

    <C extends PlayerContainer> C register(C playerContainer);

    <C extends PlayerContainer> C unregister(C playerContainer);

    default PlayerContainer register(@NonNull final Collection<Player> playerCollection, final boolean global) {
        val playerContainer = PlayerContainers.wrap(playerCollection, global);
        register(playerContainer);

        return playerContainer;
    }

    default <T> PlayerContainer register(@NonNull final Map<Player, T> playerCollection,
                                         @NonNull final Function<Player, T> defaultValueSupplier,
                                         final boolean global) {
        val playerContainer = PlayerContainers.wrap(playerCollection, defaultValueSupplier, global);
        register(playerContainer);

        return playerContainer;
    }

    void shutdown();
}
