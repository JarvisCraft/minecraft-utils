package ru.progrm_jarvis.minecraft.commons.util.shutdown;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

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
     * Removes a shutdown hook.
     *
     * @param hook shutdown hook to remove
     * @return this {@link ShutdownHooks} for chaining
     */
    ShutdownHooks remove(@NonNull Runnable hook);

    /**
     * Calls all the hooks.
     */
    @Override
    void shutdown();

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
        boolean shutDown = false;

        public Simple() {
            this(null);
        }

        @Override
        public ShutdownHooks add(@NonNull final Runnable hook) {
            if (shutDown) throw new ObjectAlreadyShutDownException(parent);

            shutdownHooks.add(hook);

            return this;
        }

        @Override
        public ShutdownHooks remove(@NonNull final Runnable hook) {
            if (shutDown) throw new ObjectAlreadyShutDownException(parent);

            shutdownHooks.remove(hook);

            return this;
        }

        @Override
        public void shutdown() {
            shutDown = true;

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

        public Concurrent() {
            this(null);
        }

        @Override
        public ShutdownHooks add(@NonNull final Runnable hook) {
            if (shutDown.get()) throw new ObjectAlreadyShutDownException(parent);

            shutdownHooks.add(hook);

            return this;
        }

        @Override
        public ShutdownHooks remove(@NonNull final Runnable hook) {
            if (shutDown.get()) throw new ObjectAlreadyShutDownException(parent);

            shutdownHooks.remove(hook);

            return this;
        }

        @Override
        public void shutdown() {
            if (shutDown.compareAndSet(false, true)) {
                for (val shutdownHook : shutdownHooks) shutdownHook.run();

                shutdownHooks.clear();
            } else throw new ObjectAlreadyShutDownException(parent);
        }
    }
}
