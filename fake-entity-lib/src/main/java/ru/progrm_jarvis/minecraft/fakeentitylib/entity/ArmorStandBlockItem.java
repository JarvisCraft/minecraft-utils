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
import ru.progrm_jarvis.minecraft.commons.math.dimensional.CuboidFigure;
import ru.progrm_jarvis.minecraft.commons.nms.NmsUtil;
import ru.progrm_jarvis.minecraft.commons.nms.metadata.MetadataGenerator.ArmorStand;

import javax.annotation.Nullable;
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
    public ArmorStandBlockItem(@Nullable final UUID uuid,
                               final Map<Player, Boolean> playersMap,
                               final boolean global, final int viewDistance, final boolean visible,
                               final Location location, @Nullable final Vector3F rotation,
                               final boolean small, final boolean marker, @NonNull final ItemStack item) {
        super(
                NmsUtil.nextEntityId(), uuid, EntityType.ARMOR_STAND,
                playersMap, global, viewDistance, visible, location, 0, null, createMetadata(rotation, small, marker)
        );

        this.rotation = rotation;

        equipmentPacket = new WrapperPlayServerEntityEquipment();
        equipmentPacket.setEntityID(entityId);
        equipmentPacket.setSlot(EnumWrappers.ItemSlot.HEAD);
        equipmentPacket.setItem(this.item = item);

        // actual block-item position (head of armorstand) is one block higher than its coordinate so normalize it
        {
            final double x = location.getX(), y = location.getY(), z = location.getZ();
            hitbox = new CuboidFigure(0.25, 0.25, 0.25);
        }
        yOffset = -1;
    }

    @Override
    public void spawn() {
        super.spawn();
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
    public static ArmorStandBlockItem create(@Nullable final UUID uuid,
                                             final boolean concurrent,
                                             final boolean global, final int viewDistance, final boolean visible,
                                             final Location location,
                                             final Vector3F rotation, final boolean small, final boolean marker,
                                             @NonNull final ItemStack item) {
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
    protected static WrappedDataWatcher createMetadata(@Nullable final Vector3F rotation,
                                                       final boolean small, final boolean marker) {
        val metadata = new ArrayList<WrappedWatchableObject>();

        metadata.add(air(300));
        metadata.add(noGravity(true));
        if (marker) {
            metadata.add(entityFlags(Flag.INVISIBLE, Flag.ON_FIRE));
            metadata.add(armorStandFlags(small ? new ArmorStand.Flag[]{
                            ArmorStand.Flag.SMALL, ArmorStand.Flag.NO_BASE_PLATE, ArmorStand.Flag.MARKER
                    } : new ArmorStand.Flag[]{ArmorStand.Flag.MARKER, ArmorStand.Flag.NO_BASE_PLATE}
            ));
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
    public void setRotation(@NonNull final Vector3F rotation) {
        if (rotation.equals(this.rotation)) return;

        addMetadata(headRotation(rotation));
        this.rotation = rotation;
    }

    /**
     * Rotates this block by specified delta. This means that its current
     * roll (<i>x</i>), pitch (<i>y</i>) and yaw (<i>z</i>) will each get incremented by those of delta specified.
     *
     * @param delta delta of rotation
     */
    public void rotate(@NonNull final Vector3F delta) {
        final Vector3F thisRotation;
        setRotation((thisRotation = rotation) == null
                ? new Vector3F(delta.getX(), delta.getY(), delta.getZ())
                : new Vector3F(
                        minimizeAngle(thisRotation.getX() + delta.getX()),
                        minimizeAngle(thisRotation.getY() + delta.getY()),
                        minimizeAngle(thisRotation.getZ() + delta.getZ())
                )
        );
    }

    /**
     * Rotates this block by specified delta. This means that its current
     * roll (<i>x</i>), pitch (<i>y</i>) and yaw (<i>z</i>) will each get incremented by those of delta specified.
     *
     * @param delta delta of rotation
     */
    public void rotateTo(@NonNull final Vector3F delta) {
        setRotation(rotation == null
                ? new Vector3F(delta.getX(), delta.getY(), delta.getZ())
                : new Vector3F(
                        minimizeAngle(rotation.getX() + delta.getX()),
                        minimizeAngle(rotation.getY() + delta.getY()),
                        minimizeAngle(rotation.getZ() + delta.getZ())
                )
        );
    }

    /**
     * Minimizes the angle so that it fits the interval of <i>[-360; 360]</i> keeping the actual rotation.
     * This means removing <i>360</i> until the number is less than or equal to <i>360</i>
     * or adding <i>360</i> until the number is bigger than or equal to <i>-360</i>.
     *
     * @param degrees non-minimized angle
     * @return minimized angle
     */
    public static float minimizeAngle(float degrees) {
        while (degrees >= 360) degrees -= 360;
        while (degrees <= -360) degrees += 360;

        return degrees;
    }

    public void setItem(@NonNull final ItemStack item) {
        equipmentPacket.setItem(this.item = item);
        for (val entry : players.entrySet()) if (entry.getValue()) equipmentPacket.sendPacket(entry.getKey());
    }
}
