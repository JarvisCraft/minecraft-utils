package ru.progrm_jarvis.minecraft.commons.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;

@UtilityClass
public class LocationUtil {

    public double getDistanceSquared(final double dx, final double dy, final double dz) {
        return dx * dx + dy * dy + dz * dz;
    }

    public double getDistanceSquared(final double x1, final double y1, final double z1,
                                     final double x2, final double y2, final double z2) {
        return getDistanceSquared(x2 - x1, y2 - y1, z2 - z1);
    }

    public double getDistanceSquared(final Location location1, final Location location2) {
        return getDistanceSquared(
                location2.getX() - location1.getX(),
                location2.getY() - location1.getY(),
                location2.getZ() - location1.getZ()
        );
    }
}
