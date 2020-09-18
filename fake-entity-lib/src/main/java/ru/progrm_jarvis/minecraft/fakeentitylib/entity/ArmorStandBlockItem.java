package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.javacommons.annotation.ownership.Own;
import ru.progrm_jarvis.minecraft.commons.nms.NmsUtil;
import ru.progrm_jarvis.minecraft.commons.nms.metadata.MetadataGenerator.ArmorStand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static ru.progrm_jarvis.minecraft.commons.nms.metadata.MetadataGenerator.ArmorStand.armorStandFlags;
import static ru.progrm_jarvis.minecraft.commons.nms.metadata.MetadataGenerator.ArmorStand.headRotation;
import static ru.progrm_jarvis.minecraft.commons.nms.metadata.MetadataGenerator.Entity.*;

/**
 * A fake small (or very small) movable block-item which can be normally rotated over all axises
 * and have floating point coordinates. This block is displayed as an item on head of invisible armor stand.
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ArmorStandBlockItem extends SimpleLivingFakeEntity {

    protected static final double PIXEL_SIZE = 0x1.0p-4,
            HALF_PIXEL_SIZE = PIXEL_SIZE * 0x1.0p-5,
            ARMOR_STAND_BODY_HEIGHT = (16 + 8) * PIXEL_SIZE,
            ARMOR_STAND_HEAD_OFFSET = ARMOR_STAND_BODY_HEIGHT - HALF_PIXEL_SIZE,
            ITEM_CENTER_Y_OFFSET = 3 * PIXEL_SIZE + HALF_PIXEL_SIZE; // offset of the item center from the rotation center

    /**
     * Rotation of this block
     */
    @Nullable Vector3F rotation;

    /**
     * Item displayed by this block
     */
    @NonNull ItemStack item;

    /**
     * Packet used for displaying this block's displayed item
     */
    WrapperPlayServerEntityEquipment equipmentPacket;

    /**
     * Initializes a newly created armor stand block-item from parameters given.
     *
     * @param uuid unique entity ID of this block-item entity
     * @param playersMap map to be used as backend for this block-item entity
     * @param global whether this block-item is global (the value returned by {@link #isGlobal()})
     * @param visible whether this block-item is initially be visible
     * @param viewDistance view distance of this block-item
     * @param location location of this block-item
     * @param rotation rotation of this block item
     * @param small whether this block-item is small
     * @param item item to be displayed by this block-item
     */
    public ArmorStandBlockItem(final @Nullable UUID uuid,
                               final Map<Player, Boolean> playersMap,
                               final boolean global, final int viewDistance, final boolean visible,
                               final Location location, final @Nullable Vector3F rotation,
                               final boolean small, final boolean marker, final @NonNull ItemStack item) {
        super(
                NmsUtil.nextEntityId(), uuid,
                EntityType.ARMOR_STAND, 0, small ? ARMOR_STAND_HEAD_OFFSET / 2 : ARMOR_STAND_HEAD_OFFSET, 0, 0, 0, 0,
                playersMap, global, viewDistance, visible, location, 0, null, createMetadata(rotation, small, marker)
        );

        this.rotation = rotation;

        equipmentPacket = new WrapperPlayServerEntityEquipment();
        equipmentPacket.setEntityID(entityId);
        equipmentPacket.setSlot(EnumWrappers.ItemSlot.HEAD);
        equipmentPacket.setItem(this.item = item);
    }

    /**
     * Creates new armor stand block-item by parameters specified.
     *
     * @param concurrent whether created block-item supports concurrent modification of players related to it
     * @param global whether created block-item is global (the value returned by {@link #isGlobal()})
     * @param viewDistance view distance of created block-item
     * @param location location of created block-item
     * @param rotation rotation of created block item
     * @param small whether created block-item is small
     * @param item item to be displayed by this block-item
     * @return newly created armor stand block-item
     */
    public static ArmorStandBlockItem create(final @Nullable UUID uuid,
                                             final boolean concurrent,
                                             final boolean global, final int viewDistance, final boolean visible,
                                             final Location location,
                                             final Vector3F rotation, final boolean small, final boolean marker,
                                             final @NonNull ItemStack item) {
        return new ArmorStandBlockItem(
                uuid, concurrent ? new ConcurrentHashMap<>() : new HashMap<>(),
                global, viewDistance, visible, location, rotation, small, marker, item
        );
    }

    /**
     * Creates valid metadata for armor stand block-item.
     *
     * @param rotation rotation of this block-item
     * @param small whether this block-item is small
     * @return created metadata object
     */
    protected static WrappedDataWatcher createMetadata(final @Nullable Vector3F rotation,
                                                       final boolean small, final boolean marker) {
        val metadata = new ArrayList<WrappedWatchableObject>();

        metadata.add(air(300));
        metadata.add(noGravity(true));
        if (marker) {
            metadata.add(entityFlags(Flag.INVISIBLE, Flag.ON_FIRE));
            metadata.add(armorStandFlags(small ? new ArmorStand.Flag[]{
                    ArmorStand.Flag.SMALL, ArmorStand.Flag.NO_BASE_PLATE, ArmorStand.Flag.MARKER
            } : new ArmorStand.Flag[]{
                    ArmorStand.Flag.MARKER, ArmorStand.Flag.NO_BASE_PLATE
            }));
        } else {
            metadata.add(entityFlags(Flag.INVISIBLE));
            metadata.add(armorStandFlags(small
                    ? new ArmorStand.Flag[]{ArmorStand.Flag.SMALL, ArmorStand.Flag.NO_BASE_PLATE}
                    : new ArmorStand.Flag[]{ArmorStand.Flag.NO_BASE_PLATE}
            ));
        }
        if (rotation != null) metadata.add(headRotation(rotation));

        return new WrappedDataWatcher(metadata);
    }

    @Override
    protected void performSpawnNoChecks(final Player player) {
        super.performSpawnNoChecks(player);
        equipmentPacket.sendPacket(player);
    }

    /**
     * Sets this blocks rotation to the one specified.
     *
     * @param rotation new rotation of this block
     */
    protected void setRotationNoChecks(final @Own @NotNull Vector3F rotation) {
        addMetadata(headRotation(rotation));
        this.rotation = rotation;
    }

    /**
     * Rotates this block by specified delta. This means that its current
     * roll (<i>x</i>), pitch (<i>y</i>) and yaw (<i>z</i>) will each get incremented by those of delta specified.
     *
     * @param delta delta of rotation
     */
    public void rotate(final @NonNull @Own Vector3F delta) {
        final float dx, dy = delta.getY(), dz = delta.getZ();
        if (((dx = delta.getX()) == 0) && dy == 0 && dz == 0) return; // no-op

        final Vector3F thisRotation;
        if (((thisRotation = rotation) != null)) {
            delta.setX(thisRotation.getX() + dx);
            delta.setY(thisRotation.getY() + dy);
            delta.setZ(thisRotation.getZ() + dz);
        }
        setRotationNoChecks(delta);
    }

    /**
     * Sets this blocks rotation to the one specified.
     *
     * @param newRotation new rotation of this block
     */
    public void setRotation(final @Own @NonNull Vector3F newRotation) {
        if (!newRotation.equals(rotation)) setRotationNoChecks(newRotation);
    }

    public void setItem(final @Own @NonNull ItemStack item) {
        equipmentPacket.setItem(this.item = item);
        for (val entry : players.entrySet()) if (entry.getValue()) equipmentPacket.sendPacket(entry.getKey());
    }
}
