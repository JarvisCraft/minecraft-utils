package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.aspect.annotation.WhenVisible;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * A simple living entity self-sustained for direct usage.
 */
@ToString
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class SimpleLivingFakeEntity extends AbstractBasicFakeEntity {

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
    @NonNull @Getter final Location location;

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

    /**
     * Difference between the actual entity <i>x</i> and its visible value
     */
    double xDelta,
    /**
     * Difference between the actual entity <i>y</i> and its visible value
     */
    yDelta,
    /**
     * Difference between the actual entity <i>z</i> and its visible value
     */
    zDelta;

    /**
     * Difference between the actual entity <i>yaw</i> and its visible value
     */
    float yawDelta = 0,

    /**
     * Difference between the actual entity <i>pitch</i> and its visible value
     */
    pitchDelta = 0,

    /**
     * Difference between the actual entity <i>head pitch</i> and its visible value
     */
    headPitchDelta = 0;

    @Builder
    public SimpleLivingFakeEntity(final int entityId, @Nullable final UUID uuid, @NonNull final EntityType type,
                                  @NonNull final Map<Player, Boolean> players,
                                  final boolean global, final int viewDistance,
                                  boolean visible,
                                  @NonNull final Location location, float headPitch, @Nullable final Vector velocity,
                                  @Nullable final WrappedDataWatcher metadata) {
        super(global, viewDistance, location, players, metadata);

        // setup fields

        this.id = entityId;
        this.uuid = uuid;
        this.type = type;

        this.players = players;
        this.global = global;
        this.viewDistance = Math.max(-1, viewDistance);
        this.visible = visible;

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

    /**
     * Spawns the entity for player without performing any checks
     * such as player containment checks or spawn packet actualization.
     *
     * @param player player to whom to spawn this entity
     */
    protected void performSpawnNoChecks(final Player player) {
        spawnPacket.sendPacket(player);
    }

    /**
     * Despawns the entity for player without performing any checks
     * such as player containment checks or spawn packet actualization.
     *
     * @param player player to whom to despawn this entity
     */
    protected void performDespawnNoChecks(final Player player) {
        despawnPacket.sendPacket(player);
    }

    protected void actualizeSpawnPacket() {
        spawnPacket.setX(location.getX() + xDelta);
        spawnPacket.setY(location.getY() + yDelta);
        spawnPacket.setZ(location.getZ() + zDelta);

        spawnPacket.setPitch(location.getPitch() + pitchDelta);
        spawnPacket.setYaw(location.getYaw() + yawDelta);
        spawnPacket.setHeadPitch(headPitch + headPitchDelta);

        if (velocity != null) {
            spawnPacket.setVelocityX(velocity.getX());
            spawnPacket.setVelocityY(velocity.getY());
            spawnPacket.setVelocityZ(velocity.getZ());
        } else {
            spawnPacket.setVelocityX(0);
            spawnPacket.setVelocityY(0);
            spawnPacket.setVelocityZ(0);
        }

        if (metadata != null) spawnPacket.setMetadata(metadata);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Spawning / Despawning
    ///////////////////////////////////////////////////////////////////////////

    @Override
    @WhenVisible
    public void spawn() {
        actualizeSpawnPacket();

        for (val entry : players.entrySet()) if (entry.getValue()) performSpawnNoChecks(entry.getKey());
    }

    @Override
    @WhenVisible
    public void despawn() {
        for (val entry : players.entrySet()) if (entry.getValue()) performDespawnNoChecks(entry.getKey());
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
    @Override
    @WhenVisible
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
    @Override
    @WhenVisible
    protected void performTeleportation(final double x, final double y, final double z,
                                        final float yaw, final float pitch) {
        if (teleportPacket == null) {
            teleportPacket = new WrapperPlayServerEntityTeleport();
            teleportPacket.setEntityID(id);
        }

        teleportPacket.setX(x + xDelta);
        teleportPacket.setY(y + yDelta);
        teleportPacket.setZ(z + zDelta);
        teleportPacket.setYaw(yaw + yawDelta);
        teleportPacket.setPitch(pitch + pitchDelta);

        for (val entry : players.entrySet()) if (entry.getValue()) teleportPacket.sendPacket(entry.getKey());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Metadata
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Sends metadata to all players seeing this entity creating packet if it has not yet been initialized.
     */
    @Override
    @WhenVisible
    protected void sendMetadata() {
        if (metadata == null) return;
        if (metadataPacket == null) {
            metadataPacket = new WrapperPlayServerEntityMetadata();
            metadataPacket.setEntityID(id);
        }
        metadataPacket.setMetadata(metadata.getWatchableObjects());

        for (val entry : players.entrySet()) if (entry.getValue()) metadataPacket.sendPacket(entry.getKey());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Rendering
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void render(final Player player) {
        actualizeSpawnPacket();
        performSpawnNoChecks(player);
    }

    @Override
    public void unrender(final Player player) {
        performDespawnNoChecks(player);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Visibility
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setVisible(final boolean visible) {
        if (this.visible == visible) return;

        this.visible = visible;

        if (visible) spawn();
        else despawn();
    }
}
