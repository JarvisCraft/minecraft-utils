package ru.progrm_jarvis.playerutils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

@UtilityClass
public class PlayerUtil {

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
