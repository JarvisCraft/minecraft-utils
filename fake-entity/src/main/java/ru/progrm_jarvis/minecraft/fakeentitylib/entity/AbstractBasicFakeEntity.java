package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

@ToString
@EqualsAndHashCode(callSuper = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractBasicFakeEntity extends AbstractPlayerContainingFakeEntity implements BasicFakeEntity {

    /**
     * Metadata of this fake entity
     */
    @Nullable @Getter WrappedDataWatcher metadata;

    public AbstractBasicFakeEntity(final boolean global, final int viewDistance,
                                   @NonNull final Location location,
                                   @NonNull final Map<Player, Boolean> players,
                                   @Nullable final WrappedDataWatcher metadata) {
        super(viewDistance, global, location, players);

        this.metadata = metadata;
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
    public void addMetadata(final List<WrappedWatchableObject> metadata) {
        if (this.metadata == null) this.metadata = new WrappedDataWatcher(metadata);
        else for (val metadatum : metadata) this.metadata.setObject(metadatum.getIndex(), metadatum);

        sendMetadata();
    }

    @Override
    public void addMetadata(final Collection<WrappedWatchableObject> metadata) {
        if (this.metadata == null) this.metadata = new WrappedDataWatcher(new ArrayList<>(metadata));
        else for (val metadatum : metadata) this.metadata.setObject(metadatum.getIndex(), metadatum);

        sendMetadata();
    }

    @Override
    public void addMetadata(final WrappedWatchableObject... metadata) {
        if (this.metadata == null) this.metadata = new WrappedDataWatcher(Arrays.asList(metadata));
        else for (val metadatum : metadata) this.metadata.setObject(metadatum.getIndex(), metadatum);

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
     */
    protected abstract void performMove(final double dx, final double dy, final double dz,
                                        final float yaw, final float pitch);

    /**
     * Performs the teleportation of this living fake entity to given coordinates changing yaw and pitch
     * not performing any checks such as using movement for less than 8-block deltas or angle minimization.
     *
     * @param x new location on X-axis
     * @param y new location on Y-axis
     * @param z new location on Z-axis
     * @param yaw new yaw
     * @param pitch new pitch
     */
    protected abstract void performTeleportation(final double x, final double y, final double z,
                                                 final float yaw, final float pitch);

    @Override
    public void teleport(final double x, final double y, final double z, final float yaw, final float pitch) {
        final double dx = x - location.getX(), dy = y - location.getY(), dz = z - location.getZ();

        if (dx > 8 || dy > 8 || dz > 8) performMove(dx, dy, dz, yaw, pitch);
        else performTeleportation(x, y, z, yaw, pitch);
    }

    @Override
    public void move(final double dx, final double dy, final double dz, final float dYaw, final float dPitch) {
        if (dx > 8 || dy > 8 || dz > 8) performMove(dx, dy, dz, location.getYaw() + dYaw, location.getPitch() + dPitch);
        else performTeleportation(
                location.getX() + dx, location.getY() + dy, location.getZ() + dz,
                location.getYaw() + dYaw, location.getPitch() + dPitch
        );
    }
}
