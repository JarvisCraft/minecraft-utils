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
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.minecraft.commons.util.LocationUtil;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A simple living entity self-sustained for direct usage.
 */
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
public class SimpleLivingFakeEntity extends AbstractBasicFakeEntity {

    ///////////////////////////////////////////////////////////////////////////
    // Basic entity data
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Unique entity ID by which it should be identified in all packets.
     */
    @Getter final int entityId; // id should not be generated before all checks are performed

    /**
     * This fake entity's UUID
     */
    final @Nullable UUID uuid; // may be null as it is not required

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
    final @NonNull Map<Player, Boolean> players;

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
    @Getter final @NonNull Location location;

    /**
     * Head pitch of this fake entity
     */
    float headPitch;

    /**
     * Metadata of this fake entity
     */
    @Getter @Nullable WrappedDataWatcher metadata;

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
     * Packet used for modifying entity's  head rotation
     */
    WrapperPlayServerEntityLook lookPacket;

    /**
     * Packet used for moving this fake entity (not more than 8 blocks per axis) and modifying head rotation
     */
    WrapperPlayServerRelEntityMoveLook moveLookPacket;

    /**
     * Packet used for teleporting this fake entity
     */
    WrapperPlayServerEntityTeleport teleportPacket;

    /**
     * Packet used for fake entity velocity
     */
    WrapperPlayServerEntityVelocity velocityPacket;

    /**
     * Difference between the actual entity <i>x</i> and its visible value
     */
    final double xOffset,
    /**
     * Difference between the actual entity <i>y</i> and its visible value
     */
    yOffset,
    /**
     * Difference between the actual entity <i>z</i> and its visible value
     */
    zOffset;

    /**
     * Difference between the actual entity <i>yaw</i> and its visible value
     */
    final float yawOffset,

    /**
     * Difference between the actual entity <i>pitch</i> and its visible value
     */
    pitchOffset,

    /**
     * Difference between the actual entity <i>head pitch</i> and its visible value
     */
    headPitchOffset;

    @Builder
    public SimpleLivingFakeEntity(final int entityId, final @Nullable UUID uuid,
                                  // Start of entities properties, TODO specific class
                                  final @NonNull EntityType type,
                                  final double xOffset, final double yOffset, final double zOffset,
                                  final float yawOffset, final float pitchOffset, final float headPitchOffset,
                                  // End of entity's properties
                                  final @NonNull Map<Player, Boolean> players,
                                  final boolean global, final int viewDistance,
                                  boolean visible, final @NonNull Location location, float headPitch,
                                  final @Nullable Vector velocity, final @Nullable WrappedDataWatcher metadata) {
        super(global, viewDistance, location, players, velocity, metadata);

        // setup fields

        this.entityId = entityId;
        this.uuid = uuid;
        this.type = type;

        this.players = players;
        this.global = global;
        this.viewDistance = Math.max(-1, viewDistance);

        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.yawOffset = yawOffset;
        this.pitchOffset = pitchOffset;
        this.headPitchOffset = headPitchOffset;

        this.visible = visible;

        this.location = location;
        this.headPitch = headPitch;

        this.metadata = metadata;

        // setup packets

        {
            final WrapperPlayServerSpawnEntityLiving thisSpawnPacket;
            spawnPacket = thisSpawnPacket = new WrapperPlayServerSpawnEntityLiving();
            thisSpawnPacket.setEntityID(entityId);
            thisSpawnPacket.setType(type);
            if (uuid != null) thisSpawnPacket.setUniqueId(uuid);
        }

        {
            final WrapperPlayServerEntityDestroy thisDespawnPacket;
            despawnPacket = thisDespawnPacket = new WrapperPlayServerEntityDestroy();
            thisDespawnPacket.setEntityIds(new int[]{entityId});
        }
    }

    public Location getLocation() {
        return LocationUtil.withOffset(location, xOffset, yOffset, zOffset, yawOffset, pitchOffset);
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
        final WrapperPlayServerSpawnEntityLiving thisSpawnPacket;
        {
            final Location thisLocation;
            (thisSpawnPacket = spawnPacket).setX((thisLocation = location).getX() + xOffset);
            thisSpawnPacket.setY(thisLocation.getY() + yOffset);
            thisSpawnPacket.setZ(thisLocation.getZ() + zOffset);

            thisSpawnPacket.setPitch(thisLocation.getPitch() + pitchOffset);
            thisSpawnPacket.setYaw(thisLocation.getYaw() + yawOffset);
            thisSpawnPacket.setHeadPitch(headPitch + headPitchOffset);
        }

        {
            final Vector thisVelocity;
            thisSpawnPacket.setVelocityX((thisVelocity = velocity).getX());
            thisSpawnPacket.setVelocityY(thisVelocity.getY());
            thisSpawnPacket.setVelocityZ(thisVelocity.getZ());
        }

        {
            final WrappedDataWatcher thisMetadata;
            if ((thisMetadata = metadata) != null) thisSpawnPacket.setMetadata(thisMetadata);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Spawning / Despawning
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void spawn() {
        if (visible) {
            actualizeSpawnPacket();

            for (val entry : players.entrySet()) if (entry.getValue()) performSpawnNoChecks(entry.getKey());
        }
    }

    @Override
    public void despawn() {
        if (visible) for (val entry : players.entrySet()) if (entry.getValue()) performDespawnNoChecks(entry.getKey());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Movement
    ///////////////////////////////////////////////////////////////////////////

    protected boolean isOnGround() {
        final Location thisLocation;
        //noinspection ConstantConditions #getWorld() may but shouldn't return null
        return (thisLocation = location).getY() % 1 == 0 && thisLocation.getWorld()
                .getBlockAt(thisLocation).getType().isSolid();
    }

    protected boolean hasVelocity() {
        return velocity.length() != 0;
    }

    /**
     * Updates the velocity packet initializing it if it haven;t been initialized.
     *
     * @apiNote call to this method guarantees that {@link #velocityPacket} won't be {@code null} after it
     */
    protected void actualizeVelocityPacket() {
        WrapperPlayServerEntityVelocity packet;
        if ((packet = velocityPacket) == null) {
            packet = velocityPacket = new WrapperPlayServerEntityVelocity();
            packet.setEntityID(entityId);
        }

        final Vector thisVelocity;
        packet.setVelocityX((thisVelocity = velocity).getX());
        packet.setVelocityY(thisVelocity.getY());
        packet.setVelocityZ(thisVelocity.getZ());
    }

    @Override
    @SuppressWarnings("Duplicates")
    protected void performMoveLook(final double dx, final double dy, final double dz,
                                   final float yaw, final float pitch, boolean sendVelocity) {
        if (visible) {
            WrapperPlayServerRelEntityMoveLook thisMoveLookPacket;
            if ((thisMoveLookPacket = moveLookPacket) == null) {
                moveLookPacket = thisMoveLookPacket = new WrapperPlayServerRelEntityMoveLook();
                thisMoveLookPacket.setEntityID(entityId);
            }

            thisMoveLookPacket.setDx(dx);
            thisMoveLookPacket.setDy(dy);
            thisMoveLookPacket.setDz(dz);
            thisMoveLookPacket.setYaw(yaw);
            thisMoveLookPacket.setPitch(pitch);
            thisMoveLookPacket.setOnGround(isOnGround());

            sendVelocity = sendVelocity && hasVelocity();
            if (sendVelocity) actualizeVelocityPacket();

            final Set<Map.Entry<Player, Boolean>> entries;
            if ((!(entries = players.entrySet()).isEmpty())) {
                val thisVelocityPacket = sendVelocity ? velocityPacket : null;

                for (val entry : entries) if (entry.getValue()) {
                    val player = entry.getKey();

                    if (sendVelocity) thisVelocityPacket.sendPacket(player);
                    thisMoveLookPacket.sendPacket(player);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    protected void performMove(final double dx, final double dy, final double dz, boolean sendVelocity) {
        if (visible) {
            WrapperPlayServerRelEntityMove thisMovePacket;
            if ((thisMovePacket = movePacket) == null) {
                movePacket = thisMovePacket = new WrapperPlayServerRelEntityMove();
                thisMovePacket.setEntityID(entityId);
            }

            thisMovePacket.setDx(dx);
            thisMovePacket.setDy(dy);
            thisMovePacket.setDz(dz);
            thisMovePacket.setOnGround(isOnGround());

            sendVelocity = sendVelocity && hasVelocity();
            if (sendVelocity) actualizeVelocityPacket();

            final Set<Map.Entry<Player, Boolean>> entries;
            if ((!(entries = players.entrySet()).isEmpty())) {
                val thisVelocityPacket = sendVelocity ? velocityPacket : null;

                for (val entry : entries) if (entry.getValue()) {
                    val player = entry.getKey();

                    if (sendVelocity) thisVelocityPacket.sendPacket(player);
                    thisMovePacket.sendPacket(player);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    protected void performTeleportation(final double x, final double y, final double z,
                                        final float yaw, final float pitch, boolean sendVelocity) {
        if (visible) {
            WrapperPlayServerEntityTeleport thisTeleportPacket;
            if ((thisTeleportPacket = teleportPacket) == null) {
                teleportPacket = thisTeleportPacket = new WrapperPlayServerEntityTeleport();
                thisTeleportPacket.setEntityID(entityId);
            }

            thisTeleportPacket.setX(x + xOffset);
            thisTeleportPacket.setY(y + yOffset);
            thisTeleportPacket.setZ(z + zOffset);
            thisTeleportPacket.setYaw(yaw + yawOffset);
            thisTeleportPacket.setPitch(pitch + pitchOffset);
            thisTeleportPacket.setOnGround(isOnGround());

            sendVelocity = sendVelocity && hasVelocity();
            if (sendVelocity) actualizeVelocityPacket();

            final Set<Map.Entry<Player, Boolean>> entries;
            if ((!(entries = players.entrySet()).isEmpty())) {
                val thisVelocityPacket = sendVelocity ? velocityPacket : null;

                for (val entry : entries) if (entry.getValue()) {
                    val player = entry.getKey();

                    if (sendVelocity) thisVelocityPacket.sendPacket(player);
                    thisTeleportPacket.sendPacket(player);
                }
            }
        }
    }

    @Override
    protected void performLook(final float yaw, final float pitch) {
        if (visible) {
            WrapperPlayServerEntityLook thisLookPacket;
            if ((thisLookPacket = lookPacket) == null) {
                lookPacket = thisLookPacket = new WrapperPlayServerEntityLook();
                thisLookPacket.setEntityID(entityId);
            }

            thisLookPacket.setYaw(yaw);
            thisLookPacket.setPitch(pitch);
            thisLookPacket.setOnGround(isOnGround());

            for (val entry : players.entrySet()) if (entry.getValue()) thisLookPacket.sendPacket(entry.getKey());
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Metadata
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Sends metadata to all players seeing this entity creating packet if it has not yet been initialized.
     */
    @Override
    protected void sendMetadata() {
        if (visible) {
            final WrappedDataWatcher thisMetadata;
            if ((thisMetadata = metadata) == null) return;

            WrapperPlayServerEntityMetadata thisMetadataPacket;
            if ((thisMetadataPacket = metadataPacket) == null) {
                metadataPacket = thisMetadataPacket = new WrapperPlayServerEntityMetadata();
                thisMetadataPacket.setEntityID(entityId);
            }
            thisMetadataPacket.setMetadata(thisMetadata.getWatchableObjects());

            for (val entry : players.entrySet()) if (entry.getValue()) thisMetadataPacket.sendPacket(entry.getKey());
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Rendering
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void render(final Player player) {
        actualizeSpawnPacket();
        performSpawnNoChecks(player);

        players.put(player, true);
    }

    @Override
    protected void unrender(final Player player) {
        performDespawnNoChecks(player);

        players.put(player, false);
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

    @Override
    public void remove() {
        despawn();

        players.clear();
    }
}
