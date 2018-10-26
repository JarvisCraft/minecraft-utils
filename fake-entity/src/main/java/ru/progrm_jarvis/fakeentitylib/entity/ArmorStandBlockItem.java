package ru.progrm_jarvis.fakeentitylib.entity;

import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.progrm_jarvis.nmsutils.NmsUtil;
import ru.progrm_jarvis.nmsutils.metadata.MetadataGenerator;
import ru.progrm_jarvis.nmsutils.metadata.MetadataGenerator.ArmorStand;
import ru.progrm_jarvis.nmsutils.metadata.MetadataGenerator.Entity;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static ru.progrm_jarvis.nmsutils.metadata.MetadataGenerator.ArmorStand.armorStandFlags;
import static ru.progrm_jarvis.nmsutils.metadata.MetadataGenerator.ArmorStand.headRotation;
import static ru.progrm_jarvis.nmsutils.metadata.MetadataGenerator.Entity.entityFlags;

/**
 * A fake small (or very small) movable block-item which can be normally rotated over all axises
 * and have floating point coordinates. This block is displayed as an item on head of invisible armor stand.
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ArmorStandBlockItem extends SimpleLivingFakeEntity {

    /**
     * Rotation of this block
     */
    Vector3F rotation;

    /**
     * Item displayed by this block
     */
    ItemStack item;

    /**
     * Packet used for displaying this block's displayed item
     */
    WrapperPlayServerEntityEquipment equipmentPacket;

    /**
     * Initializes a newly created armor stand block-item from parameters given.
     *
     * @param uuid unique entity id of this block-item entity
     * @param playersMap map to be used as backend for this block-item entity
     * @param global whether this block-item is global (the value returned by {@link #isGlobal()})
     * @param viewDistance view distance of this block-item
     * @param location location of this block-item
     * @param rotation rotation of this block item
     * @param small whether this block-item is small
     * @param item item to be displayed by this block-item
     */
    public ArmorStandBlockItem(@Nullable final UUID uuid,
                               final Map<Player, Boolean> playersMap, final boolean global,
                               final int viewDistance, final Location location,
                               final Vector3F rotation, final boolean small, @NonNull final ItemStack item) {
        super(
                NmsUtil.nextEntityId(), uuid, EntityType.ARMOR_STAND,
                playersMap, global, viewDistance, location, 0, null, createMetadata(rotation, small)
        );

        this.rotation = rotation;

        equipmentPacket = new WrapperPlayServerEntityEquipment();
        equipmentPacket.setEntityID(id);
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
    public static ArmorStandBlockItem create(final boolean concurrent, final boolean global, final int viewDistance,
                                             final Location location,
                                             final Vector3F rotation, final boolean small,
                                             @NonNull final ItemStack item) {
        return new ArmorStandBlockItem(
                null, concurrent ? new ConcurrentHashMap<>() : new HashMap(),
                global, viewDistance, location, rotation, small, item
        );
    }

    /**
     * Creates valid metadata for armor stand block-item.
     *
     * @param rotation rotation of this block-item
     * @param small whether this block-item is small
     * @return created metadata object
     */
    public static WrappedDataWatcher createMetadata(final Vector3F rotation,
                                                    final boolean small) {
        return new WrappedDataWatcher(Arrays.asList(
                entityFlags(Entity.Flag.INVISIBLE),
                armorStandFlags(small
                        ? new ArmorStand.Flag[]{ArmorStand.Flag.SMALL, ArmorStand.Flag.MARKER}
                        : new ArmorStand.Flag[]{MetadataGenerator.ArmorStand.Flag.MARKER}),
                headRotation(rotation)
        ));
    }

    @Override
    public void spawn() {
        actualizeSpawnPacket();

        for (val entry : players.entrySet()) if (entry.getValue()) {
            val player = entry.getKey();

            spawnPacket.sendPacket(player);
            equipmentPacket.sendPacket(player);
        }
    }

    @Override
    public void render(final Player player) {
        actualizeSpawnPacket();
        spawnPacket.sendPacket(player);
        equipmentPacket.sendPacket(player);

        players.put(player, true);
    }

    /**
     * Sets this blocks rotation to the one specified.
     *
     * @param rotation new rotation of this block
     */
    public void setRotation(@NonNull final Vector3F rotation) {
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
        setRotation(new Vector3F(
                minimizeAngle(rotation.getX() + delta.getX()),
                minimizeAngle(rotation.getY() + delta.getY()),
                minimizeAngle(rotation.getZ() + delta.getZ())
        ));
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
}
