package ru.progrm_jarvis.minecraft.commons.nms.metadata;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.EnumWrappers.Direction;
import org.bukkit.inventory.ItemStack;
import ru.progrm_jarvis.minecraft.commons.nms.Conversions;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public interface DataWatcherFactory {

    /**
     * Creates watchable object for {@link Byte} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchable(int id, Byte value);

    /**
     * Creates watchable object for {@link Integer} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchable(int id, Integer value);

    /**
     * Creates watchable object for {@link Float} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchable(int id, Float value);

    /**
     * Creates watchable object for {@link String} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchable(int id, String value);

    /**
     * Creates watchable object for {@code IChatBaseComponent} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchableIChatBaseComponent(int id, Object value);

    /**
     * Creates watchable object for {@link WrappedChatComponent} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default WrappedWatchableObject createWatchable(final int id, final WrappedChatComponent value) {
        return createWatchableIChatBaseComponent(id, value.getHandle());
    }

    /**
     * Creates watchable object for <i>NMS</i> {@code ItemStack} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchableItemStack(int id, Object value);

    /**
     * Creates watchable object for {@link ItemStack} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default WrappedWatchableObject createWatchable(final int id, final ItemStack value) {
        return createWatchableItemStack(id, MinecraftReflection.getMinecraftItemStack(value));
    }

    /**
     * Creates watchable object for {@code Optional<IBlockData>} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchableOptionalIBlockData(int id, Optional<Object> value);

    /**
     * Creates watchable object for {@link Boolean} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchable(int id, Boolean value);

    /**
     * Creates watchable object for {@code Vector3f} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchableVector3f(int id, Object value);
    /**
     * Creates watchable object for {@link Vector3F} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default WrappedWatchableObject createWatchable(final int id, final Vector3F value) {
        return createWatchableVector3f(id, Conversions.toNms(value));
    }

    /**
     * Creates watchable object for {@link BlockPosition} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchable(int id, BlockPosition value);

    /**
     * Creates watchable object for {@link Optional<Byte>} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchableOptionalBlockPosition(int id, Optional<BlockPosition> value);

    /**
     * Creates watchable object for {@code EnumDirection} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchableEnumDirection(int id, Object value);

    /**
     * Creates watchable object for {@link Direction} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default WrappedWatchableObject createWatchable(final int id, final Direction value) {
        return createWatchableEnumDirection(id, Conversions.toNms(value));
    }

    /**
     * Creates watchable object for {@link Optional<UUID>} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchableOptionalUUID(int id, Optional<UUID> value);

    /**
     * Creates watchable object for {@code NBTTagCompound} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchableNBTTagCompound(int id, Object value);

    /**
     * Creates watchable object for {@link Object} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    WrappedWatchableObject createWatchableObject(int id, Object value);

    /**
     * Creates new modifier for {@link WrappedDataWatcher} specified.
     *
     * @param watcher which to use as modifier backend
     * @return created modifier
     */
    DataWatcherModifier modifier(WrappedDataWatcher watcher);

    /**
     * Creates new modifier of {@link WrappedDataWatcher}.
     *
     * @return created modifier
     */
    DataWatcherModifier modifier();

    /**
     * Modifier of {@link WrappedDataWatcher} which applies all changes to DataWatcher.
     */
    interface DataWatcherModifier {

        /**
         * Deeply clones this modifier to a new one.
         *
         * @return this modifier's copy
         */
        DataWatcherModifier clone();

        /**
         * Sets DataWatcher's modifier to specified {@link Byte} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        DataWatcherModifier set(int id, Byte value);

        /**
         * Sets DataWatcher's modifier to specified {@link Integer} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        DataWatcherModifier set(int id, Integer value);

        /**
         * Sets DataWatcher's modifier to specified {@link Float} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        DataWatcherModifier set(int id, Float value);

        /**
         * Sets DataWatcher's modifier to specified {@link String} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        DataWatcherModifier set(int id, String value);

        /**
         * Sets DataWatcher's modifier to specified {@code IChatBaseComponent} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        DataWatcherModifier setIChatBaseComponent(int id, Object value);

        /**
         * Sets DataWatcher's modifier to specified {@link WrappedChatComponent} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default DataWatcherModifier setIChatBaseComponent(final int id, final WrappedChatComponent value) {
            return setIChatBaseComponent(id, value.getHandle());
        }

        /**
         * Sets DataWatcher's modifier to specified <i>NMS</i> {@code ItemStack} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        DataWatcherModifier setItemStack(int id, Object value);

        /**
         * Sets DataWatcher's modifier to specified {@link ItemStack} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default DataWatcherModifier set(final int id, final ItemStack value) {
            return setItemStack(id, MinecraftReflection.getMinecraftItemStack(value));
        }

        /**
         * Sets DataWatcher's modifier to specified {@code Optional<IBlockData>} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        DataWatcherModifier setOptionalIBlockData(int id, Optional<Object> value);

        /**
         * Sets DataWatcher's modifier to specified {@link Boolean} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        DataWatcherModifier set(int id, Boolean value);

        /**
         * Sets DataWatcher's modifier to specified {@code Vector3f} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        DataWatcherModifier setVector3f(int id, Object value);

        /**
         * Sets DataWatcher's modifier to specified {@link Vector3F} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default DataWatcherModifier set(final int id, final Vector3F value) {
            return setVector3f(id, Conversions.toNms(value));
        }

        /**
         * Sets DataWatcher's modifier to specified {@link BlockPosition} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        DataWatcherModifier set(int id, BlockPosition value);

        /**
         * Sets DataWatcher's modifier to specified {@link Optional<BlockPosition>} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        DataWatcherModifier setOptionalBlockPosition(int id, Optional<BlockPosition> value);

        /**
         * Sets DataWatcher's modifier to specified {@code EnumDirection} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        DataWatcherModifier setEnumDirection(int id, Object value);

        /**
         * Sets DataWatcher's modifier to specified {@link Direction} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default DataWatcherModifier set(final int id, final Direction value) {
            return setEnumDirection(id, Conversions.toNms(value));
        }

        /**
         * Sets DataWatcher's modifier to specified {@code NBTTagCompound} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        DataWatcherModifier setNBTTagCompound(int id, Object value);

        /**
         * Sets DataWatcher's modifier to specified {@link Optional<UUID>} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        DataWatcherModifier setOptionalUUID(int id, Optional<UUID> value);

        /**
         * Sets DataWatcher's modifier to specified {@link Object} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        // should be used if and only if none of default #setOptionalBlockPosition(id, value) methods don't provide type given
        DataWatcherModifier setObject(int id, Object value);

        /**
         * Returns DataWatcher modified.
         *
         * @return DataWatcher modified
         */
        WrappedDataWatcher dataWatcher();

        /**
         * Returns deep clone of DataWatcher modified.
         *
         * @return clone of DataWatcher modified
         */
        default WrappedDataWatcher dataWatcherClone() {
            return dataWatcher().deepClone();
        }
    }
}
