package ru.progrm_jarvis.minecraft.commons.nms;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.*;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import ru.progrm_jarvis.javacommons.invoke.InvokeUtil;
import ru.progrm_jarvis.minecraft.commons.nms.metadata.DataWatcherFactory;
import ru.progrm_jarvis.minecraft.commons.nms.metadata.LegacyDataWatcherFactory;
import ru.progrm_jarvis.minecraft.commons.nms.metadata.StandardDataWatcherFactory;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

/**
 * Utility for NMS-related features
 */
@UtilityClass
public class NmsUtil {

    private final NmsVersion NMS_VERSION = NmsVersion.computeCurrent();
    /**
     * Base package of NMS (<i>net.minecraft.server.{version}</i>)
     */
    private final String NMS_PACKAGE = "net.minecraft.server." + NMS_VERSION.name,
    /**
     * Base package of CraftBukkit (<i>org.bukkit.craftbukkit.{version}</i>)
     */
            CRAFT_BUKKIT_PACKAGE = "org.bukkit.craftbukkit." + NMS_VERSION.name;

    /**
     * Field access method-handle of <i>{nms}.Entity</i> class field responsible for entity <i>int-UID</i> generation.
     */
    private final MethodHandle ENTITY_COUNT_FIELD__GETTER, ENTITY_COUNT_FIELD__SETTER;

    private final Object ENTITY_COUNT_FIELD_MUTEX = new Object[0];

    static {
        final Class<?> nmsEntityClass;
        try {
            nmsEntityClass = Class.forName(getNmsPackage() + ".Entity");
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find NMS-entity class", e);
        }
        final Field entityCountField;
        try {
            entityCountField = nmsEntityClass.getDeclaredField("entityCount");
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(
                    "Cannot find field " + nmsEntityClass.getCanonicalName() + "#entityCount", e
            );
        }
        val accessible = entityCountField.isAccessible();
        entityCountField.setAccessible(true);
        try {
            ENTITY_COUNT_FIELD__GETTER = InvokeUtil.toGetterMethodHandle(entityCountField);
            ENTITY_COUNT_FIELD__SETTER = InvokeUtil.toSetterMethodHandle(entityCountField);
        } finally {
            entityCountField.setAccessible(accessible);
        }
    }

    /**
     * DataWatcher factory valid for current server version
     */
    private final DataWatcherFactory DATA_WATCHER_FACTORY = NMS_VERSION.generation < 9
            ? new LegacyDataWatcherFactory() : new StandardDataWatcherFactory();

    /**
     * Gets version of the current server.
     *
     * @return version of this server
     */
    public NmsVersion getVersion() {
        return NMS_VERSION;
    }

    /**
     * Gets base package of NMS (<i>net.minecraft.server.{version}</i>)
     *
     * @return base package of NMS
     */
    public String getNmsPackage() {
        return NMS_PACKAGE;
    }

    /**
     * Gets base package of CraftBukkit (<i>org.bukkit.craftbukkit.{version}</i>)
     *
     * @return base package of CraftBukkit
     */
    public String getCraftBukkitPackage() {
        return CRAFT_BUKKIT_PACKAGE;
    }

    /**
     * Gets DataWatcher factory valid for current server version.
     *
     * @return DataWatcher factory valid for current server version
     */
    public static DataWatcherFactory getDataWatcherFactory() {
        return DATA_WATCHER_FACTORY;
    }

    /**
     * Creates new DataWatcher modifier valid for current server version.
     *
     * @return DataWatcher modifier valid for current server version
     */
    public DataWatcherFactory.DataWatcherModifier dataWatcherModifier() {
        return DATA_WATCHER_FACTORY.modifier();
    }

    /**
     * Creates new DataWatcher modifier from DataWatcher specified which valid for current server version.
     *
     * @param dataWatcher DataWatcher from which to create DataWatcher modifier
     * @return DataWatcher modifier valid for current server version
     */
    public DataWatcherFactory.DataWatcherModifier dataWatcherModifier(@NonNull final WrappedDataWatcher dataWatcher) {
        return DATA_WATCHER_FACTORY.modifier(dataWatcher);
    }

    /**
     * Gets ID for entity preventing from conflicts with real entities.
     *
     * @return new ID for an entity
     */
    @SneakyThrows
    @Synchronized("ENTITY_COUNT_FIELD_MUTEX")
    public int nextEntityId() {
        val id = (int) ENTITY_COUNT_FIELD__GETTER.invokeExact();
        ENTITY_COUNT_FIELD__SETTER.invokeExact(id + 1);

        return id;
    }

    /**
     * Version of a server.
     */
    @Value
    @RequiredArgsConstructor
    public static final class NmsVersion {

        /**
         * Name of the version
         */
        @NonNull private String name;

        /**
         * Generation of a version (such as <b>13</b> for minecraft <i>1.<b>13</b>.2</i>)
         */
        private short generation;

        /**
         * Constructs a new NMS version by name specified (such as <i>v1_12_R1</i>).
         *
         * @param name name of a version
         */
        private NmsVersion(@NonNull final String name) {
            this(name, Short.parseShort(name.substring(3, name.indexOf('_', 4))));
        }

        /**
         * Computes current version of NMS for this server
         * based on implementation of {@link org.bukkit.Server} accessible via {@link Bukkit#getServer()}.
         *
         * @return current NMS version
         */
        public static NmsVersion computeCurrent() {
            val craftServerPackage = Bukkit.getServer().getClass().getPackage().getName();

            return new NmsVersion(craftServerPackage.substring(craftServerPackage.lastIndexOf('.') + 1));
        }
    }
}
