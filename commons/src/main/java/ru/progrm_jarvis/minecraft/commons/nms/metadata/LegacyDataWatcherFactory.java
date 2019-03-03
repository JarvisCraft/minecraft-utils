package ru.progrm_jarvis.minecraft.commons.nms.metadata;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

/**
 * DataWatcher factory for pre 1.9 versions.
 */
public class LegacyDataWatcherFactory implements DataWatcherFactory {

    @Override
    public DataWatcherModifier modifier(WrappedDataWatcher watcher) {
        return new DataWatcherModifier(watcher);
    }

    @Override
    public DataWatcherModifier modifier() {
        return new DataWatcherModifier();
    }

    @Override
    public WrappedWatchableObject createWatchable(final int id, final Byte value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchable(final int id, final Integer value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchable(final int id, final Float value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchable(final int id, final String value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchableIChatBaseComponent(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchableItemStack(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchableOptionalIBlockData(final int id, final Optional<Object> value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchable(final int id, final Boolean value) {
        return createWatchable(id, value ? (byte) 0x1 : (byte) 0x0);
    }

    @Override
    public WrappedWatchableObject createWatchableVector3f(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchable(final int id, final BlockPosition value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchableOptionalBlockPosition(final int id, final Optional<BlockPosition> value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchableEnumDirection(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchableOptionalUUID(final int id, final Optional<UUID> value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchableNBTTagCompound(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchableObject(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @RequiredArgsConstructor
    private class DataWatcherModifier implements DataWatcherFactory.DataWatcherModifier {

        private final WrappedDataWatcher dataWatcher;

        private DataWatcherModifier() {
            this(new WrappedDataWatcher());
        }

        @Override
        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public DataWatcherFactory.DataWatcherModifier clone() {
            return new DataWatcherModifier(dataWatcher.deepClone());
        }

        @Override
        public WrappedDataWatcher dataWatcher() {
            return dataWatcher;
        }

        @Override
        public DataWatcherModifier set(final int id, final Byte value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public DataWatcherModifier set(final int id, final Integer value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public DataWatcherModifier set(final int id, final Float value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public DataWatcherModifier set(final int id, final String value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public DataWatcherModifier setIChatBaseComponent(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }


        @Override
        public DataWatcherModifier setItemStack(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public DataWatcherModifier set(final int id, final ItemStack value) {
            dataWatcher.setObject(id, MinecraftReflection.getMinecraftItemStack(value));

            return this;
        }

        @Override
        public DataWatcherModifier setOptionalIBlockData(final int id, final Optional<Object> value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public DataWatcherModifier set(final int id, final Boolean value) {
            dataWatcher.setObject(id, value ? (byte) 0x1 : (byte) 0x0);

            return this;
        }

        @Override
        public DataWatcherModifier setVector3f(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public DataWatcherModifier set(final int id, final BlockPosition value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public DataWatcherModifier setOptionalBlockPosition(final int id, final Optional<BlockPosition> value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public DataWatcherModifier setEnumDirection(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public DataWatcherModifier setNBTTagCompound(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public DataWatcherModifier setOptionalUUID(final int id, final Optional<UUID> value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        // should be used if and only if none of default #setOptionalBlockPosition(id, value) methods don't provide type given
        @Override
        public DataWatcherModifier setObject(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }
    }
}
