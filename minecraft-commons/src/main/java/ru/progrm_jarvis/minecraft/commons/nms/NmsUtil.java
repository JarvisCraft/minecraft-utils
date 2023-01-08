package ru.progrm_jarvis.minecraft.commons.nms;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.*;
import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ru.progrm_jarvis.javacommons.invoke.InvokeUtil;
import ru.progrm_jarvis.minecraft.commons.nms.metadata.DataWatcherFactory;
import ru.progrm_jarvis.minecraft.commons.nms.metadata.DataWatcherFactory.DataWatcherModifier;
import ru.progrm_jarvis.minecraft.commons.nms.metadata.LegacyDataWatcherFactory;
import ru.progrm_jarvis.minecraft.commons.nms.metadata.StandardDataWatcherFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.invoke.MethodHandles.insertArguments;
import static java.lang.invoke.MethodType.methodType;

/**
 * Utility for NMS-related features
 */
@Log
@UtilityClass
public class NmsUtil {

    private static final MethodHandles.@NotNull Lookup LOOKUP = MethodHandles.lookup();

    private final @NotNull NmsVersion CRAFT_BUKKIT_VERSION = NmsVersion.computeCurrent();

    /**
     * Base package of NMS (<i>net.minecraft.server.{version}</i>)
     *
     * @deprecated since Spigot 1.17, official Mojang Mappings are used
     */
    @Deprecated
    private final @NotNull String NMS_PACKAGE = "net.minecraft.server." + CRAFT_BUKKIT_VERSION.getName();

    /**
     * Base package of CraftBukkit (<i>org.bukkit.craftbukkit.{version}</i>)
     */
    private final @NotNull String CRAFT_BUKKIT_PACKAGE = "org.bukkit.craftbukkit." + CRAFT_BUKKIT_VERSION.getName();

    private final @NotNull MethodHandle NEXT_ENTITY_ID;

    static {
        Class<?> minecraftVersionClass;
        try {
            minecraftVersionClass = Class.forName("net.minecraft.MinecraftVersion");
        } catch (final ClassNotFoundException e) {
            minecraftVersionClass = null;
            log.info("Current Minecraft Server version used legacy NMS structure");
        }

        val legacyMappings = minecraftVersionClass == null;
        log.info(() -> "Using " + (legacyMappings ? "legacy" : "official") + " Minecraft Server mapping");

        final Class<?> nmsEntityClass;
        {
            val nmsEntityClassName = (legacyMappings ? getNmsPackage() : "net.minecraft.world.entity") + ".Entity";
            try {
                nmsEntityClass = Class.forName(nmsEntityClassName);
            } catch (final ClassNotFoundException e) {
                throw new InternalError("Cannot find entity class by name \"" + nmsEntityClassName + '"', e);
            }
        }

        entityIdImplementation: {
            // Paper has a method for generating entity ID
            paperApi: {
                @SuppressWarnings("deprecation") val unsafe = Bukkit.getUnsafe();
                val unsafeValuesClass = unsafe.getClass();
                val nextEntityIdMethodType = methodType(int.class);
                final MethodHandle nextEntityIdMethodHandle;
                try {
                    nextEntityIdMethodHandle = LOOKUP
                            .findVirtual(unsafeValuesClass, "nextEntityId", nextEntityIdMethodType);
                } catch (final NoSuchMethodException | IllegalAccessException e) {
                    // required Paper API is not available
                    break paperApi;
                }

                NEXT_ENTITY_ID = insertArguments(nextEntityIdMethodHandle, 0, unsafe);

                break entityIdImplementation;
            }

            if (!legacyMappings) {
                for (val field : nmsEntityClass.getDeclaredFields()) { // trust field order
                    val modifiers = field.getModifiers();
                    if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
                            && AtomicInteger.class.isAssignableFrom(field.getType())) {
                        NEXT_ENTITY_ID = createAtomicIntegerNextEntityId(field);

                        break entityIdImplementation;
                    }
                }

                throw new InternalError("Cannot find entity count field in " + nmsEntityClass);
            }

            final Field entityCountField;
            try {
                entityCountField = nmsEntityClass.getDeclaredField("entityCount");
            } catch (NoSuchFieldException e) {
                throw new InternalError("Cannot find field " + nmsEntityClass.getCanonicalName() + "#entityCount", e);
            }

            final Class<?> entityCountFieldType;
            if (AtomicInteger.class.isAssignableFrom(entityCountFieldType = entityCountField.getType())) NEXT_ENTITY_ID
                    = createAtomicIntegerNextEntityId(entityCountField);
            else if (entityCountFieldType == int.class) NEXT_ENTITY_ID = createIntNextEntityId(entityCountField);
            else throw new InternalError(
                        "Field `nmsEntityClass#" + entityCountField
                                + "` is of unsupported type `" + entityCountFieldType + '`'
                );
        }
    }

    /**
     * DataWatcher factory valid for current server version
     */
    private final DataWatcherFactory DATA_WATCHER_FACTORY = CRAFT_BUKKIT_VERSION.getGeneration() < 9
            ? LegacyDataWatcherFactory.create() : StandardDataWatcherFactory.create();

    /**
     * Gets version of the current server.
     *
     * @return version of this server
     */
    public @NotNull NmsVersion getVersion() {
        return CRAFT_BUKKIT_VERSION;
    }

    /**
     * Gets base package of NMS (<i>net.minecraft.server.{version}</i>)
     *
     * @return base package of NMS
     *
     * @deprecated since Spigot 1.17, official Mojang Mappings are used
     */
    @Deprecated // this is no longer required for Minecraft 1.17
    public @NotNull String getNmsPackage() {
        return NMS_PACKAGE;
    }

    /**
     * Gets base package of CraftBukkit (<i>org.bukkit.craftbukkit.{version}</i>)
     *
     * @return base package of CraftBukkit
     */
    public @NotNull String getCraftBukkitPackage() {
        return CRAFT_BUKKIT_PACKAGE;
    }

    /**
     * Gets DataWatcher factory valid for current server version.
     *
     * @return DataWatcher factory valid for current server version
     */
    public static @NotNull DataWatcherFactory getDataWatcherFactory() {
        return DATA_WATCHER_FACTORY;
    }

    /**
     * Creates new DataWatcher modifier valid for current server version.
     *
     * @return DataWatcher modifier valid for current server version
     */
    public @NotNull DataWatcherModifier dataWatcherModifier() {
        return DATA_WATCHER_FACTORY.modifier();
    }

    /**
     * Creates new DataWatcher modifier from DataWatcher specified which valid for current server version.
     *
     * @param dataWatcher DataWatcher from which to create DataWatcher modifier
     * @return DataWatcher modifier valid for current server version
     */
    public @NotNull DataWatcherModifier dataWatcherModifier(final @NonNull WrappedDataWatcher dataWatcher) {
        return DATA_WATCHER_FACTORY.modifier(dataWatcher);
    }

    /**
     * Gets ID for entity preventing from conflicts with real entities.
     *
     * @return new ID for an entity
     */
    @SneakyThrows // `MethodHandles#invokeExact()`
    public int nextEntityId() {
        return (int) NEXT_ENTITY_ID.invokeExact();
    }

    @SneakyThrows // MethodHandle#invokeExact(..)
    private static @NotNull MethodHandle createAtomicIntegerNextEntityId(final @NotNull Field atomicIntegerField) {
        val type = methodType(int.class);
        final MethodHandle methodHandle;
        try {
            methodHandle = LOOKUP.findVirtual(AtomicInteger.class, "incrementAndGet", type);
        } catch (final NoSuchMethodException | IllegalAccessException e) {
            throw new AssertionError("Failed to find `AtomicInteger#incrementAndGet()` method", e);
        }

        return insertArguments(
                methodHandle, 0, (AtomicInteger) InvokeUtil.toGetterMethodHandle(atomicIntegerField).invokeExact()
        );
    }

    @SneakyThrows // MethodHandle#invokeExact(..)
    private static @NotNull MethodHandle createIntNextEntityId(final @NotNull Field intField) {
        val type = methodType(int.class, MethodHandle.class, MethodHandle.class);
        final MethodHandle methodHandle;
        try {
            methodHandle = LOOKUP.findStatic(NmsUtil.class, "getAndIncrementInt", type);
        } catch (final NoSuchMethodException | IllegalAccessException e) {
            throw new AssertionError("Failed to find `AtomicInteger#incrementAndGet()` method", e);
        }

        return insertArguments(
                methodHandle, 0,
                InvokeUtil.toGetterMethodHandle(intField),
                InvokeUtil.toSetterMethodHandle(intField)
        );
    }

    @SneakyThrows // `MethodHandle#invokeExact(..)`
    private static int getAndIncrementInt(final @NotNull MethodHandle getter, final @NotNull MethodHandle setter) {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException(
                "Entity IDs should only be generated on main Bukkit thread"
        );

        val id = (int) getter.invokeExact();
        setter.invokeExact(id + 1);

        return id;
    }

    /**
     * Version of a server.
     */
    @Value
    @RequiredArgsConstructor
    public static class NmsVersion {

        /**
         * Name of the version
         */
        @NonNull String name;

        /**
         * Generation of a version (such as <b>13</b> for minecraft <i>1.<b>13</b>.2</i>)
         */
        short generation;

        /**
         * Constructs a new NMS version by name specified (such as <i>v1_12_R1</i>).
         *
         * @param name name of a version
         */
        private NmsVersion(final @NonNull String name) {
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
