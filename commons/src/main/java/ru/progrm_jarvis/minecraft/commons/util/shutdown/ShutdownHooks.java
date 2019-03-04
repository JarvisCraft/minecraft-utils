package ru.progrm_jarvis.minecraft.commons.util.shutdown;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.plugin.BukkitPluginShutdownUtil;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * An object holding all shutdown hooks of an object which should be called once it should bw shut down.
 * This object also guarantees that all hooks will be ran only once and repeated call
 */
public interface ShutdownHooks extends Shutdownable {

    /**
     * Adds a shutdown hook.
     *
     * @param hook shutdown hook to add
     * @return this {@link ShutdownHooks} for chaining
     */
    ShutdownHooks add(@NonNull Runnable hook);

    /**
     * Adds a shutdown hook.
     *
     * @param hookSupplier supplier to be used instantly to create a hook
     * @return this {@link ShutdownHooks} for chaining
     *
     * @apiNote supplier is called instantly, not lazily
     */
    <T> ShutdownHooks add(@NonNull Supplier<Runnable> hookSupplier);

    /**
     * Removes a shutdown hook.
     *
     * @param hook shutdown hook to remove
     * @return this {@link ShutdownHooks} for chaining
     */
    ShutdownHooks remove(@NonNull Runnable hook);

    /**
     * Registers these {@link ShutdownHooks} as a Bukkit plugin shutdown hook.
     *
     * @param plugin plugin whose shutdown hook this is
     * @return this {@link ShutdownHooks} for chaining
     */
    ShutdownHooks registerBukkitShutdownHook(@NonNull Plugin plugin);

    /**
     * Unregisters these {@link ShutdownHooks} as a Bukkit plugin shutdown hook.
     *
     * @return this {@link ShutdownHooks} for chaining
     */
    ShutdownHooks unregisterBukkitShutdownHook();

    /**
     * Calls all the hooks.
     */
    @Override
    void shutdown();

    /**
     * Retrieves whether or not {@link #shutdown()} was called.
     *
     * @return {@link true} if this was shut down and {@link false} otherwise
     */
    boolean isShutDown();

    /**
     * Creates new {@link ShutdownHooks} instance.
     *
     * @return created {@link ShutdownHooks} instance
     */
    static ShutdownHooks create() {
        return new Simple();
    }

    /**
     * Creates new {@link ShutdownHooks} instance.
     *
     * @param parent object whose shutdown hooks those are
     * @return created {@link ShutdownHooks} instance
     */
    static ShutdownHooks create(@NonNull final Shutdownable parent) {
        return new Simple(parent);
    }

    /**
     * Creates new concurrent {@link ShutdownHooks} instance.
     *
     * @return created concurrent {@link ShutdownHooks} instance
     */
    static ShutdownHooks createConcurrent() {
        return new Concurrent();
    }

    /**
     * Creates new concurrent {@link ShutdownHooks} instance.
     *
     * @param parent object whose shutdown hooks those are
     * @return created concurrent {@link ShutdownHooks} instance
     */
    static ShutdownHooks createConcurrent(@NonNull final Shutdownable parent) {
        return new Concurrent(parent);
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PROTECTED)
    class Simple implements ShutdownHooks {

        @Nullable final Shutdownable parent;
        @NonNull final Deque<Runnable> shutdownHooks = new ArrayDeque<>();
        @Getter boolean shutDown = false;

        @Nullable Plugin bukkitPlugin;

        public Simple() {
            this(null);
        }

        protected void checkState() {
            if (shutDown) throw new ObjectAlreadyShutDownException(parent);
        }

        @Override
        public ShutdownHooks add(@NonNull final Runnable hook) {
            checkState();

            shutdownHooks.add(hook);

            return this;
        }

        @Override
        public <T> ShutdownHooks add(@NonNull final Supplier<Runnable> hookSupplier) {
            checkState();

            shutdownHooks.add(hookSupplier.get());

            return null;
        }

        @Override
        public ShutdownHooks remove(@NonNull final Runnable hook) {
            checkState();

            shutdownHooks.remove(hook);

            return this;
        }

        @Override
        public ShutdownHooks registerBukkitShutdownHook(@NonNull final Plugin plugin) {
            checkState();

            if (bukkitPlugin == null) {
                BukkitPluginShutdownUtil.addShutdownHook(bukkitPlugin = plugin, this);

                return this;
            } else {
                BukkitPluginShutdownUtil.removeShutdownHooks(bukkitPlugin, this);
                BukkitPluginShutdownUtil.addShutdownHook(plugin, this);
            }

            return this;
        }

        @Override
        public ShutdownHooks unregisterBukkitShutdownHook() {
            checkState();

            if (bukkitPlugin != null) {
                BukkitPluginShutdownUtil.removeShutdownHooks(bukkitPlugin, this);
                bukkitPlugin = null;
            }

            return this;
        }

        @Override
        public void shutdown() {
            checkState();

            shutDown = true;
            unregisterBukkitShutdownHook();

            for (val shutdownHook : shutdownHooks) shutdownHook.run();

            shutdownHooks.clear();
        }
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PROTECTED)
    class Concurrent implements ShutdownHooks {

        @Nullable final Shutdownable parent;
        @NonNull final Deque<Runnable> shutdownHooks = new ConcurrentLinkedDeque<>();
        AtomicBoolean shutDown = new AtomicBoolean();

        @NonNull final AtomicReference<Plugin> bukkitPlugin = new AtomicReference<>();

        public Concurrent() {
            this(null);
        }

        protected void checkState() {
            if (shutDown.get()) throw new ObjectAlreadyShutDownException(parent);
        }

        @Override
        public ShutdownHooks add(@NonNull final Runnable hook) {
            checkState();

            shutdownHooks.add(hook);

            return this;
        }

        @Override
        public <T> ShutdownHooks add(@NonNull final Supplier<Runnable> hookSupplier) {
            checkState();

            shutdownHooks.add(hookSupplier.get());

            return this;
        }

        @Override
        public ShutdownHooks remove(@NonNull final Runnable hook) {
            checkState();

            shutdownHooks.remove(hook);

            return this;
        }

        @Override
        public ShutdownHooks registerBukkitShutdownHook(@NonNull final Plugin plugin) {
            checkState();

            if (bukkitPlugin.compareAndSet(null, plugin)) BukkitPluginShutdownUtil.addShutdownHook(plugin, this);
            else {
                BukkitPluginShutdownUtil.removeShutdownHooks(bukkitPlugin.get(), this);
                BukkitPluginShutdownUtil.addShutdownHook(plugin, this);
            }

            return this;
        }

        @Override
        public ShutdownHooks unregisterBukkitShutdownHook() {
            checkState();

            val plugin = bukkitPlugin.get();
            if (plugin != null) {
                BukkitPluginShutdownUtil.removeShutdownHooks(plugin, this);
                bukkitPlugin.compareAndSet(plugin, null);
            }

            return this;
        }

        @Override
        public void shutdown() {
            if (shutDown.compareAndSet(false, true)) {
                unregisterBukkitShutdownHook();

                for (val shutdownHook : shutdownHooks) shutdownHook.run();

                shutdownHooks.clear();
            } else throw new ObjectAlreadyShutDownException(parent);
        }

        @Override
        public boolean isShutDown() {
            return shutDown.get();
        }
    }
}
