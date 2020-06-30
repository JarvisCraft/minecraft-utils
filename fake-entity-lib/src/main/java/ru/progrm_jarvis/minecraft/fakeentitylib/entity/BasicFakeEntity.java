package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import ru.progrm_jarvis.minecraft.commons.math.dimensional.Figure3D;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public interface BasicFakeEntity extends ObservableFakeEntity {

    ///////////////////////////////////////////////////////////////////////////
    // Spawning
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Spawns the entity for all players related to it who can see it.
     */
    void spawn();

    /**
     * Despawns this entity for all players related to it who can't see it.
     */
    void despawn();

    ///////////////////////////////////////////////////////////////////////////
    // Dimensional
    ///////////////////////////////////////////////////////////////////////////

    Figure3D getHitbox();

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

    void moveTo(double x, double y, double z, float yaw, float pitch);

    default void moveTo(final double x, final double y, final double z) {
        moveTo(x, y, z, 0, 0);
    }

    default void moveTo(@NonNull final Location location) {
        moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    void teleport(double x, double y, double z, float yaw, float pitch);

    default void teleport(final double x, final double y, final double z) {
        teleport(x, y, z, 0, 0);
    }

    default void teleport(@NonNull final Location location) {
        teleport(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Performs the location synchronization so that the clients surely have the exact location of the entity.
     */
    void syncLocation();

    ///////////////////////////////////////////////////////////////////////////
    // Metadata
    ///////////////////////////////////////////////////////////////////////////

    WrappedDataWatcher getMetadata();

    void setMetadata(WrappedDataWatcher metadata);

    void setMetadata(List<WrappedWatchableObject> metadata);

    void setMetadata(@Nonnull final Collection<WrappedWatchableObject> metadata);

    void setMetadata(@Nonnull final WrappedWatchableObject... metadata);

    void addMetadata(Collection<WrappedWatchableObject> metadata);

    void addMetadata(WrappedWatchableObject... metadata);

    void removeMetadata(Iterable<Integer> indexes);

    void removeMetadata(int... indexes);
}
