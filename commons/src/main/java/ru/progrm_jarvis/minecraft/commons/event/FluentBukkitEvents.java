package ru.progrm_jarvis.minecraft.commons.event;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import ru.progrm_jarvis.minecraft.commons.util.concurrent.ConcurrentCollections;
import ru.progrm_jarvis.minecraft.commons.util.function.UncheckedConsumer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility for fluent and comfortable registration of events.
 */
@UtilityClass
public class FluentBukkitEvents {

    /**
     * Plugin manager of Bukkit
     */
    private final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();

    /**
     * Event listeners groups stored by <i>Plugin -> Event-Type -> EventPriority</i>.
     */
    private final Map<ListenerConfiguration<?>, EventListenersGroup<?>> LISTENERS_GROUPS = new ConcurrentHashMap<>();

    /**
     * Creates an event listener registration for fluent registration of the event listener(s).
     *
     * @param eventType type of the event handled to use for registration
     * @param <E> type of the event handled
     * @return event listener registration for fluent registration of the event listener(s)
     */
    public <E extends Event> EventListenerRegistration<E> on(@NonNull final Class<E> eventType) {
        return new EventListenerRegistration<>(eventType);
    }

    @Data
    @Accessors(chain = true, fluent = true)
    public static final class EventListenerRegistration<E extends Event> {

        /**
         * Type of event registered
         */
        @NonNull Class<E> type;

        /**
         * Plugin to use for event registration
         */
        @NonNull Plugin plugin;

        /**
         * Priority of the event's handler
         */
        @NonNull EventPriority priority = EventPriority.NORMAL;

        /**
         * Instantiates new event listener registration for the specified event type.
         *
         * @param type type of event handled
         */
        public EventListenerRegistration(@NonNull final Class<E> type) {
            this.type = type;
        }

        /**
         * Gets the event listeners group for this event listeners registration's configuration.
         *
         * @return event listeners group for this event listener registration's configuration
         */
        @SuppressWarnings("unchecked")
        private EventListenersGroup<E> getListenersGroup() {
            return (EventListenersGroup<E>) LISTENERS_GROUPS.computeIfAbsent(
                    new ListenerConfiguration<>(plugin, type, priority),
                    configuration -> new EventListenersGroup<>((ListenerConfiguration<E>) configuration)
            );
        }

        /**
         * Registers the event.
         *
         * @param listener listener to use for event handling
         * @return unregister to use for event unregistration
         */
        public Unregister register(@NonNull final UncheckedConsumer<E> listener) {
            checkNotNull(plugin, "plugin has not been set");

            val listenersGroup = getListenersGroup();
            listenersGroup.addListener(listener);

            return () -> listenersGroup.removeListener(listener);
        }

        /**
         * Object used for unregistering the registered event
         */
        @FunctionalInterface
        public interface Unregister {

            /**
             * Unregisters the registered event
             */
            void unregister();
        }
    }

    /**
     * Group of event listeners having the same parameters.
     *
     * @param <E> type of handled event
     */
    @Value
    private static final class EventListenersGroup<E extends Event> implements Listener, EventExecutor {

        /**
         * Plugin managing these event listeners
         */
        @NonNull final ListenerConfiguration<E> configuration;

        /**
         * Event listeners in this event listener group's dequeue
         */
        @Getter(AccessLevel.NONE) @NonNull Deque<UncheckedConsumer<E>> eventListeners
                = ConcurrentCollections.concurrentDeque(new ArrayDeque<>());

        /**
         * Adds the listener to the deque of handled listeners for the event.
         *
         * @param listener listener to add to handling dequeue
         */
        private void addListener(@NonNull final UncheckedConsumer<E> listener) {
            if (eventListeners.isEmpty()) {
                PLUGIN_MANAGER
                        .registerEvent(configuration.type, this, configuration.priority, this, configuration.plugin);
                LISTENERS_GROUPS.putIfAbsent(configuration, this);
            }

            eventListeners.add(listener);
        }

        /**
         * Removes the listener from the deque of handled listeners for the event.
         *
         * @param listener listener to remove from handling dequeue
         */
        private void removeListener(@NonNull final UncheckedConsumer<E> listener) {
            eventListeners.remove(listener);

            if (eventListeners.isEmpty()) {
                HandlerList.unregisterAll(this);
                LISTENERS_GROUPS.remove(configuration);
            }
        }

        @Override
        public void execute(final Listener listener, final Event event) {
            @SuppressWarnings("unchecked") val castEvent = (E) event;
            for (val eventListener : eventListeners) eventListener.accept(castEvent);
        }
    }

    @Value
    private static final class ListenerConfiguration<E> {
        @NonNull final Plugin plugin;
        @NonNull final Class<E> type;
        @NonNull final EventPriority priority;
    }
}
