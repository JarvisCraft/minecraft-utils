package ru.progrm_jarvis.fakeentitylib;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public interface BasicFakeEntity extends ObservableFakeEntity {

    ///////////////////////////////////////////////////////////////////////////
    // Spawning
    ///////////////////////////////////////////////////////////////////////////

    void spawn();

    void despawn();

    ///////////////////////////////////////////////////////////////////////////
    // Location
    ///////////////////////////////////////////////////////////////////////////

    void teleport(double x, double y, double z, float yaw, float pitch);

    default void teleport(final double x, final double y, final double z) {
        teleport(x, y, z, 0, 0);
    }

    default void teleport(@NonNull final Location location) {
        teleport(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    void move(double dx, double dy, double dz, float dYaw, float dPitch);

    default void move(final double dx, final double dy, final double dz) {
        move(dx, dy, dz, 0, 0);
    }

    default void move(@NonNull final Vector direction, float dYaw, float dPitch) {
        move(direction.getX(), direction.getY(), direction.getZ(), dYaw, dPitch);
    }

    default void move(@NonNull final Vector direction) {
        move(direction, 0, 0);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Metadata
    ///////////////////////////////////////////////////////////////////////////

    WrappedDataWatcher getMetadata();

    void setMetadata(WrappedDataWatcher metadata);

    void setMetadata(List<WrappedWatchableObject> metadata);

    void setMetadata(@Nonnull final Collection<WrappedWatchableObject> metadata);

    void setMetadata(@Nonnull final WrappedWatchableObject... metadata);

    void addMetadata(List<WrappedWatchableObject> metadata);

    void addMetadata(Collection<WrappedWatchableObject> metadata);

    void addMetadata(WrappedWatchableObject... metadata);

    void removeMetadata(Iterable<Integer> indexes);

    void removeMetadata(int... indexes);
}
