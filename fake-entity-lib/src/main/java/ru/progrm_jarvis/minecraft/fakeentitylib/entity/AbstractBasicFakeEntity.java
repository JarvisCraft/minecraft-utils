package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.progrm_jarvis.minecraft.commons.math.dimensional.Figure3D;
import ru.progrm_jarvis.minecraft.commons.math.dimensional.PointFigure;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Base for most common implementations of {@link BasicFakeEntity} containing player logic base.
 */
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractBasicFakeEntity extends AbstractPlayerContainingFakeEntity implements BasicFakeEntity {

    /**
     * Metadata of this fake entity
     */
    @Nullable @Getter WrappedDataWatcher metadata;

    /**
     * Velocity of this fake entity
     */
    @NonNull Vector velocity;

    @NonNull @Getter Figure3D hitbox;

    /**
     * Whether optimized packets should use for moving the entity or not
     */
    boolean compactMoving;

    public AbstractBasicFakeEntity(final boolean global, final int viewDistance,
                                   @NonNull final Location location,
                                   @NonNull final Map<Player, Boolean> players,
                                   @Nullable final Vector velocity, @Nullable final WrappedDataWatcher metadata) {
        super(viewDistance, global, location, players);

        this.velocity = velocity == null ? new Vector() : velocity;
        this.metadata = metadata;

        hitbox = PointFigure.from(location);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Metadata
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Sends metadata to all players seeing this entity creating packet if it has not yet been initialized.
     */
    protected abstract void sendMetadata();

    @Override
    public void setMetadata(@NonNull final WrappedDataWatcher metadata) {
        this.metadata = metadata.deepClone();

        sendMetadata();
    }

    @Override
    public void setMetadata(@NonNull final List<WrappedWatchableObject> metadata) {
        this.metadata = new WrappedDataWatcher(metadata);

        sendMetadata();
    }

    @Override
    public void setMetadata(@NonNull final Collection<WrappedWatchableObject> metadata) {
        setMetadata(new ArrayList<>(metadata));

        sendMetadata();
    }

    @Override
    public void setMetadata(@NonNull final WrappedWatchableObject... metadata) {
        setMetadata(Arrays.asList(metadata));

        sendMetadata();
    }

    @Override
    public void addMetadata(final Collection<WrappedWatchableObject> metadata) {
        final WrappedDataWatcher thisMetadata;
        if ((thisMetadata = this.metadata) == null) this.metadata = new WrappedDataWatcher(Arrays.asList(metadata));
        else for (val metadatum : metadata) thisMetadata
                .setObject(metadatum.getWatcherObject(), metadatum.getRawValue());

        sendMetadata();
    }

    @Override
    public void addMetadata(final WrappedWatchableObject... metadata) {
        final WrappedDataWatcher thisMetadata;
        if ((thisMetadata = this.metadata) == null) this.metadata = new WrappedDataWatcher(Arrays.asList(metadata));
        else for (val metadatum : metadata) thisMetadata
                .setObject(metadatum.getWatcherObject(), metadatum.getRawValue());

        sendMetadata();
    }

    @Override
    public void removeMetadata(final Iterable<Integer> indexes) {
        if (metadata == null) return;

        for (val index : indexes) metadata.remove(index);

        sendMetadata();
    }

    @Override
    public void removeMetadata(final int... indexes) {
        if (metadata == null) return;

        for (val index : indexes) metadata.remove(index);

        sendMetadata();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Movement
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Performs the movement of this living fake entity by given deltas and yaw and pitch specified
     * not performing any checks such as 8-block limit of deltas or angle minimization.
     *
     * @param dx delta on X-axis
     * @param dy delta on Y-axis
     * @param dz delta on Z-axis
     * @param yaw new yaw
     * @param pitch new pitch
     * @param sendVelocity {@code true} if velocity should be considered and {@code false} otherwise
     */
    protected abstract void performMoveLook(double dx, double dy, double dz,
                                            float yaw, float pitch, boolean sendVelocity);
    /**
     * Performs the movement of this living fake entity by given deltas and yaw and pitch specified
     * not performing any checks such as 8-block limit of deltas.
     *
     * @param dx delta on X-axis
     * @param dy delta on Y-axis
     * @param dz delta on Z-axis
     * @param sendVelocity {@code true} if velocity should be considered and {@code false} otherwise
     */
    protected abstract void performMove(double dx, double dy, double dz, boolean sendVelocity);

    /**
     * Performs the teleportation of this living fake entity to given coordinates changing yaw and pitch
     * not performing any checks such as using movement for less than 8-block deltas or angle minimization.
     *
     * @param x new location on X-axis
     * @param y new location on Y-axis
     * @param z new location on Z-axis
     * @param yaw new yaw
     * @param pitch new pitch
     * @param sendVelocity {@code true} if velocity should be considered and {@code false} otherwise
     */
    protected abstract void performTeleportation(double x, double y, double z,
                                                 float yaw, float pitch, boolean sendVelocity);

    /**
     * Performs the look by specified yaw and pitch.
     *
     * @param yaw new yaw
     * @param pitch new pitch
     */
    protected abstract void performLook(float yaw, float pitch);

    @Override
    public void move(final double dx, final double dy, final double dz, final float dYaw, final float dPitch) {
        if (compactMoving) {
            if (dx == 0 && dy == 0 && dz == 0) {
                if (dYaw != 0 || dPitch != 0) {
                    final float yaw = location.getYaw() + dYaw, pitch = location.getPitch() + dPitch;

                    performLook(yaw, pitch);

                    location.setYaw(yaw);
                    location.setPitch(pitch);
                }
            } else {
                velocity.setX(dx);
                velocity.setY(dy);
                velocity.setZ(dz);
                // use teleportation if any of axises is above 8 blocks limit
                if (dx > 8 || dy > 8 || dz > 8) {
                    final double x = location.getX() + dx, y = location.getY() + dy, z = location.getZ() + dz;
                    final float yaw = location.getYaw() + dYaw, pitch = location.getPitch() + dPitch;

                    performTeleportation(x, y, z, pitch, yaw, true);

                    location.setX(x);
                    location.setY(y);
                    location.setZ(z);
                    location.setYaw(yaw);
                    location.setPitch(pitch);
                }
                // otherwise use move
                else {
                    if (dYaw == 0 && dPitch == 0) performMove(dx, dy, dz, true);
                    else {
                        performMoveLook(dx, dy, dz, dYaw, dPitch, true);

                        location.setYaw(location.getYaw() + dYaw);
                        location.setPitch(location.getPitch() + dPitch);
                    }

                    location.setX(location.getX() + dx);
                    location.setY(location.getY() + dy);
                    location.setZ(location.getZ() + dz);
                }

                velocity.setX(0);
                velocity.setY(0);
                velocity.setZ(0);
            }
        } else {
            var changeLocation = false;
            double x = Double.NaN;
            if (dx != 0) {
                changeLocation = true;

                location.setX(x = (location.getX() + dx));
            }
            double y = Double.NaN;
            if (dy != 0) {
                changeLocation = true;

                location.setY(y = (location.getY() + dy));
            }
            double z = Double.NaN;
            if (dz != 0) {
                changeLocation = true;

                location.setZ(z = (location.getZ() + dz));
            }

            var changeLook = false;
            float yaw = Float.NaN;
            if (dYaw != 0) {
                changeLook = true;

                location.setYaw(yaw = (location.getYaw() + dYaw));
            }
            float pitch = Float.NaN;
            if (dPitch != 0) {
                changeLook = true;

                location.setPitch(pitch = (location.getPitch() + dPitch));
            }

            if (changeLocation) performTeleportation(x, y, z, yaw, pitch, false);
            else if (changeLook) performLook(yaw, pitch);
        }
    }

    @Override
    public void moveTo(final double x, final double y, final double z, final float yaw, final float pitch) {
        if (compactMoving) {
            final double dx = x - location.getX(), dy = y - location.getY(), dz = z - location.getZ();

            if (dx == 0 && dy == 0 && dz == 0) {
                if (yaw != location.getYaw() || pitch != location.getPitch()) {
                    performLook(yaw, pitch);

                    location.setYaw(yaw);
                    location.setPitch(pitch);
                }
            } else {
                velocity.setX(dx);
                velocity.setY(dy);
                velocity.setZ(dz);

                if (dx > 8 || dy > 8 || dz > 8) performTeleportation(x, y, z, yaw, pitch, true);
                else if (yaw != location.getYaw() || pitch != location.getPitch()) {
                    performMoveLook(dx, dy, dz, yaw, pitch, true);

                    location.setYaw(yaw);
                    location.setPitch(pitch);
                } else performMove(dx, dy, dz, true);

                location.setX(x);
                location.setY(y);
                location.setZ(z);

                velocity.setX(0);
                velocity.setY(0);
                velocity.setZ(0);
            }
        } else performNonCompactTeleportation(x, y, z, yaw, pitch);
    }

    @Override
    public void teleport(final double x, final double y, final double z, final float yaw, final float pitch) {
        if (compactMoving) {
            final double dx = x - location.getX(), dy = y - location.getY(), dz = z - location.getZ();

            if (dx == 0 && dy == 0 && dz == 0) {
                if (yaw != location.getYaw() || pitch != location.getPitch()) {
                    performLook(yaw, pitch);

                    location.setYaw(yaw);
                    location.setPitch(pitch);
                }
            } else {
                velocity.setX(dx);
                velocity.setY(dy);
                velocity.setZ(dz);

                if (dx > 8 || dy > 8 || dz > 8) performTeleportation(x, y, z, yaw, pitch, false);
                else if (yaw != location.getYaw() || pitch != location.getPitch()) {
                    performMoveLook(dx, dy, dz, yaw, pitch, false);

                    location.setYaw(yaw);
                    location.setPitch(pitch);
                } else performMove(dx, dy, dz, false);

                location.setX(x);
                location.setY(y);
                location.setZ(z);

                velocity.setX(0);
                velocity.setY(0);
                velocity.setZ(0);
            }
        } else performNonCompactTeleportation(x, y, z, yaw, pitch);
    }

    protected void performNonCompactTeleportation(final double x, final double y, final double z,
                                                  final float yaw, final float pitch) {
        var changeLocation = false;
        if (x != location.getX()) {
            changeLocation = true;

            location.setX(x);
        }
        if (y != location.getY()) {
            changeLocation = true;

            location.setY(y);
        }
        if (z != location.getZ()) {
            changeLocation = true;

            location.setZ(z);
        }
        var changeLook = false;
        if (yaw != location.getYaw()) {
            changeLook = true;

            location.setYaw(yaw);
        }
        if (pitch != location.getPitch()) {
            changeLook = true;

            location.setPitch(pitch);
        }

        if (changeLocation) performTeleportation(x, y, z, yaw, pitch, false);
        else if (changeLook) performLook(yaw, pitch);
    }

    @Override
    public void syncLocation() {
        performTeleportation(
                location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), false
        );
    }
}
