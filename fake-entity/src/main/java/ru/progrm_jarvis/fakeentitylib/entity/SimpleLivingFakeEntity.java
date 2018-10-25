package ru.progrm_jarvis.fakeentitylib.entity;

import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.base.Preconditions;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * A simple living entity self-sustained for direct usage.
 */
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PROTECTED)
public class SimpleLivingFakeEntity implements BasicFakeEntity {

    ///////////////////////////////////////////////////////////////////////////
    // Basic entity data
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Unique entity ID by which it should be identified in all packets.
     */
    final int id; // id should not be generated before all checks are performed

    /**
     * This fake entity's UUID
     */
    @Nullable final UUID uuid; // may be null as it is not required

    /**
     * Type of this entity
     */
    final EntityType type; // type of entity

    ///////////////////////////////////////////////////////////////////////////
    // General for FakeEntity
    ///////////////////////////////////////////////////////////////////////////

    /**
     * PLayers related to this fake entity
     */
    @NonNull final Map<Player, Boolean> players;

    /**
     * Whether or not this fake entity is global
     */
    @Getter final boolean global;

    /**
     * View distance for this entity or {@code -1} if none
     */
    @Getter int viewDistance;

    ///////////////////////////////////////////////////////////////////////////
    // Entity changing parameters
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Location of this fake entity
     */
    @Getter final Location location;

    /**
     * Head pitch of this fake entity
     */
    float headPitch;

    /**
     * Velocity of this fake entity
     */
    @Nullable Vector velocity;

    /**
     * Metadata of this fake entity
     */
    @Nullable @Getter WrappedDataWatcher metadata;

    // packets should not be created before id is generated

    /**
     * Packet used for spawning this fake entity
     */
    WrapperPlayServerSpawnEntityLiving spawnPacket;

    /**
     * Packet used for despawning this fake entity
     */
    WrapperPlayServerEntityDestroy despawnPacket;

    // packets which should be initialized only when first needed

    /**
     * Packet used for updating this fake entity's metadata
     */
    @NonFinal WrapperPlayServerEntityMetadata metadataPacket;

    /**
     * Packet used for moving this fake entity (not more than 8 blocks per axis) without modifying head rotation
     */
    WrapperPlayServerRelEntityMove movePacket;

    /**
     * Packet used for moving this fake entity (not more than 8 blocks per axis) and modifying head rotation
     */
    WrapperPlayServerRelEntityMoveLook moveLookPacket;

    /**
     * Packet used for teleporting this fake entity
     */
    WrapperPlayServerEntityTeleport teleportPacket;

    @Builder
    public SimpleLivingFakeEntity(final int entityId, @Nullable final UUID uuid, @NonNull final EntityType type,
                                  @NonNull final Map<Player, Boolean> players,
                                  final boolean global, final int viewDistance,
                                  @NonNull final Location location, float headPitch, @Nullable final Vector velocity,
                                  @Nullable final WrappedDataWatcher metadata) {
        // setup fields

        this.id = entityId;
        this.uuid = uuid;
        this.type = type;

        this.players = players;
        this.global = global;
        this.viewDistance = viewDistance;

        this.location = location;
        this.headPitch = headPitch;
        this.velocity = velocity;

        this.metadata = metadata;

        // setup packets

        spawnPacket = new WrapperPlayServerSpawnEntityLiving();
        spawnPacket.setEntityID(id);
        spawnPacket.setType(type);
        if (uuid != null) spawnPacket.setUniqueId(uuid);

        despawnPacket = new WrapperPlayServerEntityDestroy();
        despawnPacket.setEntityIds(new int[]{id});
    }

    protected void actualizeSpawnPacket() {
        spawnPacket.setX(location.getX());
        spawnPacket.setY(location.getY());
        spawnPacket.setZ(location.getZ());

        spawnPacket.setPitch(location.getPitch());
        spawnPacket.setYaw(location.getYaw());
        spawnPacket.setHeadPitch(headPitch);

        if (velocity != null) {
            spawnPacket.setVelocityX(velocity.getX());
            spawnPacket.setVelocityY(velocity.getY());
            spawnPacket.setVelocityZ(velocity.getZ());
        } else {
            spawnPacket.setVelocityX(0);
            spawnPacket.setVelocityY(0);
            spawnPacket.setVelocityZ(0);
        }

        spawnPacket.setMetadata(metadata);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Player management
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void addPlayer(final Player player) {
        if (canSeeIgnoreContainment(player)) render(player);
        else players.put(player, false);
    }

    @Override
    public void removePlayer(final Player player) {
        if (players.containsKey(player)) unrender(player);
    }

    @Override
    public boolean containsPlayer(final Player player) {
        return players.containsKey(player);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Spawning / Despawning
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void spawn() {
        actualizeSpawnPacket();

        for (val entry : players.entrySet()) if (entry.getValue()) spawnPacket.sendPacket(entry.getKey());
    }

    @Override
    public void despawn() {
        for (val entry : players.entrySet()) if (entry.getValue()) despawnPacket.sendPacket(entry.getKey());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Movement
    ///////////////////////////////////////////////////////////////////////////


    protected void performMove(final double dx, final double dy, final double dz, final float yaw, final float pitch) {
        if (pitch == 0 && yaw == 0) {
            if (movePacket == null) {
                movePacket = new WrapperPlayServerRelEntityMove();
                movePacket.setEntityID(id);
            }

            movePacket.setDx((int) (dx * 32 * 128));
            movePacket.setDy((int) (dy * 32 * 128));
            movePacket.setDz((int) (dz * 32 * 128));

            for (val entry : players.entrySet()) if (entry.getValue()) movePacket.sendPacket(entry.getKey());
        } else {
            if (moveLookPacket == null) {
                moveLookPacket = new WrapperPlayServerRelEntityMoveLook();
                moveLookPacket.setEntityID(id);

                moveLookPacket.setDx((int) (dx * 32 * 128));
                moveLookPacket.setDy((int) (dy * 32 * 128));
                moveLookPacket.setDz((int) (dz * 32 * 128));
                moveLookPacket.setYaw(yaw);
                moveLookPacket.setPitch(pitch);

                for (val entry : players.entrySet()) if (entry.getValue()) moveLookPacket.sendPacket(entry.getKey());
            }
        }
    }

    protected void performTeleportation(final double x, final double y, final double z,
                                        final float yaw, final float pitch) {
        if (teleportPacket == null) {
            teleportPacket = new WrapperPlayServerEntityTeleport();
            teleportPacket.setEntityID(id);
        }

        teleportPacket.setX(x);
        teleportPacket.setY(y);
        teleportPacket.setZ(z);
        teleportPacket.setYaw(yaw);
        teleportPacket.setPitch(pitch);

        for (val entry : players.entrySet()) if (entry.getValue()) teleportPacket.sendPacket(entry.getKey());
    }

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

    ///////////////////////////////////////////////////////////////////////////
    // Metadata
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Sends metadata to all players seeing this entity creating packet if it has not yet been initialized.
     */
    protected void sendMetadata() {
        if (metadata == null) return;
        if (metadataPacket == null) {
            metadataPacket = new WrapperPlayServerEntityMetadata();
            metadataPacket.setEntityID(id);
        }
        metadataPacket.setMetadata(metadata.getWatchableObjects());

        for (val entry : players.entrySet()) if (entry.getValue()) metadataPacket.sendPacket(entry.getKey());
    }

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
    public void setMetadata(@Nonnull final Collection<WrappedWatchableObject> metadata) {
        setMetadata(new ArrayList<>(metadata));

        sendMetadata();
    }

    @Override
    public void setMetadata(@Nonnull final WrappedWatchableObject... metadata) {
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
    // Rendering
    ///////////////////////////////////////////////////////////////////////////

    protected boolean canSeeIgnoreContainment(final Player player) {
        return player.getWorld() == location.getWorld() && player.getEyeLocation().distance(location) <= viewDistance;
    }

    @Override
    public boolean isRendered(final Player player) {
        return players.getOrDefault(player, false);
    }

    @Override
    public void render(final Player player) {
        actualizeSpawnPacket();
        spawnPacket.sendPacket(player);

        players.put(player, true);
    }

    @Override
    public void unrender(final Player player) {
        despawnPacket.sendPacket(player);

        players.put(player, false);
    }

    @Override
    public boolean canSee(final Player player) {
        Preconditions.checkArgument(
                players.containsKey(player), "Player attempting to rerender should be associated with this fake entity"
        );

        return canSeeIgnoreContainment(player);
    }

    @Override
    public void attemptRerenderForAll() {
        val world = location.getWorld();
        for (val entry : players.entrySet()) {
            val player = entry.getKey();
            if (entry.getValue()) {
                if (player.getWorld() != world
                        || player.getEyeLocation().distance(location) > viewDistance) unrender(player);
            } else if (player.getWorld() == world
                    && player.getEyeLocation().distance(location) <= viewDistance) render(player);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Simple accessors
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public World getWorld() {
        return location.getWorld();
    }

    @Override
    public Collection<Player> getPlayers() {
        return players.keySet();
    }
}
