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
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.nmsutils.NmsUtil;
import ru.progrm_jarvis.nmsutils.metadata.MetadataGenerator.ArmorStand.Flag;
import ru.progrm_jarvis.playerutils.registry.PlayerRegistry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ru.progrm_jarvis.nmsutils.metadata.MetadataGenerator.ArmorStand.armorStandFlags;
import static ru.progrm_jarvis.nmsutils.metadata.MetadataGenerator.ArmorStand.headRotation;
import static ru.progrm_jarvis.nmsutils.metadata.MetadataGenerator.Entity.invisible;

@FieldDefaults(level = AccessLevel.PROTECTED)
public class ArmorStandBlock extends SimpleLivingFakeEntity {

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

    public ArmorStandBlock(final Plugin plugin,
                           @Nullable final PlayerRegistry registry, final boolean registerInDefaultRegistry,
                           @Nullable final UUID uuid,
                           final Map<Player, Boolean> players, final boolean global,
                           final int viewDistance, final Location location,
                           final Vector3F rotation, final boolean small, @NonNull final ItemStack item) {
        super(
                plugin, registry, registerInDefaultRegistry,
                NmsUtil.nextEntityId(), uuid, EntityType.ARMOR_STAND,
                players, global, viewDistance, location, 0, null, createMetadata(rotation, small)
        );

        this.rotation = rotation;

        equipmentPacket = new WrapperPlayServerEntityEquipment();
        equipmentPacket.setEntityID(id);
        equipmentPacket.setSlot(EnumWrappers.ItemSlot.HEAD);
        equipmentPacket.setItem(item);
    }

    public static ArmorStandBlock create(final Plugin plugin, final boolean register,
                                  final boolean concurrent, final boolean global, final int viewDistance,
                                  final Location location,
                                  final Vector3F rotation, final boolean small, @NonNull final ItemStack item) {
        return new ArmorStandBlock(
                plugin, null, register, null, concurrent ? new ConcurrentHashMap<>() : new HashMap(),
                global, viewDistance, location, rotation, small, item
        );
    }

    public static WrappedDataWatcher createMetadata(final Vector3F headRotation,
                                                    final boolean small) {
        return new WrappedDataWatcher(Arrays.asList(
                invisible(true),
                armorStandFlags(small ? new Flag[]{Flag.SMALL, Flag.MARKER} : new Flag[]{Flag.MARKER}),
                headRotation(headRotation)
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
