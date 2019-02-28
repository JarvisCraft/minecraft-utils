package ru.progrm_jarvis.minecraft.commons.player;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

@UtilityClass
public class PlayerUtil {

    /**
     * Gets online players as a collection of exact {@link Player} type.
     *
     * @return a view of currently online players cast to exact type
     */
    @SuppressWarnings("unchecked")
    public Collection<Player> getOnlinePlayers() {
        return (Collection<Player>) Bukkit.getOnlinePlayers();
    }

    public Collection<Player> playersAround(@NonNull final Location location, final double radius) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getLocation().distance(location) <= radius)
                .collect(Collectors.toList());
    }

    public Collection<Player> playerEyesAround(@NonNull final Location location, final double radius) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getEyeLocation().distance(location) <= radius)
                .collect(Collectors.toList());
    }
}
